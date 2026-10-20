<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">

  <img class="oj-banner__logo" src="../../assets/logo-orientationj-clear.png" alt="OrientationJ">
  <div class="oj-banner__box">
    <p class="oj-banner__sub"><span class="oj-banner__kind">Fiji/ImageJ plugins</span> — Directional Image Analysis (2D)</p>
  </div>
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
</div>

# Aggregation window

OrientationJ aggregates the gradient of many pixels before it reads an orientation: over the Gaussian window σ of the structure tensor in *Analysis*, and over the cell of the grid in *Vector Field*. This assessment runs the plugin (version 2.2.0, cubic-spline gradient) on four images of the [test set](../test-images.md) and sweeps the two sizes:

1. **the structure-tensor window** σ = 1, 2, 3, 4, 5, 6, 8, 10 px, with *Analysis* — the orientation map (degrees) and the color survey at each σ;
2. **the vector-field grid** = 8, 12, 16, 24, 32, 48, 64 px, with *Vector Field* at σ = 1 and the nematic aggregation of the cell (the default since 2.2.0) — the OJ-Vector-Field table at each grid size.

The σ parameter itself is described on the [Local window](../user-guide/local-window.md) page, the grid and its aggregation on the [plugins](../user-guide/plugins.md#vector-field) page.

## The four images

| image | size | content |
|---|---|---|
| `synthetic_wave_512` | 512 × 512 | three superposed sinusoidal waves of periods 2, 8 and 64 px at 5°, 45° and 85°, plus uniform noise, in a round window |
| `synthetic_chirp_1024` | 1024 × 1024 | radial chirp, the frequency growing from the center to the border; the orientation is tangential everywhere |
| `collagen` | 512 × 512 | collagen fibers (real) |
| `cell_aemisegger` | 728 × 728 | fluorescence cell, actin fibers (real) |

The orientation distributions count the pixels inside the [structure mask](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/test-images/masks) of each image only.

## The two macros

Every (image, σ) and every (image, grid) is one call of the plugin, as the macro recorder writes it:

```javascript
run("OrientationJ Analysis", "tensor=" + sigma + " gradient=0 "
    + "orientation=on color-survey=on radian=off ");

run("OrientationJ Vector Field", "tensor=1 gradient=0 vectorgrid=" + grid
    + " vectoraggregation=nematic vectorscale=100 vectortype=0 "
    + "vectortable=on vectoroverlay=off ");
```

`gradient=0` is the cubic-spline gradient. In the vector field, `vectortype=0` and `vectorscale=100` give every vector the same length, that of one cell; the orientation of each cell is read from the table, not from the overlay.

## Results

### Orientation distribution versus σ

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/distributions-vs-sigma.png">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/distributions-vs-sigma.png)

<p class="oj-caption">One panel per image, one curve per σ, from light blue (σ = 1) to dark blue (σ = 10); 180 bins of 1° over [−90°, 90°], normalized to probability per bin, pixels inside the structure mask only. On the wave, every σ gives a single peak at 45°, the orientation of the 8 px wave, which narrows as σ grows. On the chirp, the distribution is flat at 1/180, with three spikes at −45°, 0° and 45°. On collagen, the broad maximum lies between 30° and 60° at every σ. On the cell, the peak near −5° sharpens from σ = 1 to σ = 10.</p>

### Color survey versus σ

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/surveys-strip.png">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/surveys-strip.png)

<p class="oj-caption">The color survey of the plugin at σ = 1, 4 and 10: hue = orientation, saturation = coherency, brightness constant (the default of the macro, which has no <code>bri</code> key). Below, the same survey animated over the eight σ, 0.7 s per frame.</p>

<p>
<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/synthetic_wave_512-survey.gif" width="49%" alt="synthetic_wave_512, color survey over σ">
<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/synthetic_chirp_1024-survey.gif" width="49%" alt="synthetic_chirp_1024, color survey over σ">
</p>
<p>
<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/collagen-survey.gif" width="49%" alt="collagen, color survey over σ">
<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/cell_aemisegger-survey.gif" width="49%" alt="cell_aemisegger, color survey over σ">
</p>

### Vector field versus grid size

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/vectorfields-overview.png">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/vectorfields-overview.png)

<p class="oj-caption">The vector field at grid sizes 8, 16, 32 and 64 px (σ = 1, nematic aggregation): one segment per cell, as long as the cell, drawn from the table and colored by its orientation with the hue wheel of the survey. Below, the field animated over the seven grid sizes 8, 12, 16, 24, 32, 48 and 64 px, 0.8 s per frame.</p>

<p>
<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/synthetic_wave_512-vectors.gif" width="49%" alt="synthetic_wave_512, vector field over the grid size">
<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/synthetic_chirp_1024-vectors.gif" width="49%" alt="synthetic_chirp_1024, vector field over the grid size">
</p>
<p>
<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/collagen-vectors.gif" width="49%" alt="collagen, vector field over the grid size">
<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/assessment/aggregation_window/cell_aemisegger-vectors.gif" width="49%" alt="cell_aemisegger, vector field over the grid size">
</p>

## Files

| file | content |
|---|---|
| [macro-aggregation-analysis.ijm](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/aggregation_window/macro-aggregation-analysis.ijm) | Fiji macro — OrientationJ Analysis (spline) at the eight σ on the four images; saves the orientation map (32-bit TIFF, degrees) and the color survey (PNG) of each run |
| [macro-aggregation-vectorfield.ijm](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/aggregation_window/macro-aggregation-vectorfield.ijm) | Fiji macro — OrientationJ Vector Field (spline, σ = 1, nematic) at the seven grid sizes on the four images; saves the OJ-Vector-Field table (CSV) of each run |
| [aggregation_window.ipynb](https://github.com/Biomedical-Imaging-Group/OrientationJ/blob/master/assessment/aggregation_window/aggregation_window.ipynb) | bins the orientation maps inside the masks, draws the figures, assembles the GIFs |

```
Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-aggregation-analysis.ijm
Fiji.app/Contents/MacOS/ImageJ-macosx -macro macro-aggregation-vectorfield.ijm quit
jupyter nbconvert --to notebook --execute --inplace aggregation_window.ipynb
```

The first macro runs headless. The second needs the Fiji GUI, since the results table cannot be saved without a window manager; the `quit` argument makes Fiji exit when it is done. The plugin outputs go to `results/fiji/`, the figures to `results/`; neither folder is tracked. The notebook needs `numpy`, `pandas`, `matplotlib`, `tifffile` and `Pillow`.
