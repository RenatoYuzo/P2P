package br.com.redes.trab.p2p;

import java.awt.List;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

/**
 *
 * @author RenatoYuzo
 */
public class ClientUDP implements Runnable {

    private final List textAreaClient;
    private final List textError;
    private final List listFiles;
    private String command;
    private DatagramSocket sendSocket;
    private DatagramPacket sendPacket;
    private DatagramSocket recvSocket;
    private DatagramPacket recvPacket;

    public ClientUDP(List textAreaClient, List textError, List listFiles, String command) {
        this.textAreaClient = textAreaClient;
        this.textError = textError;
        this.listFiles = listFiles;
        this.command = command;
    }

    @Override
    public void run() {

        try {
            //Abre Socket em uma porta aleatoria para envio de pacotes
            sendSocket = new DatagramSocket();
            sendSocket.setBroadcast(true);
            sendSocket.setReuseAddress(true);

            byte[] sendData = command.getBytes();
            String ip = "255.255.255.255";

            sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip), 5555);
            sendSocket.send(sendPacket);

            textAreaClient.add(">>> Request packet sent to: " + ip);
            //System.out.println(">>> Request packet sent to: " + ip);

            byte[] recvData = new byte[1024];
            recvPacket = new DatagramPacket(recvData, recvData.length);

            recvSocket = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(5556);
            recvSocket.setReuseAddress(true);
            recvSocket.bind(address);
            recvSocket.setBroadcast(true);
            recvSocket.setSoTimeout(2000);

            String recv = "";
            String host = "";
            while (true) {

                recvSocket.receive(recvPacket);

                recv = new String(recvPacket.getData());
                //System.out.println(recv);
                String[] split = recv.split(",");
                host = split[0];

                for (int i = 1; i < split.length; i++) {
                    listFiles.add(host + ": " + split[i]);
                }

                //System.out.println(recv);
                //System.out.println(recvPacket.getAddress().getHostAddress() + ": " + new String(recvPacket.getData()));
                textAreaClient.add("Received File List from " + recvPacket.getAddress().getHostAddress());
            }

            //socket.close();
        } catch (SocketException ex) {
            textError.add(ex.getMessage());
            closeConnection();
        } catch (Exception e) {
            textError.add(e.getMessage());
            closeConnection();
        }

    }

    public void closeConnection() {
        
        try {
            if (sendSocket != null) {
                sendSocket.close();
            }
            if (recvSocket != null) {
                recvSocket.close();
            }
        } catch (Exception e) {
            textError.add(e.getMessage());
        }

    }

}
