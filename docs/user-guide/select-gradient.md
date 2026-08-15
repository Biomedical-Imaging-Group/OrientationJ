<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">
  <div class="oj-banner__top">
    <a class="oj-banner__mark" href="https://imaging.epfl.ch/" title="EPFL Center for Imaging">
      <img src="../../assets/center-for-imaging.svg" alt="EPFL Center for Imaging">
    </a>
    <p class="oj-banner__credit">
      <a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a><br>
      <a href="https://imaging.epfl.ch/">Center for Imaging</a> and
      <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a><br>
      <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a>
    </p>
  </div>
  <!-- each part is one box, so a dash can never begin a wrapped line -->
  <p class="oj-banner__title"><span class="oj-banner__part"><strong>Orientation<span class="oj-banner__j">J</span></strong></span><span
     class="oj-banner__part">Directional analysis of 2D images</span><span
     class="oj-banner__part">ImageJ/Fiji plugins</span></p>
  <p class="oj-banner__version">Version 2.1.0 · August 2026</p>
</div>

# Selecting the gradient

The gradient decides how the derivatives are estimated before the tensor is assembled — the second setting of the *Structure Tensor* block, and the one to leave alone unless you have a reason not to.

The gradient decides how the derivatives are estimated before the tensor is assembled. Keep **Cubic Spline**, the default, unless you have a reason not to: it is the exact derivative of the cubic-spline interpolation of the image and stays accurate down to fine structures.

Of the others, *Finite Difference* is the fastest but one to two orders of magnitude more biased, increasingly so as structures get finer; *Fourier*, *Riesz* and *Gaussian* are band-limited derivatives that hold their accuracy at small periods, at the cost of spatial locality — Fourier can ring near the borders.

The measured angular error of all five, against analytic ground truth, is in [the gradient](select-gradient.md) and in the [gradient assessment](../assessment/compare-gradients.md).

## The five gradients

The gradient decides how the derivatives are estimated before the tensor is assembled. On a chirp, whose local period is known everywhere, the differences are measurable:

![Angular error versus local period for the five gradients](../assets/gradient-error.png)

- **Cubic Spline** (the default) — the exact derivative of the cubic-spline interpolation of the image: accurate down to fine structures, and the setting used throughout this documentation. Keep it unless you have a reason not to.
- **Finite Difference** — the simplest and fastest, but one to two orders of magnitude more biased, increasingly so as structures get finer.
- **Fourier**, **Riesz** and **Gaussian** — band-limited derivatives that stay accurate at small periods, useful on noisy or oscillating data, at the cost of spatial locality (Fourier can ring near the borders).

The five gradients of the plugin are compared quantitatively, against analytic ground truth, in the [gradient assessment](../assessment/compare-gradients.md), and the Gaussian derivative is the one used by the [minimal operator](../assessment/operator.md).
