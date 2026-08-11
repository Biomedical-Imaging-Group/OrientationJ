"""
orientationj.py — faithful NumPy port of the OrientationJ gradient structure tensor (GST).

Every step reproduces the Java source of the plugin, operation by operation:

  OrientationJ Analysis      -> analysis() and survey()
      orientation.Gradient.gradientSpline      cubic-spline gradient (plugin default)
      orientation.filters.Gaussian             IIR exponential-cascade Gaussian (9 pole pairs)
      orientation.StructureTensor              energy, orientation, coherency, ...
      orientation.ColorMapping.colorHSB        HSB color survey (java.awt.Color.HSBtoRGB)
  OrientationJ Distribution  -> distribution()
      orientation.OrientationResults.distribution   180 bins of 1 degree, [-90, +90]
  OrientationJ Vector Field  -> vector_field() and vector_segments()
      orientation.OrientationResults.displayVectorField

Conventions (identical to the plugin):
  - images are 2D, indexed image[y, x], x = column, y = row (pointing down on screen),
  - the spline gradient is gx[x] = 0.5 * (c[x-1] - c[x+1]) on the cubic-spline
    coefficients c, i.e. minus the analytical derivative; the tensor only uses
    products of gradients so the features are unaffected,
  - theta = 0.5 * atan2(2*Jxy, Jyy - Jxx) in [-pi/2, +pi/2], counter-clockwise
    from the horizontal axis of the displayed image, along the structures,
  - intermediate maps are rounded to float32 wherever the plugin stores them in a
    float buffer, so the numbers match the Java output to float precision.

Defaults are the plugin macro defaults: sigma = 1 (structure tensor), epsilon = 0.001,
min-coherency = 0 %, min-energy = 0 %, vector grid = 10, scale = 100 %, type = 0.

Daniel Sage, Biomedical Imaging Group, EPFL.
"""

import numpy as np
import pandas as pd
from scipy.signal import lfilter
import tifffile

EPSILON = 0.001            # OrientationParameters.epsilon
SPLINE_POLE = np.sqrt(3.0) - 2.0
SPLINE_GAIN = 6.0

__all__ = ['load', 'analysis', 'survey', 'distribution', 'distribution_table',
           'vector_field', 'vector_segments', 'gradient_spline', 'gaussian_smooth']


# ------------------------------------------------------------------ image I/O

def load(path):
    """Read a TIFF as a 2D float64 image[y, x] (raw pixel values, like ImageJ
    converting the image to 32-bit)."""
    image = tifffile.imread(path).astype(np.float64)
    if image.ndim != 2:
        raise ValueError(str(path) + ': expected a single 2D image, got shape '
                         + str(image.shape))
    return image


# --------------------------------------------- cubic-spline prefilter (CubicSpline.java)

def _spline_coefficients(signal):
    """doSymmetricalExponentialFilter along the last axis (c0 = 6, a = sqrt(3) - 2),
    with the plugin's truncated-mirror initialization (epsilon = 1e-6)."""
    a = SPLINE_POLE
    n = signal.shape[-1]
    flat = signal.reshape(-1, n)

    # causal init: v = s[0] + sum_{k=1}^{k0-1} a^k s[k], k0 = ceil(ln 1e-6 / ln|a|)
    k0 = min(n, int(np.ceil(np.log(1e-6) / np.log(np.abs(a)))))
    weights = a ** np.arange(k0)
    init = flat[:, :k0] @ weights

    # causal: cp[k] = s[k] + a cp[k-1]
    causal_in = flat.copy()
    causal_in[:, 0] = init
    cp = lfilter([1.0], [1.0, -a], causal_in, axis=-1)

    # anti-causal: cn[n-1] = a/(a^2-1) (cp[n-1] + a cp[n-2]);  cn[k] = a (cn[k+1] - cp[k])
    cn = _anticausal(cp, a)
    return (SPLINE_GAIN * cn).reshape(signal.shape)


def _anticausal(cp, pole, init=None):
    """Backward recursion out[k] = pole * (out[k+1] - cp[k]) along the last axis."""
    if init is None:
        init = (pole / (pole * pole - 1.0)) * (cp[:, -1] + pole * cp[:, -2])
    reversed_in = cp[:, ::-1]
    zi = (init + pole * reversed_in[:, 0])[:, np.newaxis]
    out, _ = lfilter([-pole], [1.0, -pole], reversed_in, axis=-1, zi=zi)
    return out[:, ::-1]


# --------------------------------------------------- spline gradient (Gradient.java)

def gradient_spline(image):
    """Gradient.gradientSpline: gx, gy from the cubic-spline coefficients,
    gx[x] = 0.5 * (c[x-1] - c[x+1]), zero on the first/last column (row for gy).
    Returns float32 maps like the plugin's float buffers."""
    image = np.asarray(image).astype(np.float32).astype(np.float64)
    gx = np.zeros_like(image)
    gy = np.zeros_like(image)

    coef = _spline_coefficients(image)          # along x, row by row
    gx[:, 1:-1] = 0.5 * (coef[:, :-2] - coef[:, 2:])

    coef = _spline_coefficients(image.T).T      # along y, column by column
    gy[1:-1, :] = 0.5 * (coef[:-2, :] - coef[2:, :])

    return gx.astype(np.float32), gy.astype(np.float32)


# ------------------------------------------- Gaussian filter (filters/Gaussian.java)

def _gaussian_axis(flat, pole):
    """convolveIIR_TriplePole: 9 cascaded symmetric pole pairs along the last axis."""
    n = flat.shape[-1]
    passes = 9
    out = flat * ((1.0 - pole) * (1.0 - 1.0 / pole)) ** passes

    # weights of getInitialCausalCoefficientMirror (tolerance 1e-5, truncated horizon)
    horizon = min(n, 2 + int(np.log(1e-5) / np.log(np.abs(pole))))
    weights = np.zeros(n)
    weights[0] = 1.0
    weights[n - 1] += pole ** (n - 1)
    for k in range(1, horizon - 1):
        weights[k] += pole ** k + pole ** (2 * n - 2 - k)
    weights /= 1.0 - pole ** (2 * n - 2)

    for _ in range(passes):
        causal_in = out.copy()
        causal_in[:, 0] = out @ weights
        out = lfilter([1.0], [1.0, -pole], causal_in, axis=-1)
        init = (pole * out[:, -2] + out[:, -1]) * pole / (pole * pole - 1.0)
        out = _anticausal(out, pole, init=init)
    return out


def gaussian_smooth(image, sigma):
    """filters/Gaussian.java on one 2D image: separable IIR Gaussian, both axes."""
    if sigma <= 0:
        return image.copy()
    s2 = sigma * sigma
    pole = 1.0 + (3.0 / s2) - (np.sqrt(9.0 + 6.0 * s2) / s2)
    ny, nx = image.shape
    out = _gaussian_axis(image.T.reshape(-1, ny), pole).reshape(nx, ny).T  # along y
    out = _gaussian_axis(out.reshape(-1, nx), pole).reshape(ny, nx)        # along x
    return out


# --------------------------------------------- analysis (StructureTensor.java)

def analysis(image, sigma=1.0, epsilon=EPSILON):
    """OrientationJ Analysis with the cubic-spline gradient.

    sigma is the standard deviation of the Gaussian window of the structure
    tensor (the 'local window sigma' of the dialog; macro default 1).

    Returns a dict of float32 maps shaped like the input image:
      gx, gy, energy, orientation (radians), coherency
    """
    gx, gy = gradient_spline(image)

    dx = gx.astype(np.float64)
    dy = gy.astype(np.float64)
    dxx = dx * dx
    dxy = dx * dy
    dyy = dy * dy
    if sigma > 0:
        dxx = gaussian_smooth(dxx, sigma)
        dxy = gaussian_smooth(dxy, sigma)
        dyy = gaussian_smooth(dyy, sigma)

    return {'gx': gx, 'gy': gy,
            'energy': (dxx + dyy).astype(np.float32),
            'orientation': (0.5 * np.arctan2(2.0 * dxy, dyy - dxx)).astype(np.float32),
            'coherency': (np.sqrt((dyy - dxx) ** 2 + 4.0 * dxy * dxy)
                          / (dxx + dyy + epsilon)).astype(np.float32)}


# --------------------------------------- color survey (ColorMapping.colorHSB)

def _hsb_to_rgb(hue, sat, bri):
    """Vectorized java.awt.Color.HSBtoRGB (float32 in, uint8 out)."""
    hue = np.clip(hue, 0.0, 1.0).astype(np.float32)
    sat = np.clip(sat, 0.0, 1.0).astype(np.float32)
    bri = np.clip(bri, 0.0, 1.0).astype(np.float32)

    h6 = (hue - np.floor(hue)) * 6.0
    sector = np.floor(h6).astype(np.int32) % 6
    f = h6 - np.floor(h6)
    p = bri * (1.0 - sat)
    q = bri * (1.0 - sat * f)
    t = bri * (1.0 - sat * (1.0 - f))

    r = np.choose(sector, [bri, q, p, p, t, bri])
    g = np.choose(sector, [t, bri, bri, q, p, p])
    b = np.choose(sector, [p, p, t, bri, bri, q])
    rgb = np.stack([r, g, b], axis=-1)
    rgb = np.where(sat[..., np.newaxis] == 0, bri[..., np.newaxis], rgb)
    return (rgb * 255.0 + 0.5).astype(np.uint8)


def survey(image, features):
    """The default color survey of OrientationJ Analysis:
    hue = orientation, saturation = coherency, brightness = original image.
    Returns uint8 RGB, (ny, nx, 3)."""
    source = np.asarray(image)
    hue = (features['orientation'] + np.pi / 2.0) / np.pi
    lo, hi = source.min(), source.max()
    bri = (source - lo) / (hi - lo) if hi > lo else np.zeros_like(source)
    return _hsb_to_rgb(hue.astype(np.float32), features['coherency'],
                       bri.astype(np.float32))


# ------------------------------- distribution (OrientationResults.distribution)

def distribution(features, min_coherency=0.0, min_energy=0.0, mask=None):
    """OrientationJ Distribution: weighted-by-nothing pixel counts in 180 bins
    of 1 degree. min_coherency and min_energy are the dialog percentages (0-100);
    a pixel is selected when coherency >= min_coherency/100 and
    energy/max(energy) >= min_energy/100.

    mask restricts the count to a region of interest (nonzero = pixel counted).

    Returns (histo, angles, selected):
      histo    (180,) float counts,
      angles   the 180 bin centers, -89.5 ... +89.5 degrees,
      selected boolean map of the counted pixels.
    """
    coherency = features['coherency']
    energy = features['energy']

    energy_max = max(1e-4, float(energy.max()))
    selected = (coherency >= min_coherency / 100.0) \
        & (energy / energy_max >= min_energy / 100.0)
    if mask is not None:
        selected = selected & (np.asarray(mask) != 0)

    degrees = 90.0 + features['orientation'].astype(np.float64) * (180.0 / np.pi)
    bins = np.clip(np.floor(degrees).astype(np.int64), 0, 179)
    histo = np.bincount(bins[selected], minlength=180).astype(np.float64)

    angles = np.arange(180) - 89.5
    return histo, angles, selected


def distribution_table(features, min_coherency=0.0, min_energy=0.0, mask=None):
    """The distribution as the OJ-Distribution results table: a DataFrame with
    the columns 'Orientation' (bin centers) and 'Slice1' (counts; the column
    name is kept for parity with the plugin's table)."""
    histo, angles, _ = distribution(features, min_coherency, min_energy, mask)
    return pd.DataFrame({'Orientation': angles, 'Slice1': histo})


# --------------------------- vector field (OrientationResults.displayVectorField)

def vector_field(features, grid=10, mask=None, min_mask_fraction=0.5):
    """OrientationJ Vector Field: block averages of the orientation vector
    (cos theta, sin theta), the coherency and the energy on a grid of
    grid x grid pixels. Out-of-image samples of partial border blocks count as
    orientation 0, coherency 0, energy 0, exactly like the plugin.

    mask restricts the field to a region of interest (nonzero = inside);
    a cell is kept when more than min_mask_fraction of its grid x grid area
    is masked.

    Returns the OJ-Vector-Field results table as a DataFrame with columns
    X, Y, Slice (always 0, kept for parity with the plugin's table), DX, DY,
    Orientation (degrees), Coherency, Energy.
    """
    orientation = features['orientation'].astype(np.float64)
    coherency = features['coherency'].astype(np.float64)
    energy = features['energy'].astype(np.float64)
    ny, nx = orientation.shape

    energy_max = float(energy.max())
    if energy_max <= 0:
        return pd.DataFrame(columns=['X', 'Y', 'Slice', 'DX', 'DY',
                                     'Orientation', 'Coherency', 'Energy'])

    xstart = (nx - (nx // grid) * grid) // 2
    ystart = (ny - (ny // grid) * grid) // 2
    xblocks = len(range(xstart, nx, grid))
    yblocks = len(range(ystart, ny, grid))

    # pad to whole blocks; outside pixels give cos(0) = 1, sin(0) = 0, coh = ene = 0
    pad_y = ystart + yblocks * grid - ny
    pad_x = xstart + xblocks * grid - nx

    def block_mean(a, constant=0.0):
        padded = np.pad(a, ((0, pad_y), (0, pad_x)), constant_values=constant)
        blocks = padded[ystart:, xstart:].reshape(yblocks, grid, xblocks, grid)
        return blocks.mean(axis=(1, 3))

    dx = block_mean(np.cos(orientation), constant=1.0)
    dy = block_mean(np.sin(orientation))
    coh = block_mean(coherency)
    ene = block_mean(energy)

    angle = np.degrees(np.arctan2(dy, dx))
    angle = np.where(angle < -90.0, angle + 180.0, angle)
    angle = np.where(angle > 90.0, angle - 180.0, angle)

    ys, xs = np.mgrid[0:yblocks, 0:xblocks]
    keep = (ene > 0) & (coh > 0)
    if mask is not None:
        mask_fraction = block_mean((np.asarray(mask) != 0).astype(np.float64))
        keep = keep & (mask_fraction > min_mask_fraction)
    return pd.DataFrame({
        'X': (xstart + xs * grid + grid // 2)[keep],
        'Y': (ystart + ys * grid + grid // 2)[keep],
        'Slice': 0,
        'DX': -dx[keep],
        'DY': dy[keep],
        'Orientation': angle[keep],
        'Coherency': coh[keep],
        'Energy': ene[keep] / energy_max,
    })


def vector_segments(table, grid=10, scale=100.0, vector_type=0):
    """The overlay segments of the vector field, one line per grid cell.

    vector_type sets the length like the dialog: 0 constant, 1 ~ energy,
    2 ~ coherency, 3 ~ energy * coherency.
    Returns (x1, y1, x2, y2) arrays in pixel coordinates.
    """
    radius = scale / 100.0 * grid * 0.5
    length = np.full(len(table), radius)
    if vector_type == 1:
        length = radius * table['Energy'].to_numpy()
    elif vector_type == 2:
        length = radius * table['Coherency'].to_numpy()
    elif vector_type == 3:
        length = radius * (table['Energy'] * table['Coherency']).to_numpy()

    # the table holds DX = -dx of the drawing code
    dx = -table['DX'].to_numpy()
    dy = table['DY'].to_numpy()
    x1 = np.round(table['X'] + length * dx).astype(int)
    y1 = np.round(table['Y'] - length * dy).astype(int)
    x2 = np.round(table['X'] - length * dx).astype(int)
    y2 = np.round(table['Y'] + length * dy).astype(int)
    return x1, y1, x2, y2
