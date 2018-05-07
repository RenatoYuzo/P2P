/**************************************************************************
 * Esta classe implementa um Servidor para conexao UDP, recebendo pacotes
 * enviados para a sua porta. E envia uma resposta para quem fez 
 * a requisicao
 **************************************************************************/

package redes.trab.server;

import com.google.gson.Gson;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import redes.trab.packet.Packet;
import redes.trab.util.Util;
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
            
            recvSocket = new DatagramSocket(null);
            recvSocket.setReuseAddress(true);
            recvSocket.setBroadcast(true);
            recvSocket.bind(new InetSocketAddress(v.myIP, v.portUDPsend));

            while (true) {
                v.textArea.add(">>>   Ready to receive broadcast packets!");
                
                //O ServerUDP recebera um pacote no padrao JSON
                receivedPacket();
                
                /* Ao desempacotar a mensagem e transforma-la em um objeto pacote p
                 podera ler o comando que o Cliente requisitou (1,2,3 ou 4)
                 Para cada comando solicitado pelo Cliente, o Servidor fornecera
                 sua devida resposta */
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
        p.setListOfFiles(Util.getFiles(v.srcFolder, v.textError));

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
        ArrayList<String> listOfNameFiles = Util.getFiles(v.srcFolder, v.textError, p.getFileName());

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

        v.textArea.add(">>>Discovery packet received from: " + p.getMyIP());
        v.textArea.add(">>>Packet received; data: " + msg);
    }

}
