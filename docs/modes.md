# Plugin modes

All modes share the same core computation: the gradient structure tensor is evaluated in a
local window whose size is set by the **σ** (local window) parameter. What differs is how the
result is presented.

## Analysis

Produces color-coded maps. Orientation is mapped to hue, and you can weight the display by
coherency, by energy, or by both, so that flat or noisy regions do not dominate the picture.
The default **color survey** uses hue = orientation, saturation = coherency,
brightness = original image.

## Distribution

Builds a histogram of local orientations over the image, with optional minimum-coherency and
minimum-energy thresholds so that well-defined structures count more than background. This is
the mode most often used to quantify fiber alignment.

## Directions

Reports dominant directions per detected structure as a table.

## Measure

Returns orientation, coherency and energy inside the current ROI. Useful when the question
is about one region rather than the whole field.

## Dominant Direction

Collapses the whole image to a single angle plus a coherency value.

## Vector Field

Overlays orientation vectors on a regular grid; the vector length can be constant or scaled
by energy, coherency, or both. Good for figures; less good for quantification.

## Corner Harris

Harris corner detection, included because it shares the structure-tensor machinery.

## Choosing σ

σ is expressed in pixels and sets the size of the neighborhood over which the tensor is
averaged. Two rules of thumb:

- **Match the structure width.** σ of about half the width of the fibers or stripes of
  interest is a good starting point — σ = 1–2 for thin fibers, larger for coarse bundles.
- **Know the trade-off.** A small σ follows fine detail but yields noisy orientations and
  low coherency everywhere; a large σ gives stable, smooth orientations but blends
  neighboring structures and rounds corners. When structures exist at several scales,
  analyze at several σ and compare — the coherency map tells you at which scale each
  region is best described.

A quick way to calibrate: run the **chirp** [test image](test-images.md), whose local period
sweeps across the field, and watch where the orientation map stays faithful for your σ.
