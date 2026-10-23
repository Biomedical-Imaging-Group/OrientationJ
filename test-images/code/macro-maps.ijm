// Feature maps of the test images, for local use: ../results/maps/ is not tracked.
// OrientationJ Analysis at the plugin defaults (cubic-spline gradient, tensor
// window sigma = 1) on every image of ../images: orientation (degrees),
// coherency and energy (unscaled) as 32-bit TIFFs in ../results/maps/.
//
// Run in Fiji: Plugins > Macros > Run..., or headless:
//   Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-maps.ijm

root = "/Users/dsage/Desktop/dev/OrientationJ/";
input = root + "test-images/images/";
output = root + "test-images/results/maps/";

if (!File.exists(input)) exit("Input folder not found: " + input);
File.makeDirectory(root + "test-images/results/");
File.makeDirectory(output);

files = getFileList(input);
features = newArray("Orientation", "Coherency", "Energy");

run("Close All");
setBatchMode(true);

count = 0;
for (n = 0; n < files.length; n++) {
	if (!endsWith(files[n], ".tif")) continue;
	name = substring(files[n], 0, lengthOf(files[n]) - 4);
	open(input + files[n]);
	run("OrientationJ Analysis", "tensor=1 gradient=0 "
	    + "orientation=on coherency=on energy=on radian=off scale-energy=off ");
	for (f = 0; f < features.length; f++) {
		selectWindow("OJ-" + features[f] + "-1");
		saveAs("Tiff", output + name + "-" + toLowerCase(features[f]) + ".tif");
	}
	run("Close All");
	count++;
	print(name);
}

setBatchMode(false);
print("done: " + count + " images, 3 maps each, in " + output);
