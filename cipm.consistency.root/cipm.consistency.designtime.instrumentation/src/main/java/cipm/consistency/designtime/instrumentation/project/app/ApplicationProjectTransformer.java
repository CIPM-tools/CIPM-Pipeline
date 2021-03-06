package cipm.consistency.designtime.instrumentation.project.app;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import cipm.consistency.base.shared.util.ZipUtils;
import cipm.consistency.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import cipm.consistency.bridge.monitoring.controller.config.MonitoringConfiguration;
import cipm.consistency.designtime.instrumentation.project.ApplicationProject;
import cipm.consistency.designtime.instrumentation.project.ParsedApplicationProject;
import cipm.consistency.designtime.instrumentation.transformation.IApplicationProjectInstrumenter;
import cipm.consistency.designtime.instrumentation.transformation.impl.ApplicationProjectInstrumenterImpl;

@Service
// TODO: future work: only add the monitoring controller to the necessary projects
public class ApplicationProjectTransformer {
	private static final String MONITORING_AGENT_RESOURCE = "/monitoring-agent.zip";

	private IApplicationProjectInstrumenter projectInstrumenter = new ApplicationProjectInstrumenterImpl();

	public void performInstrumentation(ApplicationProject project, InstrumentationMetadata metadata,
			IJavaPCMCorrespondenceModel correspondence) throws IOException {
		ParsedApplicationProject parsed = new ParsedApplicationProject(project);

		// a) instrumentation
		projectInstrumenter.transform(parsed, correspondence);

		// b) build project
		// 0. paths
		File projectBasePath = new File(project.getRootPath());
		File outputBasePath = new File(metadata.getOutputPath());
		// 1. copy whole project
		FileUtils.copyDirectory(projectBasePath, outputBasePath);
		// 2. integrate sources
		parsed.saveSources(outputBasePath);
		// 3. add agent to all source folders
		for (String srcFolder : project.getSourceFolders()) {
			File srcBaseFolder = new File(outputBasePath, srcFolder);
			integrateAgent(srcBaseFolder, metadata);
		}
	}

	private void integrateAgent(File srcBaseFolder, InstrumentationMetadata metadata) throws IOException {
		ZipUtils.unzip(getClass().getResourceAsStream(MONITORING_AGENT_RESOURCE), srcBaseFolder);

		// c) build monitoring configuration and add it
		CompilationUnit nConfiguration = new CompilationUnit();
		nConfiguration.setPackageDeclaration(MonitoringConfiguration.class.getPackage().getName());

		ClassOrInterfaceDeclaration configurationDecl = nConfiguration
				.addInterface(MonitoringConfiguration.class.getSimpleName());

		configurationDecl.addFieldWithInitializer(String.class, "SERVER_HOSTNAME",
				new StringLiteralExpr(metadata.getHostName()));
		configurationDecl.addFieldWithInitializer(String.class, "SERVER_REST_INM_URL",
				new StringLiteralExpr(metadata.getInmRestPath()));
		configurationDecl.addFieldWithInitializer(int.class, "SERVER_REST_PORT",
				new IntegerLiteralExpr(String.valueOf(metadata.getRestPort())));
		configurationDecl.addFieldWithInitializer(boolean.class, "LOGARITHMIC_SCALING",
				new BooleanLiteralExpr(metadata.isLogarithmicScaling()));
		configurationDecl.addFieldWithInitializer(long.class, "LOGARITHMIC_SCALING_INTERVAL",
				new LongLiteralExpr(String.valueOf(metadata.getLogarithmicRecoveryInterval())));

		String configurationOutputPath = MonitoringConfiguration.class.getPackage().getName().replaceAll("\\.", "/")
				+ "/" + MonitoringConfiguration.class.getSimpleName() + ".java";
		FileUtils.write(new File(srcBaseFolder, configurationOutputPath), nConfiguration.toString(), "UTF-8");
	}

}
