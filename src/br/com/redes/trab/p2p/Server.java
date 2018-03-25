package br.com.redes.trab.p2p;

import java.awt.List;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author RenatoYuzo
 */
public class Server implements Runnable {

    private ServerSocket serverSocket;
    private Socket client;
    private int port;
    private String ipAddress;
    private List textArea;
    private List textError;
    private Server newServer;

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public Server(int port, String ipAddress, List textArea, List textError) throws IOException {
        this.port = port;
        this.ipAddress = ipAddress;
        this.textArea = textArea;
        this.textError = textError;

        serverSocket = new ServerSocket();
        serverSocket.bind(new InetSocketAddress(ipAddress, port));

        textArea.add("Server port: " + serverSocket.getLocalPort());
        textArea.add("Server HostAddress = " + serverSocket.getInetAddress().getHostAddress());
        textArea.add("Server HostName = " + serverSocket.getInetAddress().getHostName());

        textArea.add("Server waiting for Client . . .");
        client = serverSocket.accept();
        textArea.add("Server connected with Client " + client.getPort());
        textArea.add("Client HostAddress = " + client.getInetAddress().getHostAddress());
        textArea.add("Client HostName = " + client.getInetAddress().getHostName());

        Server newServer = new Server(port, ipAddress, textArea, textError);
        Thread thread = new Thread(newServer);
        thread.start();
        //client.close();
        //serverSocket.close();
    }

    @Override
    public void run() {
        while (true) {
            textArea.add("Server waiting for Client . . .");
            try {
                client = serverSocket.accept();
            } catch (IOException ex) {
                textError.add("Erro: "+ex.getMessage());
            }
            textArea.add("Server connected with Client " + client.getPort());
            textArea.add("Client HostAddress = " + client.getInetAddress().getHostAddress());
            textArea.add("Client HostName = " + client.getInetAddress().getHostName());
            
            
            try {
                newServer = new Server(port, ipAddress, textArea, textError);
            } catch (IOException ex) {
                textError.add("Erro: "+ex.getMessage());
            }
            Thread thread = new Thread(newServer);
            thread.start();
        }
    }

}
