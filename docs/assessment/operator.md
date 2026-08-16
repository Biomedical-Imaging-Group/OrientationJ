<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">

  <img class="oj-banner__logo" src="../../assets/logo-orientationj-clear.png" alt="OrientationJ">
  <div class="oj-banner__box">
    <p class="oj-banner__sub">Directional analysis of 2D images — ImageJ/Fiji plugins</p>
  </div>
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
</div>

## OrientationJ Operator

How short can an honest gradient-structure-tensor operator be? [gst_operator.py](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/gst_operator/gst_operator.py) computes the three OrientationJ features with **separable Gaussian derivatives in the space domain** — no Fourier transform, no spline prefilter, no boundary handling beyond mirroring — in sixty lines of NumPy:

```python
from gst_operator import features
C, E, theta = features(image, sigma=2.0, sigma_gradient=1.0)
```

Two widths control everything: `sigma_gradient` for the derivative, `sigma` for the tensor window. `C` is the coherency in [0, 1], `E` the gradient energy, `theta` the orientation of the structures in radians, counter-clockwise from the horizontal — the OrientationJ convention.

## The three features

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/gst_operator/operator-features.png">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/gst_operator/operator-features.png)

## Accuracy against analytic truth

Yes, wherever the answer is known analytically. The smoothing kernel sums to 1 and the derivative kernel differentiates exactly (−Σ x·d(x) = 1) to machine precision; sinusoidal gratings come back at exactly their imposed angle; and on the radial chirp, whose tangential orientation is known at every pixel, the median error is **0.001°**. At σ_G = 1 the minimal operator is in fact an order of magnitude *more* accurate on this smooth band-limited pattern than the plugin's cubic-spline gradient.

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/gst_operator/operator-accuracy.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/gst_operator/operator-accuracy.png)

σ_G is a choice, not a defect: 0.5 truncates the kernel too aggressively, 2 flattens fine detail, 1 is the sweet spot for this pattern.

## Agreement with the plugin

Compared against the [Python port](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/orientationj_python_port), which reproduces the Java plugin bit for bit, at the same tensor window σ = 2 so that the gradient is the only difference left:

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/gst_operator/operator-agreement.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/gst_operator/operator-agreement.png)

On the smooth analytic patterns the two are indistinguishable — 0.006° on the rings, 0.009° on the spiral, 0.012° on the chirp. On real textures they differ by a few degrees, from 1.4° on the wood section to 5.4° on the nanofibers, and by 12.7° on isotropic noise where there is no orientation to agree on. That is the Gaussian derivative averaging what the spline derivative still resolves: the disagreement grows as structures approach the pixel scale. The energy maps stay strongly correlated throughout (0.64 to 0.94).

The same trade-off, measured across all five OrientationJ gradients, is in the [gradient assessment](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/gradients).

## Files

| file | content |
|---|---|
| [gst_operator.py](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/gst_operator/gst_operator.py) | the operator: Gaussian kernels, gradient, structure tensor, features |
| [gst_operator.ipynb](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/gst_operator/gst_operator.ipynb) | the validation: exactness, agreement with the plugin, accuracy versus structure size |
| [agreement.csv](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/gst_operator/agreement.csv) | per-image angular difference and feature correlations |
