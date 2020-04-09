package dmodel.pipeline.vsum.mapping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tools.vitruv.framework.correspondence.CorrespondenceFactory;
import tools.vitruv.framework.correspondence.CorrespondenceModel;
import tools.vitruv.framework.correspondence.Correspondences;
import tools.vitruv.framework.domains.VitruvDomain;
import tools.vitruv.framework.domains.VitruvDomainProvider;

@Service
public class VsumMappingPersistence {
	@Autowired
	private List<VitruvDomainProvider<? extends VitruvDomain>> domainProviders;

	public Correspondences buildStorableCorrespondeces(CorrespondenceModel model) {
		Correspondences output = CorrespondenceFactory.eINSTANCE.createCorrespondences();

		model.getAllCorrespondences().forEach(cp -> {
		});

		return output;
	}

}
