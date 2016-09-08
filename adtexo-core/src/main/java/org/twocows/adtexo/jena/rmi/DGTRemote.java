package org.twocows.adtexo.jena.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface DGTRemote extends Remote {

	void read() throws RemoteException;
	void write() throws RemoteException;
	void commit() throws RemoteException;
	void abort() throws RemoteException;
	void end() throws RemoteException;
}
