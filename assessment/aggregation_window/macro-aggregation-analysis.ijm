// Aggregation window — OrientationJ Analysis (cubic spline) over a range of
// structure-tensor sigmas, on 4 images. For every (image, sigma) it saves in
// results/fiji/ the orientation map in degrees (32-bit TIFF) and the HSB
// color survey (PNG); aggregation_window.ipynb bins, plots and animates them.
//
// Run in Fiji: Plugins > Macros > Run..., or headless:
//   Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-aggregation-analysis.ijm

root = "/Users/dsage/Desktop/dev/OrientationJ/";
input = root + "test-images/images/";
output = root + "assessment/aggregation_window/results/fiji/";

if (!File.exists(input)) exit("Input folder not found: " + input);
File.makeDirectory(root + "assessment/aggregation_window/results/");
File.makeDirectory(output);

names = newArray("synthetic_wave_512", "synthetic_chirp_1024",
                 "collagen", "cell_aemisegger");
sigmas = newArray(1, 2, 3, 4, 5, 6, 8, 10);

run("Close All");
setBatchMode(true);

for (n = 0; n < names.length; n++) {
	for (s = 0; s < sigmas.length; s++) {
		open(input + names[n] + ".tif");
		run("OrientationJ Analysis", "tensor=" + sigmas[s]
			+ " gradient=0 orientation=on color-survey=on radian=off ");
		selectWindow("OJ-Orientation-1");
		saveAs("Tiff", output + "OJ-Orientation-" + names[n] + "-s" + sigmas[s] + ".tif");
		selectWindow("OJ-Color-survey-1");
		saveAs("PNG", output + "OJ-Survey-" + names[n] + "-s" + sigmas[s] + ".png");
		run("Close All");
	}
	print(names[n] + " done");
}

setBatchMode(false);
print("done: " + names.length * sigmas.length + " sigmas x images in " + output);
