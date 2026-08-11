<p align="center"><img src="docs/assets/logo-orientationj.png" height="64" alt="OrientationJ"></p>

<h3 align="center">Directional analysis of 2D images — ImageJ/Fiji plugins</h3>

<hr>

<p align="center"><sub><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a><br>AUGUST 2026</sub></p>

<hr>

![Color survey of the Tree Rings sample, sweeping the local window](docs/assets/tree-orientation.gif)

<p align="center">
<a href="https://Biomedical-Imaging-Group.github.io/OrientationJ/"><img src="https://img.shields.io/badge/Documentation-github.io/OrientationJ/-db3f2e?style=for-the-badge" alt="Documentation"></a>
<a href="https://Biomedical-Imaging-Group.github.io/OrientationJ/assets/OrientationJ_.jar"><img src="https://img.shields.io/badge/Download-OrientationJ__.jar-db3f2e?style=for-the-badge" alt="Download"></a>
</p>

## Directional image analysis in 2D and 3D

The direction of structures often carries the essential information in an image: collagen fibers realign as tissue remodels, growth rings record the seasons, filaments and fringes reveal how a material was formed. Measuring that direction — and how consistently it holds from one place to the next — turns a picture into numbers that can be compared, reported and tested. In **2D** those numbers are extracted from images, plane by plane; in **3D** the same reasoning extends to volumes, where a structure can also tilt through the depth of the sample. The [EPFL Center for Imaging](https://imaging.epfl.ch/), together with the [Biomedical Imaging Group](https://bigwww.epfl.ch/), contributes two complementary open-source tools, one for each case.

**1. In 2D — [OrientationJ](https://Biomedical-Imaging-Group.github.io/OrientationJ/), a suite of Java plugins for ImageJ/Fiji.** The method is described in [Püspöki et al., Springer, 2016](https://bigwww.epfl.ch/publications/puespoeki1603.html) and has been adopted across many fields — see [In the literature](https://Biomedical-Imaging-Group.github.io/OrientationJ/literature.html). The original OrientationJ website remains at [bigwww.epfl.ch/demo/orientation](https://bigwww.epfl.ch/demo/orientation/).

**2. In 3D — [OrientationPy](https://epfl-center-for-imaging.gitlab.io/orientationpy/), a Python package.** Developed by the Center for Imaging as the Pythonic successor of OrientationJ, it measures grayscale orientations in 2D images *and* in 3D volumes, which opens up applications where the direction of structures matters through the depth of the sample. Available as a [library](https://gitlab.com/epfl-center-for-imaging/orientationpy) and as a [napari plugin](https://github.com/EPFL-Center-for-Imaging/napari-orientationpy) for interactive work.

## Documentation of OrientationJ

[Installation](https://Biomedical-Imaging-Group.github.io/OrientationJ/installation/) ·
[How to use](https://Biomedical-Imaging-Group.github.io/OrientationJ/how-to-use/) ·
[Theory](https://Biomedical-Imaging-Group.github.io/OrientationJ/theory/) ·
[Test images](https://Biomedical-Imaging-Group.github.io/OrientationJ/test-images/) ·
[Benchmarking](https://Biomedical-Imaging-Group.github.io/OrientationJ/benchmarking/) ·
[How to cite](https://Biomedical-Imaging-Group.github.io/OrientationJ/) ·
[In the literature](https://Biomedical-Imaging-Group.github.io/OrientationJ/literature.html) ·
[Javadoc API](https://Biomedical-Imaging-Group.github.io/OrientationJ/api/) ·
[Python port](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/orientationj_python_port)

## Outline of OrientationJ

OrientationJ measures local directionality everywhere in the image, ... extract measure, qtogether with how consistently it holds and how strongly it stands out from the background.

At every pixel the plugins evaluate the **gradient structure tensor** over a small window and extract the **orientation** of the local structure, the **coherency** telling whether that orientation is well defined or the neighborhood is isotropic, and the **energy** telling whether there is any structure at all. From these come color surveys that paint the orientation over the image, vector fields ready for figures, angular histograms and per-region measurements — results to look at and numbers to report.

The suite covers the whole workflow. `Analysis` produces the feature maps and the color survey; `Distribution` turns them into an angular histogram; `Vector Field` overlays a readable field of directions; `Measure` and `Dominant Direction` report numbers for a selection or a whole image; `Clustering` and `Horizontal Alignment` group and straighten oriented regions; `Corner Harris` detects keypoints from the same tensor; and `MonogenicJ` extends the analysis to a multiresolution monogenic representation. Every command runs from a dialog and from an ImageJ macro, and all of them share the same two core parameters: the analysis scale σ and the gradient mode.

Two examples from the [test-images](orientationj-test-images/):

[<img src="orientationj-test-images/results/cell_aemisegger.png">](orientationj-test-images/results/cell_aemisegger.png)

[<img src="orientationj-test-images/results/synthetic_chirp_1024.png">](orientationj-test-images/results/synthetic_chirp_1024.png)

## Install

Download [`OrientationJ_.jar`](https://Biomedical-Imaging-Group.github.io/OrientationJ/assets/OrientationJ_.jar) (version 2.1.0) and copy it into the `plugins` folder of [ImageJ](https://imagej.net/ij/) or [Fiji](https://fiji.sc/), then restart — the commands appear under **Plugins ▸ OrientationJ**. Details: [installation guide](https://Biomedical-Imaging-Group.github.io/OrientationJ/installation/).
