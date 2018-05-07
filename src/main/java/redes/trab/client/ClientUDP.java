/** ************************************************************************
 * Esta classe implementa um Cliente para conexao UDP, enviando um pacote
 * que contem uma mensagem que sera lida e interpretada pelo Servidor
 ************************************************************************* */
package redes.trab.client;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import redes.trab.packet.Packet;
import redes.trab.util.Util;
import redes.trab.util.Variables;
import redes.trab.view.MainView;

public class ClientUDP implements Runnable {

    private DatagramSocket sendSocket;
    private DatagramPacket sendPacket;
    private DatagramSocket recvSocket;
    private DatagramPacket recvPacket;
    private Packet p;
    private Gson json;
    private Variables v = new Variables();

    @Override
    public void run() {

        try {
            //Abre Socket em uma porta aleatoria para envio de pacotes
            sendSocket = new DatagramSocket();
            sendSocket.setBroadcast(true);

            /*  Se command = 1, ClientUDP faz uma busca de todos os IP's da rede
                Se command = 2, ClientUDP deseja perguntar aos ServersUDP quais sao o seus arquivos disponiveis para download 
                Se command = 3, ClientUDP deseja solicitar ao ServerUDP o download do arquivo selecionado
                Se command = 4, ClientUDP pergunta para todos os ServersUDP, se eles tem o arquivo digitado 
                Logo apos uma requisicao ao servidor, o Cliente ficara em modo de espera por respostas */
            switch (v.command) {
                case 1:
                    sendingRequestFromOption1();
                    waitingResponseFromOption1();
                    break;
                case 2:
                    sendingRequestFromOption2();
                    waitingResponseFromOption2and4();
                    break;
                case 3:
                    sendingRequestFromOption3();
                    waitingResponseFromOption3();
                    break;
                case 4:
                    sendingRequestFromOption4();
                    waitingResponseFromOption2and4();
                    break;
                default:
                    break;
            }

            closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
            v.textError.add(e.getMessage());
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
            v.textAreaClient.add("Disconnected");
        } catch (Exception e) {
            v.textError.add(e.getMessage());
        }

    }

    private void sendingRequestFromOption1() throws UnknownHostException, IOException {        
        p = new Packet(v.myIP, 1);
        json = new Gson();
        String msg = json.toJson(p);

        System.out.println("JSON enviado pelo Client: " + msg);

        byte[] sendData = msg.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(v.broadcastIP), v.portUDPsend);
        sendSocket.send(sendPacket);
        v.textAreaClient.add(">>> Request sent to " + v.broadcastIP + " broadcast.");
    }

    private void sendingRequestFromOption2() throws UnknownHostException, IOException {
        p = new Packet(v.myIP, 2);
        json = new Gson();
        String msg = json.toJson(p);
        System.out.println("JSON enviado pelo Client: " + msg);

        byte[] sendData = msg.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(v.broadcastIP), v.portUDPsend);
        sendSocket.send(sendPacket);
        v.textAreaClient.add(">>> Request sent to " + v.broadcastIP + " broadcast.");
    }

    private void sendingRequestFromOption3() throws UnknownHostException, IOException {
        p = new Packet(v.myIP, 3);
        p.setFileName(Util.getSelectedFile(v.listFiles)[1]);
        json = new Gson();
        String msg = json.toJson(p);
        System.out.println("JSON enviado pelo Client: " + msg);

        byte[] sendData = msg.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(v.broadcastIP), v.portUDPsend);
        sendSocket.send(sendPacket);
        v.textAreaClient.add(">>> Request sent to " + v.broadcastIP + " broadcast.");
    }

    private void sendingRequestFromOption4() throws UnknownHostException, IOException {
        p = new Packet(v.myIP, 4);
        p.setFileName(v.askedFile);
        json = new Gson();
        String msg = json.toJson(p);
        System.out.println("JSON enviado pelo Client: " + msg);

        byte[] sendData = msg.getBytes();
        sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(v.broadcastIP), v.portUDPsend);
        sendSocket.send(sendPacket);
        v.textAreaClient.add(">>> Request sent to " + v.broadcastIP + " broadcast.");
    }

    private void waitingResponseFromOption1() {
        try {
            recvSocket = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(v.portUDPrecv);
            recvSocket.setReuseAddress(true);
            recvSocket.bind(address);
            recvSocket.setBroadcast(true);
            recvSocket.setSoTimeout(2000);

            v.listFiles.removeAll();
            while (true) {
                byte[] recvData = new byte[1024];
                recvPacket = new DatagramPacket(recvData, recvData.length);
                recvSocket.receive(recvPacket);

                String msg = new String(recvPacket.getData()).trim();
                System.out.println("JSON foi recebido pelo Client: " + msg);

                p = json.fromJson(msg, Packet.class);

                v.listFiles.add(p.getMyIP());

                v.textAreaClient.add("Received File List from " + p.getMyIP());
            }
        } catch (SocketTimeoutException ste) {
            v.textAreaClient.add(">>> Client Received TimeOut");
            Util.numberOfFileFoundCount(v.listFiles);
        } catch (SocketException se) {
            se.printStackTrace();
            v.textError.add(this.getClass().getSimpleName() + ": " + se.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            v.textError.add(this.getClass().getSimpleName() + ": " + ioe.getMessage());
        }

    }

    private void waitingResponseFromOption2and4() {

        try {
            recvSocket = new DatagramSocket(null);
            InetSocketAddress address = new InetSocketAddress(v.portUDPrecv);
            recvSocket.setReuseAddress(true);
            recvSocket.bind(address);
            recvSocket.setBroadcast(true);
            recvSocket.setSoTimeout(2000);

            v.listFiles.removeAll();
            while (true) {
                byte[] recvData = new byte[1024];
                recvPacket = new DatagramPacket(recvData, recvData.length);
                recvSocket.receive(recvPacket);

                String msg = new String(recvPacket.getData()).trim();
                System.out.println("JSON foi recebido pelo Client: " + msg);

                p = json.fromJson(msg, Packet.class);

                for (int i = 0; i < p.getListOfFiles().size(); i++) {
                    v.listFiles.add(p.getMyIP() + ":" + p.getListOfFiles().get(i));
                }
                v.textAreaClient.add("Received File List from " + p.getMyIP());
            }
        } catch (SocketTimeoutException ste) {
            v.textAreaClient.add(">>> Client Received TimeOut");
            Util.numberOfFileFoundCount(v.listFiles);
        } catch (SocketException se) {
            se.printStackTrace();
            v.textError.add(this.getClass().getSimpleName() + ": " + se.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            v.textError.add(this.getClass().getSimpleName() + ": " + ioe.getMessage());
        }

    }

    private void waitingResponseFromOption3() {
        String[] selectedFile = Util.getSelectedFile(v.listFiles);
        String ipAddress = selectedFile[0];
        String nameFile = selectedFile[1];

        ClientTCP clientTCP = new ClientTCP(nameFile, ipAddress);
        Thread threadClientTCP = new Thread(clientTCP);
        threadClientTCP.start();
    }

}
