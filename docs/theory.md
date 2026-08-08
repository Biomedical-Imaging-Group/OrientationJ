# Theory

The complete derivation — weighted inner product, directional derivative,
eigen-analysis, features and tensor invariants, typical cases — is in the
**[theoretical background PDF](assets/theoretical-background.pdf)**. This page
summarizes the essentials.

## The structure tensor

For an image \(f(x, y)\), the gradient structure tensor evaluated over a local
neighborhood \(w\) is

\[
J = \begin{bmatrix}
\langle f_x^2 \rangle_w & \langle f_x f_y \rangle_w \\
\langle f_x f_y \rangle_w & \langle f_y^2 \rangle_w
\end{bmatrix}
\]

where \(f_x\) and \(f_y\) are the partial derivatives and \(\langle \cdot \rangle_w\) denotes
weighted averaging over the neighborhood, typically with a Gaussian of standard
deviation \(\sigma\) — the **local window** of the dialogs.

## Orientation

The visible structures (fibers, edges) are aligned with the eigenvector of the
*smallest* eigenvalue — the gradient is perpendicular to the structures — so the
orientation reported by OrientationJ is

\[
\theta = \frac{1}{2}\arctan\!\left(\frac{2\langle f_x f_y \rangle_w}{\langle f_y^2 \rangle_w - \langle f_x^2 \rangle_w}\right)
\in \left[-\tfrac{\pi}{2}, \tfrac{\pi}{2}\right]
\]

measured counter-clockwise from the horizontal of the displayed image.

## Coherency

With \(\lambda_{\max}\) and \(\lambda_{\min}\) the eigenvalues of \(J\),

\[
C = \frac{\lambda_{\max} - \lambda_{\min}}{\lambda_{\max} + \lambda_{\min}} \in [0, 1]
\]

\(C = 1\) means a perfectly oriented local structure, \(C = 0\) means isotropic. Coherency
is what makes the orientation estimate interpretable: an angle reported for a region with
near-zero coherency is meaningless.

## Energy

The energy is the trace of the tensor,

\[
E = \operatorname{tr}(J) = \lambda_{\max} + \lambda_{\min} = \langle f_x^2 \rangle_w + \langle f_y^2 \rangle_w
\]

— the local gradient energy. It separates flat regions (nothing to orient) from
structured ones, which is why the color survey and the distribution can weight or
select pixels by energy. Unlike coherency it is unbounded; since version 2.1.0 the
display offers it either linearly rescaled to \([0, 1]\) or raw.

## The color survey

The default visual output of *Analysis* encodes the three features in one image:
**hue** = orientation, **saturation** = coherency, **brightness** = the original
image — so strongly aligned structures appear saturated in the color of their
direction, while flat or isotropic regions stay gray.

## Full derivation

The [theoretical background note](assets/theoretical-background.pdf) also covers the
deviator, the second invariant \(J_2\) (directionality), relative and fractional
anisotropy, and evaluates every feature on canonical eigenvalue pairs.
