<div class="oj-banner" markdown>

![OrientationJ](assets/logo-orientationj.png){ .oj-logo }

<p class="oj-subtitle">A series of ImageJ and Fiji plugins for local directional image analysis</p>

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

Open a test image from the [Test images](test-images.md) page and run
**Plugins ▸ OrientationJ ▸ OrientationJ Dominant Direction**. On the chirp image the
reported orientation should follow the local stripe direction.

## From source

```bash
git clone https://github.com/Biomedical-Imaging-Group/OrientationJ.git
cd OrientationJ
mvn package
```

The plugin lands in `target/OrientationJ_-<version>.jar`; copy it into the
`plugins` folder as above. The build needs Maven and a JDK (8 or later).
