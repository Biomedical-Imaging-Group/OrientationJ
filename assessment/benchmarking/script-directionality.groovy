// Benchmarking tools — Fiji Directionality plugin (Local gradient method)
// on the 4 benchmark images, through the plugin's Java API so it also runs
// headless (the display_table of the macro needs a GUI). Writes one CSV per
// image in results/fiji/: Direction (degrees, bin centers) and Frequency
// (normalized histogram).
//
// The Directionality plugin analyses the whole image: it has no mask support,
// which is part of what this benchmark shows.
//
// Run: Fiji.app/Contents/MacOS/ImageJ-macosx --headless --console script-directionality.groovy
// or in Fiji: File > Open... then Run.

import ij.IJ
import fiji.analyze.directionality.Directionality_

root = "/Users/dsage/Desktop/dev/OrientationJ/"
input = root + "test-images/images/"
output = root + "assessment/benchmarking/results/fiji/"
names = ["synthetic_rings_dither_512", "synthetic_nematic_512",
         "synthetic_noise_512", "collagen"]

new File(output).mkdirs()

for (name in names) {
    imp = IJ.openImage(input + name + ".tif")
    d = new Directionality_()
    d.setImagePlus(imp)
    d.setMethod(Directionality_.AnalysisMethod.LOCAL_GRADIENT_ORIENTATION)
    d.setBinNumber(180)
    d.setBinRange(-90, 90)
    d.setBuildOrientationMapFlag(false)
    d.computeHistograms()
    bins = d.getBins()                 // bin centers
    histogram = d.getHistograms().get(0)

    file = new File(output + "Directionality-" + name + ".csv")
    file.withWriter { writer ->
        writer.writeLine("Direction,Frequency")
        for (i = 0; i < bins.length; i++)
            writer.writeLine(bins[i] + "," + histogram[i])
    }
    println("saved " + file + "  (bins " + bins[0] + " ... " + bins[bins.length - 1] + ")")
    imp.close()
}
println("done")
