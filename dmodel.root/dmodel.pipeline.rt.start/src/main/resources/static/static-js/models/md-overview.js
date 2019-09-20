// globals
var basePathIcons = "/static-js/pcm/icons/";

$(document).ready(function() {
	buildIconDatabase();
	buildTree(repository, "#tree-repository", "Failed to get repository from backend.");
	buildTree(system, "#tree-system", "Failed to get system from backend.");
	buildTree(usage, "#tree-usage", "Failed to get usage model from backend.");
	buildTree(resenv, "#tree-resourceenv", "Failed to get resource environment model from backend.");
	buildTree(allocation, "#tree-allocation", "Failed to get allocation model from backend.");
});

function buildIconDatabase() {
	ecoreTree.registerIconBasePath(basePathIcons, ".gif");
}

function buildTree(eobj, parent, error_msg) {
	if (eobj != null) {
		ecoreTree.build(parent, eobj);
	} else {
		console.error(error_msg);
	}
}