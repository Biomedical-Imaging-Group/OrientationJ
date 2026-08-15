<!-- The banner. The same block on every page; only the logo path
     changes with the depth of the page in the folder tree. -->
<div class="oj-banner">
  <div class="oj-banner__top">
    <a class="oj-banner__mark" href="https://imaging.epfl.ch/" title="EPFL Center for Imaging">
      <img src="../../assets/center-for-imaging.svg" alt="EPFL Center for Imaging">
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

# Features and invariants

## Features and tensor invariants

With the eigenvalues \(\lambda_1 \geq \lambda_2 \geq 0\), the mean \(\bar\lambda = \tfrac12 \operatorname{tr}(\mathbf{J})\), and the deviator \(\mathbf{s} = \mathbf{J} - \tfrac12 \operatorname{tr}(\mathbf{J})\, \mathbf{I}\), OrientationJ computes the following features:

**Orientation** — direction of \(\mathbf{e}_2\), along the structures:

\[
\theta = \frac{1}{2} \arctan\!\left( \frac{2 \langle f_x, f_y \rangle_w}{\langle f_y, f_y \rangle_w - \langle f_x, f_x \rangle_w} \right) \in [-\pi/2,\ \pi/2]
\]

**Energy:**

\[
E = \operatorname{tr}(\mathbf{J}) = \lambda_1 + \lambda_2 \in [0, \infty)
\]

**Coherency:**

\[
C = \frac{\lambda_1 - \lambda_2}{\lambda_1 + \lambda_2}
= \frac{\sqrt{\bigl( \langle f_y, f_y \rangle_w - \langle f_x, f_x \rangle_w \bigr)^2 + 4 \langle f_x, f_y \rangle_w^2}}{\langle f_x, f_x \rangle_w + \langle f_y, f_y \rangle_w} \in [0, 1]
\]

**Directionality:**

\[
J_2 = \tfrac12 \operatorname{tr}(\mathbf{s}^2) = \tfrac14 (\lambda_1 - \lambda_2)^2 = \tfrac14\, C^2 E^2 \in [0, \infty)
\]

**Fractional anisotropy:**

\[
\mathrm{FA} = \frac{\lambda_1 - \lambda_2}{\sqrt{\lambda_1^2 + \lambda_2^2}}
= \frac{\sqrt{2}\, C}{\sqrt{1 + C^2}} \in [0, 1]
\]

The coherency indicates whether the local image features are oriented or not: \(C = 1\) when the local structure has one dominant orientation, and \(C = 0\) if the image is essentially isotropic in the local neighborhood; it is the quantity that coherence-enhancing methods build on (Weickert, 1999). The fractional anisotropy (Basser & Pierpaoli, 1996) carries the same information through the one-to-one map \(\mathrm{FA} = \sqrt{2} C / \sqrt{1 + C^2}\), but normalizes the eigenvalue contrast by the Frobenius norm of the tensor, following the usage established in diffusion-tensor imaging. The directionality \(J_2\) is the second invariant of the deviator (the von Mises invariant); it is an **unnormalized** measure that grows with both contrast and alignment.

## Summary of features and invariants

Two independent scalars fix the tensor up to rotation; complete sets include \((\lambda_1, \lambda_2)\), \((I_1, J_2)\), \((E, C)\) and \((E, \mathrm{FA})\). The gradient structure tensor is \(\mathbf{J} = \langle \nabla I\, \nabla I^\top \rangle_w\) with eigenvalues \(\lambda_1 \geq \lambda_2 \geq 0\), mean \(\bar\lambda = \tfrac12 I_1\), and \(g = \lVert \nabla I \rVert\).

| Feature | Components | Eigenvalues | Correspondence | Interpretation |
|---|---|---|---|---|
| Tensor \(\mathbf{J}\) | \(\begin{bmatrix} J_{xx} & J_{xy} \\ J_{xy} & J_{yy} \end{bmatrix}\) | \(\begin{bmatrix} \lambda_1 & 0 \\ 0 & \lambda_2 \end{bmatrix}\) | — | gradient structure tensor (Bigün 1987); positive-semidefinite |
| Orientation \(\theta\) | \(\frac12 \arctan\!\left( \frac{2 J_{xy}}{J_{yy} - J_{xx}} \right)\) | \(\mathbf{e}_2\) | — | principal direction, nematic director; radians, \([-\pi/2, \pi/2]\) |
| Energy \(E\) | \(J_{xx} + J_{yy}\) | \(\lambda_1 + \lambda_2\) | \(E = I_1\) | gradient energy (Jähne 1997); units \(g^2\) |
| Coherency \(C\) | \(\frac{\sqrt{(J_{yy} - J_{xx})^2 + 4 J_{xy}^2}}{J_{xx} + J_{yy}}\) | \(\frac{\lambda_1 - \lambda_2}{\lambda_1 + \lambda_2}\) | \(C = \frac{2\sqrt{J_2}}{I_1}\) | alignment index, nematic order parameter; 0 isotropic, 1 fiber |
| Deviator \(\mathbf{s}\) | \(\mathbf{J} - \tfrac12 \operatorname{tr}(\mathbf{J})\, \mathbf{I}\) | \(\begin{bmatrix} \lambda_1 - \bar\lambda & 0 \\ 0 & \lambda_2 - \bar\lambda \end{bmatrix}\) | — | deviatoric part of \(\mathbf{J}\); \(\operatorname{tr}(\mathbf{s}) = 0\) |
| First invariant \(I_1\) | \(\operatorname{tr}(\mathbf{J})\) | \(\lambda_1 + \lambda_2\) | \(I_1 = E\) | first invariant |
| Directionality \(J_2\) | \(\tfrac14 (J_{xx} - J_{yy})^2 + J_{xy}^2\) | \(\tfrac14 (\lambda_1 - \lambda_2)^2\) | \(J_2 = \tfrac14 C^2 E^2\) | second deviatoric invariant (von Mises 1913); units \(g^4\) |
| Distortion energy \(\sigma_d\) | \(\sqrt{2}\, \lVert \mathbf{s} \rVert\) | \(\lambda_1 - \lambda_2\) | \(\sigma_d = 2\sqrt{J_2} = I_1\, \mathrm{RA}\) | equivalent uniaxial magnitude; units \(g^2\) |
| Relative anisotropy \(\mathrm{RA}\) | \(\frac{\lVert \mathbf{s} \rVert}{\sqrt{2}\, \bar\lambda}\) | \(\frac{\lambda_1 - \lambda_2}{\lambda_1 + \lambda_2}\) | \(\mathrm{RA} = C\) | coefficient of variation of the \(\lambda_i\) (Basser 1996) |
| Fractional anisotropy \(\mathrm{FA}\) | \(\frac{\sqrt{2}\, \lVert \mathbf{s} \rVert}{\lVert \mathbf{J} \rVert}\) | \(\frac{\lambda_1 - \lambda_2}{\sqrt{\lambda_1^2 + \lambda_2^2}}\) | \(\mathrm{FA} = \frac{\sqrt{2}\, C}{\sqrt{1 + C^2}}\) | degree of anisotropy (Basser 1996) |

## Typical cases

Every feature evaluated on canonical eigenvalue pairs \((\lambda_1, \lambda_2)\), from the ideal oriented case \((1, 0)\) to the isotropic case \((1, 1)\):

| \((\lambda_1, \lambda_2)\) | Structure | \(E = I_1\) | \(J_2\) | \(\sigma_d\) | \(C\) | \(\mathrm{RA}\) | \(\mathrm{FA}\) |
|---|---|---|---|---|---|---|---|
| (1, 0) | ideal oriented | 1.000 | 0.250 | 1.000 | 1.000 | 1.000 | 1.000 |
| (5, 0.2) | strong | 5.200 | 5.760 | 4.800 | 0.923 | 0.923 | 0.959 |
| (3, 1) | oriented | 4.000 | 1.000 | 2.000 | 0.500 | 0.500 | 0.632 |
| (2, 1) | moderate | 3.000 | 0.250 | 1.000 | 0.333 | 0.333 | 0.447 |
| (1, 0.5) | weak | 1.500 | 0.062 | 0.500 | 0.333 | 0.333 | 0.447 |
| (1, 0.9) | near-isotropic | 1.900 | 0.002 | 0.100 | 0.053 | 0.053 | 0.074 |
| (1, 1) | isotropic | 2.000 | 0.000 | 0.000 | 0.000 | 0.000 | 0.000 |
| (0, 0) | flat | 0.000 | 0.000 | — | — | — | — |

## The color survey

The default visual output of *Analysis* encodes the three features in one image: **hue** = orientation, **saturation** = coherency, **brightness** = the original image — so strongly aligned structures appear saturated in the color of their direction, while flat or isotropic regions stay gray.
