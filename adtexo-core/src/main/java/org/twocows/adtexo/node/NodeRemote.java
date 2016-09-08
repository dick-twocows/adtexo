package org.twocows.adtexo.node;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.twocows.adtexo.context.Context;

public interface NodeRemote extends Remote {

	<R> R apply(Context context) throws RemoteException;
}
