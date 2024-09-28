//=============================================================================================================
//
// Project: Directional Image Analysis - OrientationJ plugins
// 
// Author: Daniel Sage
// 
// Organization: Biomedical Imaging Group (BIG)
// Ecole Polytechnique Federale de Lausanne (EPFL), Lausanne, Switzerland
//
// OrientationJ: https://bigwww.epfl.ch/demo/orientation/
// MonogenicJ: https://bigwww.epfl.ch/demo/monogenic/
// Source code: https://github.com/Biomedical-Imaging-Group/OrientationJ
//  
// Reference on OrientationJ:
// Z. Püspöki, M. Storath, D. Sage, M. Unser
// Transforms and Operators for Directional Bioimage Analysis: A Survey 
// Focus on Bio-Image Informatics, Springer International Publishing, 2016.
//
// Reference on MonogenicJ:
// M. Unser, D. Sage, D. Van De Ville
// Multiresolution Monogenic Signal Analysis Using the Riesz-Laplace Wavelet Transform
// IEEE Transactions on Image Processing, 2009.
//
// Conditions of use: We expect you to include adequate citations and 
// acknowledgments whenever you present or publish results that are based on it.
//
// License: GNU GPLv3 <http://www.gnu.org/licenses/gpl-3.0.html>
//=============================================================================================================

package orientation;

import ij.measure.ResultsTable;
import orientation.imageware.Builder;
import orientation.imageware.ImageWare;

public class OrientationKMeans {
	private boolean valueChanged;
	private int[] regions;

	private double[] osin;
	private double[] ocos;
	private double[] classSin;
	private double[] classCos;
	
	public ImageWare run(ImageWare orientation, int nbClasses, int maxIterations) {
		int ny = orientation.getHeight();
		int nx = orientation.getWidth();
		int size = nx*ny;
		float angles[] = orientation.getSliceFloat(0);
		osin = new double[size];
		ocos = new double[size];
		for(int i=0; i<size; i++) {
			osin[i] = Math.sin(angles[i]);
			ocos[i] = Math.cos(angles[i]);
		}
		
		float classAngle[] = new float[nbClasses];
        classSin = new double[nbClasses];
        classCos = new double[nbClasses];
        double max = orientation.getMaximum();
        double min = orientation.getMinimum();

        for(int i = 0; i < nbClasses; i++) {
        		classAngle[i] =(float)( i*(max-min)/nbClasses + min);
        		classSin[i] = Math.sin(classAngle[i]);
        		classCos[i] = Math.cos(classAngle[i]);
        }

        regions = new int[size];
        int iterations = 0;
        do {
        		iterate();
        		update(classAngle);
        		iterations++;
         } while(valueChanged && iterations < maxIterations);
         
        ImageWare out = Builder.create(nx, ny, 1, ImageWare.FLOAT);
        float[] pixout = out.getSliceFloat(0);
        for(int i=0; i<size; i++) {
        		pixout[i] = classAngle[regions[i]];
        }

		ResultsTable table = new ResultsTable();
		
		for(int k=0; k<nbClasses; k++) {
			table.addValue("Class k", (k+1));
			table.addValue("Orientation [Degree]", Math.toDegrees(classAngle[k]));
		}
		table.show("OJ-Table-Vector-Field-");

        return out;
	}

	private void iterate(){
		int size = osin.length;
		int nbClasses = classSin.length;
		valueChanged = false;
		double current, distance;
		for(int i=0; i<size; i++) {
			current = cost(i, regions[i]);
			for(int k=0; k<nbClasses; k++) {
				distance = cost(i, k);
				if((distance > current)) {
					current = distance;
					regions[i] = k;
					valueChanged = true;
				}
			}
        }
	}

	private double cost(int p, int m) {
		return Math.abs(ocos[p] * classCos[m] + osin[p]*classSin[m]);
	}

	private void update(float classAngle[]) {
		int nbClasses = classAngle.length;
		for(int k=0; k<nbClasses; k++) {
			double dx = 0.0;
			double dy = 0.0;
			for(int j=0; j<regions.length; j++) {
				if(regions[j] == k) {
					dx += ocos[j];
					dy += osin[j];
				}
			}
			double angle = Math.atan2(dy, dx);
			if (angle > Math.PI/2)
				angle -= Math.PI/2;
			if (angle < -Math.PI/2)
				angle += Math.PI/2;
			classAngle[k] = (float)angle;
	        classSin[k] = Math.sin(angle);
	        classCos[k] = Math.cos(angle);
		}
	}
}