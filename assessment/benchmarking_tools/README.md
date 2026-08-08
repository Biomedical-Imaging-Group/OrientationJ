# OrientationJ Benchmarking

Compares the distribution of orientation measured by **seven tools** on 4 images
(`synthetic_rings_dither_512`, `synthetic_nematic_512`, `synthetic_noise_512`,
`collagen`), all with the cubic-spline gradient and structure-tensor window σ = 1
wherever the tool exposes those choices. Two comparisons:

- **masked** — only pixels inside [../../orientationj-test-images/masks](../../orientationj-test-images/masks);
  without Directionality (no mask support);
- **full** — every pixel; without the direct gradient (its unsmoothed histogram is
  dominated by the degenerate flat-background spikes).

Coinciding curves are told apart by line style — the Fiji plugin is a thick solid
base line, the port rides on it dashed, OrientationPy dotted — and the y-limits
hug the data to magnify the nearly flat histograms.

## Results

### Inside the structure mask

[<img src="benchmark-distributions-masked.png" width="480">](benchmark-distributions-masked.png)

### Over the whole image

[<img src="benchmark-distributions-full.png" width="480">](benchmark-distributions-full.png)

*The Fiji plugin and its Python port agree exactly (RMSE 0 on all four images);
OrientationPy stays within 0.4·10⁻³ probability per bin, and every other tool
within 1.6·10⁻³ — differences traceable to its gradient flavor or histogram.*

## The seven tools

1. **OrientationJ (Fiji)** — the Java plugin, orientation maps binned here inside the mask.
2. **OrientationJ Python port** — [../../orientationj-python-port](../../orientationj-python-port), the faithful reimplementation.
3. **OrientationPy** — spline gradient, σ = 1, fiber orientation.
4. **GST minimal** — [gst_minimal.py](gst_minimal.py), the whole operator in ~15 lines:
   Gaussian-derivative gradient and Gaussian window, analytic in Fourier.
5. **scikit-image** — `feature.structure_tensor` (finite-difference gradient), same tensor formula.
6. **direct gradient** — per-pixel angle perpendicular to the spline gradient, no tensor smoothing (σ = 0).
7. **Directionality (Fiji)** — Local-gradient method, its own histogram; **no mask support**, whole image.

All tools share the OrientationJ angle convention (verified on calibration
sinusoids): degrees in [−90, +90], counter-clockwise from the horizontal of the
displayed image. Histograms: 180 bins of 1°, normalized to probability per bin.

## Files

| file | content |
|---|---|
| [benchmark_orientation.ipynb](benchmark_orientation.ipynb) | runs the five Python tools, loads the Fiji results, draws the comparison, writes the CSVs |
| [gst_minimal.py](gst_minimal.py) | the gradient structure tensor operator, minimal implementation |
| [macro-orientationj.ijm](macro-orientationj.ijm) | Fiji macro — OrientationJ Analysis (spline, σ = 1), saves the orientation maps |
| [macro-directionality.ijm](macro-directionality.ijm) | Fiji macro — Directionality plugin (GUI only: it saves the displayed table) |
| [script-directionality.groovy](script-directionality.groovy) | same as the macro through the plugin API, also works headless |
| `benchmark-distributions-masked.png` / `-full.png` | the two summary figures above |
| `results/` | per-image distributions (CSV), statistics, raw Fiji outputs — generated locally by the notebook and macros, not tracked |
