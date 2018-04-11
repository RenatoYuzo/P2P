package br.com.redes.trab.p2p;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
    private final String path;
    private String command;

    public ServerUDP(List textArea, List textError, String path) {
        this.textArea = textArea;
        this.textError = textError;
        this.path = path;
    }

    @Override
    public void run() {
        try {
            DatagramSocket socket;

            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            
            
            socket = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(5555);            
            socket.setReuseAddress(true);            
            socket.bind(address);
            socket.setBroadcast(true);

            while (true) {
                textArea.add(">>>Ready to receive broadcast packets!");
                //System.out.println(">>>Ready to receive broadcast packets!");

                //Receive a packet
                byte[] recvData = new byte[1024];
                DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
                socket.receive(recvPacket);

                //Packet received
                textArea.add(">>>Discovery packet received from: " + recvPacket.getAddress().getHostAddress());
                textArea.add(">>>Packet received; data: " + new String(recvPacket.getData()));

                command = new String(recvPacket.getData());
                

                if (command.trim().equals("1")) {
                    
                    //System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                    
                    ArrayList<String> listOfNameFiles = getFiles();
                    String msg = recvPacket.getAddress().getHostAddress();
                    for (int i = 0; i < listOfNameFiles.size(); i++) {
                        msg = msg + "," + listOfNameFiles.get(i);
                    }
                    
                    //System.out.println(msg);
                    byte[] sendData = msg.getBytes();
                    
                    
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recvPacket.getAddress().getHostAddress()), 5556);
                    socket.send(sendPacket);
                    
                } else if (command.equals("2")) {

                } else if (command.equals("3")) {

                }
                //System.out.println(">>>Discovery packet received from: " + packet.getAddress().getHostAddress());
                //System.out.println(">>>Packet received; data: " + new String(packet.getData()));

                //See if the packet holds the right command (message)
                /*String message = new String(packet.getData()).trim();
                if (message.equals("DISCOVER_FUIFSERVER_REQUEST")) {
                    byte[] sendData = "DISCOVER_FUIFSERVER_RESPONSE".getBytes();

                    //Send a response
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
                    socket.send(sendPacket);

                    System.out.println(">>>Sent packet to: " + sendPacket.getAddress().getHostAddress());
                }*/
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
