<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">

  <img class="oj-banner__logo" src="../../assets/logo-orientationj-clear.png" alt="OrientationJ">
  <div class="oj-banner__box">
    <p class="oj-banner__sub"><span class="oj-banner__kind">Fiji/ImageJ plugins</span> — Directional Image Analysis (2D)</p>
  </div>
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
</div>

# Structure tensor vs nematic tensor

Three estimators of the local orientation, built from the same gradient \(\mathbf g = \nabla f\) (central finite differences), its unit vector \(\mathbf n = \mathbf g / \lVert\mathbf g\rVert\), and the same Gaussian window \(\langle\cdot\rangle\) of width \(\sigma_{\mathrm{agg}}\):

- **naive mean** — \(\hat\theta = \langle\theta\rangle\), the arithmetic mean of the pixel angles \(\theta = \operatorname{atan2}(g_y, g_x) + 90^\circ\); no confidence;
- **nematic tensor** — \(Q = \langle \mathbf n\,\mathbf n^{\mathsf T}\rangle\), every pixel weighs 1; confidence \(S = \lambda_1 - \lambda_2\), the nematic order;
- **structure tensor** — \(J = \langle \mathbf g\,\mathbf g^{\mathsf T}\rangle\), every pixel weighs \(\lVert\mathbf g\rVert^2\); confidence \(C = (\lambda_1 - \lambda_2)/(\lambda_1 + \lambda_2)\), the coherency; energy \(E = \operatorname{tr} J\).

For both tensors the orientation is the eigenvector of \(\lambda_1\), \(\hat\theta = \tfrac12 \operatorname{atan2}(2M_{xy},\, M_{xx} - M_{yy}) + 90^\circ\).

## Test image

Concentric sine rings of amplitude \(A = 0.5\) and period \(T = 20\) px in an annulus, whose reference orientation is tangential and known at every pixel, degraded by **saturation** (a ratio \(\rho_{\mathrm{sat}}\) of clipped pixels), a vertical **intensity ramp** of slope \(\beta_{\mathrm{ramp}}\), **Gaussian noise** \(\sigma_{\mathrm{noise}}\), **shot noise** \(\lambda_{\mathrm{shot}}\) and **speckle noise** \(\rho_{\mathrm{sp}}\). The error is the axial difference to the reference inside the evaluation mask, the flat part of the annulus, where every orientation is equally represented.

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/structure_nematic/structure-nematic-abstract.png">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/structure_nematic/structure-nematic-abstract.png)

<p class="oj-caption">The medium level (ρ<sub>sat</sub> = 0.6, σ<sub>noise</sub> = 0.015, β<sub>ramp</sub> = 0.02/px), σ<sub>agg</sub> = 7 px. Top: input with the mask outlined, reference orientation, histograms of the estimated orientation in the mask with the mean absolute error of each estimator. Bottom: the estimated fields as HSB maps — hue = orientation, saturation = confidence (S, C, or 1 for the naive mean), brightness = energy.</p>

## Experiments

The four degradation levels (none, low, medium, high) with saturation, noise and ramp together; then one sweep per degradation, the others at their low level; then a sweep of \(\sigma_{\mathrm{agg}}\) from 2 to 16 px against the ring period.

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/structure_nematic/structure-nematic-histograms.png">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/structure_nematic/structure-nematic-histograms.png)

<p class="oj-caption">Orientation histograms in the evaluation mask at the four levels, σ<sub>agg</sub> = 7 px; the reference histogram is flat.</p>

## Files

| file | content |
|---|---|
| [ring_experiment_lib.py](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/structure_nematic/ring_experiment_lib.py) | the ring image and its degradations, the three estimators, the evaluation, the drawing helpers |
| [structure_nematic_experiment.ipynb](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/structure_nematic/structure_nematic_experiment.ipynb) | the experiment: levels, sweeps of each degradation, sweep of the window |
| [create_abstract_figure.ipynb](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/structure_nematic/create_abstract_figure.ipynb) | the summary figure |
| [images/](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/structure_nematic/images) | the ring image at the four levels, 32-bit TIFF |
