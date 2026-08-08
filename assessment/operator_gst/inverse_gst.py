"""InverseGST -- naive blind reconstruction of the image from (C, E, theta).

The analytic sequential chain (no iteration, no ground truth anywhere):

  1. eigenvalues   lambda_1,2 = (E +/- C.E) / 2                    (exact)
  2. tensor        spectral recomposition with phi = 90deg - theta (exact)
  3. products      Tikhonov deconvolution of the Gaussian window   (ill-posed)
  4. gradient      half-angle square root of the doubled-angle field
                   (Jxx - Jyy) + 2i.Jxy = (gx + i.gy)^2, branch chosen by
                   2D phase unwrapping                             (sign retrieval)
  5. image         Tikhonov least-squares integration              (ill-posed)

The output is defined up to the two strict invariances of the features: the
mean gray level and the global contrast flip.  It fails wherever the gradient
vanishes along whole curves (oscillating images): the sign flips across those
curves are invisible to the features.  See assessment/reconstruction for the
theory and for iterative (ADMM, variational) refinements.
"""
import numpy as np
from skimage.restoration import unwrap_phase

from gst import transfers, apply


class InverseGST:
    """Blind reconstruction: (C, E, theta) -> image.

    >>> inverse = InverseGST(C, E, theta, sigma=1.0, lambda_reg=1e-5)
    >>> image = inverse.run()
    """

    def __init__(self, C, E, theta, sigma=1.0, lambda_reg=1e-5,
                 sigma_gradient=1.0, epsilon=1e-9, mask_level=0.02):
        self.C = np.asarray(C, dtype=np.float64)
        self.E = np.asarray(E, dtype=np.float64)
        self.theta = np.asarray(theta, dtype=np.float64)
        self.sigma = sigma
        self.lambda_reg = lambda_reg
        self.sigma_gradient = sigma_gradient
        self.epsilon = epsilon
        self.mask_level = mask_level

    def run(self):
        """Return the reconstructed image (zero-mean)."""
        dx, dy, window = transfers(self.C.shape,
                                   self.sigma_gradient, self.sigma)

        # steps 1-2: features -> eigenvalues -> tensor (exact)
        lambda1 = 0.5 * (self.E + self.C * (self.E + self.epsilon))
        lambda2 = 0.5 * (self.E - self.C * (self.E + self.epsilon))
        phi = np.pi / 2.0 - self.theta
        jxx = lambda1 * np.cos(phi) ** 2 + lambda2 * np.sin(phi) ** 2
        jyy = lambda1 * np.sin(phi) ** 2 + lambda2 * np.cos(phi) ** 2
        jxy = (lambda1 - lambda2) * np.cos(phi) * np.sin(phi)

        # step 3: Tikhonov deconvolution of the window
        def deconvolve(array):
            return np.fft.ifft2(np.fft.fft2(array) * np.conj(window)
                                / (np.abs(window) ** 2 + self.lambda_reg)).real

        pxx, pxy, pyy = deconvolve(jxx), deconvolve(jxy), deconvolve(jyy)

        # step 4: half-angle square root, branch by 2D phase unwrapping
        magnitude = np.sqrt(np.maximum(pxx + pyy, 0.0))
        psi = np.ma.array(np.angle((pxx - pyy) + 2j * pxy),
                          mask=magnitude < self.mask_level * magnitude.max())
        psi_u = np.array(unwrap_phase(psi))
        gx = magnitude * np.cos(0.5 * psi_u)
        gy = magnitude * np.sin(0.5 * psi_u)

        # step 5: Tikhonov least-squares integration
        numerator = np.conj(dx) * np.fft.fft2(gx) + np.conj(dy) * np.fft.fft2(gy)
        denominator = np.abs(dx) ** 2 + np.abs(dy) ** 2 + self.lambda_reg
        return np.fft.ifft2(numerator / denominator).real
