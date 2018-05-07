/**************************************************************************
 * Esta classe implementa um Servidor para conexao TCP, enviando seu
 * arquivo para o Cliente que requisitou
 **************************************************************************/

package redes.trab.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import redes.trab.util.Variables;

public class ServerTCP implements Runnable {

    private final String fileName;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedInputStream fileReader;
    private BufferedOutputStream outByte;
    private ServerSocket serverSocket;
    private Socket client;
    private Variables v = new Variables();

    public ServerTCP(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            // Abrindo um socket no ip e na porta fornecidos pelo ServerUDP
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(v.myIP, v.portTCP));
            
            // Server espera uma conexao por 5 segundos ate dar um TimeOut
            serverSocket.setSoTimeout(5000);
            System.out.println("Server IpAddress: " + v.myIP);
            System.out.println("Server port: " + v.portTCP);

            // Esperando por uma conexao com algum ClientTCP
            System.out.println("Server criado, esperando conexão.");
            client = serverSocket.accept();
            System.out.println("Server Conectado!");

            // Variaveis para troca de mensagens entre ClientTCP e ServerTCP
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            // Envia o arquivo que o Client pediu, se estiver disponivel
            sendingFileToClientTCP();
            closeConnection();

        } catch (IOException ex) {
            ex.printStackTrace();
            closeConnection();
        }
    }

    private void sendingFileToClientTCP() throws IOException {
        File file = new File(v.srcFolder + "\\" + fileName);
        System.out.println(v.srcFolder + "\\" + fileName);
        outByte = new BufferedOutputStream(client.getOutputStream());

        if (!file.exists()) {
            outByte.write((byte) 0); // Se arquivo nao existe na pasta, manda um Byte 0
        } else {
            outByte.write((byte) 1); // Se arquivo encontrado na pasta, manda um Byte 1
            fileReader = new BufferedInputStream(new FileInputStream(file));
            byte[] buffer = new byte[1024];
            int bytesRead = 0;
            while ((bytesRead = fileReader.read(buffer)) != -1) {
                outByte.write(buffer, 0, bytesRead);
                outByte.flush();
            }

        }
    }

    private void closeConnection() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (client != null) {
                v.textArea.add("Client " + client.getPort() + " has disconnected");
                client.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }
            if (outByte != null) {
                outByte.close();
            }
        } catch (IOException e) {
            v.textError.add("Error: " + e.getMessage());
        }
    }

}
