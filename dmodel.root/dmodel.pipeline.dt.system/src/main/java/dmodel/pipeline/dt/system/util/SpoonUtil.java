package dmodel.pipeline.dt.system.util;

import java.lang.reflect.Method;

import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.SpoonClassNotFoundException;

public class SpoonUtil {

	public static Method getClassPathReferenceSoft(CtExecutableReference<?> ref) {
		try {
			return ref.getActualMethod();
		} catch (SpoonClassNotFoundException e) {
			return null;
		}
	}

	public static CtMethod<?> getMethodFromExecutable(CtModel model, CtExecutableReference<?> ref) {
		return model.filterChildren(new TypeFilter<CtMethod<?>>(CtMethod.class))
				.filterChildren(new Filter<CtMethod<?>>() {

					@Override
					public boolean matches(CtMethod<?> element) {
						if (element.getDeclaringType().getQualifiedName()
								.equals(ref.getDeclaringType().getQualifiedName())) {
							// its the same class
							return executableReferencesEqual(element.getReference(), ref);
						}
						return false;
					}

				}).first();
	}

	public static boolean executableReferencesEqual(CtExecutableReference<?> m1, CtExecutableReference<?> m2) {
		// its the same class
		boolean parasEqual = m1.getSimpleName().equals(m2.getSimpleName())
				&& m1.getParameters().size() == m2.getParameters().size();
		int k = 0;
		while (parasEqual && k < m1.getParameters().size()) {
			parasEqual = m1.getParameters().get(k).getTypeDeclaration().getQualifiedName()
					.equals(m2.getParameters().get(k).getTypeDeclaration().getQualifiedName());
			k++;
		}
		return parasEqual;
	}

	public static boolean executableReferencesEqual(Method m1, CtExecutableReference<?> m2) {
		// its the same class
		boolean parasEqual = m1.getName().equals(m2.getSimpleName())
				&& m1.getParameterCount() == m2.getParameters().size();
		int k = 0;
		while (parasEqual && k < m1.getParameterCount()) {
			parasEqual = m1.getParameters()[k].getType().getName()
					.equals(m2.getParameters().get(k).getTypeDeclaration().getQualifiedName());
			k++;
		}
		return parasEqual;
	}

}
