// Operator — the reference maps of the plugin for gst_operator.ipynb.
// OrientationJ Analysis (cubic spline, tensor window sigma = 2) on the 16 test
// images; saves orientation (radians), coherency and energy (unscaled) as
// 32-bit TIFFs in results/fiji/.
//
// Run in Fiji: Plugins > Macros > Run..., or headless:
//   Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-reference.ijm

root = "/Users/dsage/Desktop/dev/OrientationJ/";
input = root + "test-images/images/";
output = root + "assessment/gst_operator/results/fiji/";
sigma = 2;

if (!File.exists(input)) exit("Input folder not found: " + input);
File.makeDirectory(root + "assessment/gst_operator/results/");
File.makeDirectory(output);

list = getFileList(input);
features = newArray("Orientation", "Coherency", "Energy");

run("Close All");
setBatchMode(true);

count = 0;
for (i = 0; i < list.length; i++) {
	if (!endsWith(list[i], ".tif")) continue;
	name = substring(list[i], 0, lengthOf(list[i]) - 4);   // replace() would take ".tif" as a regex
	open(input + list[i]);
	// gradient=0: cubic spline; radian=on: orientation in radians;
	// scale-energy=off: the raw trace of the tensor, as computed
	run("OrientationJ Analysis", "tensor=" + sigma + " gradient=0 "
	    + "orientation=on coherency=on energy=on radian=on scale-energy=off ");
	for (f = 0; f < features.length; f++) {
		selectWindow("OJ-" + features[f] + "-1");
		saveAs("Tiff", output + name + "-" + toLowerCase(features[f]) + ".tif");
	}
	run("Close All");
	count++;
}

setBatchMode(false);
print("done: " + count + " images, 3 maps each, in " + output);
