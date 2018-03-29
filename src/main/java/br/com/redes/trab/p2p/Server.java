package br.com.redes.trab.p2p;

import java.net.Socket;
import java.awt.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

/**
 *
 * @author RenatoYuzo
 */
public class Server implements Runnable {

    private final Socket client;
    private final ServerSocket serverSocket;
    private final List textArea;
    private final List textError;
    private final List listFiles;
    private String serverMsg;
    private String clientMsg;

    public Server(ServerSocket serverSocket, Socket client, List textArea, List textError, List listFiles) {
        this.serverSocket = serverSocket;
        this.client = client;
        this.textArea = textArea;
        this.textError = textError;
        this.listFiles = listFiles;
    }

    @Override
    public void run() {
        textArea.add("New connection with Client " + client.getPort()+ " " +client.getInetAddress().getHostAddress());

        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            serverMsg = "Hello from Server!";
            out.println(serverMsg);

            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            clientMsg = input.readLine();

            if (clientMsg.equals("Files")) {
                getFiles();
            } else if (clientMsg.equals("Download File")) {
                serverMsg = "Which file do you want to download?";
                out.println(serverMsg);
                clientMsg = input.readLine();
                serverMsg = "File " + clientMsg + " has been successfully downloaded.";
                out.println(serverMsg);
            } else if (clientMsg.equals("Exit")) {
                serverMsg = "Ok, Bye!";
                out.println(serverMsg);
                input.close();
                client.close();
                textArea.add("Client " + client.getPort() + " has disconnected");
            }

            input.close();
            client.close();

            /*while (!clientMsg.equals("Exit")) {

                serverMsg = "Say something.";
                out.println(serverMsg);

                clientMsg = input.readLine();

                System.out.println("Client " + client.getPort() + " said: " + clientMsg);
            }
            serverMsg = "Ok, Bye!";
            out.println(serverMsg);*/
        } catch (IOException e) {
            textError.add("Error: " + e.getMessage());
        }
    }

    public void getFiles() {

        try {
            File directory = new File("D:\\Desktop\\Shared Files");
            File[] listOfFiles = directory.listFiles();

            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    listFiles.add(serverSocket.getInetAddress().getHostAddress() + " " + listOfFile.getName());
                }
            }
        } catch (Exception e) {
            textError.add("Error: " + e.getMessage());
        }

    }

}
