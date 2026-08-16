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

# Installation

## In ImageJ

1. Download [`OrientationJ_.jar`](../assets/OrientationJ_.jar) (version 2.1.0). Older versions are on the [ImageJ update site](https://sites.imagej.net/BIG-EPFL/plugins/).
2. Copy it into the `plugins` folder of your ImageJ installation.
3. Restart ImageJ. The commands appear under **Plugins ▸ OrientationJ**.

## In Fiji

Fiji installs and updates the plugin by itself, through the update site of the Biomedical Imaging Group:

1. **Help ▸ Update…**, and let the updater finish looking for changes.
2. **Manage update sites**, then tick **BIG-EPFL** in the list. If it is not there, add it with the URL `https://sites.imagej.net/BIG-EPFL/`.
3. **Apply changes**, then restart Fiji. The commands appear under **Plugins ▸ OrientationJ**.

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

The release notes of every version are on the [version history](history.md) page.
