package org.twocows.adtexo.rmi;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.function.Supplier;

public class Server implements Compute {

	public <T> T apply(Supplier<T> supplier) throws RemoteException {
		return supplier.get();
	}

	public static void main(String[] args) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			UnicastRemoteObject.setLog(System.out);

			Compute engine = new Server();
			Compute stub = (Compute) UnicastRemoteObject.exportObject(engine, 0);
			Registry registry = LocateRegistry.getRegistry();
			
			Arrays.stream(registry.list()).forEach(name -> System.out.println(name));

			registry.bind("compute", stub);
			System.out.println("Bound");

			Thread.sleep(60000);
			Arrays.stream(registry.list()).forEach(name -> System.out.println(name));
						
			registry.unbind("compute");
			UnicastRemoteObject.unexportObject(engine, false);
			
			System.out.println("Ok");
		} catch (Throwable throwable) {
			System.err.println(throwable);
		}
	}

}
