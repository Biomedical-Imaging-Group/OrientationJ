---
title: Assessments
---

<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">

  <img class="oj-banner__logo" src="../assets/logo-orientationj-clear.png" alt="OrientationJ">
  <div class="oj-banner__box">
    <p class="oj-banner__sub">Directional analysis of 2D images — ImageJ/Fiji plugins</p>
  </div>
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
</div>

# Assessments

Each section is the report of one experiment with the code that produced it.

- **[Benchmarking](benchmarking.md)** — the orientation distribution of OrientationJ against six other tools, on the same images and the same masks.
- **[Compare gradients](compare-gradients.md)** — the five gradients of the plugin measured against analytic ground truth, error against structure size.
- **[Python port](python-port.md)** — a faithful reimplementation in Python, agreeing with the Java plugin to the last bit, used to produce the reference maps.
- **[Operator](operator.md)** — the same measurement in sixty lines of separable convolutions, with no transform at all, and what it costs in accuracy.
