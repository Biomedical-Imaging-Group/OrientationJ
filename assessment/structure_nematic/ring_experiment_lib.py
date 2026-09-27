"""Saturated-ring test image, local orientation estimators (naive mean, nematic tensor, structure tensor), analysis,
and low-level drawing helpers. The degradation levels, figure layouts, font sizes and spacing are defined in the notebooks.

Shared by create_abstract_figure.ipynb and structure_nematic_experiment.ipynb.
"""
import numpy as np
import matplotlib.pyplot as plt
from matplotlib.colors import hsv_to_rgb, Normalize
from matplotlib.patches import Circle
from pathlib import Path
from scipy.ndimage import gaussian_filter
import tifffile

# ---------- test image and window (the degradation levels are user parameters of the notebooks) ----------
DEFAULT_SIGMA = 7.0     # Gaussian window sigma_agg [px]
A, T = 0.5, 20.0        # ring amplitude and period [px]

# ---------- estimators, colours, evaluation ----------
ESTIMATOR_NAMES = ('naive', 'nematic', 'structure')
SWEEP_ESTIMATORS = ('nematic', 'structure')     # estimators compared in the sweep figures
COLORS = {'naive': '#2ca02c', 'nematic': '#1414f8', 'structure': '#f43d3d', 'reference': '#000000'}
LINEWIDTHS = {'naive': 2.0, 'nematic': 2.0, 'structure': 2.0, 'reference': 3}
MASK_COLOR = '#ff00ff'      # outline of the evaluation mask on the images
ANGLE_BIN = 1.0             # bin width of the orientation histograms [deg]
ERROR_LIM = 30.0            # colour range of the error maps [deg]
EPS = 1e-15
RESULTS = Path('results')   # figures
IMAGES = Path('images')     # test images, 32-bit TIFF
RESULTS.mkdir(exist_ok=True)
IMAGES.mkdir(exist_ok=True)


# ============================================================
# Estimators
# ============================================================
def image_gradient(image):
    gy, gx = np.gradient(np.asarray(image, dtype=float))
    return gx, gy


def tensor_orientation(xx, xy, yy):
    """Orientation (perpendicular to the dominant gradient) and normalized anisotropy of a 2 x 2 tensor field."""
    angle = (np.degrees(0.5 * np.arctan2(2 * xy, xx - yy)) + 90.0) % 180.0
    confidence = np.sqrt((xx - yy) ** 2 + 4 * xy * xy) / (xx + yy + EPS)
    return angle, confidence


def estimate_naive(gx, gy, sigma_agg):
    """Arithmetic mean of the pixel orientation in [0, 180). Wrong around the wrap-around 0 = 180. No confidence."""
    theta = (np.degrees(np.arctan2(gy, gx)) + 90.0) % 180.0
    return {'angle': gaussian_filter(theta, sigma_agg) % 180.0, 'confidence': None}


def estimate_nematic(gx, gy, sigma_agg):
    """Nematic tensor Q = <n n^T>: every pixel has weight 1."""
    norm = np.sqrt(gx * gx + gy * gy) + EPS
    nx, ny = gx / norm, gy / norm
    angle, S = tensor_orientation(gaussian_filter(nx * nx, sigma_agg), gaussian_filter(nx * ny, sigma_agg),
                                  gaussian_filter(ny * ny, sigma_agg))
    return {'angle': angle, 'confidence': S}


def estimate_structure(gx, gy, sigma_agg):
    """Structure tensor J = <g g^T>: every pixel has weight |g|^2. Energy E = tr J."""
    jxx, jxy, jyy = (gaussian_filter(gx * gx, sigma_agg), gaussian_filter(gx * gy, sigma_agg),
                     gaussian_filter(gy * gy, sigma_agg))
    angle, C = tensor_orientation(jxx, jxy, jyy)
    return {'angle': angle, 'confidence': C, 'energy': jxx + jyy}


ESTIMATORS = {'naive': estimate_naive, 'nematic': estimate_nematic, 'structure': estimate_structure}


def run_estimators(image, sigma_agg=DEFAULT_SIGMA):
    gx, gy = image_gradient(image)
    return {name: estimator(gx, gy, sigma_agg) for name, estimator in ESTIMATORS.items()}


# ============================================================
# Test image
# ============================================================
def snr_db_from_noise(noise_std, amplitude=A):
    """SNR = (A^2 / 2) / sigma_noise^2 in dB; inf for sigma_noise = 0."""
    return np.inf if noise_std == 0 else 10 * np.log10(amplitude ** 2 / 2 / noise_std ** 2)


def smoothstep(t):
    t = np.clip(t, 0.0, 1.0)
    return t * t * (3 - 2 * t)


def saturation_gain_from_ratio(ratio):
    """Gain gamma_sat such that a ratio rho_sat of the sine is clipped: rho_sat = 1 - (2 / pi) asin(1 / gamma_sat)."""
    assert 0.0 <= ratio < 1.0, 'the ratio of saturated pixels must be in [0, 1)'
    return 1.0 / np.sin(0.5 * np.pi * (1.0 - ratio))


def make_ring_image(saturation_ratio, noise_std, ramp_slope, shot_lambda=0.0, speckle_ratio=0.0, size=384, period=T,
                    inner_radius=70.0, outer_radius=175.0, fade=24.0, seed=1234):
    """Saturated rings A clip(gamma_sat w(r) sin(2 pi r / T), -1, 1), plus a vertical ramp beta_ramp (c_y - y)
    of orientation 0 deg. Shot noise: each pixel counts k ~ Poisson((f - f_min) / lambda_shot) photons of intensity
    lambda_shot, so its variance is lambda_shot (f - f_min); lambda_shot = 0 gives no shot noise. Then Gaussian noise
    of standard deviation sigma_noise. Speckle (salt and pepper): a random ratio rho_sp of the pixels is replaced by the
    maximum (salt) or the minimum (pepper) of the clean image, in equal proportion. The noise patterns are the same for
    every call; only their strengths change (the speckle pixels of a lower ratio are a subset of those of a higher one)."""
    y, x = np.mgrid[:size, :size]
    c = (size - 1) / 2.0
    xc, yc = x - c, y - c
    r = np.hypot(xc, yc)
    window = smoothstep((r - inner_radius) / fade) * smoothstep((outer_radius - r) / fade)
    rings = A * np.clip(saturation_gain_from_ratio(saturation_ratio) * window * np.sin(2 * np.pi * r / period), -1.0, 1.0)
    ramp = -ramp_slope * yc
    clean = rings + ramp
    image = clean
    if shot_lambda > 0:
        offset = clean.min()
        image = shot_lambda * np.random.default_rng([seed, 2]).poisson((clean - offset) / shot_lambda) + offset
    image = image + noise_std * np.random.default_rng(seed).normal(size=(size, size))
    if speckle_ratio > 0:
        rng = np.random.default_rng([seed, 1])
        impulse = rng.random((size, size)) < speckle_ratio
        salt = rng.random((size, size)) < 0.5
        image = np.where(impulse, np.where(salt, clean.max(), clean.min()), image)
    reference = (np.degrees(np.arctan2(yc, xc)) + 90.0) % 180.0     # tangential: orientation of the clean rings
    return {'image': image, 'clean': clean, 'reference': reference, 'mask': window > 0.999, 'center': c,
            'saturation_ratio': saturation_ratio, 'noise_std': noise_std, 'ramp_slope': ramp_slope,
            'shot_lambda': shot_lambda, 'speckle_ratio': speckle_ratio}


def degradation_label(data):
    label = f'ρ_sat = {data["saturation_ratio"]:g}, σ_noise = {data["noise_std"]:g}, β_ramp = {data["ramp_slope"]:g}'
    label += f', λ_shot = {data["shot_lambda"]:g}' if data['shot_lambda'] > 0 else ''
    return label + (f', ρ_sp = {data["speckle_ratio"]:g}' if data['speckle_ratio'] > 0 else '')


def save_image_tiff(image, name):
    """Save an image as 32-bit float TIFF in IMAGES."""
    path = IMAGES / f'{name}.tif'
    tifffile.imwrite(path, np.asarray(image, dtype=np.float32))
    return path


# ============================================================
# Analysis
# ============================================================
def orientation_error(estimate, reference):
    """Axial difference estimate - reference, wrapped to [-90, 90) deg."""
    return (estimate - reference + 90.0) % 180.0 - 90.0


def analyze(data, sigma_agg=DEFAULT_SIGMA):
    """Run the three estimators; keep, inside the evaluation mask, the orientations, errors, confidences S and C,
    energy E, and the mean absolute error (MAE) of each estimator."""
    results = run_estimators(data['image'], sigma_agg)
    m = data['mask']
    angles = {name: results[name]['angle'][m] for name in ESTIMATOR_NAMES}
    error = {name: orientation_error(angles[name], data['reference'][m]) for name in ESTIMATOR_NAMES}
    abs_error = {name: np.abs(error[name]) for name in ESTIMATOR_NAMES}
    return {'results': results, 'reference': data['reference'][m], 'angles': angles, 'sigma_agg': sigma_agg,
            'error': error, 'abs_error': abs_error,
            'mae': {name: abs_error[name].mean() for name in ESTIMATOR_NAMES},
            'S': results['nematic']['confidence'][m], 'C': results['structure']['confidence'][m],
            'E': results['structure']['energy'][m]}


# ============================================================
# Drawing helpers (one panel each; the figure layouts are in the notebooks)
# ============================================================
def pad_layout(fig, w_pad=0.0, h_pad=0.08):
    """Space between the panels of a constrained-layout figure [inches]."""
    fig.get_layout_engine().set(w_pad=w_pad, h_pad=h_pad)


def show_image(ax, img, title, cmap='gray', vmin=None, vmax=None):
    if vmin is None: vmin, vmax = np.nanpercentile(img, [1, 99])
    im = ax.imshow(img, cmap=cmap, vmin=vmin, vmax=vmax)
    ax.set_title(title)
    ax.axis('off')
    return im


def draw_mask_outline(ax, mask, color=MASK_COLOR):
    ax.contour(mask.astype(float), levels=[0.5], colors=[color], linewidths=2.5)


def histogram_line(ax, values, lo=0.0, hi=180.0, width=ANGLE_BIN, **style):
    """Histogram (pixels per bin), drawn as a line through the bin centres."""
    edges = np.arange(lo, hi + width / 2, width)
    counts, _ = np.histogram(values, bins=edges)
    return ax.plot(0.5 * (edges[:-1] + edges[1:]), counts, ls='-', **style)[0]


def hsb_map(orientation, saturation, brightness):
    """RGB image with hue = orientation (0..180 deg on the full hue circle, so 0 = 180), S and B in [0, 1]."""
    hue = np.mod(orientation, 180.0) / 180.0
    hsv = np.stack(np.broadcast_arrays(hue, np.clip(saturation, 0.0, 1.0), np.clip(brightness, 0.0, 1.0)), axis=-1)
    return hsv_to_rgb(hsv)


def orientation_colorbar(fig, ax, label='orientation [deg]', shrink=0.9, inset=None):
    """Hue colorbar for the HSB maps. inset = [x0, y0, width, height] in axes fraction of `ax` places the colorbar
    right against that axis (e.g. [1.03, 0.1, 0.04, 0.8]); otherwise the layout engine places it."""
    mappable = plt.cm.ScalarMappable(cmap='hsv', norm=Normalize(0, 180))
    if inset is not None:
        return fig.colorbar(mappable, cax=ax.inset_axes(inset), label=label)
    return fig.colorbar(mappable, ax=ax, shrink=shrink, label=label)


def plot_reference_map(ax, data, title='reference orientation\n(clean rings, sat. = bright. = 1)', mask_only=False,
                       outside=(1.0, 1.0, 1.0)):
    """Reference orientation as an HSB map with full saturation and brightness, mask outlined.
    mask_only: show it only inside the evaluation mask, with the RGB colour `outside` elsewhere."""
    rgb = hsb_map(data['reference'], 1.0, 1.0)
    if mask_only: rgb[~data['mask']] = outside
    ax.imshow(rgb)
    ax.set_title(title)
    ax.axis('off')
    draw_mask_outline(ax, data['mask'])


HSB_TITLES = {'naive': 'naive\nsat. of HSB = 1 (no confidence)', 'nematic': 'nematic\nsat. of HSB = nematic order S',
              'structure': 'structure\nsat. of HSB = coherency C'}


def plot_hsb_maps(axes, data, analysis, estimators=ESTIMATOR_NAMES, titles=HSB_TITLES):
    """One HSB map per estimator: hue = orientation, saturation = confidence (S for the nematic tensor, C for the
    structure tensor, 1 for the naive mean, which has none), brightness = energy E normalized by its 99th percentile.
    titles: dict estimator name -> panel title."""
    energy = analysis['results']['structure']['energy']
    brightness = energy / np.percentile(energy, 99)
    for ax, name in zip(axes, estimators):
        result = analysis['results'][name]
        saturation = np.ones_like(energy) if result['confidence'] is None else result['confidence']
        ax.imshow(hsb_map(result['angle'], saturation, brightness))
        ax.set_title(titles[name])
        ax.title.set_color(COLORS[name])
        ax.axis('off')
        draw_mask_outline(ax, data['mask'])


def plot_error_maps(axes, data, analysis, estimators=ESTIMATOR_NAMES, whole_image=False, show_mae=True):
    """Absolute error map of each estimator, one per axis, inside the mask or over the whole image (with the mask
    outlined). The MAE in the title is always computed inside the mask. Returns the last image for a colorbar."""
    for ax, name in zip(axes, estimators):
        abs_error = np.abs(orientation_error(analysis['results'][name]['angle'], data['reference']))
        if not whole_image: abs_error = np.where(data['mask'], abs_error, np.nan)
        title = f'|{name} − reference|' + (f'\nMAE = {analysis["mae"][name]:.2f}°' if show_mae else '')
        im = show_image(ax, abs_error, title, cmap='inferno', vmin=0, vmax=ERROR_LIM)
        if whole_image: draw_mask_outline(ax, data['mask'])
        ax.title.set_color(COLORS[name])
    return im


CROP_HALF = 48   # the window-size figure shows a 96 x 96 crop on the annulus


def ring_crop(data, key='image', radius=122):
    """96 x 96 crop of data[key] ('image', 'mask', ...) centred on the annulus."""
    c = int(round(data['center']))
    cx = int(round(data['center'] + radius))
    return data[key][c - CROP_HALF:c + CROP_HALF, cx - CROP_HALF:cx + CROP_HALF]


def window_overlay(sigma_agg=DEFAULT_SIGMA, color='#ffcc00'):
    """Draw the aggregation footprint at the crop centre: circles of radius 1σ (thick) and 2σ (thin)."""
    def draw(ax):
        for k, lw in ((1, 3.5), (2, 1.5)):
            ax.add_patch(Circle((CROP_HALF, CROP_HALF), k * sigma_agg, fill=False, ec=color, lw=lw))
    return draw
