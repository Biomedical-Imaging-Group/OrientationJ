<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">

  <a class="oj-banner__epfl" href="https://imaging.epfl.ch/" title="EPFL Center for Imaging"><img src="../../assets/center-for-imaging.svg" alt="EPFL Center for Imaging"></a>
  <img class="oj-banner__logo" src="../../assets/logo-orientationj-clear.png" alt="OrientationJ">
  <div class="oj-banner__box">
    <p class="oj-banner__sub">Directional analysis of 2D images — ImageJ/Fiji plugins</p>
  </div>
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
</div>

# OrientationJ Operator

How short can an honest gradient-structure-tensor operator be? [gst_operator.py](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/gst_operator/gst_operator.py) computes the three OrientationJ features with **separable Gaussian derivatives in the space domain** — no Fourier transform, no spline prefilter, no boundary handling beyond mirroring — in sixty lines of NumPy:

```python
from gst_operator import features
C, E, theta = features(image, sigma=2.0, sigma_gradient=1.0)
```

Two widths control everything: `sigma_gradient` for the derivative, `sigma` for the tensor window. `C` is the coherency in [0, 1], `E` the gradient energy, `theta` the orientation of the structures in radians, counter-clockwise from the horizontal — the OrientationJ convention.

## The three features it returns

Run on one image, the operator returns the coherency, the energy and the orientation — the same three maps the *Analysis* command produces, from the same tensor:

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/gst_operator/operator-features.png">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/gst_operator/operator-features.png)

## Files

| file | content |
|---|---|
| [gst_operator.py](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/gst_operator/gst_operator.py) | the operator: Gaussian kernels, gradient, structure tensor, features |
| [gst_operator.ipynb](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/gst_operator/gst_operator.ipynb) | the validation: exactness, agreement with the plugin, accuracy versus structure size |
| [agreement.csv](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/gst_operator/agreement.csv) | per-image angular difference and feature correlations |
