<div class="oj-banner" markdown>

![OrientationJ](assets/logo-orientationj.png){ .oj-logo }

<p class="oj-subtitle">Local directional analysis of 2D images — ImageJ/Fiji plugins</p>

<hr>

<p class="oj-author"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>

<p class="oj-date">August 2026</p>

<hr>

![Color survey of the Tree Rings sample, sweeping the local window](assets/tree-orientation.gif){ .oj-tree }

</div>

# OrientationJ

OrientationJ is a suite of ImageJ and Fiji plugins that measure the **local orientation**, **energy** and **coherency** of structures in an image, based on the gradient structure tensor evaluated in a sliding local neighborhood. It answers questions of the form: which way do these fibers run, how strongly are they aligned, and how does that alignment vary across the field of view. The suite contains:

- **Analysis** — color-coded orientation, coherency, energy, directionality and anisotropy maps, and the color survey (hue = orientation, saturation = coherency, brightness = image);
- **Distribution** — the histogram of local orientations, with coherency and energy thresholds;
- **Measure** — orientation, coherency and energy inside selected ROIs;
- **Dominant Direction** — a single dominant angle and coherency for the whole image;
- **Vector Field** — orientation vectors overlaid on a grid;
- **Corner Harris** — Harris keypoint detection, built on the same tensor;
- **MonogenicJ** — multiresolution wavelet-based monogenic analysis of 2D images;
- utilities for manual measurement, structure-tensor-based image alignment, and test-image generation (chirp).

Each command is described in [How to use](how-to-use.md), and all of them are scriptable from ImageJ macros. The animated banner above sweeps the size σ of the structure-tensor window on the classic *Tree Rings* sample — small windows follow every detail, large ones summarize the trend; the macro that produces it is [tree-orientation.txt](assets/tree-orientation.txt).

## Beyond the plugin

- **[Theory](theory.md)**: the structure tensor, its features and invariants, with the full derivation.
- **[Benchmarking](benchmarking.md)**: the orientation distribution of OrientationJ compared with six other tools on a common dataset.
- **[Python port](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/orientationj_python_port)**: the faithful NumPy reimplementation of the plugin — spline gradient, color survey, distribution, vector field.
- **[GST operator](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/gst_operator)**: the minimal Python version — a `forward` model (C, E, orientation) and its blind `inverse`.
- **[In the literature](https://Biomedical-Imaging-Group.github.io/OrientationJ/literature.html)**: an annotated table of published papers that use and cite OrientationJ.

## How to cite

If OrientationJ contributed to your results, cite the publication matching what you used — the method, the angular distribution, the local measurements, or the monogenic analysis:

> Püspöki Z, Storath M, Sage D, Unser M (2016). Transforms and Operators for Directional Bioimage Analysis: A Survey. *Advances in Anatomy, Embryology and Cell Biology*, vol. 219. [doi:10.1007/978-3-319-28549-8_3](https://doi.org/10.1007/978-3-319-28549-8_3)

> Rezakhaniha R, Agianniotis A, Schrauwen JTC, Griffa A, Sage D, Bouten CVC, van de Vosse FN, Unser M, Stergiopulos N (2012). Experimental Investigation of Collagen Waviness and Orientation in the Arterial Adventitia Using Confocal Laser Scanning Microscopy. *Biomechanics and Modeling in Mechanobiology* 11(3–4): 461–473. [doi:10.1007/s10237-011-0325-z](https://doi.org/10.1007/s10237-011-0325-z)

> Fonck E, Feigl GG, Fasel J, Sage D, Unser M, Rüfenacht DA, Stergiopulos N (2009). Effect of Aging on Elastin Functionality in Human Cerebral Arteries. *Stroke* 40(7): 2552–2556. [doi:10.1161/STROKEAHA.108.528091](https://doi.org/10.1161/STROKEAHA.108.528091)

> Unser M, Sage D, Van De Ville D (2009). Multiresolution Monogenic Signal Analysis Using the Riesz–Laplace Wavelet Transform. *IEEE Transactions on Image Processing* 18(11): 2402–2418. [doi:10.1109/TIP.2009.2027628](https://doi.org/10.1109/TIP.2009.2027628)

An annotated table of the publications that use and cite OrientationJ is on the [In the literature](https://Biomedical-Imaging-Group.github.io/OrientationJ/literature.html) page.

## Other tools

[OrientationPy](https://gitlab.com/epfl-center-for-imaging/orientationpy) — the Python successor of OrientationJ, 2D and 3D · [Directionality](https://imagej.net/plugins/directionality) (Fiji) · [FiberFit](https://doi.org/10.1007/s10237-016-0776-3) · [CT-FIRE](https://doi.org/10.1117/1.JBO.19.1.016007) · [FiberO](https://doi.org/10.3389/fbioe.2024.1497837) · questions: [image.sc forum](https://forum.image.sc/)

The release notes are in the [version history](HISTORY.md).
