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

*Fibers, filaments, fringes, cracks, fractures, flows, growth rings: many scientific images are made of elongated structures, and what matters about them is their direction. OrientationJ quantifies it everywhere in the image — the local orientation, how consistently it holds, and how anisotropic the neighborhood is.*

### The gradient structure tensor
At every pixel OrientationJ evaluates the **gradient structure tensor (GST)** over a small window and extracts features from it: the **orientation** of the local structure, the **coherency**, which tells whether that orientation is well defined or the neighborhood is isotropic, and the **energy**, which measures how strong the gradient is there. The mathematics behind the GST, its features and the tensor invariants are given in [Theory](theory/index.md).

### Fiji/ImageJ plugins
OrientationJ is a suite of Fiji/ImageJ plugins written in Java, with a friendly user interface. `Analysis` produces the feature maps and the color survey; `Distribution` turns them into an angular histogram; `Vector Field` overlays a readable field of directions. Others report numbers or group structures: `Measure` and `Dominant Direction`, `Clustering` and `Horizontal Alignment`, and [`Corner Harris`](https://en.wikipedia.org/wiki/Harris_corner_detector). In addition, `MonogenicJ` brings a multiresolution analysis ([Unser et al., 2009](https://doi.org/10.1109/TIP.2009.2027628)). Every command runs from a dialog and from an ImageJ macro, so a setting that works on one image can be replayed over a whole folder — and a single parameter really matters: the analysis scale σ, which fixes the size of the structures the measurement describes, as explained in [Selecting the parameters](user-guide/select-scale.md).

### Documentation
Alongside the plugins, these pages document how well the measurement performs. The orientation distribution is compared with six other tools on a common dataset in [Benchmarking](assessment/benchmarking.md), and the images used throughout are published, with their masks, in [Test images](test-images.md).

### Directional Image Analysis in 3D

OrientationJ measures orientation in 2D images only. For volumes, the EPFL Center for Imaging develops **OrientationPy**, the Python successor of OrientationJ: the same gradient structure tensor, in 2D and in 3D, usable as a library or through its [napari plugin](https://github.com/EPFL-Center-for-Imaging/napari-orientationpy).

[OrientationPy — orientation in 2D and 3D](https://epfl-center-for-imaging.gitlab.io/orientationpy/){ .oj-button }


### Example of analysis scale

The same measurement, run at a growing analysis scale σ on the classic *Tree Rings* sample: small windows follow every detail, large ones summarize the trend ([macro](assets/tree-orientation.txt)).

![Color survey of the Tree Rings sample while the local window grows](assets/tree-orientation.gif){ .oj-tree }

### Demonstration in the browser, without installing anything

The [interactive online demo](https://bigwww.epfl.ch/demo/ip/demos/orientation/) runs the analysis in the browser, on the samples provided or on your own image: move the σ slider and the color survey follows.

<p class="oj-center"><a href="https://bigwww.epfl.ch/demo/ip/demos/orientation/" title="Open the interactive online demo"><img src="assets/online-demo.jpg" alt="The online demo: the Tree Rings sample and its color survey, side by side" width="620"></a></p>


### Input and color survey

<div class="oj-compare">
<img src="assets/collagen-input.jpg" alt="Collagen fibers, original image">
<img src="assets/collagen-survey.jpg" alt="Collagen fibers, color survey">
<span class="oj-compare-line"></span>
<input type="range" min="0" max="100" value="50" aria-label="Reveal the color survey">
</div>


<p class="oj-caption">Drag the handle: collagen fibers on the left, the same field as a color survey on the right<br>hue gives the orientation, saturation the coherency.</p>


## How to cite

Four publications document the method, the angular distribution, the local measurements and the monogenic analysis; the one to cite depends on what you used. They are listed, with their PDF and BibTeX, on the [how to cite](how-to-cite.md) page.

## Use cases

OrientationJ is used wherever the direction of a structure carries the information: the actin cytoskeleton and stress fibers in cell biology, collagen at the tumor–stroma interface in cancer research, engineered cardiac tissue, electrospun nanofibers and composites in materials science, bone and arterial wall in biomechanics, and any other image made of stripes or fronts.

**More than 300 publications** have used it since 2013. They are listed, with the sentence describing each use, on the [Use cases](use-cases/index.md) page. The release notes, version by version, are on the [version history](installation/index.md#version-history) page.
