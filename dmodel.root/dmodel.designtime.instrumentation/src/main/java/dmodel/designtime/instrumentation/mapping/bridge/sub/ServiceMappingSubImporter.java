package dmodel.designtime.instrumentation.mapping.bridge.sub;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.EObject;
import org.emftext.language.java.members.Method;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.springframework.stereotype.Service;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;

import dmodel.base.vsum.domains.java.IJavaPCMCorrespondenceModel;
import dmodel.designtime.instrumentation.project.ParsedApplicationProject;

@Service
public class ServiceMappingSubImporter extends AbstractEMFMappingSubImporter {

	@Override
	public boolean targets(List<EObject> a, List<EObject> b) {
		return a.size() == 1 && b.size() == 1 && a.get(0) instanceof ResourceDemandingSEFF
				&& b.get(0) instanceof Method;
	}

	@Override
	public void process(IJavaPCMCorrespondenceModel container, ParsedApplicationProject project, List<EObject> a,
			List<EObject> b) {
		ResourceDemandingSEFF seff = (ResourceDemandingSEFF) a.get(0);
		Method method = (Method) b.get(0);

		MethodDeclaration corrMethod = this.getCorrespondingMethod(method, project);

		if (corrMethod != null) {
			container.addSeffCorrespondence(tuidGenerator.generateId(corrMethod), seff.getId());
		}
	}

	private MethodDeclaration getCorrespondingMethod(Method meth, ParsedApplicationProject pap) {
		String qualifiedNameParent = meth.getClassClass().getQualifiedName();

		String[] qualifiedNameParentSplit = qualifiedNameParent.split("\\.");
		String[] qualifiedNameBegin = new String[qualifiedNameParentSplit.length - 1];
		System.arraycopy(qualifiedNameParentSplit, 0, qualifiedNameBegin, 0, qualifiedNameParentSplit.length - 1);

		CompilationUnit compUnit = pap.getCompilationUnit(String.join(".", qualifiedNameBegin),
				qualifiedNameParentSplit[qualifiedNameParentSplit.length - 1]);

		if (compUnit != null) {
			Optional<ClassOrInterfaceDeclaration> correspondingClass = compUnit
					.getClassByName(qualifiedNameParentSplit[qualifiedNameParentSplit.length - 1]);

			if (correspondingClass.isPresent()) {
				return correspondingClass.get().getMethods().stream().filter(f -> methodsEqual(f, meth)).findFirst()
						.orElse(null);
			}
		}

		return null;
	}

	private boolean methodsEqual(MethodDeclaration f, Method meth) {
		if (f.getParameters().size() == meth.getParameters().size()) {
			for (int i = 0; i < f.getParameters().size(); i++) {
				Parameter paraJP = f.getParameters().get(i);
				org.emftext.language.java.parameters.Parameter paraEMF = meth.getParameters().get(i);

				if (!paraJP.getTypeAsString().equals(super.classifierReferenceToString(paraEMF.getTypeReference()))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
