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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import orientation.filters.Hessian;
import orientation.imageware.ImageWare;

public class OrientationProcess extends Thread {

	private GroupImage				gim;
	private OrientationParameters	params;
	private LogAbstract				log;
	private ImageWare				source;

	public OrientationProcess(LogAbstract log, ImageWare source, OrientationParameters params) {
		this.log = log;
		this.source = source;
		this.params = params;
	}
	
	public GroupImage getGroupImage() {
		return gim;
	}

	@Override
	public void run() {
		log.reset();

		gim = new GroupImage(log, source, params);

		if (params.gradient == OrientationParameters.HESSIAN)
			new Hessian(log, gim, params).run();
		else
			new Gradient(log, gim, params).run();

		StructureTensor st = new StructureTensor(log, gim, params);
		ExecutorService executor = Executors.newFixedThreadPool(1);
		executor.execute(st);
		executor.shutdown();
		while (!executor.isTerminated()) {
		}
		log.finish();
	}


}
