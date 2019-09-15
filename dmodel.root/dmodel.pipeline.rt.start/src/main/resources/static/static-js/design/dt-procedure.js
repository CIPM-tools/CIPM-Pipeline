
function startBuildingProcedure(container) {
	$("#build-system").attr("disabled", "true");
	
	var exSystem = '{"root":{"name":"aName","id":"_nToPMMhEEemC_dbpn1EUHw","assemblys":[{"name":"aName","componentName":"PrimeManagerImpl","componentId":"_m8iCUJYGEempGaXtj6ezAw","id":"_nVbmAMhEEemC_dbpn1EUHw","provided":[{"name":"Provided_PrimeManager_PrimeManagerImpl","id":"_q8ROUJYGEempGaXtj6ezAw"}],"required":[{"id":"_GvVFkJYHEempGaXtj6ezAw","name":"Required_PrimeGenerator_PrimeManagerImpl"}]},{"name":"aName","componentName":"DumbGeneratorImpl","componentId":"_EXcoMJYHEempGaXtj6ezAw","id":"_nWFGQMhEEemC_dbpn1EUHw","provided":[{"name":"Provided_PrimeGenerator_DumbGeneratorImpl","id":"_HCkxsJYHEempGaXtj6ezAw"}],"required":[]}],"connectors":[{"delegation":false,"delegationDirection":false,"assemblyFrom":"_nWFGQMhEEemC_dbpn1EUHw","assemblyTo":"_nVbmAMhEEemC_dbpn1EUHw","role1":"_HCkxsJYHEempGaXtj6ezAw","role2":"_GvVFkJYHEempGaXtj6ezAw","id":"_nWGUYMhEEemC_dbpn1EUHw","name":"aName"},{"delegation":true,"delegationDirection":true,"assemblyFrom":"_nVbmAMhEEemC_dbpn1EUHw","assemblyTo":"_nToPMMhEEemC_dbpn1EUHw","role1":"_q8ROUJYGEempGaXtj6ezAw","role2":"_nWHigMhEEemC_dbpn1EUHw","id":"_nWIwoMhEEemC_dbpn1EUHw","name":"aName"}],"provided":[{"name":"aName","id":"_nWHigMhEEemC_dbpn1EUHw"}],"required":[]}}';
	var model = new PCMSystemGraph(container);
	model.apply(exSystem);
	model.layout(new HorizontalSystemModelLayouter(20));
	model.draw();
}