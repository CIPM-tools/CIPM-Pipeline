
$(document).ready(function() {
	buildTree(repository, "#tree-repository", "Failed to get repository from backend.");
});

function buildTree(eobj, parent, error_msg) {
	if (eobj != null) {
		ecoreTree.build(parent, eobj);
	} else {
		console.error(error_msg);
	}
}