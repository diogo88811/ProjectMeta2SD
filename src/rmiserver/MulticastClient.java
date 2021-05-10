package rmiserver;

import java.net.MulticastSocket;

import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class MulticastClient extends Thread {

    private String MULTICAST_ADDRESS;
    private static MulticastClient client;
    boolean state = true;
    private int PORT = 7000;
    String args;
    HashMap<String, String> info = new HashMap<String, String>();
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    Scanner keyboardScanner = new Scanner(System.in);

    public MulticastClient(String MULTICAST_ADDRESS, String args) {
        super("TERMINAL " + (long) (Math.random() * 1000));
        this.MULTICAST_ADDRESS = MULTICAST_ADDRESS;
        this.args = args;
    }

    public String sendData(MulticastSocket socket, String msg) throws IOException {

        Scanner keyboardScanner = new Scanner(System.in);
        String aux = msg;
        byte[] buffer = aux.getBytes();
        InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        socket.send(packet);
        return aux;
    }

    public String receiveData(MulticastSocket socket) throws IOException {

        byte[] buffer = new byte[256];

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);

        String message = new String(packet.getData(), 0, packet.getLength());

        return message;

    }

    public void analyzeData(MulticastSocket socket, String data) throws IOException, InterruptedException {

        String aux[] = data.split("[;]");

        for (String a : aux) {
            String types[] = a.split("\\|");
            info.put(types[0].trim(), types[1].trim());
        }

        if (info.get("type").equals("request") && state == true) {
            /*->*/ if (state == true) {
                sendData(socket,
                        "type | requestAnswer ; NumberRequest | " + info.get("NumberRequest") + " ; IDclient | "
                                + this.getName() + " ; msg | FREE ; eleicao | " + info.get("eleicao")
                                + " ; tamanhoLista | " + info.get("tamanhoLista") + " ; serverName | "
                                + info.get("serverName"));
            }
        }

        else if (info.get("type").equals("reserve")) {
            if (this.getName().equals(info.get("terminalID"))) {
                sendData(socket,
                        "type | reserved ; IDclient | " + this.getName() + " ; eleicao | " + info.get("eleicao")
                                + " ; IDclient | " + this.getName() + " ; tamanhoLista | " + info.get("tamanhoLista")
                                + " ; serverName | " + info.get("serverName"));
                state = false;
                System.out.print("NOME : " + info.get("username"));
                System.out.print("\nNUMERO CC : " + info.get("ccNumber"));
                System.out.print("\nPASSWORD: ");
                String password = null;

                Scanner sc = new Scanner(System.in);
                long sTime = System.currentTimeMillis();
                while (System.currentTimeMillis() - sTime < 60000) {
                    if (System.in.available() > 0) {
                        password = sc.nextLine();
                        sendData(socket,"type | authentication ; username | " + info.get("username") + " ; IDclient | " + this.getName() + " ; ccNumber | " + info.get("ccNumber") + " ; PASSWORD | " + password + " ; eleicao | " + info.get("eleicao") + " ; tamanhoLista | " + info.get("tamanhoLista") + " ; serverName | " + info.get("serverName"));
                        break;
                    }
                }

                if (password == null) {
                    System.out.println("\nERRO NA AUTENTICACAO DO UTILIZADOR");
                    state = true;
                }
            }
        }

        else if (info.get("type").equals("vote") && info.get("userData").equals("valid")) {
            if (this.getName().equals(info.get("terminalID"))) {
                System.out.println("\nBEM VINDO, " + info.get("username") + " !\n");
                sendData(socket,
                        "type | item_listRequire ; username | " + info.get("username") + " ; ccNumber | "
                                + info.get("ccNumber") + " ; IDclient | " + this.getName() + " ; eleicao | "
                                + info.get("eleicao") + " ; tamanhoLista | " + info.get("tamanhoLista")
                                + " ; serverName | " + info.get("serverName"));
            }
        }

        else if (info.get("type").equals("item_list")) {
            if (this.getName().equals(info.get("terminalID"))) {
                System.out.println("LISTA DE CANDIDATOS");
                int k = 0;
                for (int i = 0; i < Integer.parseInt(info.get("item_count")); i++) {
                    System.out.println("<" + i + "> " + info.get("item_" + i + "_name"));

                    k = i;

                }

                k += 1;
                System.out.println("<" + k + "> " + "VOTO BRANCO ");
                String auxVoto = null;

                Scanner scan = new Scanner(System.in);

                long sTime = System.currentTimeMillis();

                while (System.currentTimeMillis() - sTime < 60000) {
                    if (System.in.available() > 0) {
                        auxVoto = scan.nextLine();
                        if (Integer.parseInt(auxVoto) <= Integer.parseInt(info.get("item_count"))) {
                            sendData(socket,"type | done ; IDclient | " + info.get("username") + " ; voto | " + info.get("item_" + Integer.parseInt(auxVoto) + "_name") + " ; serverName | " + info.get("serverName"));
                            sendData(socket,
                                    "type | voteDone ; username | " + info.get("username") + " ; eleicao | "
                                            + info.get("eleicao") + " ; ccNumber | " + info.get("ccNumber")
                                            + " ; serverName | " + info.get("serverName"));
                            state = true;

                            System.out.println("O SEU VOTO FOI REGISTADO COM SUCESSO !");

                        }

                        else {
                            sendData(socket, "type | null ; username | " + info.get("username")
                                    + " ; voto | NULL ; serverName | " + info.get("serverName"));
                            sendData(socket,
                                    "type | voteDone ; username | " + info.get("username") + " ; eleicao | "
                                            + info.get("eleicao") + " ; ccNumber | " + info.get("ccNumber")
                                            + " ; serverName | " + info.get("serverName"));
                            state = true;
                            System.out.println("O SEU VOTO FOI REGISTADO COM SUCESSO !");
                        }
                        break;
                    }
                }
                if (auxVoto == null) {
                    System.out.println("\nERRO NA VOTACAO");
                    state = true;
                }
            }
        }

        else if (info.get("type").equals("restart")) {
            if (this.getName().equals(info.get("terminalID"))) {
                state = true;
            }
        }
    }

    public void run() {

        MulticastSocket socket = null;
        try {
            
            FileInputStream fis = new FileInputStream("config.properties");
            Properties prop = new Properties();
            prop.load(fis);
            MULTICAST_ADDRESS = String.valueOf(prop.getProperty(args));

            System.out.println("______________________________< " + args + " >________________________________________");
            System.out.println("______________________________< " + this.getName() + " >________________________________________");
            socket = new MulticastSocket(PORT);
            InetAddress group = InetAddress.getByName(MULTICAST_ADDRESS);
            socket.joinGroup(group);
            String data;

            while (true) {
                data = receiveData(socket);
                analyzeData(socket, data);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        client = new MulticastClient(args[0], args[0]);
        client.start();
    }

}
