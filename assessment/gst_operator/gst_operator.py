"""The gradient structure tensor in its shortest honest form.

One function, three features, no transform: the derivatives are Gaussian
derivatives applied as separable 1D convolutions in the space domain, and the
tensor window is a Gaussian blur, also separable and in space.  Nothing here
needs an FFT, a spline prefilter or a boundary trick beyond mirroring, which
makes the whole operator readable in one screen and portable to any language.

    C, E, theta = features(image, sigma=2.0, sigma_gradient=1.0)

Angle convention: theta is in radians in [-pi/2, pi/2], counter-clockwise from
the horizontal axis of the displayed image, along the structures (the direction
in which the intensity varies the least) -- the OrientationJ convention.
"""
import numpy as np
from scipy.ndimage import convolve1d

__all__ = ['gaussian_kernels', 'gradient', 'structure_tensor', 'features']


def gaussian_kernels(sigma, truncate=4.0):
    """A sampled Gaussian and its first derivative, both normalized.

    The smoothing kernel sums to one; the derivative kernel is normalized so
    that it differentiates exactly, i.e. sum(x * d(x)) = -1 with x the sample
    positions, which removes the systematic gain error of a naive sampling.
    """
    radius = max(1, int(truncate * sigma + 0.5))
    x = np.arange(-radius, radius + 1, dtype=float)
    g = np.exp(-0.5 * (x / sigma) ** 2)
    g /= g.sum()
    d = -x / sigma ** 2 * g
    d /= -np.sum(x * d)
    return g, d


def gradient(image, sigma_gradient=1.0):
    """Gaussian-derivative gradient (gx, gy), separable, in the space domain.

    gx = (d/dx G) * image is computed as a 1D derivative along the columns
    followed by a 1D smoothing along the rows, and symmetrically for gy.
    Boundaries are mirrored, as everywhere in ImageJ.
    """
    image = np.asarray(image, dtype=float)
    g, d = gaussian_kernels(sigma_gradient)
    gx = convolve1d(convolve1d(image, d, axis=1, mode='mirror'),
                    g, axis=0, mode='mirror')
    gy = convolve1d(convolve1d(image, d, axis=0, mode='mirror'),
                    g, axis=1, mode='mirror')
    return gx, gy


def structure_tensor(image, sigma=2.0, sigma_gradient=1.0):
    """The three components (Jxx, Jxy, Jyy) of the gradient structure tensor.

    The products of the gradient are averaged with a Gaussian window of width
    sigma -- the "local window" of the OrientationJ dialogs.
    """
    gx, gy = gradient(image, sigma_gradient)
    g, _ = gaussian_kernels(sigma)

    def window(a):
        return convolve1d(convolve1d(a, g, axis=1, mode='mirror'),
                          g, axis=0, mode='mirror')

    return window(gx * gx), window(gx * gy), window(gy * gy)


def features(image, sigma=2.0, sigma_gradient=1.0, epsilon=1e-12):
    """Coherency, energy and orientation of the local structures.

    Returns (C, E, theta):
      C      (lambda1 - lambda2) / (lambda1 + lambda2), in [0, 1];
             1 where a single orientation dominates, 0 where isotropic
      E      lambda1 + lambda2 = trace of the tensor, the gradient energy
      theta  orientation of the structures, radians in [-pi/2, pi/2]
    """
    jxx, jxy, jyy = structure_tensor(image, sigma, sigma_gradient)
    trace = jxx + jyy
    split = np.hypot(jxx - jyy, 2.0 * jxy)      # lambda1 - lambda2
    return split / (trace + epsilon), trace, 0.5 * np.arctan2(2.0 * jxy, jyy - jxx)
