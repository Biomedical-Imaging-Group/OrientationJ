# Benchmarking

The repository carries its own reproducible benchmark in
[`assessment/benchmarking_tools/`](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/benchmarking_tools):
the **distribution of orientation** measured by **seven tools** on four test images
(`synthetic_rings_dither_512`, `synthetic_nematic_512`, `synthetic_noise_512`,
`collagen`), all with the cubic-spline gradient and a structure-tensor window σ = 1
wherever the tool exposes those choices — once restricted to the structure masks,
once over the whole image.

![Orientation distributions, masked comparison](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/benchmarking_tools/benchmark-distributions-masked.png)

**Result in one line:** the Fiji plugin and its
[Python port](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/orientationj-python-port)
agree **exactly** (RMSE 0 on all four images); OrientationPy stays within
0.4·10⁻³ probability per bin, and every other tool within 1.6·10⁻³ — the
differences traceable to its gradient flavor or histogram.

## The compared tools

| Tool | Approach | Notes |
|---|---|---|
| **OrientationJ** (Fiji) | structure tensor, cubic-spline gradient | the reference |
| [OrientationJ Python port](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/orientationj-python-port) | same algorithm in NumPy | bitwise-faithful |
| [OrientationPy](https://gitlab.com/epfl-center-for-imaging/orientationpy) | structure tensor, 2D and 3D | the Python successor, same group |
| GST minimal | structure tensor in ~15 lines, Fourier | [gst_minimal.py](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/benchmarking_tools/gst_minimal.py) |
| scikit-image | `feature.structure_tensor`, finite differences | same tensor formula |
| direct gradient | per-pixel angle, no tensor smoothing | shows what σ = 0 costs |
| [Directionality](https://imagej.net/plugins/directionality) (Fiji) | local gradient, own histogram | no mask support |

Other tools compared against OrientationJ in the literature:
[FiberFit](https://doi.org/10.1007/s10237-016-0776-3) (FFT),
[CT-FIRE](https://doi.org/10.1117/1.JBO.19.1.016007) (curvelets, common in collagen imaging),
[FiberO](https://doi.org/10.3389/fbioe.2024.1497837) (benchmarked against OrientationJ in 2024).

## Inverting the features

A companion experiment,
[`assessment/operator_gst/`](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/operator_gst),
asks the converse question: how much of the image do the features (C, E, θ)
retain? A naive blind inversion reconstructs smooth images recognizably and fails
exactly where theory says it must (oscillating patterns, whose gradient sign
flips are invisible to the features).

## Reproducing the results

Everything runs from the repository: the Fiji macros produce the plugin outputs
headless, one notebook computes the Python tools and draws the comparison, and
the per-image distributions are written as CSV next to the figures. See the
[benchmark README](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/benchmarking_tools)
for the file map.
