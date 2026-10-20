# Aggregation window — OrientationJ across the sizes of its aggregation

Runs **OrientationJ** (the Fiji plugin, cubic-spline gradient) on 4 images whose
structures span several sizes — `synthetic_wave_512` (periods 2, 8 and 64 px),
`synthetic_chirp_1024` (frequency sweep), `collagen`, `cell_aemisegger` — and
sweeps the two sizes over which it aggregates:

1. the **structure-tensor window** σ = 1, 2, 3, 4, 5, 6, 8, 10 (Analysis),
2. the **vector-field grid size** = 8, 12, 16, 24, 32, 48, 64 px (σ = 1,
   nematic aggregation of the cell, the default since 2.2.0).

## Files

| file | content |
|---|---|
| [aggregation_window.ipynb](aggregation_window.ipynb) | bins, plots and animates the Fiji results |
| [macro-aggregation-analysis.ijm](macro-aggregation-analysis.ijm) | σ sweep: orientation map (TIFF, degrees) + color survey (PNG) per (image, σ) |
| [macro-aggregation-vectorfield.ijm](macro-aggregation-vectorfield.ijm) | grid sweep: OJ-Vector-Field table (CSV) per (image, grid) |
| `*.png`, `*.gif` | the figures below, copied from `results/` for the documentation page |
| `results/` | the figures as the notebook writes them; `results/fiji/` holds the raw plugin outputs (not tracked) |

## How to run

```
Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-aggregation-analysis.ijm
Fiji.app/Contents/MacOS/ImageJ-macosx -macro macro-aggregation-vectorfield.ijm quit
jupyter nbconvert --to notebook --execute --inplace aggregation_window.ipynb
```

The vector-field macro needs the GUI (its results table cannot be saved headless);
the `quit` argument makes Fiji exit by itself when done.

## Outputs

- `distributions-vs-sigma.png` — masked distribution of orientation, one curve per
  σ (masks of [../../test-images/masks](../../test-images/masks)),
- `surveys-strip.png` — the color survey (hue = orientation, saturation = coherency,
  brightness constant, the macro default) at σ = 1, 4, 10,
- `<name>-survey.gif` — the color survey animated over σ,
- `vectorfields-overview.png` — vector fields at grid 8 / 16 / 32 / 64 px,
- `<name>-vectors.gif` — the vector field animated over the grid sizes.
