package dmodel.pipeline.rt.pcm.usagemodel.util;

import java.util.Comparator;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import dmodel.pipeline.dt.mmmodel.MmmodelFactory;
import dmodel.pipeline.dt.mmmodel.UsageServiceCallDescriptor;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.monitoring.util.ServiceParametersWrapper;

public class UsageServiceUtil {

	public static Comparator<ServiceCallRecord> COMPARE_SCAL_ENTRY_TIME_ASCENDING = (a, b) -> {
		if (a.getEntryTime() > b.getEntryTime()) {
			return -1;
		} else if (a.getEntryTime() < b.getEntryTime()) {
			return 1;
		} else {
			return 0;
		}
	};

	public static UsageServiceCallDescriptor createDescriptor(ServiceCallRecord rec) {
		if (rec == null) {
			return null;
		}

		UsageServiceCallDescriptor ret = MmmodelFactory.eINSTANCE.createUsageServiceCallDescriptor();

		ret.setServiceId(rec.getServiceId());
		for (Entry<String, Object> entry : ServiceParametersWrapper.buildFromJson(rec.getParameters()).getParameters()
				.entrySet()) {
			EList<Object> nList = new BasicEList<>();
			nList.add(entry.getValue());
			ret.getParameterValues().put(entry.getKey(), nList);
		}

		return ret;
	}

}
