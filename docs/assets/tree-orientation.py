"""Assemble the Tree Rings animation from the maps saved by tree-orientation.txt.

    python tree-orientation.py ~/tree-orientation/frames tree-orientation.gif

For every local window the color survey is composed in HSB: hue = orientation,
saturation = sqrt(coherency), which keeps the colors vivid where the coherency
is moderate, brightness = the image. Each frame is labelled with its window,
scaled from 1200 x 162 to 800 x 108 px and quantized to its own 256-color
palette. The sequence sweeps the window up, holds, sweeps back down and holds
again, so the loop has no jump. Needs numpy and Pillow.
"""
import os
import sys

import numpy as np
from PIL import Image, ImageDraw, ImageFont

FRAMES, OUT = sys.argv[1], sys.argv[2]
K = range(0, 31)                 # sigma = 2^(k/6): 1 ... 32 px, as in the macro
SIZE = (800, 108)                # two thirds of the 1200 x 162 crop
STEP_MS, HOLD_MS = 150, 1200     # per frame, and at the two ends of the sweep
SATURATION_POWER = 0.5

try:
    FONT = ImageFont.truetype('/System/Library/Fonts/Helvetica.ttc', 13)
except OSError:
    FONT = ImageFont.load_default()


def load(name):
    return np.asarray(Image.open(os.path.join(FRAMES, name)), dtype=np.float64)


def to_byte(x):
    return Image.fromarray(np.round(np.clip(x, 0.0, 1.0) * 255.0).astype(np.uint8))


image = load('input.tif')
brightness = (image - image.min()) / (image.max() - image.min())


def frame(k):
    theta = load('orientation-%02d.tif' % k)                    # radians, [-pi/2, pi/2]
    coherency = load('coherency-%02d.tif' % k)                  # [0, 1]
    hue = (theta + np.pi / 2.0) / np.pi                         # the wheel of the plugin
    saturation = coherency ** SATURATION_POWER
    survey = Image.merge('HSV', [to_byte(hue), to_byte(saturation), to_byte(brightness)])
    survey = survey.convert('RGB').resize(SIZE, Image.LANCZOS)
    sigma = 2 ** (k / 6)
    label = 'local window %.1f px' % sigma if sigma < 10 else 'local window %.0f px' % sigma
    draw = ImageDraw.Draw(survey, 'RGBA')
    w, h = draw.textbbox((0, 0), label, font=FONT)[2:]
    x, y = SIZE[0] - w - 8, SIZE[1] - h - 8
    draw.rectangle((x - 4, y - 4, x + w + 4, y + h + 4), fill=(0, 0, 0, 150))
    draw.text((x, y), label, font=FONT, fill='white')
    return survey.quantize(colors=256, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE)


up = [frame(k) for k in K]
sequence = up + up[-2:0:-1]                  # up, then down without repeating the ends
durations = [STEP_MS] * len(sequence)
durations[0] = durations[len(up) - 1] = HOLD_MS
sequence[0].save(OUT, save_all=True, append_images=sequence[1:], duration=durations,
                 loop=0, disposal=1)
print(OUT, len(sequence), 'frames,', round(os.path.getsize(OUT) / 1e6, 2), 'MB,',
      round(sum(durations) / 1000, 1), 's per loop')
