package dmodel.pipeline.records.instrument;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.emftext.language.java.JavaClasspath;
import org.emftext.language.java.JavaPackage;
import org.emftext.language.java.classifiers.impl.ClassImpl;
import org.emftext.language.java.resource.JavaSourceOrClassFileResourceFactoryImpl;

public class InstrumentationTest {

	protected static final ResourceSet rs = new ResourceSetImpl();

	public static void main(String[] args) throws IOException {
		setup();

		JavaClasspath cp = JavaClasspath.get(rs);

		loadAllFilesInResourceSet(new File(
				"/Users/david/Desktop/Dynamic Approach/Implementation/git/dModel/dmodel.root/dmodel.pipeline.rexample/src/main/java/"),
				".java");

		resolveAllProxies(0);

		Iterator<Notifier> it = rs.getAllContents();
		while (it.hasNext()) {
			Notifier next = it.next();
			if (next instanceof EObject) {
				EObject o = (EObject) next;
				if (o instanceof ClassImpl) {
					System.out.println(((ClassImpl) o).getName());
				}
			}
		}
	}

	private static void setup() {
		EPackage.Registry.INSTANCE.put("http://www.emftext.org/java", JavaPackage.eINSTANCE);
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("java",
				new JavaSourceOrClassFileResourceFactoryImpl());
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put(Resource.Factory.Registry.DEFAULT_EXTENSION,
				new XMIResourceFactoryImpl());
	}

	protected static boolean resolveAllProxies(int resourcesProcessedBefore) {
		boolean failure = false;
		List<EObject> eobjects = new LinkedList<EObject>();
		for (Iterator<Notifier> i = rs.getAllContents(); i.hasNext();) {
			Notifier next = i.next();
			if (next instanceof EObject) {
				eobjects.add((EObject) next);
			}
		}
		int resourcesProcessed = rs.getResources().size();
		if (resourcesProcessed == resourcesProcessedBefore) {
			return true;
		}

		System.out.println("Resolving cross-references of " + eobjects.size() + " EObjects.");
		int resolved = 0;
		int notResolved = 0;
		int eobjectCnt = 0;
		for (EObject next : eobjects) {
			eobjectCnt++;
			if (eobjectCnt % 1000 == 0) {
				System.out.println(eobjectCnt + "/" + eobjects.size() + " done: Resolved " + resolved + " crossrefs, "
						+ notResolved + " crossrefs could not be resolved.");
			}

			InternalEObject nextElement = (InternalEObject) next;
			for (EObject crElement : nextElement.eCrossReferences()) {
				crElement = EcoreUtil.resolve(crElement, rs);
				if (crElement.eIsProxy()) {
					failure = true;
					notResolved++;
					System.out.println("Can not find referenced element in classpath: "
							+ ((InternalEObject) crElement).eProxyURI());
				} else {
					resolved++;
				}
			}
		}

		System.out.println(eobjectCnt + "/" + eobjects.size() + " done: Resolved " + resolved + " crossrefs, "
				+ notResolved + " crossrefs could not be resolved.");

		// call this method again, because the resolving might have triggered loading
		// of additional resources that may also contain references that need to be
		// resolved.
		return !failure && resolveAllProxies(resourcesProcessed);
	}

	protected static void loadAllFilesInResourceSet(File startFolder, String extension) throws IOException {
		for (File member : startFolder.listFiles()) {
			if (member.isFile()) {
				if (member.getName().endsWith(extension)) {
					System.out.println("Parsing " + member);
					parseResource(member);
				} else {
					System.out.println("Skipping " + member);
				}
			}
			if (member.isDirectory()) {
				if (!member.getName().startsWith(".")) {
					System.out.println("Recursing into " + member);
					loadAllFilesInResourceSet(member, extension);
				} else {
					System.out.println("Skipping " + member);
				}
			}
		}
	}

	protected static void parseResource(File file) throws IOException {
		loadResource(file.getCanonicalPath());
	}

	protected static void loadResource(String filePath) throws IOException {
		loadResource(URI.createFileURI(filePath));
	}

	protected static void loadResource(URI uri) throws IOException {
		rs.getResource(uri, true);
	}
}
