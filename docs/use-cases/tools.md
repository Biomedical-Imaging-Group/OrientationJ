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

# Validation and related tools

The tools OrientationJ is most often compared with, and the studies that measure it against them. A quantitative comparison on a common dataset is in [Benchmarking](../assessment/benchmarking.md).

**[FiberFit](https://doi.org/10.1007/s10237-016-0776-3)** — Morrill EE, Tulepbergenov AN, Stender CJ, Lamichhane R, Brown RJ, Lujan TJ (2016), *A validated software application to measure fiber organization in soft tissue*, Biomech Model Mechanobiol 15:1467–1478. Compares Fourier-based and structure-tensor fiber orientation; the validation reference listed on the original OrientationJ page.

**[DiameterJ](https://doi.org/10.1016/j.biomaterials.2015.02.015)** — Hotaling NA, Bharti K, Kriel H, Simon CG (2015), *DiameterJ: a validated open source nanofiber diameter measurement tool*, Biomaterials 61:327–338. The fiber-diameter companion many nanofiber papers run alongside OrientationJ.

**[FiberO](https://doi.org/10.3389/fbioe.2024.1497837)** — Muñoz A, Docaj A, Fernandez J, Carriero A (2025), Front Bioeng Biotechnol. A recent open-source tool benchmarked against OrientationJ, Directionality, FiberFit and CT-FIRE.

**[Directionality](https://imagej.net/plugins/directionality)** (Fiji) — the Fourier-component and local-gradient alternative for orientation histograms, frequently used beside OrientationJ.

**[CT-FIRE](https://doi.org/10.1117/1.JBO.19.1.016007)** — fiber tracking and extraction for collagen in second-harmonic-generation images.

**[skimage.feature.structure_tensor](https://scikit-image.org/docs/stable/api/skimage.feature.html#skimage.feature.structure_tensor)** — the minimal structure tensor of scikit-image (finite-difference gradients only), the closest thing in the Python ecosystem outside OrientationPy.
