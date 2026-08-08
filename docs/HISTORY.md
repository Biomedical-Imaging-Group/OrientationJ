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

