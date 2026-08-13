# Introduction

OrientationJ is a suite of plugins for [ImageJ](https://imagej.net/ij/) and [Fiji](https://fiji.sc/), written in plain Java with no dependency to install: a single jar dropped into the `plugins` folder adds every command under **Plugins ▸ OrientationJ** (see [Installation](../installation.md)). Open a 2D grayscale image, pick a command, and the result appears as new images, an overlay or a table — nothing to configure beyond the dialog in front of you.

Whatever the command, the computation underneath is the same: a gradient is taken at every pixel, the gradient structure tensor is averaged over a local window, and its eigen-analysis gives the **orientation** of the local structure, the **coherency** saying how well defined that orientation is, and the **energy** saying whether there is any structure at all. The derivation, from the weighted inner product to the tensor invariants, is in [Theory](../theory.md); everything you need in order to run the plugins is on these three pages.

## The dialogs

<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/docs/assets/gui-analysis.png" alt="The OrientationJ Analysis dialog" width="290" align="right" style="margin-left: 1.5em;">

The *Analysis* dialog, shown here, is representative of all of them. The upper block — *Structure Tensor* — sets the two settings that change the measurement, σ ("Local window") and the gradient, then selects which feature maps to produce. Energy and directionality are unbounded, so they carry a display scaling: *Scale [0..1]* for a normalized view, *No scale* for the raw values you want to measure; coherency and anisotropy are already in [0, 1] and are shown as computed. The lower block builds the color survey — which feature drives the hue, the saturation and the brightness.

Every field has a macro equivalent, so once a setting works it can be recorded with the ImageJ macro recorder and replayed over a whole folder. That is the usual way to go from one exploratory image to a batch of hundreds.

## Where to go next

- [Selecting the parameters](parameters.md) — how to choose σ and the gradient, the only two settings that change the numbers.
- [Plugins](plugins.md) — what each command produces, with an example of its output.
- [Test images](../test-images.md) — sixteen images, real and synthetic, with their masks: the quickest way to try a command on data whose answer is known.
