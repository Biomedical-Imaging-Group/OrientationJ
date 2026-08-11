<div class="oj-banner" markdown>

![OrientationJ](assets/logo-orientationj.png){ .oj-logo }

<p class="oj-subtitle">Directional analysis of 2D images — ImageJ/Fiji plugins</p>

<hr>

<p class="oj-author"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>

<p class="oj-date">August 2026</p>

<hr>

![Color survey of the Tree Rings sample, sweeping the local window](assets/tree-orientation.gif){ .oj-tree }

</div>

# OrientationJ

Fibers, filaments, fringes, crack,fractures, flows, growth rings: many images are made of elongated structures, and what matters about them is not their intensity but the direction they follow. OrientationJ measures that direction everywhere in the image, together with how consistently it holds and how strongly it stands out from the background.

At every pixel the plugins evaluate the **gradient structure tensor** over a small window and extract the **orientation** of the local structure, the **coherency** telling whether that orientation is well defined or the neighborhood is isotropic, and the **energy** telling whether there is any structure at all. From these come color surveys, vector fields, angular histograms and per-region measurements — figures to look at and numbers to report.

<div class="oj-compare">
<img src="assets/collagen-input.jpg" alt="Collagen fibers, original image">
<img src="assets/collagen-survey.jpg" alt="Collagen fibers, color survey">
<span class="oj-compare-line"></span>
<input type="range" min="0" max="100" value="50" aria-label="Reveal the color survey">
</div>

<p class="oj-caption">Drag the handle: collagen fibers on the left, the same field as a color survey on the right — hue gives the orientation, saturation the coherency.</p>

The suite covers the whole workflow. **Analysis** produces the feature maps and the color survey; **Distribution** turns them into an angular histogram; **Vector Field** overlays a readable field of directions; **Measure** and **Dominant Direction** report numbers for a selection or a whole image; **Clustering** and **Horizontal Alignment** group and straighten oriented regions; **Corner Harris** detects keypoints from the same tensor; **Test Image** generates chirps and stacks to calibrate on; and **MonogenicJ** extends the analysis to a multiresolution monogenic representation. Every command runs from a dialog and from an ImageJ macro, and all of them share the same two core settings — the analysis scale σ and the gradient — described in [How to use](how-to-use.md). The animated banner above sweeps σ on the classic *Tree Rings* sample: small windows follow every detail, large ones summarize the trend ([macro](assets/tree-orientation.txt)).

The mathematics behind the measurement, from the weighted inner product to the tensor invariants, is derived in [Theory](theory.md). The orientation distribution of OrientationJ is compared with six other tools on a common dataset in [Benchmarking](benchmarking.md), and the images used throughout are published in [Test images](test-images.md). Two Python versions accompany the plugin: the faithful [Python port](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/orientationj_python_port), which reproduces the plugin bit for bit, and the minimal [GST operator](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/gst_operator), a `forward` model with its blind `inverse`.

If OrientationJ contributed to your results, please cite the publication matching what you used — the method, the angular distribution, the local measurements, or the monogenic analysis:

> Püspöki Z, Storath M, Sage D, Unser M (2016). Transforms and Operators for Directional Bioimage Analysis: A Survey. *Advances in Anatomy, Embryology and Cell Biology*, vol. 219, Focus on Bio-Image Informatics, Springer. [doi:10.1007/978-3-319-28549-8_3](https://doi.org/10.1007/978-3-319-28549-8_3)

> Rezakhaniha R, Agianniotis A, Schrauwen JTC, Griffa A, Sage D, Bouten CVC, van de Vosse FN, Unser M, Stergiopulos N (2012). Experimental Investigation of Collagen Waviness and Orientation in the Arterial Adventitia Using Confocal Laser Scanning Microscopy. *Biomechanics and Modeling in Mechanobiology* 11(3–4): 461–473. [doi:10.1007/s10237-011-0325-z](https://doi.org/10.1007/s10237-011-0325-z)

> Fonck E, Feigl GG, Fasel J, Sage D, Unser M, Rüfenacht DA, Stergiopulos N (2009). Effect of Aging on Elastin Functionality in Human Cerebral Arteries. *Stroke* 40(7): 2552–2556. [doi:10.1161/STROKEAHA.108.528091](https://doi.org/10.1161/STROKEAHA.108.528091)

> Unser M, Sage D, Van De Ville D (2009). Multiresolution Monogenic Signal Analysis Using the Riesz–Laplace Wavelet Transform. *IEEE Transactions on Image Processing* 18(11): 2402–2418. [doi:10.1109/TIP.2009.2027628](https://doi.org/10.1109/TIP.2009.2027628)

An annotated table of the publications that use and cite OrientationJ is on the [In the literature](https://Biomedical-Imaging-Group.github.io/OrientationJ/literature.html) page, and the release notes are in the [version history](HISTORY.md).
