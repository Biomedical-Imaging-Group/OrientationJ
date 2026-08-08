"""
synthetic_images_2D.py

Synthetic test-image generators for OrientationJ.

All generators return a square 2D image, 32-bit float, values in [0, 1]
(default size N, except the entries of SIZES, e.g. chirp at 1024).
Running the module (or calling save_all) writes the images in this folder
as synthetic_<name>_<size>.tif. The faithful Python port of the analysis
lives in ../gst_python/orientationj.py (gradient, structure tensor,
features, survey, distribution, vector field).

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


def save(image, name, folder=None):
    """Save a 32-bit TIFF as <folder>/synthetic_<name>_<N>.tif and return the path.

    The default folder is the one holding this module (orientationj-test-images).
    """
    if folder is None:
        folder = os.path.dirname(os.path.abspath(__file__))
    path = os.path.join(folder, 'synthetic_' + name + '_' + str(image.shape[0]) + '.tif')
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


def wave(size=N, angle1=30.0, period1=8.0, angle2=-60.0, period2=64.0):
    """Two overlapping sinusoidal waves at different orientations and very different periods.

    The fringes of each wave appear at (90 - angle) degrees in the displayed image:
    +60 and -30 degrees with the default settings. The two waves have equal amplitude,
    so both stay clearly visible: fine fringes of period1 crossed by broad bands of
    period2, with a large ratio (8x) between the two periods.
    """
    y, x = np.mgrid[0:size, 0:size]
    radian1 = np.deg2rad(angle1)
    radian2 = np.deg2rad(angle2)
    projection1 = (x - size / 2) * np.cos(radian1) + (y - size / 2) * np.sin(radian1)
    projection2 = (x - size / 2) * np.cos(radian2) + (y - size / 2) * np.sin(radian2)
    return normalize(np.sin(2 * np.pi * projection1 / period1)
                     + np.sin(2 * np.pi * projection2 / period2))


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


GENERATORS = {
    'chirp': chirp,
    'wave': wave,
    'spiral': spiral,
    'rings_dither': rings_dither,
    'noise': noise,
    'filaments': filaments,
    'nematic': nematic,
}

# per-image size; every other image uses the default N
SIZES = {'chirp': 1024}


def save_all(folder=None):
    """Generate every test image and save them as 32-bit TIFF."""
    paths = []
    for name, generator in GENERATORS.items():
        paths.append(save(generator(SIZES.get(name, N)), name, folder))
    return paths


if __name__ == '__main__':
    for path in save_all():
        print('saved', path)
