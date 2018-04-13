package br.com.redes.trab.p2p;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 *
 * @author RenatoYuzo
 */
public class ServerUDP implements Runnable {

    private final List textArea;
    private final List textError;
    private final List listFiles;
    private final String path;
    private String command;
    private DatagramPacket recvPacket = null;

    public ServerUDP(List textArea, List textError, List listFiles, String path) {
        this.textArea = textArea;
        this.textError = textError;
        this.path = path;
        this.listFiles = listFiles;
    }

    @Override
    public void run() {
        try {
            DatagramSocket recvSocket;

            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            recvSocket = new DatagramSocket(5555);
            //recvSocket.setReuseAddress(true);
            //recvSocket.setBroadcast(true);
            //recvSocket.bind(new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), 5555));            

            while (true) {
                textArea.add(">>>Ready to receive broadcast packets!");
                //System.out.println(">>>Ready to receive broadcast packets!");

                //Receive a packet
                byte[] recvData = new byte[1024];
                //recvPacket = new DatagramPacket(recvData, recvData.length, InetAddress.getByName("255.255.255.255"), 5555);
                recvPacket = new DatagramPacket(recvData, recvData.length);
                recvSocket.receive(recvPacket);

                //Packet received
                textArea.add(">>>Discovery packet received from: " + recvPacket.getAddress().getHostAddress());
                textArea.add(">>>Packet received; data: " + new String(recvPacket.getData()));

                command = new String(recvPacket.getData());

                if (command.trim().equals("2")) {

                    //System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                    ArrayList<String> listOfNameFiles = getFiles();
                    System.out.println(Inet4Address.getLocalHost().getHostAddress());
                    String msg = Inet4Address.getLocalHost().getHostAddress();
                    
                    for (int i = 0; i < listOfNameFiles.size(); i++) {
                        msg = msg + "," + listOfNameFiles.get(i);
                    }

                    System.out.println("Enviado: " + msg);
                    byte[] sendData = msg.getBytes();

                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recvPacket.getAddress().getHostAddress()), 5556);
                    recvSocket.send(sendPacket);

                } else if (command.trim().equals("3")) {
                    if (recvPacket != null) {
                        textArea.add("Tres RECEBIDO!! UHUL!!");
                        System.out.println("recvPacket.getAddress().getHostAddress(): " + Inet4Address.getLocalHost().getHostAddress());
                        ServerTCP serverTCP = new ServerTCP(textArea, textError, listFiles, path, Inet4Address.getLocalHost().getHostAddress());
                        Thread threadServerTCP = new Thread(serverTCP);
                        threadServerTCP.start();
                    }
                }

            }
        } catch (IOException ex) {
            textError.add(ex.getMessage());
        }
    }

    public ArrayList<String> getFiles() {

        try {
            File file = new File(path);
            File[] listOfFiles = file.listFiles();
            ArrayList<String> listOfNameFiles = new ArrayList();

            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    listOfNameFiles.add(listOfFile.getName());
                }

                /*if (listOfFile.isFile()) {
                    listFiles.add(serverSocket.getInetAddress().getHostAddress() + " " + listOfFile.getName());
                }*/
            }
            return listOfNameFiles;
        } catch (Exception e) {
            textError.add("Error: " + e.getMessage());
        }
        return null;

    }

}
