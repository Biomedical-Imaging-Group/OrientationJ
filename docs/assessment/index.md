<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">
  <div class="oj-banner__top">
    <a class="oj-banner__mark" href="https://imaging.epfl.ch/" title="EPFL Center for Imaging">
      <img src="../assets/center-for-imaging.svg" alt="EPFL Center for Imaging">
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

# Assessment

How well the measurement performs, measured rather than asserted. Each page is the report of one experiment, kept in step with the code that produced it in the repository.

## The pages of this section

- **[Benchmarking](benchmarking.md)** — the orientation distribution of OrientationJ against six other tools, on the same images and the same masks.
- **[Compare gradients](compare-gradients.md)** — the five gradients of the plugin measured against analytic ground truth, error against structure size.
- **[Python port](python-port.md)** — a faithful reimplementation in Python, agreeing with the Java plugin to the last bit, used to produce the reference maps.
- **[Operator](operator.md)** — the same measurement in sixty lines of separable convolutions, with no transform at all, and what it costs in accuracy.
