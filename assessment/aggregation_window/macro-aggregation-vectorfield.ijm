// Aggregation window — OrientationJ Vector Field (cubic spline, sigma = 1, nematic
// aggregation of the cell, the default since 2.2.0) with
// increasing grid-cell size, on 4 images. For every (image, grid) it saves the
// OJ-Vector-Field results table as CSV in results/fiji/; aggregation_window.ipynb
// draws and animates the fields.
//
// The results table cannot be saved in headless mode (no window manager), so
// run this macro in Fiji: Plugins > Macros > Run..., or from the command line
// with the GUI and an automatic quit:
//   Fiji.app/Contents/MacOS/ImageJ-macosx -macro macro-aggregation-vectorfield.ijm quit

root = "/Users/dsage/Desktop/dev/OrientationJ/";
input = root + "test-images/images/";
output = root + "assessment/aggregation_window/results/fiji/";

if (!File.exists(input)) exit("Input folder not found: " + input);
File.makeDirectory(root + "assessment/aggregation_window/results/");
File.makeDirectory(output);

names = newArray("synthetic_wave_512", "synthetic_chirp_1024",
                 "collagen", "cell_aemisegger");
grids = newArray(8, 12, 16, 24, 32, 48, 64);

run("Close All");
setBatchMode(true);

for (n = 0; n < names.length; n++) {
	for (g = 0; g < grids.length; g++) {
		open(input + names[n] + ".tif");
		run("OrientationJ Vector Field", "tensor=1 gradient=0 vectorgrid=" + grids[g]
			+ " vectoraggregation=nematic"
			+ " vectorscale=100 vectortype=0 vectortable=on vectoroverlay=off ");
		selectWindow("OJ-Table-Vector-Field-");
		saveAs("Text", output + "OJ-Vectors-" + names[n] + "-g" + grids[g] + ".csv");
		run("Close");
		run("Close All");
	}
	print(names[n] + " done");
}

setBatchMode(false);
print("done: " + names.length * grids.length + " vector tables in " + output);

// passed as macro argument by the command-line runner; quits Fiji when done
if (getArgument() == "quit")
	eval("script", "java.lang.System.exit(0);");
