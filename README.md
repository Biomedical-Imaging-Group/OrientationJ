<p align="center"><img src="docs/assets/logo-orientationj.png" height="64" alt="OrientationJ"></p>


<h3 align="center">FIJI/IMAGEJ PLUGINS — DIRECTIONAL IMAGE ANALYSIS (2D)</h3>

<hr>

<p align="center"><sub><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a><br>AUGUST 2026</sub></p>

<hr>

![Color survey of the Tree Rings sample, sweeping the local window](docs/assets/tree-orientation.gif)

<p align="center">
<a href="https://Biomedical-Imaging-Group.github.io/OrientationJ/"><img src="https://img.shields.io/badge/Documentation-github.io/OrientationJ/-db3f2e?style=for-the-badge" alt="Documentation"></a>
<a href="https://Biomedical-Imaging-Group.github.io/OrientationJ/assets/OrientationJ_.jar"><img src="https://img.shields.io/badge/Download-OrientationJ__.jar-db3f2e?style=for-the-badge" alt="Download"></a>
</p>

OrientationJ is **open-source software for the directional analysis of 2D images**: a series of Java plugins, easy to install on [ImageJ and Fiji](https://Biomedical-Imaging-Group.github.io/OrientationJ/installation/), free under the GPL-3.0 licence — [how to cite](https://Biomedical-Imaging-Group.github.io/OrientationJ/how-to-cite/). It relies on the **gradient structure tensor**, evaluated over a small window at every pixel, from which it computes the [directional features](https://Biomedical-Imaging-Group.github.io/OrientationJ/theory/#features-and-invariants) that say how the local structure is organized: its **orientation**, the **coherency** telling whether that orientation is well defined, and how anisotropic the neighborhood is. One parameter controls the measurement, [the analysis scale σ](https://Biomedical-Imaging-Group.github.io/OrientationJ/user-guide/select-scale/), the size of that window.

OrientationJ holds several [plugins](https://Biomedical-Imaging-Group.github.io/OrientationJ/user-guide/plugins/): `Analysis` for the feature maps and the [color survey](https://Biomedical-Imaging-Group.github.io/OrientationJ/user-guide/color-survey/), `Distribution` for the angular histogram, `Vector Field` for the overlay, and `MonogenicJ` for a multiresolution analysis. Every command runs from a dialog and from an [ImageJ macro](https://Biomedical-Imaging-Group.github.io/OrientationJ/user-guide/macros/). In addition, 16 [test images](https://Biomedical-Imaging-Group.github.io/OrientationJ/test-images/) come with the documentation, together with the [assessments](https://Biomedical-Imaging-Group.github.io/OrientationJ/assessment/) built on them: the angular distribution against [other tools](https://Biomedical-Imaging-Group.github.io/OrientationJ/assessment/benchmarking/), the [accuracy of the five gradients](https://Biomedical-Imaging-Group.github.io/OrientationJ/assessment/compare-gradients/), and two Python implementations of the same tensor — a [faithful port](https://Biomedical-Imaging-Group.github.io/OrientationJ/assessment/python-port/) and a [minimal operator](https://Biomedical-Imaging-Group.github.io/OrientationJ/assessment/operator/).

## Applications

Fibers, filaments, fringes, cracks, fractures, flows, growth rings: many scientific images are made of elongated structures, and what matters about them is their direction. OrientationJ quantifies that direction everywhere in the image, and more than [300 published studies](https://Biomedical-Imaging-Group.github.io/OrientationJ/use-cases/) have used it to do so — from the actin cytoskeleton to nanofibers and bone.

## In 3D, with OrientationPy

OrientationJ measures orientation in 2D images only. For volumes, the EPFL [Center for Imaging](https://imaging.epfl.ch/) develops **[OrientationPy](https://epfl-center-for-imaging.gitlab.io/orientationpy/)**, its Python successor: the same gradient structure tensor, in 2D and in 3D, usable as a library or through its [napari plugin](https://github.com/EPFL-Center-for-Imaging/napari-orientationpy).

## Installation

**On ImageJ** — download [`OrientationJ_.jar`](https://Biomedical-Imaging-Group.github.io/OrientationJ/assets/OrientationJ_.jar) (version 2.1.0), copy it into the `plugins` folder, restart. **On Fiji** — **Help ▸ Update… ▸ Manage update sites**, tick **BIG-EPFL**, apply and restart; Fiji then keeps the plugin up to date by itself. Either way the commands appear under **Plugins ▸ OrientationJ**. Both routes, with the older versions and the build from source: [installation](https://Biomedical-Imaging-Group.github.io/OrientationJ/installation/).

## Documentation

[Installation](https://Biomedical-Imaging-Group.github.io/OrientationJ/installation/) ·
[How to use](https://Biomedical-Imaging-Group.github.io/OrientationJ/user-guide/) ·
[How to cite](https://Biomedical-Imaging-Group.github.io/OrientationJ/how-to-cite/) ·
[Theory](https://Biomedical-Imaging-Group.github.io/OrientationJ/theory/) ·
[Test images](https://Biomedical-Imaging-Group.github.io/OrientationJ/test-images/) ·
[Assessments](https://Biomedical-Imaging-Group.github.io/OrientationJ/assessment/) ·
[Use cases](https://Biomedical-Imaging-Group.github.io/OrientationJ/use-cases/) ·
[Javadoc API](https://Biomedical-Imaging-Group.github.io/OrientationJ/api/)

*The original OrientationJ website remains at [bigwww.epfl.ch/demo/orientation](https://bigwww.epfl.ch/demo/orientation/).*

## Color survey

<p align="center"><img src="docs/assets/collagen-wipe.gif" width="380" alt="Collagen fibers and their color survey"></p>

<p align="center"><sub>Collagen fibers on the left, the same field as a color survey on the right: hue gives the orientation, saturation the coherency. How that image is built, and how to read it: <a href="https://Biomedical-Imaging-Group.github.io/OrientationJ/user-guide/color-survey/">Color Survey HSB</a>.</sub></p>

## How to cite

Z. Püspöki, M. Storath, D. Sage, M. Unser, [Transforms and Operators for Directional Bioimage Analysis: A Survey](https://bigwww.epfl.ch/publications/puespoeki1603.html), *Advances in Anatomy, Embryology and Cell Biology*, vol. 219, Focus on Bio-Image Informatics, Springer, 2016.

Three further references cover the angular distribution, the local measurements and the monogenic analysis, each with its PDF and BibTeX: [how to cite](https://Biomedical-Imaging-Group.github.io/OrientationJ/how-to-cite/).

## Conditions of use

OrientationJ is free and open-source software, distributed under the [GNU General Public License v3.0](LICENSE), and provided as is, without warranty of any kind. If it contributes to work you publish, we expect a citation or an acknowledgement.
