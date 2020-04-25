package dmodel.base.models.callgraph.editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;

import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraph;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphEdge;
import dmodel.base.models.callgraph.ServiceCallGraph.ServiceCallGraphFactory;
import dmodel.base.shared.ModelUtil;
import dmodel.base.shared.pcm.util.PCMUtils;

public class CommandLineSCGBuilder {

	public static void main(String[] args) {
		PCMUtils.loadPCMModels();

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in));

		try {
			scgBuilderTool(inputReader);
			inputReader.close();
		} catch (IOException e) {
		}

	}

	private static void scgBuilderTool(BufferedReader inputReader) throws IOException {
		System.out.println("Please select a repository file:");
		String repoFile = inputReader.readLine();

		System.out.println("Please select a output file:");
		String outputFile = inputReader.readLine();

		ServiceCallGraph output = ServiceCallGraphFactory.eINSTANCE.createServiceCallGraph();

		Repository repository = ModelUtil.readFromFile(new File(repoFile).getAbsolutePath(), Repository.class);

		if (repository != null) {
			singleStep(inputReader, output, repository, outputFile);
		}

	}

	private static void singleStep(BufferedReader inputReader, ServiceCallGraph output, Repository repository,
			String outputFile) {
		List<BasicComponent> comps = ModelUtil.getObjects(repository, BasicComponent.class);

		System.out.println("Select an action:");
		System.out.println("[0] - Add a link");
		System.out.println("[1] - Remove a link");
		System.out.println("[2] - Exit");

		int sel = readNumberFromCommandLine(inputReader, 2);

		if (sel == 0) {
			ResourceDemandingSEFF from = selectSEFF(inputReader, comps);
			ResourceDemandingSEFF to = selectSEFF(inputReader, comps);

			output.addEdge(from, to, null, null, null, 1);
		} else if (sel == 1) {
			List<ServiceCallGraphEdge> edges = output.getEdges();
			System.out.println("Select an edge:");
			for (int i = 0; i < edges.size(); i++) {
				System.out.println("[" + i + "] " + edges.get(i).getFrom().getSeff().getId() + " -> "
						+ edges.get(i).getTo().getSeff().getId());
			}

			sel = readNumberFromCommandLine(inputReader, edges.size() - 1);
			output.removeEdge(edges.get(sel));
		}

		if (sel != 2) {
			singleStep(inputReader, output, repository, outputFile);
		} else {
			ModelUtil.saveToFile(output, new File(outputFile));
		}
	}

	private static ResourceDemandingSEFF selectSEFF(BufferedReader inputReader, List<BasicComponent> comps) {
		System.out.println("Select a component:");
		for (int i = 0; i < comps.size(); i++) {
			System.out.println("[" + i + "] - " + comps.get(i).getEntityName() + " {" + comps.get(i).getId() + "}");
		}
		int sel2 = readNumberFromCommandLine(inputReader, comps.size() - 1);

		List<ResourceDemandingSEFF> seffs = comps.get(sel2).getServiceEffectSpecifications__BasicComponent().stream()
				.filter(f -> f instanceof ResourceDemandingSEFF).map(ResourceDemandingSEFF.class::cast)
				.collect(Collectors.toList());

		System.out.println("Select a SEFF:");
		for (int i = 0; i < seffs.size(); i++) {
			System.out.println("[" + i + "] - " + seffs.get(i).getDescribedService__SEFF().getEntityName() + " {"
					+ seffs.get(i).getId() + "}");
		}
		int sel3 = readNumberFromCommandLine(inputReader, seffs.size() - 1);

		return seffs.get(sel3);
	}

	private static int readNumberFromCommandLine(BufferedReader inputReader, int max) {
		int num = -1;
		while (num < 0 || num > max) {
			try {
				num = Integer.parseInt(inputReader.readLine());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return num;
	}

}
