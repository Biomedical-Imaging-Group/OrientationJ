<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">
  <div class="oj-banner__top">
    <a class="oj-banner__mark" href="https://imaging.epfl.ch/" title="EPFL Center for Imaging">
      <img src="../assets/center-for-imaging.svg" alt="EPFL Center for Imaging">
    </a>
    <p class="oj-banner__credit">
      <a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a><br>
      <a href="https://imaging.epfl.ch/">Center for Imaging</a> and
      <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a><br>
      <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a>
    </p>
  </div>
  <!-- each part is one box, so a dash can never begin a wrapped line -->
  <p class="oj-banner__title"><span class="oj-banner__part"><strong>Orientation<span class="oj-banner__j">J</span></strong></span><span
     class="oj-banner__part">Directional analysis of 2D images</span><span
     class="oj-banner__part">ImageJ/Fiji plugins</span></p>
  <p class="oj-banner__version">Version 2.1.0 · August 2026</p>
</div>

[PDF](../assets/theoretical-background.pdf "The theoretical background as a typeset PDF"){ .oj-button .oj-button--right }

# Theory

How OrientationJ measures a direction, from the definition of the tensor to the choice of its two parameters. The same material, typeset, is in the [PDF](../assets/theoretical-background.pdf).

## The pages of this section

- **[The structure tensor](structure-tensor.md)** — the weighted inner product, the tensor of a region, and what its eigen-decomposition means.
- **[Features and invariants](features.md)** — orientation, coherency, energy, directionality and anisotropy, their formulas, the summary table and the typical cases.
- **[The scale parameter σ](scale.md)** — the size of the analysis window: what it changes, and how to choose it.
- **[The gradient](gradient.md)** — the five ways of taking the derivative, and what each costs in accuracy.
