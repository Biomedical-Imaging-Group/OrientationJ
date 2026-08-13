# User guide

Open a 2D grayscale image and pick a command under **Plugins ▸ OrientationJ**. Whatever the command, the computation is the same: a gradient is taken at every pixel, the gradient structure tensor is averaged over a local window, and its eigen-analysis gives the orientation, the coherency and the energy. Two settings govern the measurement itself — the **analysis scale σ** and the **gradient** — and both appear in the *Structure Tensor* block of every dialog; each command then adds its own options, such as the coherency and energy thresholds of *Distribution* or the grid of *Vector Field*. What σ and the gradient actually do, and how to choose them, is explained in [Theory](theory.md#the-scale-parameter); the rest of this page describes the commands.

<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/docs/assets/gui-analysis.png" alt="The OrientationJ Analysis dialog" width="290" align="right" style="margin-left: 1.5em;">

The *Analysis* dialog, shown here, is representative of all of them. The upper block sets σ ("Local window") and the gradient, then selects which feature maps to produce. Energy and directionality are unbounded, so they carry a display scaling — *Scale [0..1]* for a normalized view, *No scale* for the raw values you want to measure; coherency and anisotropy are already in [0, 1] and are shown as computed. The lower block builds the color survey: which feature drives the hue, the saturation and the brightness. Every field has a macro equivalent, so once a setting works it can be recorded and replayed over a whole folder.

## Analysis

Produces the feature maps — orientation, coherency, energy, directionality, anisotropy — and the color survey, which paints them over the image: hue for the orientation, saturation for the coherency, brightness for the original intensity. Flat and isotropic regions therefore stay gray, and only genuinely oriented structures take on color.

![The orientation color scale](assets/color-scale.jpg)

<p class="oj-caption">The color coding of the orientation: green at 0°, blue at +45°, orange at −45°, red at ±90°.</p>

Four images analyzed at the same setting — the survey turns every field into the same visual language:

![Color surveys of four test images](assets/surveys-smoothing.jpg)

## Distribution

Bins the local orientations into an angular histogram, with minimum-coherency and minimum-energy thresholds so that only meaningful pixels are counted. This is the command most often used to quantify alignment, and its table can be exported for statistics.

![Orientation distribution of a collagen image](assets/montage3.jpg)

## Vector Field

Overlays one vector per grid cell, with a length that is constant or scaled by energy, coherency, or both. The most readable summary for a figure, though the histogram is the better instrument for quantification.

![Vector field overlaid on an image](assets/vector-field.png)

The grid and the analysis scale act together: the same field, drawn while σ grows, keeps only the trend that survives at that scale.

![Vector field of collagen while the analysis scale grows](assets/vectorfield-scale.gif)

## Measure

Reports orientation, coherency and energy inside the current selections, as a table — the tool for comparing a few regions rather than mapping the whole field.

![Measurements inside elliptical selections](assets/montage2.jpg)

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

![Harris corner detection](assets/harris.png)
