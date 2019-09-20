// globals
var basePathIcons = "/static-js/pcm/icons/";

$(document).ready(function() {
	buildIconDatabase();
	buildTree(repository, "#tree-repository", "Failed to get repository from backend.");
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