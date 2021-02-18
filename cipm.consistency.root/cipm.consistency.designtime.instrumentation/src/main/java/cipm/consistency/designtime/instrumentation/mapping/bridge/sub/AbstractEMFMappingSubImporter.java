package cipm.consistency.designtime.instrumentation.mapping.bridge.sub;

import java.util.HashMap;
import java.util.Map;

import org.emftext.language.java.classifiers.Classifier;
import org.emftext.language.java.generics.QualifiedTypeArgument;
import org.emftext.language.java.generics.TypeArgument;
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

import cipm.consistency.designtime.instrumentation.mapping.bridge.IEMFSubMappingImporter;
import cipm.consistency.designtime.instrumentation.tuid.JavaTuidGeneratorAndResolver;

public abstract class AbstractEMFMappingSubImporter implements IEMFSubMappingImporter {

	protected JavaTuidGeneratorAndResolver tuidGenerator = new JavaTuidGeneratorAndResolver();

	protected static Map<java.lang.Class<?>, String> primitiveTypeStrings = new HashMap<java.lang.Class<?>, String>() {
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

	protected String classifierReferenceToString(TypeReference typeReference) {
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

	protected String namespacesToString(final NamespaceClassifierReference classifierReference) {
		final StringBuilder sb = new StringBuilder();
		for (final String segment : classifierReference.getNamespaces()) {
			sb.append(segment);
			sb.append('.');
		}
		return sb.toString();
	}

}
