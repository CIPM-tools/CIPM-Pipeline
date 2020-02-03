package dmodel.pipeline.records.instrument.bridge;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.emftext.language.java.classifiers.Class;
import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.containers.Package;
import org.emftext.language.java.generics.QualifiedTypeArgument;
import org.emftext.language.java.generics.TypeArgument;
import org.emftext.language.java.members.Method;
import org.emftext.language.java.types.ClassifierReference;
import org.emftext.language.java.types.NamespaceClassifierReference;
import org.emftext.language.java.types.PrimitiveType;
import org.emftext.language.java.types.Type;
import org.emftext.language.java.types.TypeReference;
import org.emftext.language.java.types.impl.BooleanImpl;
import org.emftext.language.java.types.impl.ByteImpl;
import org.emftext.language.java.types.impl.CharImpl;
import org.emftext.language.java.types.impl.DoubleImpl;
import org.emftext.language.java.types.impl.FloatImpl;
import org.emftext.language.java.types.impl.IntImpl;
import org.emftext.language.java.types.impl.LongImpl;
import org.emftext.language.java.types.impl.ShortImpl;
import org.emftext.language.java.types.impl.VoidImpl;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

public class SpoonEMFBridge {

	private static Map<java.lang.Class<?>, String> primitiveTypeStrings = new HashMap<java.lang.Class<?>, String>() {
		private static final long serialVersionUID = 8465597480374410579L;
		{
			this.put(IntImpl.class, "int");
			this.put(CharImpl.class, "char");
			this.put(ByteImpl.class, "byte");
			this.put(DoubleImpl.class, "double");
			this.put(LongImpl.class, "long");
			this.put(VoidImpl.class, "void");
			this.put(BooleanImpl.class, "boolean");
			this.put(FloatImpl.class, "float");
			this.put(ShortImpl.class, "short");
		}
	};

	private ResourceSet emfModel;
	private CtModel spoonModel;

	public SpoonEMFBridge(CtModel spoonModel, ResourceSet emfModel) {
		this.emfModel = emfModel;
		this.spoonModel = spoonModel;
	}

	public CtPackage packageEMFToSpoon(Package pack) {
		return spoonModel.getAllPackages().stream().filter(p -> pack.getName().equals(p.getQualifiedName())).findFirst()
				.orElse(null);
	}

	public CtClass<?> classEMFToSpoon(Class clazz) {
		CtClass<?> res = spoonModel.filterChildren(new TypeFilter<CtClass<?>>(CtClass.class))
				.filterChildren(new Filter<CtClass<?>>() {

					@Override
					public boolean matches(CtClass<?> element) {
						if (element.getQualifiedName().equals(clazz.getQualifiedName())) {
							return true;
						}
						return false;
					}

				}).first();
		return res;
	}

	public CtMethod<?> methodEMFToSpoon(Method method) {
		return spoonModel.filterChildren(new TypeFilter<CtMethod<?>>(CtMethod.class))
				.filterChildren(new Filter<CtMethod<?>>() {
					@Override
					public boolean matches(CtMethod<?> element) {
						// first check class
						if (element.getParent() instanceof CtClass) {
							if (!((CtClass<?>) element.getParent()).getQualifiedName()
									.equals(method.getClassClass().getQualifiedName())) {
								return false;
							}
						}

						// method names equal
						if (!element.getSimpleName().equals(method.getName())) {
							return false;
						}

						// now check method signature
						return parameterEqual(element, method);
					}
				}).first();
	}

	public boolean parameterEqual(CtMethod<?> spoon, Method emf) {
		if (spoon.getParameters().size() != emf.getParameters().size())
			return false;

		return IntStream.range(0, spoon.getParameters().size()).allMatch(i -> {
			return spoon.getParameters().get(i).getType().getQualifiedName()
					.equals(classifierReferenceToString(emf.getParameters().get(i).getTypeReference()));
		});
	}

	private String classifierReferenceToString(TypeReference typeReference) {
		String prefix = "";
		final ClassifierReference classifierReference = typeReference.getPureClassifierReference();
		if (classifierReference == null) {
			final Type type = typeReference.getTarget();
			if (!(type instanceof PrimitiveType)) {
				throw new IllegalStateException("This should have been a primtive type.");
			}
			return primitiveTypeStrings.get(type.getClass());
		}

		if (typeReference instanceof NamespaceClassifierReference) {
			prefix = namespacesToString((NamespaceClassifierReference) typeReference);
		}
		final StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		final Classifier target = classifierReference.getTarget();
		sb.append(target.getName());
		if (classifierReference.getTypeArguments().size() != 0) {
			sb.append('<');
			for (final TypeArgument typeArg : classifierReference.getTypeArguments()) {
				sb.append(classifierReferenceToString(
						((QualifiedTypeArgument) typeArg).getTypeReference().getPureClassifierReference()));
				sb.append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			sb.append('>');
		}
		return sb.toString();
	}

	private String namespacesToString(final NamespaceClassifierReference classifierReference) {
		final StringBuilder sb = new StringBuilder();
		for (final String segment : classifierReference.getNamespaces()) {
			sb.append(segment);
			sb.append('.');
		}
		return sb.toString();
	}

}
