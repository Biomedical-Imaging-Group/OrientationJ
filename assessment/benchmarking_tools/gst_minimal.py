"""Minimal gradient-structure-tensor operator, in ~15 lines.

Gaussian-derivative gradient and Gaussian tensor window, both applied
analytically in Fourier (periodic boundaries).  This is the whole GST:
everything else in the compared tools is gradient flavor and bookkeeping.
"""
import numpy as np


def orientation(image, sigma=1.0, sigma_gradient=1.0):
    """Orientation map of the structures in degrees, in [-90, 90],
    counter-clockwise from the horizontal (OrientationJ convention)."""
    ny, nx = image.shape
    wy = 2.0 * np.pi * np.fft.fftfreq(ny)[:, None]
    wx = 2.0 * np.pi * np.fft.fftfreq(nx)[None, :]
    blur = lambda a, t: np.fft.ifft2(np.fft.fft2(a) * t).real
    gauss = np.exp(-0.5 * sigma_gradient ** 2 * (wx ** 2 + wy ** 2))
    window = np.exp(-0.5 * sigma ** 2 * (wx ** 2 + wy ** 2))
    gx = blur(image, 1j * wx * gauss)
    gy = blur(image, 1j * wy * gauss)
    jxx, jxy, jyy = (blur(gx * gx, window), blur(gx * gy, window),
                     blur(gy * gy, window))
    return np.degrees(0.5 * np.arctan2(2.0 * jxy, jyy - jxx))
