package org.twocows.adtexo.rmi;

import java.io.Serializable;
import java.util.function.Supplier;

public class Pi implements Serializable, Supplier<Double> {

	@Override
	public Double get() {
		return Math.PI;
	}

}
