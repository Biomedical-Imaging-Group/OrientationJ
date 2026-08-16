<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
  <div class="oj-banner__box">
    <img class="oj-banner__logo" src="../assets/logo-orientationj-clear.png" alt="OrientationJ">
    <p class="oj-banner__sub">Directional analysis of 2D images — ImageJ/Fiji plugins</p>
  </div>
  <p class="oj-banner__version">Version 2.1.0 · August 2026</p>
</div>

# Assessment

How well the measurement performs, measured rather than asserted. Each page is the report of one experiment, kept in step with the code that produced it in the repository.

## The pages of this section

- **[Benchmarking](benchmarking.md)** — the orientation distribution of OrientationJ against six other tools, on the same images and the same masks.
- **[Compare gradients](compare-gradients.md)** — the five gradients of the plugin measured against analytic ground truth, error against structure size.
- **[Python port](python-port.md)** — a faithful reimplementation in Python, agreeing with the Java plugin to the last bit, used to produce the reference maps.
- **[Operator](operator.md)** — the same measurement in sixty lines of separable convolutions, with no transform at all, and what it costs in accuracy.
