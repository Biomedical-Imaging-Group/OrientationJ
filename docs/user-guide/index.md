---
title: User guide
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

OrientationJ is a suite of plugins for [ImageJ](https://imagej.net/ij/) and [Fiji](https://fiji.sc/), written in plain Java with no dependency to install: a single jar dropped into the `plugins` folder adds every command under **Plugins ▸ OrientationJ** (see [Installation](../installation/index.md)). Open a 2D grayscale image, pick a command, and the result appears as new images, an overlay or a table — nothing to configure beyond the dialog in front of you.

Whatever the command, the computation underneath is the same: a gradient is taken at every pixel, the gradient structure tensor is averaged over a local window, and its eigen-analysis gives the **orientation** of the local structure, the **coherency** saying how well defined that orientation is, and the **energy** saying whether there is any structure at all. The derivation, from the weighted inner product to the tensor invariants, is in [Theory](../theory/index.md); everything you need in order to run the plugins is on the pages of this section.

## The user interface

<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/docs/assets/gui-analysis.png" alt="The OrientationJ Analysis dialog" width="290" align="right" style="margin-left: 1.5em;">

The *Analysis* dialog, shown here, is representative of all of them. The upper block — *Structure Tensor* — sets the two settings that change the measurement, σ ("Local window") and the gradient, then selects which feature maps to produce. Energy and directionality are unbounded, so they carry a display scaling: *Scale [0..1]* for a normalized view, *No scale* for the raw values you want to measure; coherency and anisotropy are already in [0, 1] and are shown as computed. The lower block builds the color survey — which feature drives the hue, the saturation and the brightness.

Every field has a macro equivalent, so once a setting works it can be recorded with the ImageJ macro recorder and replayed over a whole folder. That is the usual way to go from one exploratory image to a batch of hundreds.

## The pages of this section

- **[Selecting the parameters](select-scale.md)** — how to choose σ and the gradient, the only two settings that change the numbers.
- **[Plugins](plugins.md)** — what each command produces, with an example of its output.

To try them on data whose answer is known, take one of the [test images](../test-images.md): sixteen images, real and synthetic, each with its mask.

## Macros

Every command is **recordable**: open **Plugins ▸ Macros ▸ Record…**, run a command from its dialog, and the line that appears is the macro that reproduces it. Replayed on another image, or looped over a folder, it gives the same measurement with the same settings — which is how a figure made on one image becomes a figure made on a hundred.

### The color survey of an image

```javascript
open("collagen.tif");
run("OrientationJ Analysis", "tensor=2.0 gradient=0 color-survey=on "
    + "hue=Orientation sat=Coherency bri=Original-Image ");
saveAs("PNG", "collagen-survey.png");
```

![The color survey written by the macro](../assets/macro-survey.jpg)

### A vector field over the structures

```javascript
open("cell_aemisegger.tif");
run("OrientationJ Vector Field", "tensor=4.0 gradient=0 grid=24 "
    + "scale=120 type=Coherency overlay=on ");
saveAs("PNG", "cell-vectorfield.png");
```

![The vector field written by the macro](../assets/macro-vectorfield.jpg)

### A whole folder in one run

```javascript
in  = "test-images/images/";
out = "surveys/";
list = getFileList(in);
setBatchMode(true);                       // no window opens: much faster
for (i = 0; i < list.length; i++) {
    if (!endsWith(list[i], ".tif")) continue;
    open(in + list[i]);
    run("OrientationJ Analysis", "tensor=2.0 gradient=0 color-survey=on "
        + "hue=Orientation sat=Coherency bri=Original-Image ");
    saveAs("PNG", out + replace(list[i], ".tif", "") + "-survey.png");
    close("*");
}
```

![The sixteen surveys written by the batch macro](../assets/macro-batch.jpg)

<p class="oj-caption">The sixteen test images, analyzed and saved by the macro above in a single run.</p>

## Questions

Questions about the plugins, their parameters or the interpretation of their output are best asked on the [image.sc forum](https://forum.image.sc/), the forum of the scientific imaging community, where they are read by the authors and by other users. Tag the post `orientationj`, and say which command and which σ you used.
