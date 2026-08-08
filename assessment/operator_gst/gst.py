"""GST -- Gaussian-gradient structure tensor, the forward model.

Analytic Fourier implementation (periodic boundaries): the gradient is a
Gaussian derivative of width sigma_gradient, the tensor window is a Gaussian
of width sigma.  Every operator is diagonal in Fourier, hence exactly known
to the inverse problem (see inverse_gst.py).
"""
import numpy as np
import tifffile


def load_image(path):
    """Read a 2D TIFF as float64 normalized to [0, 1]."""
    image = tifffile.imread(path).astype(np.float64)
    if image.ndim != 2:
        raise ValueError(f'{path}: expected a 2D image, got shape {image.shape}')
    return (image - image.min()) / (image.max() - image.min())


def transfers(shape, sigma_gradient, sigma):
    """Fourier transfer functions (dx, dy, window) for one image shape."""
    ny, nx = shape
    wy = 2.0 * np.pi * np.fft.fftfreq(ny)[:, None]
    wx = 2.0 * np.pi * np.fft.fftfreq(nx)[None, :]
    gauss = np.exp(-0.5 * sigma_gradient ** 2 * (wx ** 2 + wy ** 2))
    window = np.exp(-0.5 * sigma ** 2 * (wx ** 2 + wy ** 2))
    return 1j * wx * gauss, 1j * wy * gauss, window


def apply(array, transfer):
    """Apply a Fourier transfer function to a real 2D array."""
    return np.fft.ifft2(np.fft.fft2(array) * transfer).real


class GST:
    """Forward model: image -> gradients, structure tensor, eigenvalues.

    >>> gst = GST(image, sigma=1.0)
    >>> gradientX, gradientY, Jxx, Jxy, Jyy, lambda1, lambda2 = gst.run()
    >>> C, E, theta = gst.getFeatures()
    """

    def __init__(self, image, sigma=1.0, sigma_gradient=1.0, epsilon=1e-9):
        self.image = np.asarray(image, dtype=np.float64)
        self.sigma = sigma
        self.sigma_gradient = sigma_gradient
        self.epsilon = epsilon
        self._computed = False

    def run(self, sigma=None):
        """Compute the forward chain (optionally overriding the window sigma).

        Returns (gradientX, gradientY, Jxx, Jxy, Jyy, lambda1, lambda2); the
        same arrays remain available as attributes afterwards."""
        if sigma is not None:
            self.sigma = sigma
        dx, dy, window = transfers(self.image.shape,
                                   self.sigma_gradient, self.sigma)
        self.gradientX = apply(self.image, dx)
        self.gradientY = apply(self.image, dy)
        self.Jxx = apply(self.gradientX * self.gradientX, window)
        self.Jxy = apply(self.gradientX * self.gradientY, window)
        self.Jyy = apply(self.gradientY * self.gradientY, window)
        trace = self.Jxx + self.Jyy
        split = np.sqrt((self.Jxx - self.Jyy) ** 2 + 4.0 * self.Jxy ** 2)
        self.lambda1 = 0.5 * (trace + split)
        self.lambda2 = 0.5 * (trace - split)
        self._computed = True
        return (self.gradientX, self.gradientY, self.Jxx, self.Jxy, self.Jyy,
                self.lambda1, self.lambda2)

    def getFeatures(self):
        """Return the OrientationJ features (C, E, theta) of the last run.

        C     coherency (lambda1 - lambda2) / (lambda1 + lambda2), in [0, 1]
        E     energy, trace of the tensor, in [0, inf)
        theta orientation of the structures in radians, in [-pi/2, pi/2]
        """
        if not self._computed:
            self.run()
        E = self.Jxx + self.Jyy
        C = (self.lambda1 - self.lambda2) / (E + self.epsilon)
        theta = 0.5 * np.arctan2(2.0 * self.Jxy, self.Jyy - self.Jxx)
        return C, E, theta
