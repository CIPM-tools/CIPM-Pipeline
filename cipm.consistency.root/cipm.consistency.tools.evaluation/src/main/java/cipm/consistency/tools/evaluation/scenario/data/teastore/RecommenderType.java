package cipm.consistency.tools.evaluation.scenario.data.teastore;

import lombok.Getter;

/*
 * 
 * recommenderIdMapping.put(0, "Popularity");
		recommenderIdMapping.put(1, "SlopeOne");
		recommenderIdMapping.put(2, "PreprocessedSlopeOne");
		recommenderIdMapping.put(3, "OrderBased");
 * 
 */
public enum RecommenderType {
	POPULARITY(0, "_raxjcDVgEeqPG_FgW3bi6Q"), SLOPE_ONE(1, "_YkXeIDVgEeqPG_FgW3bi6Q"),
	PREPROCESSED_SLOPE_ONE(2, "_iaElgKpwEeqHXcsU55mirw"), ORDER_BASED(3, "_kgbngDVgEeqPG_FgW3bi6Q"),
	DUMMY(4, "_ouzFYDVgEeqPG_FgW3bi6Q");

	@Getter
	private int id;

	@Getter
	private String componentId;

	private RecommenderType(int i, String cpId) {
		id = i;
		componentId = cpId;
	}
}
