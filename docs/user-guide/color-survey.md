<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">

  <img class="oj-banner__logo" src="../../assets/logo-orientationj-clear.png" alt="OrientationJ">
  <div class="oj-banner__box">
    <p class="oj-banner__sub">Directional analysis of 2D images — ImageJ/Fiji plugins</p>
  </div>
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
</div>

# Color Survey HSB

The color survey is the output OrientationJ is recognized by: one image in which the direction of every structure is readable at a glance, without looking at three maps at once. It works because **orientation and hue are both circular**. An angle is defined modulo 180° — a structure at +89° and one at −89° are nearly the same structure — and hue returns to itself after a full turn of the color wheel. Mapping one onto the other therefore leaves no seam: the color changes continuously as the structure turns, and no false boundary appears where the numbers merely wrapped around. A gray scale from −90° to +90° would put a black-to-white cliff exactly where nothing happens.

The three features drive the three channels of the HSB space: **hue** the orientation, over half the wheel so that the whole range of angles is covered once — green at 0°, blue at +45°, red at ±90°, orange at −45°; **saturation** the coherency, so that a well-defined direction is vivid while an isotropic neighborhood stays gray whatever angle was measured there; **brightness** the original image, so that the structures remain recognizable. Read together they answer three questions at once: which direction, how sure, and where.

![The orientation color scale](../assets/color-scale.jpg)

<p class="oj-caption">The color coding of the orientation: green at 0°, blue at +45°, orange at −45°, red at ±90°. Any feature can be assigned to any channel in the dialog; this is the default.</p>

![Color surveys of three test images at two analysis scales](../assets/analysis-surveys.jpg)

<p class="oj-caption">Three images, each analyzed at a small and at a large scale. The hue follows the local direction in both; the small window resolves every fiber, the larger one keeps only the trend that survives at its size.</p>

## Reading a survey

Gray means *no orientation here* — either flat (no energy) or isotropic (no coherency), and the two are told apart by the brightness: dark gray is background, light gray is structure without a direction. A vivid color means a well-defined direction, and which direction is given by the hue. Two regions of the same color have the same orientation, wherever they are in the image, which is what makes the survey comparable across a figure — and across images, as long as the analysis scale is the same.
