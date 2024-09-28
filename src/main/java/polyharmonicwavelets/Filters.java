//====================================================================
//
// Project: 
// Multiscale Directional Analysis
// 
// Organization: 
// Danie Sage
// Biomedical Imaging Group (BIG)
// Ecole Polytechnique Fédérale de Lausanne (EPFL)
// Lausanne, Switzerland
//
// Information:
// http://bigwww.epfl.ch/demo/monogenic
//
// Conditions of use:
// You'll be free to use this software for research purposes, but you
// should not redistribute it without our consent. In addition, we 
// expect you to include a citation or acknowledgement whenever 
// you present or publish results that are based on it.
//
// History:
// 25.08.2009 Daniel Sage: Completely revamp this class
//====================================================================

package polyharmonicwavelets;

public abstract class Filters {

	public ComplexImage[] FA;		// Analysis Filter,  FA[0] the lowpass, FA[1], FA[2], FA[3] the highpass
    public ComplexImage[] FS;		// Synthesis filters, FS[0] the lowpass, FS[1], FS[2], FS[3] the highpass
	public ComplexImage[] FP;		// Pyramid synthesis filters
  	public ComplexImage P;			// Prefilter
	public ComplexImage ac;			// Autocorrelation	
	protected Parameters param;     // Parameters for the transform	
	protected int nx;				// Size of filters
	protected int ny;				// Size of filters
	
	public Filters(Parameters param, int nx, int ny) {
		this.param = param;
		this.nx = nx;
		this.ny = ny;
		FA = new ComplexImage[4];
		FS = new ComplexImage[4];
	}

	public void setParameters(Parameters param) {
		this.param = param;
	}
	
	public abstract void compute();

}