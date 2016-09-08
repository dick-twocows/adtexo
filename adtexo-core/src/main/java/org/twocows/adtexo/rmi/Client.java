package org.twocows.adtexo.rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			String host = null;
			Registry registry = LocateRegistry.getRegistry(host);
			Compute compute = (Compute) registry.lookup("compute");
			Pi task = new Pi();
            System.out.println(compute.apply(task));
		} catch (Throwable throwable) {
			System.err.println(throwable);
		}
	}
}
