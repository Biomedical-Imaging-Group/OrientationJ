"""The gradient-structure-tensor operator, minimal: a forward and an inverse.

forward(image, sigma)  ->  (C, E, orientation)
    Gaussian-derivative gradient and Gaussian tensor window, both applied
    analytically in Fourier (periodic boundaries); computes only the three
    OrientationJ features.

inverse(C, E, orientation, sigma)  ->  image
    Naive blind reconstruction, one pass of the analytic chain:

      1. eigenvalues   lambda_1,2 = (E +/- C.E) / 2                    (exact)
      2. tensor        spectral recomposition with phi = 90deg - theta (exact)
      3. products      Tikhonov deconvolution of the Gaussian window   (ill-posed)
      4. gradient      half-angle square root of the doubled-angle field
                       (Jxx - Jyy) + 2i.Jxy = (gx + i.gy)^2, branch chosen by
                       2D phase unwrapping                             (sign retrieval)
      5. image         Tikhonov least-squares integration              (ill-posed)

    The output is defined up to the two strict invariances of the features:
    the mean gray level and the global contrast flip.  It fails wherever the
    gradient vanishes along whole curves (oscillating images): the sign flips
    across those curves are invisible to the features.
"""
import numpy as np
import tifffile
from skimage.restoration import unwrap_phase


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


def forward(image, sigma=1.0, sigma_gradient=1.0, epsilon=1e-9):
    """Forward model: image -> (C, E, orientation).

    C            coherency, in [0, 1]
    E            energy (trace of the tensor), in [0, inf)
    orientation  of the structures in radians, in [-pi/2, pi/2]
    """
    image = np.asarray(image, dtype=np.float64)
    dx, dy, window = transfers(image.shape, sigma_gradient, sigma)
    gx = apply(image, dx)
    gy = apply(image, dy)
    jxx = apply(gx * gx, window)
    jxy = apply(gx * gy, window)
    jyy = apply(gy * gy, window)
    E = jxx + jyy
    C = np.sqrt((jxx - jyy) ** 2 + 4.0 * jxy ** 2) / (E + epsilon)
    orientation = 0.5 * np.arctan2(2.0 * jxy, jyy - jxx)
    return C, E, orientation


def inverse(C, E, orientation, sigma=1.0, lambda_reg=1e-5, sigma_gradient=1.0,
            epsilon=1e-9, mask_level=0.02):
    """Inverse model: (C, E, orientation) -> image (zero-mean, blind)."""
    C = np.asarray(C, dtype=np.float64)
    E = np.asarray(E, dtype=np.float64)
    orientation = np.asarray(orientation, dtype=np.float64)
    dx, dy, window = transfers(C.shape, sigma_gradient, sigma)

    # steps 1-2: features -> eigenvalues -> tensor (exact)
    lambda1 = 0.5 * (E + C * (E + epsilon))
    lambda2 = 0.5 * (E - C * (E + epsilon))
    phi = np.pi / 2.0 - orientation
    jxx = lambda1 * np.cos(phi) ** 2 + lambda2 * np.sin(phi) ** 2
    jyy = lambda1 * np.sin(phi) ** 2 + lambda2 * np.cos(phi) ** 2
    jxy = (lambda1 - lambda2) * np.cos(phi) * np.sin(phi)

    # step 3: Tikhonov deconvolution of the window
    def deconvolve(array):
        return np.fft.ifft2(np.fft.fft2(array) * np.conj(window)
                            / (np.abs(window) ** 2 + lambda_reg)).real

    pxx, pxy, pyy = deconvolve(jxx), deconvolve(jxy), deconvolve(jyy)

    # step 4: half-angle square root, branch by 2D phase unwrapping
    magnitude = np.sqrt(np.maximum(pxx + pyy, 0.0))
    psi = np.ma.array(np.angle((pxx - pyy) + 2j * pxy),
                      mask=magnitude < mask_level * magnitude.max())
    psi_u = np.array(unwrap_phase(psi))
    gx = magnitude * np.cos(0.5 * psi_u)
    gy = magnitude * np.sin(0.5 * psi_u)

    # step 5: Tikhonov least-squares integration
    numerator = np.conj(dx) * np.fft.fft2(gx) + np.conj(dy) * np.fft.fft2(gy)
    denominator = np.abs(dx) ** 2 + np.abs(dy) ** 2 + lambda_reg
    return np.fft.ifft2(numerator / denominator).real
