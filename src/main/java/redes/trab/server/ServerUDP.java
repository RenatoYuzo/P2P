package redes.trab.server;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ServerUDP implements Runnable {

    private final List textArea;
    private final List textError;
    private final String path;
    private String command;
    private DatagramPacket recvPacket = null;
    DatagramSocket recvSocket;
    private String fileName;
    private String[] commandSplit;

    public ServerUDP(List textArea, List textError, String path) {
        this.textArea = textArea;
        this.textError = textError;
        this.path = path;
    }

    @Override
    public void run() {
        try {
            // Abre um socket para escutar todo conteudo destinado a porta 5555
            recvSocket = new DatagramSocket(null);
            recvSocket.setReuseAddress(true);
            recvSocket.setBroadcast(true);
            recvSocket.bind(new InetSocketAddress(5555));
            
            // Loop infinito para sempre manter o ServerUDP escutando
            while (true) {
                textArea.add(">>>   Ready to receive broadcast packets!");
                
                // O ServerUDP recebera um pacote contendo uma String,
                // essa String tem uma padrao: numero do comando,nome do arquivo
                // Exemplo: 1,doc.txt
                receivedPacket();

                // Para cada comando (1, 2, 3 ou 4), o ServerUDP fornece sua devida resposta para aquele comando
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
            textError.add(this.getClass().getSimpleName() + ": " + ex.getMessage());
        }
    }

    private ArrayList<String> getFiles() {

        try {
            File file = new File(path);
            File[] listOfFiles = file.listFiles();
            ArrayList<String> listOfNameFiles = new ArrayList();

            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    listOfNameFiles.add(listOfFile.getName());
                }
            }
            return listOfNameFiles;
        } catch (Exception e) {
            textError.add("Error: " + e.getMessage());
        }
        return null;

    }

    private void sendingRespondeFromOption1() throws IOException {
        String ip = Inet4Address.getLocalHost().getHostAddress();
        
        byte[] sendData = ip.getBytes();

        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recvPacket.getAddress().getHostAddress()), 5556);
        recvSocket.send(sendPacket);
    }

    private void sendingRespondeFromOption3() throws UnknownHostException {
        ServerTCP serverTCP = new ServerTCP(textArea, textError, path, Inet4Address.getLocalHost().getHostAddress(), fileName);
        Thread threadServerTCP = new Thread(serverTCP);
        threadServerTCP.start();
    }

    private void sendingResponseFromOption2and4() throws IOException {
        ArrayList<String> listOfNameFiles = getFiles();
        System.out.println(Inet4Address.getLocalHost().getHostAddress());
        String msg = Inet4Address.getLocalHost().getHostAddress();

        for (int i = 0; i < listOfNameFiles.size(); i++) {
            msg = msg + "," + listOfNameFiles.get(i);
        }

        byte[] sendData = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(recvPacket.getAddress().getHostAddress()), 5556);
        recvSocket.send(sendPacket);
    }

    /*  Este metodo ira receber um pacote contendo o comando que o Client pediu,
        quando recebe esse pacote, utiliza da funcao split para dividir a String,
        a primeira parte da String é a opcao pedida pelo Client e 
        a segunda parte é o nome do arquivo, se precisar fornece-lo
    */
    private void receivedPacket() throws IOException {
        byte[] recvData = new byte[1024];
        recvPacket = new DatagramPacket(recvData, recvData.length);
        recvSocket.receive(recvPacket);

        textArea.add(">>>   Discovery packet from: " + recvPacket.getAddress().getHostAddress());
        textArea.add("          PacketData: " + new String(recvPacket.getData()));
        
        command = new String(recvPacket.getData());
        commandSplit = command.split(",");
        fileName = commandSplit[1];
        fileName = fileName.trim();
    }

}
