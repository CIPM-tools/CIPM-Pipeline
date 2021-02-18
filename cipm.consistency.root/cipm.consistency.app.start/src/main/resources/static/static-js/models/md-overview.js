// globals
var basePathIcons = "/static-js/pcm/icons/";

$(document).ready(function() {
	buildIconDatabase();
	buildTree(repository, "#tree-repository", "Failed to get repository from backend.");
	buildTree(system, "#tree-system", "Failed to get system from backend.");
	buildTree(usage, "#tree-usage", "Failed to get usage model from backend.");
	buildTree(resenv, "#tree-resourceenv", "Failed to get resource environment model from backend.");
	buildTree(allocation, "#tree-allocation", "Failed to get allocation model from backend.");
	buildTree(instrumentation, "#tree-inm", "Failed to get instrumentation model from backend.");
	buildTree(runtimeEnv, "#tree-rem", "Failed to get runtime environment from backend.");
	buildTree(correspondences, "#tree-correspondence", "Failed to get correspondences from backend.");
});

function buildIconDatabase() {
	ecoreTree.registerIconBasePath(basePathIcons, ".gif");
	
	// correspondences
	ecoreTree.registerIcon("Correspondences", "fa fa-long-arrow-alt-right");
	ecoreTree.registerIcon('ManualCorrespondence', "fa fa-hand-pointer");
	ecoreTree.registerIcon('ReactionsCorrespondence', "fa fa-vial");
	
	// instrumentation model
	ecoreTree.registerIcon("InstrumentationModel", "fa fa-search");
	ecoreTree.registerIcon("ServiceInstrumentationPoint", basePathIcons + "ResourceDemandingSEFF.gif");
	ecoreTree.registerIcon("ActionInstrumentationPoint", basePathIcons + "InternalAction.gif");
}

function buildTree(eobj, parent, error_msg) {
	if (eobj != null) {
		ecoreTree.build(parent, eobj, "#property-container", function() {
			// open modal
			$("#modal-attributes").modal('show');
		});
	} else {
		console.error(error_msg);
	}
}