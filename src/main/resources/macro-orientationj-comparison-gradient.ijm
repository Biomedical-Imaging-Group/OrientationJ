// Macro OrientationJ
// Comparison of gradient methods (isotropic properties vs runtime)
//
run("Chirp Image Custom", "width=512 height=512");
selectWindow("Chirp");
run("OrientationJ Distribution", "tensor=1.0 gradient=0 radian=on histogram=on min-coherency=20.0 min-energy=10.0 ");
rename("Histogram Cubic Spline)");
selectWindow("Chirp");
run("OrientationJ Distribution", "tensor=1.0 gradient=1 radian=on histogram=on min-coherency=20.0 min-energy=10.0 ");
rename("Histogram (Finite Difference)");
selectWindow("Chirp");
run("OrientationJ Distribution", "tensor=1.0 gradient=2 radian=on histogram=on min-coherency=20.0 min-energy=10.0 ");
rename("Histogram (Fourier)");
selectWindow("Chirp");
run("OrientationJ Distribution", "tensor=1.0 gradient=4 radian=on histogram=on min-coherency=20.0 min-energy=10.0 ");
rename("Histogram (Gaussian)");
