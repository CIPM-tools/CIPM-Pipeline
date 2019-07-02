package dmodel.pipeline.rt.entry.contracts.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import dmodel.pipeline.rt.entry.contracts.AbstractIterativePipelinePart;

@Retention(RUNTIME)
@Target(METHOD)
public @interface OutputPort {

	public Class<? extends AbstractIterativePipelinePart<?>>[] to();

	boolean async();

}
