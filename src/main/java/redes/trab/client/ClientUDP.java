package redes.trab.client;

import java.awt.List;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;

/**
 *
 * @author RenatoYuzo
 */
public class ClientUDP implements Runnable {

    private final List textAreaClient;
    private final List textError;
    private final List listFiles;
    private final String command;
    private final String askedFile;
    private DatagramSocket sendSocket;
    private DatagramPacket sendPacket;
    private DatagramSocket recvSocket;
    private DatagramPacket recvPacket;
    private final String ip = "192.168.0.255";
    private final String path;
    private String strAux;

    public ClientUDP(List textAreaClient, List textError, List listFiles, String command, String path, String askedFile) {
        this.textAreaClient = textAreaClient;
        this.textError = textError;
        this.listFiles = listFiles;
        this.command = command;
        this.path = path;
        this.askedFile = askedFile;
    }

    @Override
    public void run() {

        try {
            //Abre Socket em uma porta aleatoria para envio de pacotes
            sendSocket = new DatagramSocket();
            sendSocket.setBroadcast(true);

            strAux = command + ",";

            /*  Se command = 2, ClientUDP deseja perguntar aos ServersUDP quais sao o seus arquivos disponiveis para download 
                Se command = 3, ClientUDP deseja solicitar ao ServerUDP o download do devido arquivo */
            if (command.equals("1")) {
                sendingRequestFromOption1();
                waitingResponseFromOption1();
            } else if (command.equals("2")) {
                sendingRequestFromOption2();
                waitingResponseFromOption2();
            } else if (command.equals("3")) {
                sendingRequestFromOption3();
                waitingResponseFromOption3();
            } else if (command.equals("4")) {
                sendingRequestFromOption4();
                waitingResponseFromOption4();
            }

            closeConnection();
        } catch (SocketException ex) {
            textError.add(ex.getMessage());
            closeConnection();
        } catch (Exception e) {
            textError.add(e.getMessage());
            fileFoundCount();
            closeConnection();
        }

    }

    private void closeConnection() {

        try {
            if (sendSocket != null) {
                sendSocket.close();
            }
            if (recvSocket != null) {
                recvSocket.close();
            }
            textAreaClient.add("Disconnected");
        } catch (Exception e) {
            textError.add(e.getMessage());
        }

    }

    private void fileFoundCount() {
        switch (listFiles.getItemCount()) {
            case 0:
                JOptionPane.showMessageDialog(null, "No file found.", "Message", JOptionPane.INFORMATION_MESSAGE);
                break;
            case 1:
                JOptionPane.showMessageDialog(null, "1 file found.", "Message", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                JOptionPane.showMessageDialog(null, listFiles.getItemCount() + " files found.", "Message", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    private String[] getSelectedFile() {
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

    private void sendingRequestFromOption1() throws UnknownHostException, IOException {

    }

    private void sendingRequestFromOption2() throws UnknownHostException, IOException {
        System.out.println("strAux: " + strAux);
        byte[] sendData = strAux.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip), 5555);
        sendSocket.send(sendPacket);
        textAreaClient.add(">>> Request sent to " + ip + " broadcast.");
    }

    private void sendingRequestFromOption3() throws UnknownHostException, IOException {
        String[] aux = getSelectedFile();
        strAux += aux[1];
        System.out.println("strAux += aux[1]: " + strAux);
        byte[] sendData = strAux.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip), 5555);
        sendSocket.send(sendPacket);
        textAreaClient.add(">>> Request sent to " + ip + " broadcast.");
    }

    private void sendingRequestFromOption4() throws UnknownHostException, IOException {
        strAux += askedFile;
        System.out.println("strAux += aux[1]: " + strAux);
        byte[] sendData = strAux.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip), 5555);
        sendSocket.send(sendPacket);
        textAreaClient.add(">>> Request sent to " + ip + " broadcast.");
    }

    private void waitingResponseFromOption1() {
        JOptionPane.showMessageDialog(null, "Not implemented yet.");

    }

    private void waitingResponseFromOption2() throws SocketException, IOException {

        recvSocket = new DatagramSocket(null);
        InetSocketAddress address = new InetSocketAddress(5556);
        recvSocket.setReuseAddress(true);
        recvSocket.bind(address);
        recvSocket.setBroadcast(true);
        recvSocket.setSoTimeout(2000);

        String recv = "";
        String host = "";
        listFiles.removeAll();
        while (true) {
            byte[] recvData = new byte[1024];
            recvPacket = new DatagramPacket(recvData, recvData.length);
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
    }

    private void waitingResponseFromOption3() {
        String[] selectedFile = getSelectedFile();
        String ipAddress = selectedFile[0];
        String nameFile = selectedFile[1];

        ClientTCP clientTCP = new ClientTCP(textAreaClient, textError, path, nameFile, ipAddress);
        Thread threadClientTCP = new Thread(clientTCP);
        threadClientTCP.start();
    }

    private void waitingResponseFromOption4() throws SocketException, IOException {

        recvSocket = new DatagramSocket(null);
        InetSocketAddress address = new InetSocketAddress(5556);
        recvSocket.setReuseAddress(true);
        recvSocket.bind(address);
        recvSocket.setBroadcast(true);
        recvSocket.setSoTimeout(2000);

        String recv = "";
        String host = "";

        listFiles.removeAll();
        while (true) {
            byte[] recvData = new byte[1024];
            recvPacket = new DatagramPacket(recvData, recvData.length);
            recvSocket.receive(recvPacket);
            System.out.println("Recebido: " + new String(recvPacket.getData()));

            recv = new String(recvPacket.getData());
            String[] split = recv.split(",");
            host = split[0];

            for (int i = 1; i < split.length; i++) {
                if (split[i].equals(askedFile)) {
                    listFiles.add(host + " " + split[i]);
                }
            }
            textAreaClient.add("Received File List from " + host);
            
        }
    }

}
