package rmiserver;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.ArrayList;
import java.rmi.Remote;

public interface InterfaceServerRMI extends Remote{
    public void SaveRegistry(Pessoa string) throws RemoteException;
    public void criarEleicao(Eleicao eleicao) throws RemoteException;
	public void saveClients(String name, InterfaceClientRMI a) throws RemoteException;
	public void saveAdmin(String name, InterfaceClientRMI a) throws RemoteException ;
    public void print_on_server(String s) throws RemoteException;
	public ArrayList<Pessoa> getPerson() throws RemoteException ;
    public ArrayList<Pessoa> getEstudantes() throws RemoteException;
	public ArrayList<Pessoa> getDocentes() throws RemoteException;
	public ArrayList<Pessoa> getFuncionarios() throws RemoteException;
	public ArrayList<Eleicao> getEleicoes() throws RemoteException;
	public ArrayList<InterfaceClientRMI> getClients() throws RemoteException;
	public ArrayList<InterfaceClientRMI> getAdminClients() throws RemoteException;
	public boolean verifyUser(String nome, String ccNumber, String password)throws RemoteException;
	public boolean verifyLogin(String nome, String ccNumber) throws RemoteException ;
	public void saveVotes(String eleicao, String lista) throws RemoteException;
	public void loadDataElection() throws RemoteException;
	public void notifyClient(String name, String tag) throws RemoteException;
	public void gerirEleicao(Lista l, int eleNum,int opt,int indexLi)throws RemoteException, IOException;
	public void alteraEleicao(Eleicao e, int numEle) throws RemoteException, IOException;
	public boolean stateOfElections(Eleicao eleicao, int option) throws RemoteException, ParseException;
	public void saveUserVote(String name, String ccNumber, String election) throws RemoteException;
	public boolean verifyUserinArray(String name, String ccNUmber, Eleicao election) throws RemoteException;
	public void saveVotedPlaceOnPeople(String name, String ccNumber, String table) throws RemoteException;
	public String getCrashName() throws RemoteException;
	public String getCrashCC() throws RemoteException;
	public void setCrashName(String name) throws RemoteException;
	public void setCrashCC(String CC) throws RemoteException;
	public String getUserproperties(String name, String ccNumber) throws RemoteException;
	public void writeToFile(String ficheiro) throws RemoteException;
	public ArrayList<String> getLocal() throws RemoteException;
	public void addLocal(String data) throws RemoteException;
	public void remeveLocal(int a) throws RemoteException;
	public void addTable(String name, String cc, String table) throws RemoteException ;
}