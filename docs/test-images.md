<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">
  <p class="oj-banner__credit"><a href="mailto:daniel.sage@epfl.ch">Daniel Sage</a> · <a href="https://imaging.epfl.ch/">Center for Imaging</a> and <a href="https://bigwww.epfl.ch/">Biomedical Imaging Group</a>, <a href="https://www.epfl.ch/">Ecole Polytechnique Fédérale de Lausanne (EPFL)</a></p>
  <div class="oj-banner__box">
    <img class="oj-banner__logo" src="../assets/logo-orientationj-clear.png" alt="OrientationJ">
    <p class="oj-banner__sub">Directional analysis of 2D images — ImageJ/Fiji plugins</p>
  </div>
  <p class="oj-banner__version">Version 2.1.0 · August 2026</p>
</div>

# OrientationJ Test Images

16 grayscale 2D images for testing and benchmarking orientation analysis: **9 synthetic**, whose orientation is known by construction, and **7 real** images, tagged ![real](https://img.shields.io/badge/real-db3f2e) below. Each image comes with a binary mask of its meaningful structures, and with an overview panel of the results.

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/montage.png" width="560">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/montage.png)

The folder holds the 16 source images in [images/](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/orientationj-test-images/images), the 16 binary masks in [masks/](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/orientationj-test-images/masks), and in [results/](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/orientationj-test-images/results) one overview panel per image (original, mask, orientation, coherency, energy, color survey, masked distribution, vector field). All of it is computed with the [OrientationJ Python port](https://github.com/Biomedical-Imaging-Group/OrientationJ/tree/master/assessment/orientationj_python_port) at the plugin defaults (cubic-spline gradient, σ = 1). The images are listed below in the order of the montage; click a panel for full size.


## cell_aemisegger ![real](https://img.shields.io/badge/real-db3f2e)

728 × 728, uint8, values in [0, 252] — real: fluorescence cell, actin fibers.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/cell_aemisegger.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/cell_aemisegger.tif) · [orientation](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/maps/cell_aemisegger-orientation.tif) · [coherency](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/maps/cell_aemisegger-coherency.tif) · [energy](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/maps/cell_aemisegger-energy.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/cell_aemisegger.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/cell_aemisegger.png)

## collagen ![real](https://img.shields.io/badge/real-db3f2e)

512 × 512, uint8, values in [2, 255] — real: collagen fibers.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/collagen.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/collagen.tif) · [orientation](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/maps/collagen-orientation.tif) · [coherency](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/maps/collagen-coherency.tif) · [energy](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/maps/collagen-energy.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/collagen.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/collagen.png)

## dendrochronology ![real](https://img.shields.io/badge/real-db3f2e)

512 × 512, uint8, values in [0, 255] — real: wood section, growth rings.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/dendrochronology.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/dendrochronology.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/dendrochronology.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/dendrochronology.png)

## fibronectin_arafat_plos2025 ![real](https://img.shields.io/badge/real-db3f2e)

1024 × 1024, uint8, values in [0, 255] — real: fibronectin network (Arafat et al., PLOS ONE 2025).

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/fibronectin_arafat_plos2025.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/fibronectin_arafat_plos2025.tif)

<sub>M. Arafat et al., *PLOS ONE* (2025), [doi:10.1371/journal.pone.0320006](https://doi.org/10.1371/journal.pone.0320006).</sub>

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/fibronectin_arafat_plos2025.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/fibronectin_arafat_plos2025.png)

## fibrous_tissues_fibero2024 ![real](https://img.shields.io/badge/real-db3f2e)

2048 × 2048, uint16, values in [87, 4094] — real: fibrous tissue (FiberO, 2024).

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/fibrous_tissues_fibero2024.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/fibrous_tissues_fibero2024.tif)

<sub>FiberO dataset (2024), [doi:10.3389/fbioe.2024.1497837](https://doi.org/10.3389/fbioe.2024.1497837).</sub>

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/fibrous_tissues_fibero2024.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/fibrous_tissues_fibero2024.png)

## fiji_directionality_montage

1536 × 1536, uint16, values in [3089, 65535] — real: montage from the Fiji Directionality documentation.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/fiji_directionality_montage.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/fiji_directionality_montage.tif)

<sub>Montage from the documentation of the Fiji [Directionality](https://imagej.net/plugins/directionality) plugin.</sub>

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/fiji_directionality_montage.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/fiji_directionality_montage.png)

## nanofiber_fiji_diameterj ![real](https://img.shields.io/badge/real-db3f2e)

512 × 512, uint8, values in [0, 255] — real: SEM nanofibers (DiameterJ sample).

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/nanofiber_fiji_diameterj.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/nanofiber_fiji_diameterj.tif)

<sub>DiameterJ sample — S. A. Hotaling et al., *Data in Brief* (2015), [doi:10.1016/j.dib.2015.07.012](https://doi.org/10.1016/j.dib.2015.07.012).</sub>

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/nanofiber_fiji_diameterj.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/nanofiber_fiji_diameterj.png)

## polymer_slice_quanfima2018 ![real](https://img.shields.io/badge/real-db3f2e)

600 × 600, float32, values in [−0.0029, 0.0030] — real: polymer slice (quanfima, 2018).

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/polymer_slice_quanfima2018.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/polymer_slice_quanfima2018.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/polymer_slice_quanfima2018.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/polymer_slice_quanfima2018.png)

## synthetic_chirp_1024

1024 × 1024, float32, values in [0, 1] — synthetic: radial chirp with a frequency sweep; the tangential ground-truth orientation is known at every pixel.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/synthetic_chirp_1024.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/synthetic_chirp_1024.tif) · [orientation](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/maps/synthetic_chirp_1024-orientation.tif) · [coherency](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/maps/synthetic_chirp_1024-coherency.tif) · [energy](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/maps/synthetic_chirp_1024-energy.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_chirp_1024.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_chirp_1024.png)

## synthetic_filaments_512

512 × 512, float32, values in [0, 1] — synthetic: random curved filaments.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/synthetic_filaments_512.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/synthetic_filaments_512.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_filaments_512.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_filaments_512.png)

## synthetic_nematic_512

512 × 512, float32, values in [0, 1] — synthetic: nematic-like texture with a dominant direction.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/synthetic_nematic_512.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/synthetic_nematic_512.tif)

<sub>Pattern inspired by active nematics — A. Doostmohammadi, J. Ignés-Mullol, J. M. Yeomans, F. Sagués, *Active nematics*, Nature Communications 9:3246 (2018), [doi:10.1038/s41467-018-05666-8](https://doi.org/10.1038/s41467-018-05666-8).</sub>

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_nematic_512.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_nematic_512.png)

## synthetic_noise_512

512 × 512, float32, values in [0, 1] — synthetic: isotropic noise; any measured anisotropy is bias.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/synthetic_noise_512.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/synthetic_noise_512.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_noise_512.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_noise_512.png)

## synthetic_rings_dither_512

512 × 512, float32, values in [0, 1] — synthetic: thin concentric rings with a small dither (σ = 10⁻⁴) that avoids degenerate-tensor spikes; exact tangential truth.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/synthetic_rings_dither_512.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/synthetic_rings_dither_512.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_rings_dither_512.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_rings_dither_512.png)

## synthetic_spiral_512

512 × 512, float32, values in [0, 1] — synthetic: spiral.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/synthetic_spiral_512.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/synthetic_spiral_512.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_spiral_512.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_spiral_512.png)

## synthetic_wave_512

512 × 512, float32, values in [0, 1] — synthetic: fringes at exactly +60° and −30°, two scales at once.

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/synthetic_wave_512.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/synthetic_wave_512.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_wave_512.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/synthetic_wave_512.png)

## z_artificial_fibers

683 × 512, uint8, values in [0, 225] — artificial: fiber phantom (drawn, not generated by the synthetic-image notebook).

Download: [image](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/images/z_artificial_fibers.tif) · [mask](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/masks/z_artificial_fibers.tif)

[<img src="https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/z_artificial_fibers.png" width="620">](https://raw.githubusercontent.com/Biomedical-Imaging-Group/OrientationJ/master/orientationj-test-images/results/z_artificial_fibers.png)
