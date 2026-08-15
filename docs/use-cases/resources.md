<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">
  <div class="oj-banner__top">
    <a class="oj-banner__mark" href="https://imaging.epfl.ch/" title="EPFL Center for Imaging">
      <img src="../../assets/center-for-imaging.svg" alt="EPFL Center for Imaging">
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

# Software and web resources

**[OrientationJ, the original page](https://bigwww.epfl.ch/demo/orientation/)** — the distribution page at the Biomedical Imaging Group: the jar, a description of every mode, recordable macro examples, test images (chirp, artificial fibers, collagen MIP) and a gallery of published applications.

**[Source code on GitHub](https://github.com/Biomedical-Imaging-Group/OrientationJ)** — the Java sources under GPL-3.0, the `plugins.config` that declares the menu commands, and the releases.

**[Theoretical background](../assets/theoretical-background.pdf)** — the mathematics of the gradient structure tensor, of the orientation, energy and coherency, as a typeset PDF; the same material is on the [Theory](../theory/index.md) pages.

**[MonogenicJ](https://bigwww.epfl.ch/demo/monogenicj/)** — the companion plugin: wavelet-based multiresolution monogenic analysis (orientation, coherency, wavenumber per scale), bundled with OrientationJ since version 2.0.7.

**[OrientationPy](https://epfl-center-for-imaging.gitlab.io/orientationpy/)** — the Python successor from the EPFL Center for Imaging, for 2D images and 3D volumes, with a choice of gradients; on [PyPI](https://pypi.org/project/orientationpy/) and bioconda, with a [napari plugin](https://github.com/EPFL-Center-for-Imaging/napari-orientationpy).

**[BIII, the BioImage Informatics Index](https://biii.eu/orientationj)** — the curated registry entry: function, platform, licence and links, in the NEUBIAS tools index.
