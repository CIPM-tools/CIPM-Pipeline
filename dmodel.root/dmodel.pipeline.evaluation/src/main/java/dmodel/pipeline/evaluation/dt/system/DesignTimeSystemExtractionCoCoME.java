package dmodel.pipeline.evaluation.dt.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.ProvidedRole;

import dmodel.pipeline.dt.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.pipeline.dt.system.pcm.data.AbstractConflict;
import dmodel.pipeline.dt.system.pcm.data.AssemblyConflict;
import dmodel.pipeline.dt.system.pcm.data.ConnectionConflict;
import dmodel.pipeline.dt.system.pcm.impl.PCMSystemBuilder;
import dmodel.pipeline.shared.ModelUtil;

public class DesignTimeSystemExtractionCoCoME {

	public static void main(String[] args) {
		File systemOutputPath = new File("data/dt-system/cocome/output.system");
		PCMSystemBuilder systemBuilder = new PCMSystemBuilder();

		File scgPath = new File("data/dt-system/cocome/callgraph.scg");
		ServiceCallGraph serviceCallGraph = ModelUtil.readFromFile(scgPath.getAbsolutePath(), ServiceCallGraph.class);

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

		boolean finished = systemBuilder.startBuildingSystem(serviceCallGraph);
		while (!finished) {
			AbstractConflict<?> conflict = systemBuilder.getCurrentConflict();
			if (conflict instanceof ConnectionConflict) {
				ConnectionConflict connConflict = (ConnectionConflict) conflict;
				System.out
						.println("Conflict for Required Role '" + connConflict.getRequired().getEntityName() + "' of '"
								+ connConflict.getRequired().getRequiringEntity_RequiredRole().getEntityName() + "'.");
				System.out.println("Possible solutions:");
				for (int i = 0; i < connConflict.getProvided().size(); i++) {
					ProvidedRole possibility = connConflict.getProvided().get(i);
					System.out.println("[" + i + "] - " + possibility.getProvidingEntity_ProvidedRole().getEntityName()
							+ " of " + possibility.getProvidingEntity_ProvidedRole().getEntityName());
				}
				System.out.println("[" + (connConflict.getProvided().size()) + "] - None");
				try {
					int answerConv = -1;

					while (answerConv < 0 || answerConv > connConflict.getProvided().size()) {
						String answer = inputReader.readLine();
						try {
							answerConv = Integer.parseInt(answer);
						} catch (NumberFormatException e) {
							answerConv = -1;
						}
					}

					if (answerConv == connConflict.getProvided().size()) {
						conflict.setSolution(null);
					} else {
						connConflict.setSolution(connConflict.getProvided().get(answerConv));
					}
				} catch (IOException e) {
					System.exit(0);
				}
			} else if (conflict instanceof AssemblyConflict) {
				AssemblyConflict acConflict = (AssemblyConflict) conflict;
				System.out.println("Conflict for Assembly Context '" + acConflict.getReqRole().getEntityName()
						+ "' of '" + acConflict.getReqRole().getRequiringEntity_RequiredRole().getEntityName()
						+ "' - Service = " + acConflict.getServiceTo().getId() + ".");
				System.out.println("Possible solutions:");

				for (int i = 0; i < acConflict.getPoss().size(); i++) {
					AssemblyContext possibility = acConflict.getPoss().get(i);
					System.out.println(
							"[" + i + "] - " + possibility.getEncapsulatedComponent__AssemblyContext().getEntityName()
									+ " of " + possibility.getEntityName());
				}
				System.out.println("[" + (acConflict.getPoss().size()) + "] - None");

				try {
					int answerConv = -1;

					while (answerConv < 0 || answerConv > acConflict.getPoss().size()) {
						String answer = inputReader.readLine();
						try {
							answerConv = Integer.parseInt(answer);
						} catch (NumberFormatException e) {
							answerConv = -1;
						}
					}

					if (answerConv == acConflict.getPoss().size()) {
						conflict.setSolution(null);
					} else {
						acConflict.setSolution(acConflict.getPoss().get(answerConv));
					}
				} catch (IOException e) {
					System.exit(0);
				}
			}

			systemBuilder.continueBuilding();
		}

		try {
			inputReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ModelUtil.saveToFile(systemBuilder.getCurrentSystem(), systemOutputPath);
	}

}
