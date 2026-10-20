// Feature maps of the test images, for local use: ../results/maps/ is not tracked.
// OrientationJ Analysis at the plugin defaults (cubic-spline gradient, tensor
// window sigma = 1) on a selection of the images in ../images: orientation
// (degrees), coherency and energy (unscaled) as 32-bit TIFFs in ../results/maps/.
//
// Run in Fiji: Plugins > Macros > Run..., or headless:
//   Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-maps.ijm

root = "/Users/dsage/Desktop/dev/OrientationJ/";
input = root + "test-images/images/";
output = root + "test-images/results/maps/";

if (!File.exists(input)) exit("Input folder not found: " + input);
File.makeDirectory(root + "test-images/results/");
File.makeDirectory(output);

// a selection of the images (all 16 would take 128 MB) -- extend and rerun at will
names = newArray("collagen", "cell_aemisegger", "synthetic_chirp_1024");
features = newArray("Orientation", "Coherency", "Energy");

run("Close All");
setBatchMode(true);

for (n = 0; n < names.length; n++) {
	open(input + names[n] + ".tif");
	run("OrientationJ Analysis", "tensor=1 gradient=0 "
	    + "orientation=on coherency=on energy=on radian=off scale-energy=off ");
	for (f = 0; f < features.length; f++) {
		selectWindow("OJ-" + features[f] + "-1");
		saveAs("Tiff", output + names[n] + "-" + toLowerCase(features[f]) + ".tif");
	}
	run("Close All");
}

setBatchMode(false);
print("done: " + names.length + " images, 3 maps each, in " + output);
