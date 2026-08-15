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

# Community discussions

Threads on the [Scientific Community Image Forum](https://forum.image.sc/) where the use, the parameters and the interpretation of OrientationJ are discussed — several answered by the author of the plugin.

- [Coherency in OrientationJ](https://forum.image.sc/t/coherency-in-orientation-j/83176) (2023) — how to read coherency values on collagen fibers in second-harmonic-generation images, gel-only control against cell-containing samples.
- [Dealing with dark regions](https://forum.image.sc/t/orientationj-dealing-with-dark-regions/61179) (2021) — how background regions weigh on the coherency and on the dominant direction.
- [How is the dominant direction calculated?](https://forum.image.sc/t/how-is-the-dominant-direction-calculated/38330) (2020) — what the orientation and coherency returned by *Dominant Direction* mean, and the part played by the local window.
- [Angle histograms](https://forum.image.sc/t/orientationj-and-angle-histograms/119187) (2026) — why the *Distribution* histogram can show a large spike at 0°, and the preprocessing that avoids it.
- [Interpretation for electrospun nanofibers](https://forum.image.sc/t/orientation-j-and-its-interpretation-for-electrospun-nanofibers/3821) (2017) — what energy, orientation and coherency mean when quantifying nanofiber alignment.
- [Where is the documentation?](https://forum.image.sc/t/orientationj-documentation/69672) (2022) — the functions and parameters, beyond the original page.
- [Macro for the plugin](https://forum.image.sc/t/macro-for-plug-in-orientationj/7224) (2017) — batch processing from the ImageJ macro language, and saving the result tables.
