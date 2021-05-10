import java.net.MulticastSocket;

import java.rmi.NotBoundException;

import java.rmi.RemoteException;

import java.rmi.registry.LocateRegistry;

import java.rmi.registry.Registry;

import java.net.DatagramPacket;

import java.net.InetAddress;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.InputStreamReader;

import java.text.ParseException;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

public class MulticastServer extends Thread {

    // "224.0.224.0"

    private String MULTICAST_ADDRESS;
    private int PORT = 7000;
    private static MulticastServer server;
    private static MulticastUser user;
    Scanner keyboardScanner = new Scanner(System.in);
    String dataReceived;
    // Defesa para a conexão entre server e terminal
    int number = 0;
    boolean state = true;
    InterfaceServerRMI h;
    String args;

    // Construtor

    public MulticastServer(InterfaceServerRMI h, String MULTICAST_ADDRESS, String args) throws IOException {

        super("MESA " + (long) (Math.random() * 1000));
        this.h = h;
        FileInputStream fis = new FileInputStream("config.properties");
        Properties prop = new Properties();
        prop.load(fis);
        this.MULTICAST_ADDRESS = String.valueOf(prop.getProperty(args));
        this.args = args;
    }

    // Funcao que recebe dados

    public String receiveData(MulticastSocket socket) throws IOException {

        byte[] buffer = new byte[256];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        String message = new String(packet.getData(), 0, packet.getLength());

        return message;

    }

    // Analisa os dados que recebe e guarda-os numa hashMap
    // Consoante o que recebe manda uma mensagem de resposta para o teminal de voto
    // com que este server está a comunicar
    public void analyseData(MulticastSocket socket, String data, HashMap<String, String> info) throws IOException, NotBoundException {

        String aux[] = data.split("[;]");
        for (String a : aux) {
            String types[] = a.split("\\|");
            info.put(types[0].trim(), types[1].trim());

        }

        // Mandamos uma mensagem ao terminal de voto com um nember request
        if (info.get("type").equals("login")) {
            if (user.getName().equals(info.get("serverName"))) {
                user.sendData(socket, "type | request ; username | " + info.get("username") 
                             + " ; ccNumber | " + info.get("ccNumber") 
                        + " ; NumberRequest | " + number + " ; eleicao | " + info.get("eleicao")
                         + " ; tamanhoLista | " + info.get("tamanhoLista") + " ; serverName | " + user.getName());
            }
        }

        //Se o number Request que mandamos for igual ao que recebemos comecamoa a conexao
        else if (info.get("type").equals("requestAnswer") && number == Integer.parseInt(info.get("NumberRequest"))) {
            if (user.getName().equals(info.get("serverName"))) {
                user.sendData(socket,
                        "type | reserve ; username | " + info.get("username") + " ; ccNumber | " + info.get("ccNumber")
                                + " ; NumberRequest | " + number + " ; terminalID | " + info.get("IDclient")
                                + " ; eleicao | " + info.get("eleicao") + " ; tamanhoLista | "
                                + info.get("tamanhoLista") + " ; serverName | " + user.getName());

                // Incrementamos o num depois de mandar a mensagem para quando o server
                // comunicar so com um terminal
                // EX: Server 123 - Client 456 -> numero 1 | Server 123 - Client 456 -> numero 2
                number++;
            }
        }

        else if (info.get("type").equals("reserved")) {
            if (user.getName().equals(info.get("serverName"))) {
                System.out.println("GO TO TERMINAL " + info.get("IDclient") + " !");
                System.out.println("________________________________________________________________________");
            }
        }

        //A mesa de voto vai receber os dados todos do utilizador e verifica se o utilizador existe na storage
        else if (info.get("type").equals("authentication")) {
            if (user.getName().equals(info.get("serverName"))) {
                try {
                    if (h.verifyUser(info.get("username"), info.get("ccNumber"), info.get("PASSWORD"))) {
                        user.sendData(socket,
                                "type | vote ; username | " + info.get("username") + " ; ccNumber | "
                                        + info.get("ccNumber") + " ; NumberRequest | " + number + " ; terminalID | "
                                        + info.get("IDclient") + " ; userData | valid" + " ; eleicao | "
                                        + info.get("eleicao") + " ; tamanhoLista | " + info.get("tamanhoLista")
                                        + " ; serverName | " + user.getName());
                    }
                    else {
                        System.out.println("UTILIZADOR NAO ESTA REGISTADO, POR FAVOR REGISTE-SE NA NOSSA PLATAFORMA !");
                        user.sendData(socket, "type | restart ; state | true" + " ; terminalID | "
                                + info.get("IDclient") + " ; serverName | " + user.getName());
                    }
                } catch (Exception e) {
                    h = user.reconectRMI(h);
                    if (h.verifyUser(info.get("username"), info.get("ccNumber"), info.get("PASSWORD"))) {
                        user.sendData(socket,
                                "type | vote ; username | " + info.get("username") + " ; ccNumber | "
                                        + info.get("ccNumber") + " ; NumberRequest | " + number + " ; terminalID | "
                                        + info.get("IDclient") + " ; userData | valid" + " ; eleicao | "
                                        + info.get("eleicao") + " ; tamanhoLista | " + info.get("tamanhoLista")
                                        + " ; serverName | " + user.getName());
                    }
                    else {
                        System.out.println("UTILIZADOR NAO ESTA REGISTADO, POR FAVOR REGISTE-SE NA NOSSA PLATAFORMA !");
                        user.sendData(socket, "type | restart ; state | true" + " ; terminalID | "
                                + info.get("IDclient") + " ; serverName | " + user.getName());
                    }
                }
            }
        }

        // Mesa de Voto manda ao terminal de voto as listas da eleicao escolhida pelo utilizador
        else if (info.get("type").equals("item_listRequire")) {
            if (user.getName().equals(info.get("serverName"))) {
                ArrayList<Lista> listaEleicao = null;
                String lista = "type | item_list ; item_count | " + info.get("tamanhoLista") + " ; ";
                ArrayList<Eleicao> eleicoes = h.getEleicoes();

                for (int i = 0; i < eleicoes.size(); i++) {
                    if (eleicoes.get(i).getNome().equals(info.get("eleicao"))) {
                        Eleicao eleicao = eleicoes.get(i);
                        listaEleicao = eleicao.getListas();
                    }
                }

                for (int i = 0; i < Integer.parseInt(info.get("tamanhoLista")); i++) {
                    lista += "item_" + i + "_name | " + listaEleicao.get(i).getNomeLista() + " ; ";
                }
                lista += "item_" + Integer.parseInt(info.get("tamanhoLista")) + "_name | " + "BRANCO" + " ; ";
                user.sendData(socket,
                        lista + "username | " + info.get("username") + " ; ccNumber | " + info.get("ccNumber")
                                + " ; terminalID | " + info.get("IDclient") + " ; eleicao | " + info.get("eleicao")
                                + " ; tamanhoLista | " + info.get("tamanho") + " ; serverName | " + user.getName());
            }
        }

        // O voto é guardado de modo secreto na storage do RMI e é feito display do voto em tempo real nos admin console
        else if (info.get("type").equals("done")) {
            if (user.getName().equals(info.get("serverName"))) {
                try {
                    h.saveVotes(info.get("eleicao"), info.get("voto"));
                    h.notifyClient(info.get("username"), " VOTOU NA MESA " + args );
                    state = true;

                } catch (Exception e) {
                    h = user.reconectRMI(h);
                    h.saveVotes(info.get("eleicao"), info.get("voto"));
                    h.notifyClient(info.get("username"), " VOTOU NA MESA " + args );
                    state = true;
                }
            }
        }

        //Se o voto for nulo
        else if (info.get("type").equals("null")) {
            if (user.getName().equals(info.get("serverName"))) {
                try {
                    h.saveVotes(info.get("eleicao"), info.get("voto"));
                    h.notifyClient(info.get("username") + " VOTO ", info.get("voto"));
                    state = true;

                } catch (Exception e) {
                    h = user.reconectRMI(h);
                    h.saveVotes(info.get("eleicao"), info.get("voto"));
                    h.notifyClient(info.get("username") + " VOTO ", info.get("voto"));
                    state = true;
                }
            }
        }

        else if (info.get("type").equals("voteDone")) {
            if (user.getName().equals(info.get("serverName"))) {
                try {
                    h.saveUserVote(info.get("username"), info.get("ccNumber"), info.get("eleicao"));
                    h.saveVotedPlaceOnPeople(info.get("username"), info.get("ccNumber"), this.getName());
                } catch (Exception e) {
                    h = user.reconectRMI(h);
                    h.saveUserVote(info.get("username"), info.get("ccNumber"), info.get("eleicao"));
                    h.saveVotedPlaceOnPeople(info.get("username"), info.get("ccNumber"), this.getName());
                }
            }
        }
    }

    // Run da Thread do Multicast Server

    public void run() {

        MulticastSocket socket = null;

        try {

            System.out.println("______________________________< " + args + " >________________________________________");
            System.out.println("______________________________< " + this.getName() + " >________________________________________");
            ClientRMI client = new ClientRMI(this.getName());

            try {

                // Guardamos esta Mesa no Array dos clientes
                h.saveClients(this.getName(), (InterfaceClientRMI) client);
                h.notifyClient(this.getName(), " -> on");

            } catch (Exception e) {

                h = (InterfaceServerRMI) LocateRegistry.getRegistry(7000).lookup("RMI Server");
                h.saveClients(this.getName(), (InterfaceClientRMI) client);
                h.notifyClient(this.getName(), " -> on");
            }

            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            HashMap<String, String> info = new HashMap<String, String>();

            while (true) {
                dataReceived = receiveData(socket);
                analyseData(socket, dataReceived, info);
            }

        } catch (IOException | NotBoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws IOException, NotBoundException {

        InterfaceServerRMI h = (InterfaceServerRMI) LocateRegistry.getRegistry(7000).lookup("RMI Server");
        server = new MulticastServer(h, args[0], args[0]); // THREAD RECEBE
        server.start();
        RuntimeDemo time = new RuntimeDemo(h, server);
        user = new MulticastUser(h, server, args[0], time); // THREAD ENVIA
        user.start();
        Runtime.getRuntime().addShutdownHook(time);

    }
}

class MulticastUser extends Thread {

    // "224.0.224.0"
    private String MULTICAST_ADDRESS;
    String name;
    private int PORT = 7000;
    MulticastServer server;
    boolean verify = false;
    InterfaceServerRMI h;
    RuntimeDemo time;
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    Scanner keyboardScanner = new Scanner(System.in);

    public InterfaceServerRMI reconectRMI(InterfaceServerRMI h) {

        long sTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - sTime < 30000) {
            try {
                h = (InterfaceServerRMI) LocateRegistry.getRegistry(7000).lookup("RMI Server");
                break;
            } catch (Exception ee) {
                System.out.println("Erro na conecao");
            }
        }
        return h;
    }

    public MulticastUser(InterfaceServerRMI h, MulticastServer server, String args, RuntimeDemo time) throws IOException {
        super("MESA " + (long) (Math.random() * 1000));
        this.h = h;
        this.server = server;
        FileInputStream fis = new FileInputStream("config.properties");
        Properties prop = new Properties();
        prop.load(fis);
        this.MULTICAST_ADDRESS = String.valueOf(prop.getProperty(args));
        this.name = args;
        this.time = time;
    }

    public void sendData(MulticastSocket socket, String data) throws IOException {
        String aux = data + " ";
        byte[] buffer = aux.getBytes();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }

    public void sendToServer(MulticastSocket socket, String data) throws IOException {
        String aux = data + " ";
        byte[] buffer = aux.getBytes();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
    }

    public void run() {
        MulticastSocket socket = null;
        try {
            String job;
            sleep(1000);
            String dep = name;
            System.out.println("\nDEPARTAMENTO DA MESA -> " + dep);
            socket = new MulticastSocket();
            System.out.println("<1> - LOGIN NA MESA DE VOTO");
            while (true) {
                System.out.print(">");
                String teste = reader.readLine();
                if (teste.equals("1")) {
                    server.state = false;
                    String aux, cc;
                    try {
                        if(!h.getLocal().contains(dep)){                            
                            System.out.print("NAME: ");
                            aux = reader.readLine();
                            System.out.print("CC NUMBER: ");
                            cc = reader.readLine();
                            time.name = aux;
                            time.ccNumber = cc;
                            time.tableLocal = dep;
                        } else {
                            int k = h.getLocal().indexOf(dep);
                            aux = h.getLocal().get(k+1);
                            cc = h.getLocal().get(k+2);
                            System.out.println("NAME: " + aux);
                            System.out.println("CC NUMBER: " + cc);   
                        }
                        h.addTable(aux, cc, dep);
                        job = h.getUserproperties(aux, cc);
                    }
                    catch (Exception e) {
                        h = reconectRMI(h);
                        if(!h.getLocal().contains(dep)){                            
                            System.out.print("NAME: ");
                            aux = reader.readLine();
                            System.out.print("CC NUMBER: ");
                            cc = reader.readLine();
                            time.name = aux;
                            time.ccNumber = cc;
                            time.tableLocal = dep;
                        }
                        else {
                            int k = h.getLocal().indexOf(dep);
                            aux = h.getLocal().get(k+1);
                            cc = h.getLocal().get(k+2);
                            System.out.println("NAME: " + aux);
                            System.out.println("CC NUMBER: " + cc);  
                        }
                        h.addTable(aux, cc, dep);
                        job = h.getUserproperties(aux, cc);
                    }
                    try {
                        h.verifyLogin(aux, cc);
                    } catch (Exception e) {
                        h = reconectRMI(h);
                        h.verifyLogin(aux, cc);
                    }
                    if(h.getLocal().indexOf(dep) != -1){
                        h.remeveLocal(h.getLocal().indexOf(dep));
                        h.remeveLocal(h.getLocal().indexOf(aux));
                        h.remeveLocal(h.getLocal().indexOf(cc));
                    }
                    System.out.println("SELECIONA A ELEICAO EM QUE QUER VOTAR:");
                    String eleicao;
                    int tamanho;
                    int flg = 0;
                    ArrayList<Eleicao> votar = new ArrayList<>();
                    try {
                        for (int i = 0; i < h.getEleicoes().size(); i++) {
                            // Não dá display das eleicoes que o utilizador já votou ou das eleicoes que ja
                            // acabaram
                            if (h.verifyUserinArray(aux, cc, h.getEleicoes().get(i)) == false && h.stateOfElections(h.getEleicoes().get(i), 2)) {
                                if (h.getEleicoes().get(i).publicoAlvo.toUpperCase().equals(job.toUpperCase())) {
                                    votar.add(h.getEleicoes().get(i));
                                    flg = 1;
                                }
                            }
                        }
                        for(int i = 0; i< votar.size(); i++){
                            System.out.println("\t<" + i + "> " + votar.get(i).getNome());
                        }
                        if (flg == 1) {
                            int numEleicoes = keyboardScanner.nextInt();
                            eleicao = votar.get(numEleicoes).getNome();
                            tamanho = votar.get(numEleicoes).getListas().size();
                            sendToServer(socket,
                                    "type | login ; username | " + aux + " ; ccNumber | " + cc + " ; eleicao | "
                                            + eleicao + " ; tamanhoLista | " + tamanho + " ; serverName | "
                                            + getName());
                        }
                        else {
                            System.out.println("NAO HA ELEICOES DISPONIVEIS");
                        }

                    } catch (Exception e) {
                        h = reconectRMI(h);
                        for (int i = 0; i < h.getEleicoes().size(); i++) {
                            // Não dá display das eleicoes que o utilizador já votou ou das eleicoes que ja acabaram
                            if (h.verifyUserinArray(aux, cc, h.getEleicoes().get(i)) == false && h.stateOfElections(h.getEleicoes().get(i), 2)) {
                                if (h.getEleicoes().get(i).publicoAlvo.toUpperCase().equals(job.toUpperCase())) {
                                    votar.add(h.getEleicoes().get(i));
                                    flg = 1;
                                }
                            }
                        }
                        for(int i = 0; i< votar.size(); i++){
                            System.out.println("\t<" + i + "> " + votar.get(i).getNome());
                        }
                        if (flg == 1) {
                            int numEleicoes = keyboardScanner.nextInt();
                            eleicao = votar.get(numEleicoes).getNome();
                            tamanho = votar.get(numEleicoes).getListas().size();
                            sendToServer(socket,
                                    "type | login ; username | " + aux + " ; ccNumber | " + cc + " ; eleicao | "
                                            + eleicao + " ; tamanhoLista | " + tamanho + " ; serverName | "
                                            + getName());
                        }
                        else {
                            System.out.println("NAO HA ELEICOES DISPONIVEIS");
                        }
                    }
                }
            }
        } catch (IOException | ParseException e) {

            System.out.println("ERRO FATAL NOS SERVIDORES, POR FAVOR TENTE SE CONECTAR MAIS TARDE !");

        } catch (InterruptedException e1) {

            e1.printStackTrace();

        }
        finally {

            socket.close();

        }
    }
}

// class para detetar falha no programa
class RuntimeDemo extends Thread {

    InterfaceServerRMI h;
    MulticastServer server;
    String name, ccNumber, tableLocal;

    public RuntimeDemo(InterfaceServerRMI h, MulticastServer server) {
        this.h = h;
        this.server = server;
    }

    public void run() {

        try {
            h.notifyClient("MESA " + server.getName(), " -> off");

            h.addLocal(tableLocal);
            h.addLocal(name);
            h.addLocal(ccNumber);

            h.notifyClient(String.valueOf(h.getLocal().size()), "--");

        } catch (RemoteException e) {
            System.out.println("ERRO!");
        }

    }

}
