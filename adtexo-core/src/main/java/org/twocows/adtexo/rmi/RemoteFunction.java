package org.twocows.adtexo.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.function.Function;

public interface RemoteFunction extends Remote {

	<T, R> R execute(Function<T, R> f) throws RemoteException;
}
