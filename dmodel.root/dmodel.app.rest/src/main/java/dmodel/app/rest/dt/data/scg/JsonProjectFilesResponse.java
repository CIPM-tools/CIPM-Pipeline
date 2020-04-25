package dmodel.app.rest.dt.data.scg;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import com.google.common.collect.Lists;

import lombok.Data;

@Data
public class JsonProjectFilesResponse {
	private List<JsonProjectFilesResponse> subfolders;
	private String name;

	private List<String> files;

	public JsonProjectFilesResponse() {
		this.subfolders = Lists.newArrayList();
		this.files = Lists.newArrayList();
	}

	public static JsonProjectFilesResponse from(File basePath, FileFilter filter) {
		JsonProjectFilesResponse resp = new JsonProjectFilesResponse();
		resp.setName(basePath.getName());

		FileFilter combinedFileFilter = f -> filter.accept(f) || f.isDirectory();
		File[] subFiles = basePath.listFiles(combinedFileFilter);
		for (File subFile : subFiles) {
			if (subFile.isDirectory()) {
				resp.subfolders.add(from(subFile, filter));
			} else {
				resp.files.add(subFile.getName());
			}
		}

		return resp;
	}
}
