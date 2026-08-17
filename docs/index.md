<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">

  <img class="oj-banner__logo" src="assets/logo-orientationj-clear.png" alt="OrientationJ">
  <div class="oj-banner__box">
    <p class="oj-banner__sub">Directional analysis of 2D images — ImageJ/Fiji plugins</p>
  </div>
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
</div>


# OrientationJ

OrientationJ is open-source software for the directional analysis of 2D images: a series of Java plugins, easy to install on [ImageJ and Fiji](installation/index.md), free under the GPL-3.0 licence — [how to cite](how-to-cite.md). For volumetric data, a Python successor works in 2D and in 3D, [OrientationPy](https://epfl-center-for-imaging.gitlab.io/orientationpy/).

It relies on the [gradient structure tensor](theory/index.md#gradient-structure-tensor), evaluated over a small window at every pixel, from which it computes the [directional features](theory/index.md#features-and-invariants) that say how the local structure is organized: its **orientation**, the **coherency** telling whether that orientation is well defined, the **energy** telling whether there is any structure at all, and the **anisotropy** of the neighborhood. One parameter controls the measurement, [the analysis scale σ](user-guide/select-scale.md), the size of that window.

OrientationJ holds several [plugins](user-guide/plugins.md): `Analysis` for the feature maps and the [color survey](user-guide/color-survey.md), `Distribution` for the angular histogram, `Vector Field` for the overlay, and `MonogenicJ` for a multiresolution analysis. Every command runs from a [dialog](user-guide/index.md#the-user-interface) and from an [ImageJ macro](user-guide/macros.md).

In addition, sixteen [test images](test-images.md) come with the documentation, and the [assessments](assessment/index.md) built on them measure what the plugin does: the angular distribution against [six other tools](assessment/benchmarking.md), the five gradients against [analytic truth](assessment/compare-gradients.md), and two Python implementations of the same tensor — a [faithful port](assessment/python-port.md) and a [minimal operator](assessment/operator.md).

## Applications

Fibers, filaments, fringes, cracks, fractures, flows, growth rings: many scientific images are made of elongated structures, and what matters about them is their direction. OrientationJ quantifies it everywhere in the image — the local orientation, how consistently it holds, and how anisotropic the neighborhood is. More than 300 published studies have used it, from the actin cytoskeleton to nanofibers and bone: [what they did with it](use-cases/index.md).

## In 3D, with OrientationPy

OrientationJ measures orientation in 2D images only. For volumes, the EPFL Center for Imaging develops **OrientationPy**, its Python successor: the same gradient structure tensor, in 2D and in 3D, usable as a library or through its [napari plugin](https://github.com/EPFL-Center-for-Imaging/napari-orientationpy).

[OrientationPy — orientation in 2D and 3D](https://epfl-center-for-imaging.gitlab.io/orientationpy/){ .oj-button }


## Example of analysis scale

The same measurement, run at a growing analysis scale σ on the classic *Tree Rings* sample: small windows follow every detail, large ones summarize the trend ([macro](assets/tree-orientation.txt)).

![Color survey of the Tree Rings sample while the local window grows](assets/tree-orientation.gif){ .oj-tree }

## Demonstration in the browser, without installing anything

The [interactive online demo](https://bigwww.epfl.ch/demo/ip/demos/orientation/) runs the analysis in the browser, on the samples provided or on your own image: move the σ slider and the color survey follows.

<p class="oj-center"><a href="https://bigwww.epfl.ch/demo/ip/demos/orientation/" title="Open the interactive online demo"><img src="assets/online-demo.jpg" alt="The online demo: the Tree Rings sample and its color survey, side by side" width="620"></a></p>


## Color survey

<div class="oj-compare">
<img src="assets/collagen-input.jpg" alt="Collagen fibers, original image">
<img src="assets/collagen-survey.jpg" alt="Collagen fibers, color survey">
<span class="oj-compare-line"></span>
<input type="range" min="0" max="100" value="50" aria-label="Reveal the color survey">
</div>


<p class="oj-caption">Drag the handle: collagen fibers on the left, the same field as a color survey on the right<br>hue gives the orientation, saturation the coherency.</p>

