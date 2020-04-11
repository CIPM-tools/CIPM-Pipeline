package dmodel.pipeline.rt.pipeline;

import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.ecore.EObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Sets;

import dmodel.pipeline.core.config.ConfigurationContainer;
import dmodel.pipeline.monitoring.records.ServiceCallRecord;
import dmodel.pipeline.rt.pipeline.blackboard.RuntimePipelineBlackboard;
import dmodel.pipeline.shared.pcm.InMemoryPCM;
import dmodel.pipeline.shared.pcm.util.PCMUtils;
import dmodel.pipeline.shared.structure.Tree;
import dmodel.pipeline.shared.structure.Tree.TreeNode;
import dmodel.pipeline.shared.util.ExtendedEqualityHelper;

@RunWith(SpringRunner.class)
@Import(BaseSpringTestConfiguration.class)
public abstract class AbstractTransformationTest {

	private static final Pattern BRACKET_PATTERN = Pattern.compile("\\[(.*)\\]");
	private static final Pattern PARAMETER_PATTERN = Pattern.compile("(\\{.*\\})");
	private final static Pattern LTRIM = Pattern.compile("^\\s+");

	protected RuntimePipelineBlackboard blackboard;

	@BeforeClass
	public static void loadPCMResources() {
		// load models
		PCMUtils.loadPCMModels();
	}

	@Before
	public void createBlackboard() {
		blackboard = new RuntimePipelineBlackboard();

		// configuration
		try {
			blackboard.setConfiguration(new ObjectMapper(new YAMLFactory()).readValue(
					AbstractTransformationTest.class.getResourceAsStream("/defaultConfig.yml"),
					ConfigurationContainer.class));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// pcm
		blackboard.setArchitectureModel(new InMemoryPCM());

		// delegate to load
		this.loadPCMModels();
	}

	@Test
	public void initialTest() {
		assertNotNull(blackboard.getConfiguration());
	}

	protected abstract void loadPCMModels();

	protected boolean modelsEqual(EObject o1, EObject o2) {
		return new ExtendedEqualityHelper().equals(o1, o2, Sets.newHashSet());
	}

	protected List<Tree<ServiceCallRecord>> parseMonitoringResource(String resourceName) {
		return monitoringDataFromStrings(
				readInputStreamToArray(AbstractTransformationTest.class.getResourceAsStream(resourceName)));
	}

	protected String[] readInputStreamToArray(InputStream is) {
		try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is))) {
			List<String> lines = new ArrayList<String>();
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
			return lines.toArray(new String[lines.size()]);
		} catch (IOException e) {
			return new String[] {};
		}
	}

	protected List<Tree<ServiceCallRecord>> monitoringDataFromStrings(String[] data) {
		List<Tree<ServiceCallRecord>> out = Lists.newArrayList();

		Tree<ServiceCallRecord> currentTree = new Tree<>(parseRecord(data[0]));
		TreeNode<ServiceCallRecord> currentNode = currentTree.getRoot();
		int currentIndent = 0;
		for (int i = 1; i < data.length; i++) {
			if (data[i].length() > 0 && !data[i].startsWith("#")) {
				int nIndent = countIndent(data[i]);
				if (nIndent > currentIndent) {
					currentNode = currentNode.addChildren(parseRecord(data[i]));
					currentIndent = nIndent;
				} else if (nIndent <= currentIndent) {
					if (nIndent == 0) {
						out.add(currentTree);
						currentTree = new Tree<>(parseRecord(data[i]));
						currentNode = currentTree.getRoot();
						currentIndent = 0;
					} else if (nIndent < currentIndent) {
						currentNode = currentNode.getParent();
						currentNode = currentNode.addChildren(parseRecord(data[i]));
						currentIndent = nIndent;
					} else {
						// they are equal
						currentNode.addChildren(parseRecord(data[i]));
					}
				}
			}
		}

		out.add(currentTree);

		return out;
	}

	private int countIndent(String str) {
		return str.length() - LTRIM.matcher(str).replaceAll("").length();
	}

	private ServiceCallRecord parseRecord(String record) {
		record = LTRIM.matcher(record).replaceAll("");
		record = record.substring(2); // remove leading "- "
		String[] dataSplit = record.split(" ");
		Matcher hostIdMatcher = BRACKET_PATTERN.matcher(dataSplit[1]);

		if (hostIdMatcher.find()) {
			if (dataSplit.length == 2) {
				ServiceCallRecord rec = new ServiceCallRecord("", "", hostIdMatcher.group(1), "", dataSplit[0], "", "",
						"", System.currentTimeMillis(), System.currentTimeMillis());
				return rec;
			} else if (dataSplit.length == 3) {
				// check for parameters
				Matcher parameterMatcher = PARAMETER_PATTERN.matcher(dataSplit[2]);
				if (parameterMatcher.find()) {
					ServiceCallRecord rec = new ServiceCallRecord("", "", hostIdMatcher.group(1), "", dataSplit[0],
							parameterMatcher.group(1), "", "", System.currentTimeMillis(), System.currentTimeMillis());
					return rec;
				}
				return null;
			} else if (dataSplit.length == 4) {
				// check for parameters
				Matcher parameterMatcher = PARAMETER_PATTERN.matcher(dataSplit[2]);
				if (parameterMatcher.find()) {
					ServiceCallRecord rec = new ServiceCallRecord(dataSplit[3], "", hostIdMatcher.group(1), "",
							dataSplit[0], parameterMatcher.group(1), "", "", System.currentTimeMillis(),
							System.currentTimeMillis());
					return rec;
				}
			}

			return null;
		}

		return null;
	}

}
