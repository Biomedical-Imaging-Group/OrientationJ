# OrientationJ Python Port

A faithful NumPy port of the OrientationJ gradient structure tensor (GST) and three
notebooks that run it on the 16 images of [../orientationj-test-images](../orientationj-test-images).

## Contents

| file | content |
|---|---|
| [orientationj.py](orientationj.py) | the port: spline gradient, IIR Gaussian, tensor features, color survey, distribution, vector field |
| [analysis.ipynb](analysis.ipynb) | OrientationJ Analysis → orientation / coherency / energy TIFFs + color survey PNG per image |
| [distribution.ipynb](distribution.ipynb) | OrientationJ Distribution → 180-bin histogram CSV per image + statistics |
| [vector_field.ipynb](vector_field.ipynb) | OrientationJ Vector Field → vector table CSV per image + overlays |
| [make_gallery.py](make_gallery.py) | generates the panel gallery of test images |


## Experiments

The images are published in the [test-images results](../orientationj-test-images).

##### Masks
The distribution and the vector field are computed **inside the masks** of
[../orientationj-test-images/masks](../orientationj-test-images/masks) (nonzero = analyzed): This removes the flat background, whose degenerate structure tensor.

##### Settings

All experiments use the **cubic-spline gradient** only (the plugin default, gradient code 0), **σ = 1** for the Gaussian window of the structure tensor,
a **16 × 16** vector-field grid, and the plugin defaults everywhere else
(ε = 0.001, min-coherency = 0 %, min-energy = 0 %, vector scale 100 %).
