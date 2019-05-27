package dmodel.pipeline.monitoring.records;


/**
 * @author Generic Kieker
 * 
 * @since 1.13
 */
public interface ServiceContextRecord extends RecordWithSession {
	public String getServiceExecutionId();
	
}
