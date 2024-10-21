OrientationJ
============

[**ImageJ plugins for directional analysis in images**](https://bigwww.epfl.ch/demo/orientation/)

_Written by Daniel Sage at the Biomedical Image Group (BIG), EPFL, Switzerland_

## Outline
The aim is to characterize the orientation and isotropy properties of a region of interest (ROI) in an image, based on the evaluation of the gradient structure tensor in a local neighborhood. 
The theoretical background is fully described in this [PDF document](https://bigwww.epfl.ch/demo/orientationj/theoretical-background.pdf). 
The software OrientationJ automates the orientation analysis. 

It is a series of Java plugins for ImageJ. 
OrientationJ has four functionalities: 
* performing a [visual](https://bigwww.epfl.ch/demo/orientationj/#analysis) representation of the orientation of a image;
* creation of a [vector field](https://bigwww.epfl.ch/demo/orientationj/#vector) map;
* plotting the [distribution](https://bigwww.epfl.ch/demo/orientationj/#dist) of orientations;
* detection of [keypoints](https://bigwww.epfl.ch/demo/orientationj/#corner)</a> (Harris Corner). 

OrientationJ has also others tools: 
* manual [measurement](https://bigwww.epfl.ch/demo/orientationj/#measure) of the orientation and coherency in a ROI;
* computation of the dominant orientation;
* alignment of images based on the gradient structure tensor;
* plugins to generate test images (chirp).

From the version 2.0.7. OrientationJ includes a plugin for a multiresolution wavelet-based 
[monogenic](https://bigwww.epfl.ch/demo/monogenicj/) analyse of 2D images. 

## References
* Reference on the method: Z. Püspöki, M. Storath, D. Sage, M. Unser, [Transforms and Operators for Directional Bioimage Analysis: A Survey](https://bigwww.epfl.ch/publications/puespoeki1603.html) Advances in Anatomy, Embryology and Cell Biology, vol. 219, Focus on Bio-Image Informatics, Springer International Publishing, 2016.
* Reference on the monogenic analysis: M. Unser, D. Sage, D. Van De Ville, [Multiresolution Monogenic Signal Analysis Using the Riesz-Laplace Wavelet Transform](https://bigwww.epfl.ch/publications/unser0907.html) IEEE Transactions on Image Processing, 2009.
* Reference on the angular distribution: R. Rezakhaniha, A. Agianniotis, J.T.C. Schrauwen, A. Griffa, D. Sage, C.V.C. Bouten, F.N. van de Vosse, M. Unser, N. Stergiopulos, [Experimental Investigation of Collagen Waviness and Orientation in the Arterial Adventitia Using Confocal Laser Scanning Microscopy](https://bigwww.epfl.ch/publications/rezakhaniha1201.html) Biomechanics and Modeling in Mechanobiology, vol. 11, no. 3-4, pp. 461-473, 2012.
* Reference on the local measurements: E. Fonck, G.G. Feigl, J. Fasel, D. Sage, M. Unser, D.A. Rüfenacht, N. Stergiopulos, [Effect of Aging on Elastin Functionality in Human Cerebral Arteries](https://bigwww.epfl.ch/publications/fonck0901.html) Stroke, vol. 40, no. 7, pp. 2552-2556, July 2009.
