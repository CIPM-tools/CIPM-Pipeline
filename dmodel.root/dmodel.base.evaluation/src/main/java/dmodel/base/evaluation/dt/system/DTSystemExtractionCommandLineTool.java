package dmodel.base.evaluation.dt.system;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.OperationInterface;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphPackage;
import dmodel.base.shared.ModelUtil;
import dmodel.base.shared.pcm.util.PCMUtils;
import dmodel.designtime.system.pcm.data.AbstractConflict;
import dmodel.designtime.system.pcm.data.AssemblyConflict;
import dmodel.designtime.system.pcm.data.ConnectionConflict;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.AssemblyRequiredRole;
import dmodel.designtime.system.pcm.impl.PCMSystemBuilder.SystemProvidedRole;
import dmodel.designtime.system.pcm.impl.util.Xor;

@Service
public class DTSystemExtractionCommandLineTool {
	private BufferedReader inputReader;
	private DTSystemExtractionConfiguration configuration;

	@Autowired
	private PCMSystemBuilder systemBuilder;

	private ServiceCallGraph serviceCallGraph;

	public DTSystemExtractionCommandLineTool() {
		PCMUtils.loadPCMModels();
		ServiceCallGraphPackage.eINSTANCE.eClass();
	}

	public void startSystemExtraction(DTSystemExtractionConfiguration config) {
		inputReader = new BufferedReader(new InputStreamReader(System.in));
		configuration = config;
		serviceCallGraph = ModelUtil.readFromFile(config.getScgFile().getAbsolutePath(), ServiceCallGraph.class);
		serviceCallGraph.rebuild();

		startSystemExtractionProcess();

		try {
			inputReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// save finally
		ModelUtil.saveToFile(systemBuilder.getCurrentSystem(), config.getSystemOutputFile());
	}

	private void startSystemExtractionProcess() {
		Repository repository = ModelUtil.readFromFile(configuration.getRepositoryFile().getAbsolutePath(),
				Repository.class);
		List<OperationInterface> coreInterfaces = configuration.getSystemProvidedInterfaceIds().stream()
				.map(str -> PCMUtils.getElementById(repository, OperationInterface.class, str))
				.collect(Collectors.toList());

		boolean finished = systemBuilder.startBuildingSystem(serviceCallGraph, coreInterfaces);
		while (!finished) {
			AbstractConflict<?> conflict = systemBuilder.getCurrentConflict();
			if (conflict instanceof ConnectionConflict) {
				ConnectionConflict connConflict = (ConnectionConflict) conflict;
				resolveConnectionConflict(connConflict);
			} else if (conflict instanceof AssemblyConflict) {
				AssemblyConflict acConflict = (AssemblyConflict) conflict;
				resolveAssemblyConflict(acConflict);
			}

			finished = systemBuilder.continueBuilding();
		}
	}

	private void resolveAssemblyConflict(AssemblyConflict acConflict) {
		printTarget(acConflict.getTarget());
		System.out.println("Possible solutions:");

		for (int i = 0; i < acConflict.getSolutions().size(); i++) {
			AssemblyContext possibility = acConflict.getSolutions().get(i);
			System.out
					.println("[" + i + "] - " + possibility.getEncapsulatedComponent__AssemblyContext().getEntityName()
							+ " of " + possibility.getEntityName());
		}
		System.out.println("[" + (acConflict.getSolutions().size()) + "] - None");

		try {
			int answerConv = -1;

			while (answerConv < 0 || answerConv > acConflict.getSolutions().size()) {
				String answer = inputReader.readLine();
				try {
					answerConv = Integer.parseInt(answer);
				} catch (NumberFormatException e) {
					answerConv = -1;
				}
			}

			if (answerConv == acConflict.getSolutions().size()) {
				acConflict.setSolution(null);
			} else {
				acConflict.setSolution(acConflict.getSolutions().get(answerConv));
			}
			acConflict.setSolved(true);
		} catch (IOException e) {
			System.exit(0);
		}
	}

	private void resolveConnectionConflict(ConnectionConflict connConflict) {
		printTarget(connConflict.getTarget());
		System.out.println("Possible solutions:");
		for (int i = 0; i < connConflict.getSolutions().size(); i++) {
			RepositoryComponent possibility = connConflict.getSolutions().get(i);
			System.out.println("[" + i + "] - " + possibility.getEntityName() + " - ID: " + possibility.getId());
		}
		System.out.println("[" + (connConflict.getSolutions().size()) + "] - None");
		try {
			int answerConv = -1;

			while (answerConv < 0 || answerConv > connConflict.getSolutions().size()) {
				String answer = inputReader.readLine();
				try {
					answerConv = Integer.parseInt(answer);
				} catch (NumberFormatException e) {
					answerConv = -1;
				}
			}

			if (answerConv == connConflict.getSolutions().size()) {
				connConflict.setSolution(null);
			} else {
				connConflict.setSolution(connConflict.getSolutions().get(answerConv));
			}
			connConflict.setSolved(true);
		} catch (IOException e) {
			System.exit(0);
		}
	}

	private void printTarget(Xor<AssemblyRequiredRole, SystemProvidedRole> target) {
		if (target.leftPresent()) {
			System.out.println("Conflict for assembly required role [interface = '"
					+ target.getLeft().getRole().getRequiredInterface__OperationRequiredRole().getEntityName()
					+ "', component = '"
					+ target.getLeft().getCtx().getEncapsulatedComponent__AssemblyContext().getEntityName()
					+ "', assembly = '" + target.getLeft().getCtx().getEntityName() + "']");
		} else if (target.rightPresent()) {
			System.out.println("Conflict for system provided role [interface = '"
					+ target.getRight().getRole().getProvidedInterface__OperationProvidedRole().getEntityName() + "].");
		}
	}

}
