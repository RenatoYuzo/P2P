package redes.trab.server;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import redes.trab.packet.Packet;
import redes.trab.util.Variables;

public class ServerUDP implements Runnable {

    private DatagramPacket recvPacket = null;
    private DatagramSocket recvSocket;
    private Packet p;
    private Gson json;
    private Variables v = new Variables();

    @Override
    public void run() {
        try {
            v = new Variables();
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            recvSocket = new DatagramSocket(null);
            recvSocket.setReuseAddress(true);
            recvSocket.setBroadcast(true);
            recvSocket.bind(new InetSocketAddress(v.myIP, v.portUDPsend));

            while (true) {
                v.textArea.add(">>>   Ready to receive broadcast packets!");

                receivedPacket();

                switch (p.getCommand()) {
                    case 1:
                        sendingRespondeFromOption1();
                        break;
                    case 2:
                        sendingResponseFromOption2();
                        break;
                    case 3:
                        sendingRespondeFromOption3();
                        break;
                    case 4:
                        sendingResponseFromOption4();
                        break;
                    default:
                        break;
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
            v.textError.add(this.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }

    private ArrayList<String> getFiles() {

        try {
            File file = new File(v.srcFolder);
            File[] listOfFiles = file.listFiles();
            ArrayList<String> listOfNameFiles = new ArrayList();

            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    listOfNameFiles.add(listOfFile.getName());
                }
            }
            return listOfNameFiles;
        } catch (Exception e) {
            e.printStackTrace();
            v.textError.add("Error: " + e.getMessage());
        }
        return null;

    }

    private ArrayList<String> getFiles(String fileName) {

        try {
            File file = new File(v.srcFolder);
            File[] listOfFiles = file.listFiles();
            ArrayList<String> listOfNameFiles = new ArrayList();

            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    if (listOfFile.getName().equals(fileName)) {
                        listOfNameFiles.add(listOfFile.getName());
                    }
                }
            }
            return listOfNameFiles;
        } catch (Exception e) {
            e.printStackTrace();
            v.textError.add("Error: " + e.getMessage());
        }
        return null;

    }

    private void sendingRespondeFromOption1() throws IOException {
        p = new Packet(v.myIP, 1);
        json = new Gson();
        String msg = json.toJson(p);

        System.out.println("JSON enviado pelo Server: " + msg);

        byte[] sendData = msg.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recvPacket.getAddress().getHostAddress()), v.portUDPrecv);
        recvSocket.send(sendPacket);
    }

    private void sendingResponseFromOption2() throws IOException {
        p = new Packet(v.myIP, 2);
        p.setListOfFiles(getFiles());

        json = new Gson();
        String msg = json.toJson(p);
        System.out.println("JSON enviado pelo Server: " + msg);
        byte[] sendData = msg.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recvPacket.getAddress().getHostAddress()), v.portUDPrecv);
        recvSocket.send(sendPacket);

    }

    private void sendingRespondeFromOption3() throws UnknownHostException {
        ServerTCP serverTCP = new ServerTCP(p.getFileName());
        Thread threadServerTCP = new Thread(serverTCP);
        threadServerTCP.start();
    }

    private void sendingResponseFromOption4() throws IOException {
        ArrayList<String> listOfNameFiles = getFiles(p.getFileName());

        if (listOfNameFiles.size() > 0) {
            p = new Packet(v.myIP, 4);
            p.setListOfFiles(listOfNameFiles);
            
            json = new Gson();
            String msg = json.toJson(p);
            System.out.println("JSON enviado pelo Server: " + msg);
            byte[] sendData = msg.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recvPacket.getAddress().getHostAddress()), v.portUDPrecv);
            recvSocket.send(sendPacket);
        }
    }

    private void receivedPacket() throws IOException {
        byte[] recvData = new byte[1024];
        recvPacket = new DatagramPacket(recvData, recvData.length);
        recvSocket.receive(recvPacket);
        
        String msg = new String(recvPacket.getData()).trim();
        System.out.println("JSON foi recebido pelo Server: " + msg);
        
        json = new Gson();
        p = json.fromJson(msg, Packet.class);
        
        //Packet received
        v.textArea.add(">>>Discovery packet received from: " + p.getMyIP());
        v.textArea.add(">>>Packet received; data: " + msg);
    }

}
