package org.twocows.adtexo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.function.Supplier;

public interface Compute extends Remote {

	<T> T apply(Supplier<T> supplier) throws RemoteException;
}
