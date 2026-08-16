<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">

  <img class="oj-banner__logo" src="../../assets/logo-orientationj-clear.png" alt="OrientationJ">
  <div class="oj-banner__box">
    <p class="oj-banner__sub">Directional analysis of 2D images — ImageJ/Fiji plugins</p>
  </div>
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
</div>

# Version history

All releases are by Daniel Sage. Older versions are also on the [ImageJ update site](https://sites.imagej.net/BIG-EPFL/plugins/).

### 2.1.0 — 6 August 2026

* New feature: Directionality, the second deviatoric invariant J2 = (&lambda;1-&lambda;2)&sup2;/4 of the structure tensor
* New feature: Anisotropy FA, the fractional anisotropy FA = |&lambda;1-&lambda;2| / &radic;(&lambda;1&sup2;+&lambda;2&sup2;), bounded in [0..1]
* Directionality and Anisotropy FA are available in the Analysis plugin only
* New scaling option for Energy and Directionality: "Scale [0..1]" (default) or "No scale" (raw values), selectable in the dialog and scriptable in macros (`scale-energy`, `scale-directionality`)
* Color survey channels are clamped to [0,1] to avoid overflow with unscaled channels
* Fixed the macro recorder that did not record the Directionality view

### 2.0.8 — 24 July 2026

* Fixed swapped Gradient-X / Gradient-Y channels in the color survey
* Fixed the Distribution histogram window opening when only Binary Mask or Orientation Mask was selected
* Fixed the macro keys of the two mask views, now `binary_mask` and `orientation_mask`. The keys were derived from the display labels, and ImageJ truncates a macro key at its first space: "binary mask" was read as "binary" and never matched, so both views were unreachable from a macro, while "orientation mask" collapsed onto "orientation" and switched on the tensor orientation and the distribution mask together

### 2.0.4 — 15 March 2019

* Fixed a bug in the computation of the mean of cells for the vector field. The overlay line was also not at the right position (shifted by size/2)

### 2.0.3 — 15 June 2018

* Fixed a bug in the record of the macro (vectortype)

### 2.0.2 — 11 June 2018

* Make the orientation in degrees [-90,90] in the table VectorField, like in the other plugins

### 2.0.0 — 1 May 2018

* Redesign the graphical user interface
* Release on the source code on GitHub
* Push on Fiji
