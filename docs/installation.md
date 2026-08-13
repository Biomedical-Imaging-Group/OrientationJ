<div class="oj-banner" markdown>

![OrientationJ](assets/logo-orientationj.png){ .oj-logo }

<p class="oj-subtitle">Directional analysis of 2D images — ImageJ/Fiji plugins</p>

<hr>

<p class="oj-author"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>

<p class="oj-date">August 2026</p>

<hr>

![Color survey of the Tree Rings sample, sweeping the local window](assets/tree-orientation.gif){ .oj-tree }

</div>

# Installation

## ImageJ or Fiji

1. Download [`OrientationJ_.jar`](assets/OrientationJ_.jar) (version 2.1.0; older versions on the [releases page](https://github.com/Biomedical-Imaging-Group/OrientationJ/releases)).
2. Copy it into the `plugins` folder of your ImageJ or Fiji installation.
3. Restart. The commands appear under **Plugins ▸ OrientationJ**.

!!! note "Java version"
    The plugin is built for **Java 8 or later**, so it runs on every current ImageJ and Fiji distribution.

## Checking the installation

Run **Plugins ▸ OrientationJ ▸ Test Image ▸ Chirp Image Small** to generate a chirp, then **Plugins ▸ OrientationJ ▸ OrientationJ Analysis** on it. In the color survey the hue must follow the local stripe direction everywhere, turning smoothly around the center of the pattern.

## From source

```bash
git clone https://github.com/Biomedical-Imaging-Group/OrientationJ.git
cd OrientationJ
mvn package
```

The plugin lands in `target/OrientationJ_-<version>.jar`; copy it into the
`plugins` folder as above. The build needs Maven and a JDK (8 or later).

## Version history

Version 2.1.0 (6 August 2026)
=============
_Daniel Sage_

* New feature: Directionality, the second deviatoric invariant J2 = (&lambda;1-&lambda;2)&sup2;/4 of the structure tensor
* New feature: Anisotropy FA, the fractional anisotropy FA = |&lambda;1-&lambda;2| / &radic;(&lambda;1&sup2;+&lambda;2&sup2;), bounded in [0..1]
* Directionality and Anisotropy FA are available in the Analysis plugin only
* New scaling option for Energy and Directionality: "Scale [0..1]" (default) or "No scale" (raw values), selectable in the dialog and scriptable in macros (`scale-energy`, `scale-directionality`)
* Color survey channels are clamped to [0,1] to avoid overflow with unscaled channels
* Fixed the macro recorder that did not record the Directionality view

Version 2.0.8 (24 July 2026)
=============
_Daniel Sage_

* Fixed swapped Gradient-X / Gradient-Y channels in the color survey
* Fixed the Distribution histogram window opening when only Binary Mask or Orientation Mask was selected
* Fixed the macro keys of the two mask views, now `binary_mask` and `orientation_mask`. The keys were derived from the display labels, and ImageJ truncates a macro key at its first space: "binary mask" was read as "binary" and never matched, so both views were unreachable from a macro, while "orientation mask" collapsed onto "orientation" and switched on the tensor orientation and the distribution mask together

Version 2.0.4 (15 March 2019)
=============
_Daniel Sage_

* Fixed a bug in the computation of the mean of cells for the vector field. The overlay line was also not at the right position (shifted by size/2)

Version 2.0.3 (15 June 2018)
=============
_Daniel Sage_

* Fixed a bug in the record of the macro (vectortype)

Version 2.0.2 (11 June 2018)
=============
_Daniel Sage_

* Make the orientation in degrees [-90,90] in the table VectorField, like in the other plugins

Version 2.0.0 (1 May 2018)
=============
_Daniel Sage_

* Redesign the graphical user interface
* Release on the source code on GitHub
* Push on Fiji
