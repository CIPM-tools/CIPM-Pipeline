var treeItemId = 0;
var configuration = {};

// ids
var saveId = "#save";

// helper function
$.postJSON = function(url, data, func) {
	$.post(url, data, func, 'json');
}

$(document).ready(function() {
	configureModelAjax();
	configureSaveAjax();
	configureToolEvents();

	getCurrentConfig();
});

function configureToolEvents() {
	var sampleRepo = 
`<?xml version="1.0" encoding="UTF-8"?>
<repository:Repository xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:repository="http://palladiosimulator.org/PalladioComponentModel/Repository/5.2" xmlns:seff="http://palladiosimulator.org/PalladioComponentModel/SEFF/5.2" xmlns:stoex="http://sdq.ipd.uka.de/StochasticExpressions/2.2" id="_HZJnoJSbEd-mVsWhjZHqXg" entityName="sampleRepository" repositoryDescription="Empty Sample Repository">
</repository:Repository>`;
	
	var sampleSystem = 
`<?xml version="1.0" encoding="ASCII"?>
<system:System xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:composition="http://palladiosimulator.org/PalladioComponentModel/Core/Composition/5.2" xmlns:repository="http://palladiosimulator.org/PalladioComponentModel/Repository/5.2" xmlns:system="http://palladiosimulator.org/PalladioComponentModel/System/5.2" id="_3Sgb8FcmEd23wcZsd06DZg" entityName="Sample System">
</system:System>`;
	
	var sampleEnv = 
`<?xml version="1.0" encoding="ASCII"?>
<resourceenvironment:ResourceEnvironment xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:resourceenvironment="http://palladiosimulator.org/PalladioComponentModel/ResourceEnvironment/5.2">
</resourceenvironment:ResourceEnvironment>`;
	
	var sampleAllocation = 
`<?xml version="1.0" encoding="ASCII"?>
<allocation:Allocation xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:allocation="http://palladiosimulator.org/PalladioComponentModel/Allocation/5.2" id="_pY23oFc-Ed23wcZsd06DZg" entityName="defaultAllocation">
</allocation:Allocation>`;
	
	var sampleUsage =
`<?xml version="1.0" encoding="UTF-8"?>
<usagemodel:UsageModel xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:stoex="http://sdq.ipd.uka.de/StochasticExpressions/2.2" xmlns:usagemodel="http://palladiosimulator.org/PalladioComponentModel/UsageModel/5.2">
</usagemodel:UsageModel>`;
	
	var sampleCorr = 
`<?xml version="1.0" encoding="ASCII"?>
<correspondence:Correspondences xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:correspondence="http://tools.vitruv/Correspondence/1.0"/>`;
	
	var sampleInm = 
`<?xml version="1.0" encoding="ASCII"?>
<imm:InstrumentationModel xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:imm="http://www.dmodel.com/InstrumentationMetamodel" id="_YbsOYIF_Eeq5X9KEEqof3g"/>`;

	var sampleRem = 
`<?xml version="1.0" encoding="ASCII"?>
<rem:RuntimeEnvironmentModel xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:rem="http://www.dmodel.com/RuntimeEnvironmentModel" id="_YbfaEIF_Eeq5X9KEEqof3g"/>`;
	
	registerDownload("#download_repo", "sample.repository", sampleRepo);
	registerDownload("#download_sys", "sample.system", sampleSystem);
	registerDownload("#download_resenv", "sample.resourceenvironment", sampleEnv);
	registerDownload("#download_allocation", "sample.allocation", sampleAllocation);
	registerDownload("#download_usage", "sample.usagemodel", sampleUsage);
	
	registerDownload("#download_corr", "sample.correspondences", sampleCorr);
	registerDownload("#download_inm", "sample.imm", sampleInm);
	registerDownload("#download_rem", "sample.rem", sampleRem);
	
	configureMappingTool();
	configureInstrumentationTool();
}

function configureInstrumentationTool() {
	$("#init_inm").click(function() {
		$("#modal-inm-init").modal('show');
	});
	
	$("#inm-init-start").click(function() {
		$("#modal-inm-init").modal('hide');
		$.postJSON(rest.config.models.initInstrumentation, function(result) {
			if (result.success) {
				toastr.success('Successfully initialized the instrumentation model.', 'Success');
			} else {
				toastr.error('Failed to initialize the instrumentation model.', 'Error');
			}
		});
	});
}

function configureMappingTool() {
	$("#init_mapping").click(function() {
		$('#modal-mapping').modal('show');
	});
	
	$("#mapping-extract").click(function() {
		$('#modal-mapping').modal('hide');
		$.postJSON(rest.design.mapping.resolve, function(result) {
			if (result.hasOwnProperty("mappings")) {
				toastr.success('Successfully extracted the mapping. The procedure found ' + result.mappings + ' mappings.', 'Success');
			} else {
				toastr.error('Failed to automatically extract the mapping.', 'Error');
			}
		});
	});
}

function registerDownload(id, name, content) {
	$(id).click(function() {
		saveFile(name, "application/xml", content);
	});
}

function saveFile(name, type, data) {
	if (data !== null && navigator.msSaveBlob)
		return navigator.msSaveBlob(new Blob([ data ], {
			type : type
		}), name);
	var a = $("<a style='display: none;'/>");
	var url = window.URL.createObjectURL(new Blob([ data ], {
		type : type
	}));
	a.attr("href", url);
	a.attr("download", name);
	$("body").append(a);
	a[0].click();
	window.URL.revokeObjectURL(url);
	a.remove();
}

function configureSaveAjax() {
	$(saveId).click(function() {
		var nConfig = getModelPaths();
		$.postJSON(rest.config.models.save, {
			"models" : JSON.stringify(nConfig)
		}, function(data) {
			if (data.success) {
				toastr.success('Saved configuration successfully.', 'Success');
			} else {
				toastr.error('Failed to save configuration.', 'Error');
			}
		});
	});
}

function getCurrentSourceFolders() {
	var checkedItems = $(treeId).jstree("get_checked", null, true);
	var res = [];
	for (var i = 0; i < checkedItems.length; i++) {
		res.push(getFullFolderString(checkedItems[i]));
	}
	return res;
}

function getCurrentConfig() {
	$.getJSON(rest.config.models.get, function(data) {
		configuration = data;

		// refresh
		applyModelValues();
		modelValueChanged();
	});
}

function applyModelValues() {
	$("#repo_path").val(configuration.repo == null ? "" : configuration.repo);
	$("#sys_path").val(configuration.sys == null ? "" : configuration.sys);
	$("#res_path").val(configuration.res == null ? "" : configuration.res);
	$("#alloc_path")
			.val(configuration.alloc == null ? "" : configuration.alloc);
	$("#usage_path")
			.val(configuration.usage == null ? "" : configuration.usage);

	$("#corr_path").val(
			configuration.correspondences == null ? ""
					: configuration.correspondences);
	$("#rem_path").val(
			configuration.runtimeenv == null ? "" : configuration.runtimeenv);
	$("#inm_path").val(
			configuration.instrumentation == null ? ""
					: configuration.instrumentation);
}

function configureModelAjax() {
	$("#repo_path").change(modelValueChanged);
	$("#sys_path").change(modelValueChanged);
	$("#res_path").change(modelValueChanged);
	$("#alloc_path").change(modelValueChanged);
	$("#usage_path").change(modelValueChanged);

	$("#corr_path").change(modelValueChanged);
	$("#rem_path").change(modelValueChanged);
	$("#inm_path").change(modelValueChanged);
}

function modelValueChanged() {
	var modelPaths = getModelPaths();

	$.postJSON(rest.config.models.validate, {
		"models" : JSON.stringify(modelPaths)
	}, function(data) {
		applyTick("#repo_image", data.repo);
		applyTick("#sys_image", data.sys);
		applyTick("#res_image", data.res);
		applyTick("#alloc_image", data.alloc);
		applyTick("#usage_image", data.usage);

		applyTick("#corr_image", data.correspondences);
		applyTick("#rem_image", data.runtimeenv);
		applyTick("#inm_image", data.instrumentation);
	});
}

function getModelPaths() {
	return {
		repo : $("#repo_path").val(),
		sys : $("#sys_path").val(),
		res : $("#res_path").val(),
		alloc : $("#alloc_path").val(),
		usage : $("#usage_path").val(),

		correspondences : $("#corr_path").val(),
		runtimeenv : $("#rem_path").val(),
		instrumentation : $("#inm_path").val()
	};
}