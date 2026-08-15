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

# Selecting the parameters

Every dialog opens with the same *Structure Tensor* block, and only two of its fields change the numbers: the analysis scale **σ** and the **gradient**. Everything else is presentation — which maps to display, how to scale them, how to color the survey.

## The analysis scale σ

σ ("Local window") is the standard deviation, in pixels, of the Gaussian window over which the tensor is averaged. It decides what *local* means, and therefore which structures the measurement describes.

Start at about **half the width of the structures you care about**: σ = 1–2 px for thin fibers, more for coarse bundles. Then keep the trade-off in mind — a small σ follows fine detail but gives noisy angles and low coherency everywhere, while a large σ gives stable, smooth angles that blend neighboring structures and round off corners. When the structures live at several scales, run the analysis at several σ and compare: the coherency map tells you at which scale each region is best described.

The effect, image by image and on the angular histogram, is shown in [the scale parameter σ](../theory/scale.md).

## The gradient

The gradient decides how the derivatives are estimated before the tensor is assembled. Keep **Cubic Spline**, the default, unless you have a reason not to: it is the exact derivative of the cubic-spline interpolation of the image and stays accurate down to fine structures.

Of the others, *Finite Difference* is the fastest but one to two orders of magnitude more biased, increasingly so as structures get finer; *Fourier*, *Riesz* and *Gaussian* are band-limited derivatives that hold their accuracy at small periods, at the cost of spatial locality — Fourier can ring near the borders.

The measured angular error of all five, against analytic ground truth, is in [the gradient](../theory/gradient.md) and in the [gradient assessment](../assessment/gradients.md).

## The thresholds

*Distribution* and *Vector Field* add a minimum coherency and a minimum energy. These do not change the measurement; they decide which pixels are allowed to vote. Raising the coherency threshold keeps only the well-oriented pixels, and raising the energy threshold discards the flat background — the practical way to stop empty regions from filling a histogram with meaningless angles.
