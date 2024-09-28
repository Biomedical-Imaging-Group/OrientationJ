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
// 01.01.2008 Katarina Balac: Creation of the class
//====================================================================
package polyharmonicwavelets;


/**
* This class stores all the parameters neded to perform the wavelet transform.
*/

public class Parameters {

	/**
	* If true, only compute the analyses filters.
	*/	
	public boolean analysesonly=false;
	/**
	* If rieszfreq=1 analysis wavelet filter will be multiplied by V2.
	* Used for Riesz transform.
	* No need to change this, it is not a user input.
	*/
	public int rieszfreq=0; 
	/**
	* Constant that defines the polyharmonic B-spline flavor.
	*/											
	public final static int BSPLINE=0;
	/**
	* Constant that defines the orthogonal flavor, quincunx only.
	*/
	public final static int ORTHOGONAL=1;
	/**
	* Constant that defines the dual of the B-spline.
	*/
	public final static int DUAL=2;
	/**
	* Constant that defines the operator wavelet.
	*/
	public final static int OPERATOR=3;
	/**
	* Constant that defines the Marr wavelet.
	*/
	public final static int MARR=7;
	/**
	* Constant that defines the dual of the operator.
	*/
	public final static int DUALOPERATOR=8;
	/**
	* Defines the wavelet flavor: BSPLINE, ORTHOGONAL, DUAL, OPERATOR, MARR or DUALOPERATOR.
	*/
	public int flavor=ORTHOGONAL;
	
	/**
	* Constant that defines the basis transform.
	*/
	public final static int BASIS=0;
	/**
	* Constant that defines the fully redundant transform.
	*/
	public final static int REDUNDANT=1;
	/**
	* Constant that defines the pyramid transform.
	*/
	public final static int PYRAMID=2;	
	/**
	* The redundancy, should be set to BASIS, PYRAMID, or REDUNDANT.
	*/
	public int redundancy=PYRAMID;
	/**
	* Constant that defines standard isotropic polyharmonic Bspline.
	*/
	public final static int ISOTROPIC=1;
	/**
	* Constant that defines isotropic polyharmonic Bspline that allowes to change standard deviation.
	*/
	public final static int CHANGESIGMA=4;
	/**
	* Te isotropy type.
	*/
	public int type=ISOTROPIC;
	/**
	* Defines the standard deviation of gaussian if type=changesigma
	*/
	public double s2=6.0;   //only used if type=changesigma   
	/**
	* Constant that defines the quincunx lattice.
	*/
	public final static int QUINCUNX=0;
	/**
	* Constant that defines the dyadic lattice.
	*/
	public final static int DYADIC=1;
	/**
	* The lattice type, set to QUINCUNX or DYADIC.
	*/
	public int lattice=DYADIC;
	
	/**
	* This parameter defines whether the prefilter should be used (if true) or not (if false).
	*/
	public boolean prefilter=true;
	
	/**
	* The B-spline order, gamma.
	*/
	public double order=2.0;  
	
	/**
	* The iterate of a rotation covariant operator. Usually 0 for polyharmonic wavelets or 1 for Marr wavelets, but other values are also possible.
	*/
	public int N=1;  
	
	/**
	* The number of wavelet decomposition levels.
	*/
	public int J=1; 
	
	public String toString() {
		String s = "";
		
		s += "analysesonly: " + analysesonly + " ::: flavor: " + flavor + " ::: redundancy: " + redundancy + " ::: type: " + type + " \n";
		s += "lattice: " + lattice + " :::  prefilter: " + prefilter + " \n";
		s += "order: " + order + " ::: N: " + N + " ::: J: " + J + " \n";
		return s;
		
	}
}
