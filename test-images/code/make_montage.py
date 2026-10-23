"""Montage of the test images, small, in one PNG for the README.

Three titled panels: the real images (2 rows x 4), the synthetic images
(2 rows x 4) and the compositions, the degraded rings (1 row x 4, by level).

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
COLUMNS = 4
TITLE_COLOR = '#3a3a37'

names = sorted(os.path.basename(path)[:-4] for path in glob.glob(os.path.join(IMAGES, '*.tif')))
LEVELS = ('none', 'low', 'medium', 'high')
PANELS = [
    ('Real images', [n for n in names if not n.startswith(('synthetic_', 'composition_'))]),
    ('Synthetic images', [n for n in names if n.startswith('synthetic_')]),
    ('Compositions: rings, ramp and noise at four levels of degradation',
     [n for level in LEVELS for n in names if n.startswith('composition_') and f'_{level}_' in n]),
]

rows = [(len(panel) + COLUMNS - 1) // COLUMNS for _, panel in PANELS]
TITLE = 0.22   # height of a panel title, in rows
fig = plt.figure(figsize=(12, 2.9 * (sum(rows) + TITLE * len(PANELS))))
subfigures = fig.subfigures(len(PANELS), 1, height_ratios=[r + TITLE for r in rows], hspace=0.0)
for subfigure, (title, panel), n_rows in zip(subfigures, PANELS, rows):
    subfigure.suptitle(title, fontsize=13, fontweight='bold', color=TITLE_COLOR, y=0.995)
    axes = subfigure.subplots(n_rows, COLUMNS, squeeze=False,
                              gridspec_kw={'top': 1 - 0.9 * TITLE / (n_rows + TITLE), 'bottom': 0.03,
                                           'left': 0.01, 'right': 0.99, 'hspace': 0.12, 'wspace': 0.05})
    for ax in axes.flat:
        ax.axis('off')
    for ax, name in zip(axes.flat, panel):
        image = tifffile.imread(os.path.join(IMAGES, name + '.tif')).astype(np.float64)
        ax.imshow(image, cmap='gray')
        ax.set_title(name, fontsize=8, color=TITLE_COLOR)
fig.savefig(OUTPUT, dpi=100, bbox_inches='tight', facecolor='white')
print('written', OUTPUT, f'({os.path.getsize(OUTPUT) // 1024} KB)')
