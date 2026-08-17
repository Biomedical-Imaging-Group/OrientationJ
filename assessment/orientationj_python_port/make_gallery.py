"""Gallery of the OrientationJ test images, rendered with the Python port.

For every image of ../../test-images: one 2 x 4 panel figure --
image, mask, orientation, coherency, energy, color survey, orientation
distribution inside the mask, vector field -- written to
../../test-images/results/<name>.png (analysis sigma = 1, the
plugin default).

Run:  python3 make_gallery.py
"""
import glob
import os

import numpy as np
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import tifffile

import orientationj as oj

SIGMA = 1.0
INPUT = os.path.join('..', '..', 'test-images')
GALLERY = os.path.join(INPUT, 'results')
os.makedirs(GALLERY, exist_ok=True)

plt.rcParams.update({
    'figure.facecolor': 'white', 'font.size': 10, 'axes.titlesize': 10,
    'axes.spines.top': False, 'axes.spines.right': False,
    'axes.edgecolor': '#cfcec6', 'grid.color': '#e8e8e4', 'grid.linewidth': 0.8,
    'text.color': '#3a3a37', 'axes.labelcolor': '#3a3a37',
    'xtick.color': '#6b6a63', 'ytick.color': '#6b6a63'})

for path in sorted(glob.glob(os.path.join(INPUT, 'images', '*.tif'))):
    name = os.path.basename(path)[:-4]
    image = oj.load(path)
    mask = tifffile.imread(os.path.join(INPUT, 'masks', name + '.tif')) > 0
    features = oj.analysis(image, sigma=SIGMA)

    fig, axes = plt.subplots(2, 4, figsize=(16, 8.4))

    def show(ax, array, title, **kwargs):
        ax.imshow(array, **kwargs)
        ax.set_title(title)
        ax.axis('off')

    show(axes[0, 0], image, name, cmap='gray')
    show(axes[0, 1], mask, 'mask', cmap='gray', vmin=0, vmax=1)
    show(axes[0, 2], np.degrees(features['orientation']),
         'orientation [deg]', cmap='hsv', vmin=-90, vmax=90)
    show(axes[0, 3], features['coherency'], 'coherency', cmap='gray',
         vmin=0, vmax=1)
    energy = features['energy']
    show(axes[1, 0], energy, 'energy', cmap='gray',
         vmin=0, vmax=np.percentile(energy, 99.5))
    show(axes[1, 1], oj.survey(image, features), 'color survey')

    # orientation distribution inside the mask
    histo, angles, _ = oj.distribution(features, mask=mask)
    density = histo / max(histo.sum(), 1.0)
    ax = axes[1, 2]
    ax.fill_between(angles, density, color='#4878a8', alpha=0.25, linewidth=0)
    ax.plot(angles, density, color='#4878a8', linewidth=1.4)
    ax.set_xlim(-90, 90)
    ax.set_xticks(range(-90, 91, 45))
    ax.set_title('distribution (masked)')
    ax.grid(True, axis='y')
    ax.set_axisbelow(True)

    # vector field on the image, masked cells, length ~ coherency
    grid = max(10, min(image.shape) // 32)
    table = oj.vector_field(features, grid=grid, mask=mask)
    x1, y1, x2, y2 = oj.vector_segments(table, grid=grid, scale=90.0,
                                        vector_type=2)
    ax = axes[1, 3]
    ax.imshow(image, cmap='gray', alpha=0.8)
    ax.plot([x1, x2], [y1, y2], color='#E69F00', linewidth=1.1,
            solid_capstyle='round')
    ax.set_title(f'vector field (grid {grid})')
    ax.axis('off')

    fig.tight_layout()
    fig.savefig(os.path.join(GALLERY, name + '.png'), dpi=110,
                bbox_inches='tight')
    plt.close(fig)
    print('panel:', name)

print('done ->', GALLERY)
