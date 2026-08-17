// Gradient comparison — OrientationJ Analysis with each of the 5 gradient
// methods (cubic spline, finite difference, Fourier, Riesz, Gaussian; no
// Hessian), structure tensor sigma = 1, on 4 images. Saves the orientation map
// in degrees (32-bit TIFF) in results/fiji/; gradients.ipynb measures the
// angular error against the analytic ground truth.
//
// Run in Fiji: Plugins > Macros > Run..., or headless:
//   Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-gradients.ijm

root = "/Users/dsage/Desktop/dev/OrientationJ/";
input = root + "test-images/images/";
output = root + "assessment/gradients/results/fiji/";

if (!File.exists(input)) exit("Input folder not found: " + input);
File.makeDirectory(root + "assessment/gradients/results/");
File.makeDirectory(output);

names = newArray("synthetic_chirp_1024", "synthetic_rings_dither_512",
                 "synthetic_wave_512", "synthetic_noise_512");

// index = OrientationJ gradient code
gradients = newArray("spline", "finite-difference", "fourier", "riesz", "gaussian");

run("Close All");
setBatchMode(true);

for (n = 0; n < names.length; n++) {
	for (g = 0; g < gradients.length; g++) {
		open(input + names[n] + ".tif");
		run("OrientationJ Analysis", "tensor=1 gradient=" + g
			+ " orientation=on radian=off ");
		selectWindow("OJ-Orientation-1");
		saveAs("Tiff", output + "OJ-Orientation-" + names[n] + "-" + gradients[g] + ".tif");
		run("Close All");
	}
	print(names[n] + " done");
}

setBatchMode(false);
print("done: " + names.length * gradients.length + " orientation maps in " + output);
