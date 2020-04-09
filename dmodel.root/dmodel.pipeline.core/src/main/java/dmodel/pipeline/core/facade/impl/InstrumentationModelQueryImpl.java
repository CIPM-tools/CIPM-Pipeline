package dmodel.pipeline.core.facade.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;

import dmodel.pipeline.core.ISpecificModelProvider;
import dmodel.pipeline.core.facade.IInstrumentationModelQueryFacade;
import dmodel.pipeline.dt.inmodel.InstrumentationMetamodel.ServiceInstrumentationPoint;

@Component
public class InstrumentationModelQueryImpl implements IInstrumentationModelQueryFacade {
	@Autowired
	private ISpecificModelProvider modelProvider;

	@Override
	public Set<ServiceInstrumentationPoint> getFineGrainedInstrumentedServices() {
		return modelProvider.getInstrumentation().getPoints().stream().filter(instr -> {
			return instr.getActionInstrumentationPoints().stream().anyMatch(ac -> ac.isActive());
		}).collect(Collectors.toSet());
	}

	@Override
	public Set<String> getInstrumentedActionIds() {
		Set<String> ret = Sets.newHashSet();
		getFineGrainedInstrumentedServices().stream().forEach(service -> {
			ret.add(service.getService().getId());
			service.getActionInstrumentationPoints().stream().forEach(action -> {
				if (action.isActive()) {
					ret.add(action.getAction().getId());
				}
			});
		});
		return ret;
	}

}
