// Benchmarking tools — Fiji Directionality plugin (Local gradient method)
// on the 4 benchmark images. Saves each histogram table as CSV in results/fiji/.
//
// The Directionality plugin analyses the whole image: it has no mask support,
// which is part of what this benchmark shows.
//
// Run in Fiji: Plugins > Macros > Run..., or headless:
//   Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console -macro macro-directionality.ijm

root = "/Users/dsage/Desktop/dev/OrientationJ/";
input = root + "orientationj-test-images/";
output = root + "assessment/benchmarking_tools/results/fiji/";

if (!File.exists(input)) exit("Input folder not found: " + input);
File.makeDirectory(root + "assessment/benchmarking_tools/results/");
File.makeDirectory(output);

names = newArray("synthetic_rings_dither_512", "synthetic_nematic_512",
                 "synthetic_noise_512", "collagen");

run("Close All");

for (n = 0; n < names.length; n++) {
	open(input + names[n] + ".tif");
	run("Directionality", "method=[Local gradient] nbins=180 histogram_start=-90 histogram_end=90 display_table");
	// the plugin opens a text window "Directionality histograms for <title> ..."
	titles = getList("window.titles");
	for (i = 0; i < titles.length; i++) {
		if (startsWith(titles[i], "Directionality histograms")) {
			selectWindow(titles[i]);
			saveAs("Text", output + "Directionality-" + names[n] + ".csv");
			run("Close");
		}
	}
	run("Close All");
}

print("done: " + names.length + " histograms in " + output);
