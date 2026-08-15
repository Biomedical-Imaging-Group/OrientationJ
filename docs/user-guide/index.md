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

# User guide

OrientationJ is a suite of plugins for [ImageJ](https://imagej.net/ij/) and [Fiji](https://fiji.sc/), written in plain Java with no dependency to install: a single jar dropped into the `plugins` folder adds every command under **Plugins ▸ OrientationJ** (see [Installation](../installation/index.md)). Open a 2D grayscale image, pick a command, and the result appears as new images, an overlay or a table — nothing to configure beyond the dialog in front of you.

Whatever the command, the computation underneath is the same: a gradient is taken at every pixel, the gradient structure tensor is averaged over a local window, and its eigen-analysis gives the **orientation** of the local structure, the **coherency** saying how well defined that orientation is, and the **energy** saying whether there is any structure at all. The derivation, from the weighted inner product to the tensor invariants, is in [Theory](../theory/index.md); everything you need in order to run the plugins is on the pages of this section.

## The dialogs

<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/docs/assets/gui-analysis.png" alt="The OrientationJ Analysis dialog" width="290" align="right" style="margin-left: 1.5em;">

The *Analysis* dialog, shown here, is representative of all of them. The upper block — *Structure Tensor* — sets the two settings that change the measurement, σ ("Local window") and the gradient, then selects which feature maps to produce. Energy and directionality are unbounded, so they carry a display scaling: *Scale [0..1]* for a normalized view, *No scale* for the raw values you want to measure; coherency and anisotropy are already in [0, 1] and are shown as computed. The lower block builds the color survey — which feature drives the hue, the saturation and the brightness.

Every field has a macro equivalent, so once a setting works it can be recorded with the ImageJ macro recorder and replayed over a whole folder. That is the usual way to go from one exploratory image to a batch of hundreds.

## The pages of this section

- **[Selecting the parameters](select-scale.md)** — how to choose σ and the gradient, the only two settings that change the numbers.
- **[Plugins](plugins.md)** — what each command produces, with an example of its output.

To try them on data whose answer is known, take one of the [test images](../test-images.md): sixteen images, real and synthetic, each with its mask.
