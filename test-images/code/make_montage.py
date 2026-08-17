"""Montage of the 16 test images, small, in one PNG for the README.

Run from this folder:  python3 make_montage.py
Output: ../results/montage.png
"""
import glob
import os

import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import tifffile

IMAGES = os.path.join('..', 'images')
OUTPUT = os.path.join('..', 'results', 'montage.png')

paths = sorted(glob.glob(os.path.join(IMAGES, '*.tif')))
fig, axes = plt.subplots(4, 4, figsize=(12, 12.4))
for ax, path in zip(axes.flat, paths):
    image = tifffile.imread(path).astype(np.float64)
    ax.imshow(image, cmap='gray')
    ax.set_title(os.path.basename(path)[:-4], fontsize=8, color='#3a3a37')
    ax.axis('off')
for ax in axes.flat[len(paths):]:
    ax.axis('off')
fig.tight_layout(pad=0.8)
fig.savefig(OUTPUT, dpi=100, bbox_inches='tight', facecolor='white')
print('written', OUTPUT, f'({os.path.getsize(OUTPUT) // 1024} KB)')
