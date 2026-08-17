---
title: How to use
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

# How to use

*OrientationJ is a suite of plugins for [ImageJ](https://imagej.net/ij/) and [Fiji](https://fiji.sc/), written in plain Java with no dependency to install (see [Installation](../installation/index.md)). Open a 2D grayscale image, pick a command, and the result appears — nothing to configure beyond the dialog in front of you. Whatever the command, the computation underneath is the same: the gradient structure tensor is computed over a local window (see [Theory](../theory/index.md)) to provide pixelwise directional quantities.*

The pages of this section take it in order: what [all the plugins](plugins.md) produce, how to drive them from a [macro](macros.md), and how to choose the two settings that change the numbers — [the analysis scale σ](select-scale.md) and [the gradient](select-gradient.md). To try them on data whose answer is known, take one of the sixteen [test images](../test-images.md), each with its mask.

## The user interface

<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/docs/assets/gui-analysis.png" alt="The OrientationJ Analysis dialog" width="290" align="right" style="margin-left: 1.5em;">

The *Analysis* dialog, shown here, is representative of all of them. The upper block — *Structure Tensor* — sets the two settings that change the measurement, σ ("Local window") and the gradient, then selects which feature maps to produce. Energy and directionality are unbounded, so they carry a display scaling: *Scale [0..1]* for a normalized view, *No scale* for the raw values you want to measure; coherency and anisotropy are already in [0, 1] and are shown as computed. The lower block builds the color survey — which feature drives the hue, the saturation and the brightness.

Every field has a macro equivalent, so once a setting works it can be recorded with the ImageJ macro recorder and replayed over a whole folder. That is the usual way to go from one exploratory image to a batch of hundreds.

