package dmodel.pipeline.monitoring.records;


/**
 * @author Generic Kieker
 * 
 * @since 1.13
 */
public interface HostContextRecord extends PCMContextRecord {
	public String getHostId();
	
	public String getHostName();
	
}