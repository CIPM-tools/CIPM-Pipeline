package dmodel.pipeline.rt.pipeline.blackboard;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.core.facade.ICoreBlackboardQueryFacade;
import dmodel.pipeline.core.facade.IInstrumentationModelQueryFacade;
import dmodel.pipeline.core.facade.IPCMQueryFacade;
import dmodel.pipeline.core.facade.IResettableQueryFacade;
import dmodel.pipeline.core.facade.IRuntimeEnvironmentQueryFacade;
import dmodel.pipeline.rt.pipeline.blackboard.facade.IPipelineHealthQueryFacade;
import dmodel.pipeline.rt.pipeline.blackboard.facade.IValidationQueryFacade;
import dmodel.pipeline.rt.pipeline.blackboard.facade.IValidationResultsQuery;
import dmodel.pipeline.vsum.facade.ISpecificVsumFacade;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@Getter
@NoArgsConstructor
public class RuntimePipelineBlackboard {
	private static final long CONSIDER_APPLICATION_RUNNING_BUFFER = 60000;

	@Autowired
	@Setter
	private ConfigurationContainer configuration;

	@Autowired
	private IValidationQueryFacade validationQuery;

	@Autowired
	private IPCMQueryFacade pcmQuery;

	@Autowired
	private IValidationResultsQuery validationResultsQuery;

	@Autowired
	private ISpecificVsumFacade vsumQuery;

	@Autowired
	private ICoreBlackboardQueryFacade query;

	@Autowired
	private IRuntimeEnvironmentQueryFacade remQuery;

	@Autowired
	private IInstrumentationModelQueryFacade inmQuery;

	@Autowired
	private List<IResettableQueryFacade> resettableQueries;

	@Autowired
	private IPipelineHealthQueryFacade healthQuery;

	// TODO below really here?
	@Setter
	private boolean applicationRunning = false;

	private long lastMonitoringDataReceivedTimestamp = 0;

	@Scheduled(initialDelay = 1000 * 60, fixedRate = 1000 * 60)
	public void refreshApplicationRunning() {
		if (System.currentTimeMillis() - lastMonitoringDataReceivedTimestamp < CONSIDER_APPLICATION_RUNNING_BUFFER) {
			this.applicationRunning = true;
		} else {
			this.applicationRunning = false;
		}
	}

	public void receivedMonitoringData() {
		this.lastMonitoringDataReceivedTimestamp = System.currentTimeMillis();
	}

	public void reset() {
		reset(true);
	}

	public void reset(boolean hard) {
		resettableQueries.forEach(rq -> rq.reset(hard));
	}

}
