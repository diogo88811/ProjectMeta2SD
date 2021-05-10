package rmiserver;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface InterfaceClientRMI extends Remote{
    public void print_on_client(String s) throws RemoteException;
}
