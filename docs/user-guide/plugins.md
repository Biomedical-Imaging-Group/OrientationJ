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

# Plugins

What each command under **Plugins ▸ OrientationJ** produces, in the order the menu lists them, with an example of its output.

<p class="oj-toc">On this page:
<a href="#principle">Principle</a> ·
<a href="#analysis">Analysis</a> ·
<a href="#distribution">Distribution</a> ·
<a href="#vector-field">Vector Field</a> ·
<a href="#measure">Measure</a> ·
<a href="#dominant-direction">Dominant Direction</a> ·
<a href="#clustering">Clustering</a> ·
<a href="#horizontal-alignment">Horizontal Alignment</a> ·
<a href="#monogenicj">MonogenicJ</a> ·
<a href="#corner-harris">Corner Harris</a></p>

## Principle

Every command works from the same measurement: a gradient at each pixel, the gradient structure tensor averaged over the local window σ, and the eigen-analysis that gives orientation, coherency and energy. What changes from one command to the next is only what is done with those three numbers — painted over the image, binned into a histogram, drawn as arrows, or reported in a table. They therefore share the settings described in [Selecting the parameters](parameters.md), and every one of them is scriptable from an ImageJ macro.

## Analysis

Produces the feature maps — orientation, coherency, energy, directionality, anisotropy — and the color survey, which paints them over the image: hue for the orientation, saturation for the coherency, brightness for the original intensity. Flat and isotropic regions therefore stay gray, and only genuinely oriented structures take on color.

![The orientation color scale](../assets/color-scale.jpg)

<p class="oj-caption">The color coding of the orientation: green at 0°, blue at +45°, orange at −45°, red at ±90°.</p>

Four images analyzed at the same setting — the survey turns every field into the same visual language:

![Color surveys of four test images](../assets/surveys-smoothing.jpg)

## Distribution

Bins the local orientations into an angular histogram, with minimum-coherency and minimum-energy thresholds so that only meaningful pixels are counted. This is the command most often used to quantify alignment, and its table can be exported for statistics.

![Orientation distribution of a collagen image](../assets/montage3.jpg)

## Vector Field

Overlays one vector per grid cell, with a length that is constant or scaled by energy, coherency, or both. The most readable summary for a figure, though the histogram is the better instrument for quantification.

![Vector field overlaid on an image](../assets/vector-field.png)

The grid and the analysis scale act together: the same field, drawn while σ grows, keeps only the trend that survives at that scale.

![Vector field of collagen while the analysis scale grows](../assets/vectorfield-scale.gif)

## Measure

Reports orientation, coherency and energy inside the current selections, as a table — the tool for comparing a few regions rather than mapping the whole field.

![Measurements inside elliptical selections](../assets/montage2.jpg)

## Dominant Direction

Collapses the whole image to a single angle with its coherency: a one-number answer, convenient for batch comparisons across a series.

## Clustering

Groups locally oriented regions into clusters and reports one representative vector per cluster — position, direction, coherency, energy — a structure-level summary of the vector field.

## Horizontal Alignment

Rotates each slice of a stack so that its dominant direction becomes horizontal, which registers fibrous samples acquired at arbitrary angles before further analysis.

## MonogenicJ

A companion plugin, on a different footing: instead of one local window it builds a multiresolution **monogenic** representation of the image with the Riesz–Laplace wavelet transform, and reports orientation, coherency and phase at every scale. Use it when the structures of interest live at several scales at once. Details on the [MonogenicJ page](https://bigwww.epfl.ch/demo/monogenicj/).

## Corner Harris

Harris keypoint detection, built on the same structure tensor: corners are the places where both eigenvalues are large.

![Harris corner detection](../assets/harris.png)
