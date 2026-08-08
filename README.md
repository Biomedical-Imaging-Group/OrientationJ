# OrientationJ

**ImageJ and Fiji plugins for directional image analysis** — local orientation,
energy and coherency from the gradient structure tensor.

*Written by Daniel Sage, Biomedical Imaging Group (BIG), EPFL, Switzerland —
[bigwww.epfl.ch/demo/orientation](https://bigwww.epfl.ch/demo/orientation/)*

[![Download](https://img.shields.io/badge/%E2%AC%87%EF%B8%8F_Download-OrientationJ__.jar-4878a8?style=for-the-badge)](https://github.com/Biomedical-Imaging-Group/OrientationJ/releases/latest/download/OrientationJ_.jar)
[![Documentation](https://img.shields.io/badge/%F0%9F%93%96_Documentation-website-3a3a37?style=for-the-badge)](https://Biomedical-Imaging-Group.github.io/OrientationJ/)

| | |
|---|---|
| 📖 **[Documentation](https://Biomedical-Imaging-Group.github.io/OrientationJ/)** | installation, plugin modes, theory, how to cite |
| ⬇️ **[Releases](https://github.com/Biomedical-Imaging-Group/OrientationJ/releases)** | the plugin JAR, ready for the ImageJ/Fiji `plugins` folder |
| 📐 **[Theoretical background](docs/assets/theoretical-background.pdf)** | the gradient structure tensor, features and invariants (PDF) |
| 🧪 **[Test images](orientationj-test-images/)** | 16 images with masks and a full result gallery |
| 🐍 **[Python port](orientationj-python-port/)** | faithful NumPy reimplementation of the analysis, distribution and vector field |
| 📊 **[Benchmarks](assessment/benchmarking_tools/)** | cross-tool comparison of the orientation distribution (7 tools) |
| 🔬 **[GST operator](assessment/operator_gst/)** | minimal forward model and blind inversion of (C, E, θ) |
| 📚 **[In the literature](orientationj-in-scientific-literature/)** | how published papers use and cite OrientationJ |
| 📜 **[Version history](docs/HISTORY.md)** | releases and changes |

## Outline

The aim is to characterize the orientation and isotropy properties of a region of
interest (ROI) in an image, based on the evaluation of the gradient structure tensor
in a local neighborhood. OrientationJ automates this orientation analysis with a
series of Java plugins for ImageJ and Fiji:

* [visual representation](https://bigwww.epfl.ch/demo/orientationj/#analysis) of the orientation of an image (color survey);
* [vector field](https://bigwww.epfl.ch/demo/orientationj/#vector) map on a grid;
* [distribution](https://bigwww.epfl.ch/demo/orientationj/#dist) of orientations;
* detection of [keypoints](https://bigwww.epfl.ch/demo/orientationj/#corner) (Harris corner).

Other tools: manual [measurement](https://bigwww.epfl.ch/demo/orientationj/#measure)
of orientation and coherency in a ROI, computation of the dominant orientation,
alignment of images based on the gradient structure tensor, and generators of test
images (chirp).

Since version 2.0.7, OrientationJ also includes a plugin for the multiresolution
wavelet-based [monogenic](https://bigwww.epfl.ch/demo/monogenicj/) analysis of 2D
images.

## Install

Download [`OrientationJ_.jar`](https://github.com/Biomedical-Imaging-Group/OrientationJ/releases/latest/download/OrientationJ_.jar),
copy it into the `plugins` folder of ImageJ or Fiji, restart — the commands appear
under **Plugins ▸ OrientationJ**. Details: [installation guide](https://Biomedical-Imaging-Group.github.io/OrientationJ/installation/).

## References

* Reference on the method: Z. Püspöki, M. Storath, D. Sage, M. Unser, [Transforms and Operators for Directional Bioimage Analysis: A Survey](https://bigwww.epfl.ch/publications/puespoeki1603.html), Advances in Anatomy, Embryology and Cell Biology, vol. 219, Focus on Bio-Image Informatics, Springer, 2016.
* Reference on the monogenic analysis: M. Unser, D. Sage, D. Van De Ville, [Multiresolution Monogenic Signal Analysis Using the Riesz-Laplace Wavelet Transform](https://bigwww.epfl.ch/publications/unser0907.html), IEEE Transactions on Image Processing, 2009.
* Reference on the angular distribution: R. Rezakhaniha, A. Agianniotis, J.T.C. Schrauwen, A. Griffa, D. Sage, C.V.C. Bouten, F.N. van de Vosse, M. Unser, N. Stergiopulos, [Experimental Investigation of Collagen Waviness and Orientation in the Arterial Adventitia Using Confocal Laser Scanning Microscopy](https://bigwww.epfl.ch/publications/rezakhaniha1201.html), Biomechanics and Modeling in Mechanobiology, vol. 11, no. 3-4, 2012.
* Reference on the local measurements: E. Fonck, G.G. Feigl, J. Fasel, D. Sage, M. Unser, D.A. Rüfenacht, N. Stergiopulos, [Effect of Aging on Elastin Functionality in Human Cerebral Arteries](https://bigwww.epfl.ch/publications/fonck0901.html), Stroke, vol. 40, no. 7, 2009.
