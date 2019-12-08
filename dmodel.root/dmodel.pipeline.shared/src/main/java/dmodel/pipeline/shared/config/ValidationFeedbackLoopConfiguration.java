package dmodel.pipeline.shared.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import dmodel.pipeline.shared.util.IGenericListener;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationFeedbackLoopConfiguration {
	@Setter(AccessLevel.NONE)
	private String url;
	@Setter(AccessLevel.NONE)
	private int port;

	private long simulationTime = 150000;
	private long measurements = 10000;

	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@JsonIgnore
	private List<IGenericListener<Void>> changeListener = new ArrayList<>();

	public void setUrl(String url) {
		this.url = url;
		this.changeListener.forEach(c -> c.inform(null));
	}

	public void setPort(int port) {
		this.port = port;
		this.changeListener.forEach(c -> c.inform(null));
	}

	public void registerChangeListener(IGenericListener<Void> list) {
		this.changeListener.add(list);
	}
}
