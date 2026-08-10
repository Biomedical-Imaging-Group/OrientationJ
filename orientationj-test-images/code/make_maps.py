"""Feature maps of the test images, downloadable from the README.

For a selection of the images in ../images: orientation (degrees), coherency and energy as
zlib-compressed float32 TIFFs in ../results/maps/, computed with the
OrientationJ Python port at the plugin defaults (cubic-spline gradient,
structure-tensor window sigma = 1).

Run from this folder:  python3 make_maps.py
"""
import glob
import os
import sys

import numpy as np
import tifffile

sys.path.insert(0, os.path.join('..', 'orientationj_python_port'))
import orientationj as oj

SIGMA = 1.0
# maps tracked in the repository (128 MB for all 16 -- extend and rerun at will)
SELECTED = {'collagen', 'cell_aemisegger', 'synthetic_chirp_1024'}
IMAGES = os.path.join('..', 'images')
MAPS = os.path.join('..', 'results', 'maps')
os.makedirs(MAPS, exist_ok=True)

total = 0
for path in sorted(glob.glob(os.path.join(IMAGES, '*.tif'))):
    name = os.path.basename(path)[:-4]
    if name not in SELECTED:
        continue
    features = oj.analysis(oj.load(path), sigma=SIGMA)
    for suffix, array in [
            ('orientation', np.degrees(features['orientation'])),
            ('coherency', features['coherency']),
            ('energy', features['energy'])]:
        out = os.path.join(MAPS, f'{name}-{suffix}.tif')
        tifffile.imwrite(out, array.astype(np.float32), compression='zlib')
        total += os.path.getsize(out)
    print(f'{name}: orientation, coherency, energy')
print(f'total size: {total / 1e6:.1f} MB in {MAPS}')
