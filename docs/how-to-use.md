<div class="oj-banner" markdown>

![OrientationJ](assets/logo-orientationj.png){ .oj-logo }

<p class="oj-subtitle">Local directional analysis of 2D images — ImageJ/Fiji plugins</p>

<hr>

<p class="oj-author"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>

<p class="oj-date">August 2026</p>

<hr>

![Color survey of the Tree Rings sample, sweeping the local window](assets/tree-orientation.gif){ .oj-tree }

</div>

# How to use

All plugins share the same core computation: the gradient structure tensor is evaluated in a local window whose size is set by the **σ** (local window) parameter, from a gradient computed by the selected **gradient method**. What differs is how the result is presented.

<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/docs/assets/gui-analysis.png" alt="The OrientationJ Analysis dialog" width="290" align="right" style="margin-left: 1.5em;">

The *Analysis* dialog, shown here, is representative of all the plugins: the **Structure Tensor** block sets the two parameters that matter (local window σ, gradient method) and selects which feature maps to produce — each with its display scaling (*Scale [0..1]* or raw values) — and the **Color survey** block chooses which feature drives each HSB channel (by default hue = orientation, saturation = coherency, brightness = original image).

## Choosing σ

σ is expressed in pixels and sets the size of the neighborhood over which the tensor is averaged. Two rules of thumb:

- **Match the structure width.** σ of about half the width of the fibers or stripes of interest is a good starting point — σ = 1–2 for thin fibers, larger for coarse bundles.
- **Know the trade-off.** A small σ follows fine detail but yields noisy orientations and low coherency everywhere; a large σ gives stable, smooth orientations but blends neighboring structures and rounds corners. When structures exist at several scales, analyze at several σ and compare — the coherency map tells you at which scale each region is best described.

A quick way to calibrate: run the **chirp** [test image](test-images.md), whose local period sweeps across the field, and watch where the orientation map stays faithful for your σ.

## Choosing the gradient

The gradient method sets how the derivatives \(f_x, f_y\) are computed before the tensor is assembled:

- **Cubic Spline** (the default) — an exact derivative of the cubic-spline interpolation of the image; the most accurate choice on fine structures, and the setting used in all the benchmarks of this documentation. Keep it unless you have a reason not to.
- **Finite Difference** — the simplest and fastest scheme; noticeably more biased where the structures approach the pixel scale.
- **Fourier** — the exact spectral derivative; well suited to smooth periodic patterns, but its global support can ring near edges and image borders.
- **Riesz** and **Gaussian** — smoother, band-limited variants that trade spatial locality for noise robustness.

As with σ, the chirp test image makes the differences visible: compare the orientation error as the local period shrinks.

## Analysis

Produces color-coded maps of orientation, coherency, energy, directionality and anisotropy. Orientation is mapped to hue, and you can weight the display by coherency, by energy, or by both, so that flat or noisy regions do not dominate the picture. The default **color survey** uses hue = orientation, saturation = coherency, brightness = original image.

## Distribution

Builds a histogram of local orientations over the image, with optional minimum-coherency and minimum-energy thresholds so that well-defined structures count more than background. This is the plugin most often used to quantify fiber alignment.

## Measure

Returns orientation, coherency and energy inside the current selection. Useful when the question is about one local area rather than the whole field.

## Dominant Direction

Collapses the whole image to a single angle plus a coherency value.

## Vector Field

Overlays orientation vectors on a regular grid; the vector length can be constant or scaled by energy, coherency, or both. Good for figures; less good for quantification.

## Clustering

Groups locally oriented regions into clusters and reports one representative vector per cluster (position, direction, coherency, energy) — a compact, structure-level summary of the vector field.

## Horizontal Alignment

Takes a stack and rotates each slice so that its dominant direction becomes horizontal — useful to register fibrous samples acquired at arbitrary orientations before further analysis.

## Corner Harris

Harris corner detection, included because it shares the structure-tensor machinery.
