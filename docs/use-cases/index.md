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

# Use cases

Since 2013, OrientationJ has been used in **304 peer-reviewed publications**, in 194 distinct fields of application, using 61 different combinations of its commands. The fields that recur most tell what the measurement is good for: the actin cytoskeleton (37 papers), collagen and the extracellular matrix in tumors (27), materials and nanofibers (27), cardiac tissue engineering (10), bone and mineralized tissue (5).

[Open the reference table](literature.html){ .oj-button }

## In the literature

The reference table lists every one of those publications, with the sentence from the paper describing how the plugin was used and which command it relied on. It sorts by year, field or mode, and it searches.

## Primary references

The publications to cite when OrientationJ contributed to your results, each matching what you used — the method, the angular distribution, the local measurements, or the monogenic analysis.

<p class="oj-cite-topic">The method — the gradient structure tensor and its features</p>

> Püspöki Z, Storath M, Sage D, Unser M (2016). Transforms and Operators for Directional Bioimage Analysis: A Survey. *Advances in Anatomy, Embryology and Cell Biology*, vol. 219, Focus on Bio-Image Informatics, Springer, pp. 69–93. [doi:10.1007/978-3-319-28549-8_3](https://doi.org/10.1007/978-3-319-28549-8_3)

[PDF](https://bigwww.epfl.ch/publications/puespoeki1603.pdf){ .oj-chip } [BibTeX](https://bigwww.epfl.ch/publications/puespoeki1603.html){ .oj-chip }

<p class="oj-cite-topic">The angular distribution — collagen waviness in the arterial adventitia</p>

> Rezakhaniha R, Agianniotis A, Schrauwen JTC, Griffa A, Sage D, Bouten CVC, van de Vosse FN, Unser M, Stergiopulos N (2012). Experimental Investigation of Collagen Waviness and Orientation in the Arterial Adventitia Using Confocal Laser Scanning Microscopy. *Biomechanics and Modeling in Mechanobiology* 11(3–4): 461–473. [doi:10.1007/s10237-011-0325-z](https://doi.org/10.1007/s10237-011-0325-z)

[PDF](https://bigwww.epfl.ch/publications/rezakhaniha1201.pdf){ .oj-chip } [BibTeX](https://bigwww.epfl.ch/publications/rezakhaniha1201.html){ .oj-chip }

<p class="oj-cite-topic">The local measurements — elastin in human cerebral arteries</p>

> Fonck E, Feigl GG, Fasel J, Sage D, Unser M, Rüfenacht DA, Stergiopulos N (2009). Effect of Aging on Elastin Functionality in Human Cerebral Arteries. *Stroke* 40(7): 2552–2556. [doi:10.1161/STROKEAHA.108.528091](https://doi.org/10.1161/STROKEAHA.108.528091)

[PDF](https://bigwww.epfl.ch/publications/fonck0901.pdf){ .oj-chip } [BibTeX](https://bigwww.epfl.ch/publications/fonck0901.html){ .oj-chip }

<p class="oj-cite-topic">The multiresolution analysis — MonogenicJ</p>

> Unser M, Sage D, Van De Ville D (2009). Multiresolution Monogenic Signal Analysis Using the Riesz–Laplace Wavelet Transform. *IEEE Transactions on Image Processing* 18(11): 2402–2418. [doi:10.1109/TIP.2009.2027628](https://doi.org/10.1109/TIP.2009.2027628)

[PDF](https://bigwww.epfl.ch/publications/unser0907.pdf){ .oj-chip } [BibTeX](https://bigwww.epfl.ch/publications/unser0907.html){ .oj-chip }

## Software and resources

**[OrientationJ, the original page](https://bigwww.epfl.ch/demo/orientation/)** — the distribution page at the Biomedical Imaging Group: the jar, a description of every mode, recordable macro examples, test images (chirp, artificial fibers, collagen MIP) and a gallery of published applications.

**[Source code on GitHub](https://github.com/Biomedical-Imaging-Group/OrientationJ)** — the Java sources under GPL-3.0, the `plugins.config` that declares the menu commands, and the releases.

**[Theoretical background](../assets/theoretical-background.pdf)** — the mathematics of the gradient structure tensor, of the orientation, energy and coherency, as a typeset PDF; the same material is on the [Theory](../theory/index.md) pages.

**[MonogenicJ](https://bigwww.epfl.ch/demo/monogenicj/)** — the companion plugin: wavelet-based multiresolution monogenic analysis (orientation, coherency, wavenumber per scale), bundled with OrientationJ since version 2.0.7.

**[OrientationPy](https://epfl-center-for-imaging.gitlab.io/orientationpy/)** — the Python successor from the EPFL Center for Imaging, for 2D images and 3D volumes, with a choice of gradients; on [PyPI](https://pypi.org/project/orientationpy/) and bioconda, with a [napari plugin](https://github.com/EPFL-Center-for-Imaging/napari-orientationpy).

**[BIII, the BioImage Informatics Index](https://biii.eu/orientationj)** — the curated registry entry: function, platform, licence and links, in the NEUBIAS tools index.

## Community discussions

Threads on the [Scientific Community Image Forum](https://forum.image.sc/) where the use, the parameters and the interpretation of OrientationJ are discussed — several answered by the author of the plugin.

- [Coherency in OrientationJ](https://forum.image.sc/t/coherency-in-orientation-j/83176) (2023) — how to read coherency values on collagen fibers in second-harmonic-generation images, gel-only control against cell-containing samples.
- [Dealing with dark regions](https://forum.image.sc/t/orientationj-dealing-with-dark-regions/61179) (2021) — how background regions weigh on the coherency and on the dominant direction.
- [How is the dominant direction calculated?](https://forum.image.sc/t/how-is-the-dominant-direction-calculated/38330) (2020) — what the orientation and coherency returned by *Dominant Direction* mean, and the part played by the local window.
- [Angle histograms](https://forum.image.sc/t/orientationj-and-angle-histograms/119187) (2026) — why the *Distribution* histogram can show a large spike at 0°, and the preprocessing that avoids it.
- [Interpretation for electrospun nanofibers](https://forum.image.sc/t/orientation-j-and-its-interpretation-for-electrospun-nanofibers/3821) (2017) — what energy, orientation and coherency mean when quantifying nanofiber alignment.
- [Where is the documentation?](https://forum.image.sc/t/orientationj-documentation/69672) (2022) — the functions and parameters, beyond the original page.
- [Macro for the plugin](https://forum.image.sc/t/macro-for-plug-in-orientationj/7224) (2017) — batch processing from the ImageJ macro language, and saving the result tables.

## Validation and related tools

The tools OrientationJ is most often compared with, and the studies that measure it against them. A quantitative comparison on a common dataset is in [Benchmarking](../assessment/benchmarking.md).

**[FiberFit](https://doi.org/10.1007/s10237-016-0776-3)** — Morrill EE, Tulepbergenov AN, Stender CJ, Lamichhane R, Brown RJ, Lujan TJ (2016), *A validated software application to measure fiber organization in soft tissue*, Biomech Model Mechanobiol 15:1467–1478. Compares Fourier-based and structure-tensor fiber orientation; the validation reference listed on the original OrientationJ page.

**[DiameterJ](https://doi.org/10.1016/j.biomaterials.2015.02.015)** — Hotaling NA, Bharti K, Kriel H, Simon CG (2015), *DiameterJ: a validated open source nanofiber diameter measurement tool*, Biomaterials 61:327–338. The fiber-diameter companion many nanofiber papers run alongside OrientationJ.

**[FiberO](https://doi.org/10.3389/fbioe.2024.1497837)** — Muñoz A, Docaj A, Fernandez J, Carriero A (2025), Front Bioeng Biotechnol. A recent open-source tool benchmarked against OrientationJ, Directionality, FiberFit and CT-FIRE.

**[Directionality](https://imagej.net/plugins/directionality)** (Fiji) — the Fourier-component and local-gradient alternative for orientation histograms, frequently used beside OrientationJ.

**[CT-FIRE](https://doi.org/10.1117/1.JBO.19.1.016007)** — fiber tracking and extraction for collagen in second-harmonic-generation images.

**[skimage.feature.structure_tensor](https://scikit-image.org/docs/stable/api/skimage.feature.html#skimage.feature.structure_tensor)** — the minimal structure tensor of scikit-image (finite-difference gradients only), the closest thing in the Python ecosystem outside OrientationPy.
