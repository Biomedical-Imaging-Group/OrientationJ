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

[PDF](../assets/theoretical-background.pdf "The theoretical background as a typeset PDF"){ .oj-button .oj-button--right }

# The structure tensor

## Quantitative orientation analysis

The aim is to characterize the orientation and isotropy properties of a local area of interest (Region of Interest, ROI) in an image. To that end, we first define the weighted inner product

\[
\langle f, g \rangle_w = \iint_{\mathbb{R}^2} w(x,y)\, f(x,y)\, g(x,y)\, \mathrm{d}x\, \mathrm{d}y ,
\]

where \(w(x,y) \geq 0\) is a weighting function that specifies the area of interest. It is typically a normalized square window of size \(L\) centered on a location of interest \((x_0, y_0)\), or a Gaussian window of standard deviation \(\sigma_w\). The norm associated with this inner product is \(\lVert f \rVert_w = \sqrt{\langle f, f \rangle_w}\). Next, we consider the derivative in the direction specified by the unit vector \(\mathbf{u}_\theta = (\cos\theta, \sin\theta)^\top\), which is given by

\[
D_{\mathbf{u}_\theta} f(x,y) = \mathbf{u}_\theta^\top\, \nabla f(x,y) ,
\]

where \(\nabla f = (f_x, f_y)^\top\) is the gradient of the image under consideration. We are now interested in finding the direction \(\mathbf{u}\) along which the directional derivative is maximized over the ROI:

\[
\mathbf{u}_{\max} = \arg\max_{\lVert\mathbf{u}\rVert = 1}\ \lVert D_{\mathbf{u}} f \rVert_w^2 .
\]

A standard inner-product manipulation then yields

\[
\lVert D_{\mathbf{u}} f \rVert_w^2 = \langle \mathbf{u}^\top \nabla f,\ \nabla f^\top \mathbf{u} \rangle_w = \mathbf{u}^\top \mathbf{J}\, \mathbf{u} ,
\]

\[
\mathbf{J} = \langle \nabla f, \nabla f^\top \rangle_w =
\begin{bmatrix}
\langle f_x, f_x \rangle_w & \langle f_x, f_y \rangle_w \\
\langle f_x, f_y \rangle_w & \langle f_y, f_y \rangle_w
\end{bmatrix},
\]

where \(\mathbf{J}\) is the so-called **structure tensor**, a \(2 \times 2\) symmetric positive-semidefinite matrix. The solution of the optimization problem is obtained by setting the derivative of \(\mathbf{u}^\top \mathbf{J} \mathbf{u} + \lambda (1 - \mathbf{u}^\top \mathbf{u})\) with respect to \(\mathbf{u}\) to zero, which yields the eigenvector equation

\[
\mathbf{J}\, \mathbf{u} = \lambda\, \mathbf{u} .
\]

This implies that the first eigenvector \(\mathbf{e}_1\) of \(\mathbf{J}\) gives the direction of maximal gradient energy; the corresponding eigenvalue is \(\lambda_1 = \max \lVert D_{\mathbf{u}} f \rVert_w^2\). Conversely, the directional derivative is minimized along the second eigenvector \(\mathbf{e}_2\), with \(\lambda_2 = \min \lVert D_{\mathbf{u}} f \rVert_w^2\). Since the gradient is perpendicular to the local structures, the visible structures (fibers, edges) are aligned with \(\mathbf{e}_2\); **this is the orientation reported by OrientationJ**. The structure tensor therefore contains all the relevant directional information of the ROI.

## Implementation notes

In OrientationJ, the weighting function \(w\) is a Gaussian window whose standard deviation \(\sigma_w\) ("local window") is set in the dialog; the gradient is computed by cubic-spline interpolation by default. A small regularization \(\epsilon\) is added to the denominators of \(C\) and \(\mathrm{FA}\) to avoid division by zero in flat regions.

Coherency and fractional anisotropy are dimensionless and naturally bounded in \([0, 1]\); they are displayed as computed. Energy and directionality are unbounded (units \(g^2\) and \(g^4\)); since version 2.1.0, they are either linearly rescaled to \([0, 1]\) for display (option *Scale [0..1]*, the default) or shown with their raw values (option *No scale*). The same option applies to the channels used to build the HSB/RGB color survey, where channel values are clamped to \([0, 1]\).

## References

- J. Bigün and G. H. Granlund, "Optimal orientation detection of linear symmetry," *Proceedings of the First IEEE International Conference on Computer Vision*, London, 1987.
- B. Jähne, *Digital Image Processing*, Springer, 1997.
- J. Weickert, "Coherence-enhancing diffusion filtering," *International Journal of Computer Vision*, vol. 31, 1999.
- R. von Mises, "Mechanik der festen Körper im plastisch-deformablen Zustand," *Nachrichten von der Gesellschaft der Wissenschaften zu Göttingen*, 1913.
- P. J. Basser and C. Pierpaoli, "Microstructural and physiological features of tissues elucidated by quantitative-diffusion-tensor MRI," *Journal of Magnetic Resonance, Series B*, vol. 111, 1996.
- Z. Püspöki, M. Storath, D. Sage, and M. Unser, "Transforms and operators for directional bioimage analysis: A survey," *Advances in Anatomy, Embryology and Cell Biology*, vol. 219, Focus on Bio-Image Informatics, Springer, 2016.
