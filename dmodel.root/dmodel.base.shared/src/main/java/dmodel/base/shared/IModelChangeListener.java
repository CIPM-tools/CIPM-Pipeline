package dmodel.base.shared;

import org.eclipse.emf.common.notify.Notification;

@FunctionalInterface
public interface IModelChangeListener {

	public void onChange(Notification notification);

}
