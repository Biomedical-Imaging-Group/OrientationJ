// Benchmarking tools — OrientationJ Analysis (cubic spline, sigma = 1)
// on the 4 benchmark images. Saves the orientation map in degrees as 32-bit TIFF
// in results/fiji/; benchmark_orientation.ipynb applies the mask and bins it.
//
// Run in Fiji: Plugins > Macros > Run..., or headless:
//   Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-orientationj.ijm

root = "/Users/dsage/Desktop/dev/OrientationJ/";
input = root + "orientationj-test-images/";
output = root + "assessment/benchmarking_tools/results/fiji/";

if (!File.exists(input)) exit("Input folder not found: " + input);
File.makeDirectory(root + "assessment/benchmarking_tools/results/");
File.makeDirectory(output);

names = newArray("synthetic_rings_dither_512", "synthetic_nematic_512",
                 "synthetic_noise_512", "collagen");

run("Close All");
setBatchMode(true);

for (n = 0; n < names.length; n++) {
	open(input + names[n] + ".tif");
	// gradient=0: cubic spline; radian=off: orientation map in degrees
	run("OrientationJ Analysis", "tensor=1 gradient=0 orientation=on radian=off ");
	selectWindow("OJ-Orientation-1");
	saveAs("Tiff", output + "OJ-Orientation-" + names[n] + ".tif");
	run("Close All");
}

setBatchMode(false);
print("done: " + names.length + " orientation maps in " + output);
