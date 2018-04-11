package br.com.redes.trab.p2p;

import java.awt.List;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author RenatoYuzo
 */
public class ServerTCP {

    private final String path;
    private final String ipAddress;
    private final int port;
    private final List textArea;
    private final List textError;
    private final List listFiles;
    private Socket client;
    private ServerSocket serverSocket;
    private String serverMsg;
    private String clientMsg;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedInputStream fileReader;
    private BufferedOutputStream outByte;

    public ServerTCP(ServerSocket serverSocket, Socket client, List textArea, List textError, List listFiles, String path, String ipAddress, int port) {
        this.serverSocket = serverSocket;
        this.client = client;
        this.textArea = textArea;
        this.textError = textError;
        this.listFiles = listFiles;
        this.path = path;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void open() {
        try {
            // Abrindo um socket no ip e na porta fornecidos pelo ServerUDP
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(ipAddress, port));

            // Esperando por uma conexao com algum ClientTCP
            client = serverSocket.accept();

            // Variaveis para troca de mensagens entre ClientTCP e ServerTCP
            out = new PrintWriter(client.getOutputStream(), true);
            //serverMsg = "Hello from Server!";
            //out.println(serverMsg);

            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            //clientMsg = in.readLine();

            File file = new File(path + "\\" + clientMsg);
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

            //serverMsg = "File " + clientMsg + " has been successfully downloaded.";
            closeConnection();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void closeConnection() {
        try {
            if (client != null) {
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
            textArea.add("Client " + client.getPort() + " has disconnected");
        } catch (IOException e) {
            textError.add("Error: " + e.getMessage());
        }
    }

}
