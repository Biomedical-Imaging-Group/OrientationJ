// Benchmarking tools — OrientationJ Analysis (cubic spline) on the 4 benchmark
// images. Saves the orientation map in degrees as 32-bit TIFF in results/fiji/,
// twice per image: with the tensor window sigma = 1 (the plugin), and with
// sigma = 0, i.e. no tensor smoothing at all (the "direct gradient" of the
// comparison). benchmark_orientation.ipynb applies the mask and bins them.
//
// Run in Fiji: Plugins > Macros > Run..., or headless:
//   Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-orientationj.ijm

root = "/Users/dsage/Desktop/dev/OrientationJ/";
input = root + "test-images/images/";
output = root + "assessment/benchmarking/results/fiji/";

if (!File.exists(input)) exit("Input folder not found: " + input);
File.makeDirectory(root + "assessment/benchmarking/results/");
File.makeDirectory(output);

names = newArray("synthetic_rings_dither_512", "synthetic_nematic_512",
                 "synthetic_noise_512", "collagen");
sigmas = newArray(1, 0);
suffixes = newArray("", "-sigma0");

run("Close All");
setBatchMode(true);

for (n = 0; n < names.length; n++) {
	for (s = 0; s < sigmas.length; s++) {
		open(input + names[n] + ".tif");
		// gradient=0: cubic spline; radian=off: orientation map in degrees
		run("OrientationJ Analysis", "tensor=" + sigmas[s] + " gradient=0 orientation=on radian=off ");
		selectWindow("OJ-Orientation-1");
		saveAs("Tiff", output + "OJ-Orientation-" + names[n] + suffixes[s] + ".tif");
		run("Close All");
	}
}

setBatchMode(false);
print("done: " + 2 * names.length + " orientation maps in " + output);
