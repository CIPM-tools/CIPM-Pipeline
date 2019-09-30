package dmodel.pipeline.shared;

import java.io.File;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;

public class FileBackedModelUtil {

	public static <T extends EObject> T synchronize(T model, final File file, Class<T> clz) {
		return synchronize(model, file, clz, null, null);
	}

	public static <T extends EObject> T synchronize(T model, final File file, Class<T> clz,
			final IModelChangeListener listener, Function<Void, T> createFunction) {

		if (file == null) {
			return null;
		}

		if (model == null) {
			if (file.exists()) {
				model = ModelUtil.readFromFile(file.getAbsolutePath(), clz);
			} else {
				if (createFunction != null) {
					model = createFunction.apply(null);
				} else {
					return null;
				}
			}
		}

		// SAVE IT AT THE BEGINNING
		// BECAUSE WE DONT KNOW IF IT IS SYNCED
		if (model != null) {
			file.getParentFile().mkdirs();
			ModelUtil.saveToFile(model, file.getAbsolutePath());
		}

		final T modelRefCopy = model;
		model.eAdapters().add(new EContentAdapter() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void notifyChanged(org.eclipse.emf.common.notify.Notification notification) {
				super.notifyChanged(notification);

				// SYNC WITH FILE
				if (!file.exists()) {
					// CREATE PARENT DIRS
					file.getParentFile().mkdirs();
				}
				ModelUtil.saveToFile(modelRefCopy, file);

				if (listener != null) {
					listener.onChange(notification);
				}
			}
		});

		return model;
	}

	public static void clear(EObject obj) {
		obj.eAdapters().clear();
	}

}
