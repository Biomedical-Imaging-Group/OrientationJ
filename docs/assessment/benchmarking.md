<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">

  <img class="oj-banner__logo" src="../../assets/logo-orientationj-clear.png" alt="OrientationJ">
  <div class="oj-banner__box">
    <p class="oj-banner__sub">Directional analysis of 2D images — ImageJ/Fiji plugins</p>
  </div>
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
</div>

## OrientationJ Benchmarking

Compares the distribution of orientation measured by **seven tools**, all with the cubic-spline gradient and the structure-tensor window σ = 1 wherever the tool exposes those choices, on four images of the [test set](../test-images.md). Two comparisons:

- **masked** — only pixels inside [the structure masks](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/orientationj-test-images/masks);
  without Directionality (no mask support);
- **full** — every pixel of the image, without the direct gradient, whose unsmoothed histogram is dominated by the flat background.

## Results

### Inside the structure mask

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/benchmarking/benchmark-distributions-masked.png" width="480">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/benchmarking/benchmark-distributions-masked.png)

### Over the whole image

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/benchmarking/benchmark-distributions-full.png" width="480">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/benchmarking/benchmark-distributions-full.png)


## The seven tools

1. **OrientationJ (Fiji)** — the Java plugin; its saved orientation maps are binned here.
2. **OrientationJ Python port** — [the Python port](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/orientationj_python_port), the faithful reimplementation.
3. **[OrientationPy](https://epfl-center-for-imaging.gitlab.io/orientationpy/)** — the Python successor, 2D and 3D; spline gradient, σ = 1, fiber orientation.
4. **GST operator** — [gst_operator.py](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/gst_operator/gst_operator.py), the `forward()` of the minimal Python version:
   Gaussian-derivative gradient and Gaussian window, analytic in Fourier.
5. **[scikit-image](https://scikit-image.org/docs/stable/api/skimage.feature.html#skimage.feature.structure_tensor)** — `feature.structure_tensor` (finite-difference gradient), same tensor formula.
6. **direct gradient** — per-pixel angle perpendicular to the spline gradient, no tensor smoothing (σ = 0).
7. **[Directionality](https://imagej.net/plugins/directionality)** (Fiji) — local-gradient method, its own histogram; **no mask support**, whole image.

All tools share the OrientationJ angle convention (verified on calibration
sinusoids): degrees in [−90, +90], counter-clockwise from the horizontal of the
displayed image. Histograms: 180 bins of 1°, normalized to probability per bin.

## Files

| file | content |
|---|---|
| [benchmark_orientation.ipynb](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/benchmarking/benchmark_orientation.ipynb) | runs the five Python tools, loads the Fiji results, draws the comparison |
| [macro-orientationj.ijm](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/benchmarking/macro-orientationj.ijm) | Fiji macro — OrientationJ Analysis (spline, σ = 1), saves the orientation maps |
| [macro-directionality.ijm](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/benchmarking/macro-directionality.ijm) | Fiji macro — Directionality plugin (GUI only: it saves the displayed table) |
| [script-directionality.groovy](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/benchmarking/script-directionality.groovy) | same as the macro through the plugin API, also works headless |



