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

import java.util.Vector;

public class Cluster implements Comparable<Cluster> {

	public Vector<Cluster> list = null;
	private double costBest;
	public int indexBest;
	public int x;
	public int y;
	public int mx;
	public int my;
	public double dx;
	public double dy;
	public double coherency;
	public double energy;
	private double intensity;
	public int count;

	public Cluster(int x, int y, int mx, int my, double dx, double dy, double coherency, double energy) {
		this.x = x;
		this.y = y;
		this.mx = mx;
		this.my = my;
		this.dx = dx;
		this.dy = dy;
		this.coherency = coherency;
		this.energy = energy;
		list = new Vector<Cluster>();
	}

	public void connect(Vector<Cluster> clusters, int index, int nx, int ny) {
		int k;
		int nc = clusters.size();
		for (int i = -1; i <= 1; i++)
			for (int j = -1; j <= 1; j++) {
				if (i != 0 || j != 0) {
					int xi = (int) x + i;
					int yi = (int) y + j;
					if (xi >= 0 && yi >= 0)
						if (xi < nx && yi < ny) {
							k = xi + yi * nx;
							if (k >= 0)
								if (k < nc)
									list.add(clusters.get(k));
						}
				}
			}
	}

	public void measure() {
		int n = list.size();
		costBest = -Double.MAX_VALUE;
		indexBest = -1;
		if (n == 0)
			return;
		double cost = 0.0;
		for (int i = 0; i < n; i++) {
			cost = 500 - Math.abs(intensity - list.get(i).intensity);
			if (costBest < cost) {
				costBest = cost;
				indexBest = i;
			}
		}
	}

	@Override
	public int compareTo(Cluster test) {
		return (test.costBest > costBest ? 1 : 0);
	}

	@Override
	public String toString() {
		if (indexBest >= 0) {
			String s = "(" + x + "," + y + ") " + " -- " + costBest + " sizeliste:" + list.size() + " ";
			for (int i = 0; i < list.size(); i++) {
				if (i == indexBest)
					s += "**";
				s += "(" + list.get(i).x + "," + list.get(i).y + ") ";
			}
			return s;
		}
		return "(" + x + "," + y + ") " + " -- " + costBest + " -> nothing";
	}
}
