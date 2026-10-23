"""
synthetic_images_2D.py

Synthetic test-image generators for OrientationJ.

All generators return a square 2D image, 32-bit float, values in [0, 1],
of default size N. The notebook synthetic_images_2D.ipynb selects the
generators, their sizes, and writes the images in this folder as
synthetic_<name>_<size>.tif.

Daniel Sage, Biomedical Imaging Group, EPFL.
"""

import os

import numpy as np
from skimage.filters import gaussian
from skimage.io import imread, imsave

N = 512


# ----------------------------------------------------------------- helpers

def normalize(image):
    """Return the image as 32-bit float rescaled to [0, 1]."""
    image = image.astype(np.float32)
    low = image.min()
    high = image.max()
    if high <= low:
        return np.zeros(image.shape, dtype=np.float32)
    return (image - low) / (high - low)


def save(image, name, folder=None, prefix='synthetic'):
    """Save a 32-bit TIFF as <folder>/<prefix>_<name>_<N>.tif and return the path.

    The default folder is ../images relative to this module.
    """
    if folder is None:
        folder = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'images')
    path = os.path.join(folder, prefix + '_' + name + '_' + str(image.shape[0]) + '.tif')
    imsave(path, image.astype(np.float32), check_contrast=False)
    return path


def load(path):
    """Read a 2D image file as 32-bit float grayscale rescaled to [0, 1].

    A color image is averaged over its channels.
    """
    image = imread(path)
    if image.ndim == 3:
        image = image[:, :, :3].mean(axis=2)
    return normalize(image)


def polar(size):
    """Return the radius and the polar angle measured from the center of the image."""
    y, x = np.mgrid[0:size, 0:size]
    dx = x - size / 2
    dy = y - size / 2
    return np.hypot(dx, dy), np.arctan2(dy, dx)


# -------------------------------------------------------------- generators

def chirp(size=N, fmin=0.02, fmax=0.16):
    """Radial chirp. Port of orientation.TestImage.chirp (OrientationJ test image)."""
    radius, angle = polar(size)
    apodisation = 1 / (1 + np.exp((radius - size * 0.45) / 2))
    frequency = fmin + radius * (fmax - fmin) / size
    return normalize(1 + np.sin(2 * np.pi * frequency * radius) * apodisation)


def wave(size=N, angles=(5.0, 45.0, 85.0), periods=(2.0, 8.0, 64.0),
         noise=0.5, diameter=480.0, taper=0.25, seed=0):
    """Three overlapping sinusoidal waves of equal amplitude, plus uniform noise, in a round window.

    `angles` are the orientations of the fringes in the displayed image, in degrees
    counter-clockwise from the horizontal (the OrientationJ convention), one per period:
    5 degrees at 2 px (the Nyquist limit), 45 degrees at 8 px, 85 degrees at 64 px.
    Uniform noise in [-noise, noise] is added on top (one wave has amplitude 1), then
    the whole is multiplied by a circular Tukey window of the given diameter: flat
    inside, cosine taper over the outer fraction `taper` of the radius, zero outside.
    """
    y, x = np.mgrid[0:size, 0:size].astype(float)
    image = np.zeros((size, size))
    for angle, period in zip(angles, periods):
        radian = np.deg2rad(angle)
        projection = (x - size / 2) * np.sin(radian) + (y - size / 2) * np.cos(radian)
        image = image + np.sin(2 * np.pi * projection / period)
    image = image + np.random.default_rng(seed).uniform(-noise, noise, image.shape)

    radius = diameter / 2
    r, _ = polar(size)
    flat = (1 - taper) * radius
    window = np.where(r <= flat, 1.0,
                      np.where(r >= radius, 0.0,
                               0.5 * (1 + np.cos(np.pi * (r - flat) / (radius - flat)))))
    return normalize(image * window)


def spiral(size=N, turns=12):
    """Archimedean spiral, the analytic form of the pynamix spiral used by OrientationPy."""
    radius, angle = polar(size)
    return normalize(np.sin(2 * np.pi * turns * radius / (size / 2) - angle))


def rings_dither(size=N, radii=(0.26, 0.34, 0.40), width=3.0, dither=1e-4, seed=0):
    """A few full bright rings around the center, with a Gaussian radial profile.

    Purely tangential orientation. The radii are given as a fraction of the size.
    A tiny Gaussian noise (dither, invisible at 1e-4 of the intensity range)
    breaks the exact mirror symmetry of the pattern about the center; without it,
    the pixels on the symmetry axes carry a structure tensor with exactly
    Jxy = 0 or Jxx = Jyy, and the orientation distribution shows artificial
    spikes at exactly 0, +/-45 and +/-90 degrees.
    """
    radius, angle = polar(size)
    image = np.zeros((size, size))
    for fraction in radii:
        image = image + np.exp(-0.5 * ((radius - fraction * size) / width) ** 2)
    image = image + np.random.default_rng(seed).normal(0, dither, image.shape)
    return normalize(image)


def smoothstep(t):
    t = np.clip(t, 0.0, 1.0)
    return t * t * (3 - 2 * t)


def rings_window(size=384, inner=70.0, outer=175.0, fade=24.0):
    """Radial window of rings_degraded: 1 on the annulus [inner, outer] px, with a
    smooth fade of `fade` px on both sides, 0 elsewhere."""
    y, x = np.mgrid[0:size, 0:size]
    radius = np.hypot(x - (size - 1) / 2, y - (size - 1) / 2)
    return smoothstep((radius - inner) / fade) * smoothstep((outer - radius) / fade)


def rings_mask(size=384, **window):
    """Evaluation mask of rings_degraded: the annulus where the window is full."""
    return rings_window(size, **window) > 0.999


def rings_degraded(size=384, saturation=0.0, noise=0.0, ramp=0.0, period=20.0, amplitude=0.5,
                   seed=1234, **window):
    """Saturated sine rings on a vertical ramp, plus Gaussian noise: the test image of
    assessment/structure_nematic, A clip(gamma w(r) sin(2 pi r / T), -1, 1) - beta (y - c),
    then Gaussian noise of standard deviation sigma.

    The three degradations: `saturation` is the ratio rho of saturated (clipped) pixels,
    in [0, 1) (the gain is gamma = 1 / sin(pi (1 - rho) / 2)); `noise` is the standard
    deviation sigma; `ramp` is the slope beta of the vertical ramp. The reference
    orientation is tangential; the evaluation mask is rings_mask.
    """
    y, x = np.mgrid[0:size, 0:size]
    center = (size - 1) / 2
    radius = np.hypot(x - center, y - center)
    gain = 1.0 / np.sin(0.5 * np.pi * (1.0 - saturation))
    rings = amplitude * np.clip(gain * rings_window(size, **window) * np.sin(2 * np.pi * radius / period), -1, 1)
    image = rings - ramp * (y - center) + noise * np.random.default_rng(seed).normal(size=(size, size))
    return normalize(image)


def noise(size=N, sigma=1.0, seed=0):
    """Isotropic band-limited noise. No preferred orientation, low coherency."""
    generator = np.random.default_rng(seed)
    return normalize(gaussian(generator.normal(size=(size, size)), sigma))


def filaments(size=N, count=100, length=300, curvature=0.08, width=1.5, seed=0):
    """Network of bright filaments, each one a persistent random walk wrapped at the borders.

    Mimics a fluorescence image of a cytoskeleton or collagen network.
    """
    generator = np.random.default_rng(seed)
    image = np.zeros((size, size))
    for index in range(count):
        x = generator.uniform(0, size)
        y = generator.uniform(0, size)
        angle = generator.uniform(0, 2 * np.pi)
        for step in range(length):
            angle = angle + generator.normal(0, curvature)
            x = x + np.cos(angle)
            y = y + np.sin(angle)
            image[int(round(y)) % size, int(round(x)) % size] = 1.0
    return normalize(gaussian(image, width))


def nematic(size=N, defects=8, length=30, sigma=1.0, seed=0):
    """Active-nematic-like flow texture built from +/-1/2 topological defects.

    Reference: Doostmohammadi, Ignes-Mullol, Yeomans & Sagues,
    "Active nematics", Nature Communications 9:3246, 2018.
    The director field is rendered by line integral convolution of white noise.
    """
    generator = np.random.default_rng(seed)
    y, x = np.mgrid[0:size, 0:size].astype(float)
    centers_x = generator.uniform(0.1 * size, 0.9 * size, defects)
    centers_y = generator.uniform(0.1 * size, 0.9 * size, defects)

    theta = np.full((size, size), generator.uniform(0, np.pi))
    for index in range(defects):
        charge = 0.5 if index % 2 == 0 else -0.5
        theta = theta + charge * np.arctan2(y - centers_y[index], x - centers_x[index])

    return normalize(line_integral_convolution(theta, length, sigma, seed))


def line_integral_convolution(theta, length=20, sigma=1.0, seed=0):
    """Smear white noise along the director field theta. Renders any prescribed orientation field."""
    size = theta.shape[0]
    generator = np.random.default_rng(seed)
    y, x = np.mgrid[0:size, 0:size].astype(float)
    field_x = np.cos(theta)
    field_y = np.sin(theta)
    white = generator.normal(size=(size, size))

    total = white.copy()
    count = np.ones((size, size))
    for direction in (1, -1):
        position_x = x.copy()
        position_y = y.copy()
        step_x = direction * field_x
        step_y = direction * field_y
        for step in range(length):
            row = np.clip(position_y.astype(int), 0, size - 1)
            column = np.clip(position_x.astype(int), 0, size - 1)
            local_x = field_x[row, column]
            local_y = field_y[row, column]
            # the field is a director: flip it to keep following the same way
            flip = np.sign(local_x * step_x + local_y * step_y + 1e-12)
            step_x = flip * local_x
            step_y = flip * local_y
            position_x = position_x + step_x
            position_y = position_y + step_y
            row = np.clip(position_y.astype(int), 0, size - 1)
            column = np.clip(position_x.astype(int), 0, size - 1)
            total = total + white[row, column]
            count = count + 1
    return gaussian(total / count, sigma)


def tube(image, x0, y0, x1, y1, radius, value):
    """Composite by maximum a lit-cylinder segment of the given radius into the image.

    The profile across the axis is sqrt(1 - (d / radius)^2), the shading of a cylinder
    under frontal light; `value` is the brightness on the axis.
    """
    size = image.shape[0]
    pad = int(np.ceil(radius)) + 1
    xa = int(max(0, min(x0, x1) - pad))
    xb = int(min(size, max(x0, x1) + pad + 1))
    ya = int(max(0, min(y0, y1) - pad))
    yb = int(min(size, max(y0, y1) + pad + 1))
    if xa >= xb or ya >= yb:
        return
    y, x = np.mgrid[ya:yb, xa:xb].astype(np.float32)
    dx = x1 - x0
    dy = y1 - y0
    t = np.clip(((x - x0) * dx + (y - y0) * dy) / (dx * dx + dy * dy), 0, 1)
    distance = np.hypot(x - (x0 + t * dx), y - (y0 + t * dy))
    profile = np.sqrt(np.clip(1 - (distance / radius) ** 2, 0, 1))
    np.maximum(image[ya:yb, xa:xb], value * profile, out=image[ya:yb, xa:xb])


def bundles(size=N, count=4000, length=(45.0, 0.5), curvature=0.2, step=3.0,
            width=(0.1, 3.0), branch=2, patches=3, radius=(25.0, 60.0), pull=0.5,
            density=0.0, sigma=0.5, noise=0.02, seed=0):
    """Matted isotropic web of wavy fibers, rendered as lit tubes, with a few random bundles.

    Each fiber is a persistent random walk, as in `filaments`: the heading turns by
    N(0, curvature) radians at every step of `step` pixels. Its length is lognormal
    (`length` = median, log-sigma), its radius uniform in `width`, and it tapers at both
    ends. A fraction `branch` of the fibers start on an existing fiber (a random pixel
    that already carries one), which knits the web together with junctions. The
    brightness is skewed toward dim values, so most fibers form a gray haze and a few
    bright ones lie on top. Fibers are composited by maximum, then lightly blurred, with
    a touch of Gaussian noise.

    The bundles are `patches` elliptical Gaussian fields of random center, size (`radius`,
    uniform) and direction, elongated 3:1 along that direction. Inside a field the heading
    of the walk is pulled toward the bundle direction with strength `pull`; with a
    `density` > 0, fibers are also seeded up to (1 + density) times more often there
    (off by default: together with the branching, it empties the mesh around).
    """
    generator = np.random.default_rng(seed)
    y, x = np.mgrid[0:size, 0:size]
    centers = generator.uniform(0, size, (patches, 2))
    radii = generator.uniform(radius[0], radius[1], patches)
    directions = generator.uniform(0, np.pi, patches)
    fields = []
    for (cy, cx), r, phi in zip(centers, radii, directions):
        u = (x - cx) * np.cos(phi) + (y - cy) * np.sin(phi)
        v = -(x - cx) * np.sin(phi) + (y - cy) * np.cos(phi)
        fields.append(np.exp(-0.5 * ((u / (3 * r)) ** 2 + (v / r) ** 2)))
    inside = np.clip(sum(fields), 0, 1)

    image = np.zeros((size, size), dtype=np.float32)
    fibers = 0
    while fibers < count:
        px, py = generator.uniform(0, size, 2)
        if generator.uniform() < branch:
            # branch: a random pixel of the image that already carries a fiber
            for attempt in range(20):
                cx, cy = generator.uniform(0, size, 2)
                if image[int(cy), int(cx)] > 0.1:
                    px, py = cx, cy
                    break
        if generator.uniform() > (1 + density * inside[int(py), int(px)]) / (1 + density):
            continue
        fibers = fibers + 1
        angle = generator.uniform(0, 2 * np.pi)
        steps = max(2, int(length[0] * np.exp(generator.normal(0, length[1])) / step))
        thickness = generator.uniform(width[0], width[1])
        brightness = 0.12 + 0.88 * generator.beta(1.2, 4.0)
        for j in range(steps):
            row = int(np.clip(py, 0, size - 1))
            column = int(np.clip(px, 0, size - 1))
            turn = generator.normal(0, curvature)
            for field, phi in zip(fields, directions):
                # the bundle direction is an axis: the nearest of phi and phi + pi
                delta = (phi - angle + np.pi / 2) % np.pi - np.pi / 2
                turn = turn + pull * field[row, column] * delta
            angle = angle + turn
            next_x = px + step * np.cos(angle)
            next_y = py + step * np.sin(angle)
            taper = min(1.0, (min(j, steps - 1 - j) + 1) / (0.15 * steps + 1))
            tube(image, px, py, next_x, next_y, thickness, brightness * taper)
            px, py = next_x, next_y
    image = gaussian(image, sigma) + generator.normal(0, noise, image.shape)
    return normalize(image)
