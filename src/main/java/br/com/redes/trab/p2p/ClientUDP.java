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
    private final String command;
    private DatagramSocket sendSocket;
    private DatagramPacket sendPacket;
    private DatagramSocket recvSocket;
    private DatagramPacket recvPacket;
    private final String ip = "192.168.0.255";
    private final String path;

    public ClientUDP(List textAreaClient, List textError, List listFiles, String command, String path) {
        this.textAreaClient = textAreaClient;
        this.textError = textError;
        this.listFiles = listFiles;
        this.command = command;
        this.path = path;
    }

    @Override
    public void run() {

        try {
            //Abre Socket em uma porta aleatoria para envio de pacotes
            sendSocket = new DatagramSocket();
            sendSocket.setBroadcast(true);
            
            textAreaClient.add(">>> Request sent to " + ip + " broadcast.");
            
            /*  Se command = 1, ClientUDP deseja perguntar aos ServersUDP quais sao o seus arquivos disponiveis para download 
                Se command = 2, ClientUDP deseja solicitar ao ServerUDP o download do devido arquivo */

            byte[] sendData = command.getBytes();
            sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip), 5555);
            sendSocket.send(sendPacket);
            
            if (command.equals("2")) {

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
                    System.out.println("Recebido: " + new String(recvPacket.getData()));

                    recv = new String(recvPacket.getData());
                    String[] split = recv.split(",");
                    host = split[0];

                    for (int i = 1; i < split.length; i++) {
                        listFiles.add(host + " " + split[i]);
                    }
                    textAreaClient.add("Received File List from " + host);
                }
            } else if (command.equals("3")) {
                String[] selectedFile = getSelectedFile();
                String ip = selectedFile[0];
                String fileName = selectedFile[1];

                ClientTCP clientTCP = new ClientTCP(textAreaClient, textError, path, fileName, ip);
                Thread threadClientTCP = new Thread(clientTCP);
                threadClientTCP.start();
                closeConnection();
            } 
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

    public String[] getSelectedFile() {
        String[] separated;
        
        if (listFiles.getItemCount() == 0) {
            return null;
        }

        for (int i = 0; i < listFiles.getItemCount(); i++) {
            if (listFiles.isIndexSelected(i)) {
                separated = listFiles.getItem(i).split(" ");
                return separated;
            }
        }
        return null;
    }

}
