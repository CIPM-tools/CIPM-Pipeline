package dmodel.base.shared;

import java.util.List;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;

public class JsonUtil {

	public static String wrapAsObject(String attribute, Object value, boolean string) {
		return wrapAsObject(Lists.newArrayList(Triple.of(attribute, value, string)));
	}

	public static String wrapAsObject(List<Triple<String, Object, Boolean>> values) {
		StringBuilder outputJson = new StringBuilder();
		outputJson.append("{");
		for (Triple<String, Object, Boolean> attr : values) {
			outputJson.append("\"");
			outputJson.append(attr.getLeft());
			outputJson.append("\"");
			outputJson.append(" : ");
			if (attr.getRight()) {
				outputJson.append("\"");
			}
			outputJson.append(String.valueOf(attr.getMiddle()));
			if (attr.getRight()) {
				outputJson.append("\"");
			}
			outputJson.append(",");
		}
		if (values.size() > 0) {
			outputJson.setLength(outputJson.length() - 1);
		}
		outputJson.append("}");

		return outputJson.toString();
	}

	public static String emptyObject() {
		return "{}";
	}

	public static String emptyArray() {
		return "[]";
	}

}
