package dmodel.base.shared.vitruv;

import tools.vitruv.dsls.reactions.meta.correspondence.reactions.ReactionsPackage;
import tools.vitruv.framework.correspondence.CorrespondencePackage;

/**
 * 
 * Utility for initializing all model types that are used from Vitruvius.
 * 
 * @author David Monschein
 *
 */
public class VitruviusUtil {

	/**
	 * Loads all model types that are used by Vitruvius.
	 */
	public static void initVitruv() {
		CorrespondencePackage.eINSTANCE.eClass();
		ReactionsPackage.eINSTANCE.eClass();
	}

}
