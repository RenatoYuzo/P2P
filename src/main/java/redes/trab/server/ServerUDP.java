package redes.trab.server;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import redes.trab.util.Variables;

public class ServerUDP implements Runnable {

    private String command;
    private DatagramPacket recvPacket = null;
    DatagramSocket recvSocket;
    private String fileName;
    private String[] commandSplit;
    private Variables v = new Variables();

    @Override
    public void run() {
        try {
            v = new Variables();
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            recvSocket = new DatagramSocket(null);
            recvSocket.setReuseAddress(true);
            recvSocket.setBroadcast(true);
            recvSocket.bind(new InetSocketAddress(v.myIP, 5555));

            while (true) {
                v.textArea.add(">>>   Ready to receive broadcast packets!");
                
                receivedPacket();

                if (commandSplit[0].equals("1")) {
                    sendingRespondeFromOption1();
                } else if (commandSplit[0].equals("2")) {
                    sendingResponseFromOption2and4();
                } else if (commandSplit[0].equals("3")) {
                    sendingRespondeFromOption3();
                } else if (commandSplit[0].equals("4")) {
                    sendingResponseFromOption2and4();
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

    private void sendingRespondeFromOption1() throws IOException {
        //String ip = Inet4Address.getLocalHost().getHostAddress();
        String ip = v.myIP;
        
        byte[] sendData = ip.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recvPacket.getAddress().getHostAddress()), 5556);
        recvSocket.send(sendPacket);
    }

    private void sendingRespondeFromOption3() throws UnknownHostException {
        System.out.println("Inet4Address.getLocalHost().getHostAddress(): " + Inet4Address.getLocalHost().getHostAddress());

        ServerTCP serverTCP = new ServerTCP(v.textArea, v.textError, v.srcFolder, v.myIP, "p.fileName");
        Thread threadServerTCP = new Thread(serverTCP);
        threadServerTCP.start();
    }

    private void sendingResponseFromOption2and4() throws IOException {
        ArrayList<String> listOfNameFiles = getFiles();
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        //String msg = Inet4Address.getLocalHost().getHostAddress();
        String msg = v.myIP;
        
        for (int i = 0; i < listOfNameFiles.size(); i++) {
            msg = msg + "," + listOfNameFiles.get(i);
        }

        System.out.println("Enviado: " + msg);
        byte[] sendData = msg.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recvPacket.getAddress().getHostAddress()), 5556);
        recvSocket.send(sendPacket);
    }

    private void receivedPacket() throws IOException {
        byte[] recvData = new byte[1024];
        recvPacket = new DatagramPacket(recvData, recvData.length);
        recvSocket.receive(recvPacket);

        //Packet received
        v.textArea.add(">>>Discovery packet received from: " + recvPacket.getAddress().getHostAddress());
        v.textArea.add(">>>Packet received; data: " + new String(recvPacket.getData()));

        command = new String(recvPacket.getData());
        commandSplit = command.split(",");
        fileName = commandSplit[1];
        fileName = fileName.trim();
        System.out.println("fileName: " + fileName);
    }

}
