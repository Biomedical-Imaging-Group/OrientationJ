# Installation

## ImageJ or Fiji

1. Download [`OrientationJ_.jar`](https://github.com/Biomedical-Imaging-Group/OrientationJ/releases/latest/download/OrientationJ_.jar)
   (or pick a specific version from the
   [releases page](https://github.com/Biomedical-Imaging-Group/OrientationJ/releases)).
2. Copy it into the `plugins` folder of your ImageJ or Fiji installation.
3. Restart. The commands appear under **Plugins ▸ OrientationJ**.

!!! note "Java version"
    The plugin is built for **Java 8 or later**, so it runs on every current
    ImageJ and Fiji distribution.

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
