package br.com.redes.trab.p2p;

import java.net.Socket;
import java.awt.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 *
 * @author RenatoYuzo
 */
public class Server implements Runnable {

    private final Socket client;
    private final List textArea;
    private final List textError;
    private String serverMsg;
    private String clientMsg;

    public Server(Socket client, List textArea, List textError) {
        this.client = client;
        this.textArea = textArea;
        this.textError = textError;
    }

    @Override
    public void run() {
        textArea.add("New connection with Client " + client.getInetAddress().getHostAddress());

        try {
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            serverMsg = "Hello from Server!";
            out.println(serverMsg);

            BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            clientMsg = input.readLine();

            while (!clientMsg.equals("Exit")) {

                serverMsg = "Say something.";
                out.println(serverMsg);

                clientMsg = input.readLine();

                System.out.println("Client " + client.getPort() + " said: " + clientMsg);
            }
            serverMsg = "Ok, Bye!";
            out.println(serverMsg);

            input.close();
            client.close();
            textArea.add("Client " + client.getPort() + " has disconnected");
        } catch (IOException e) {
            textError.add("Error: " + e.getMessage());
        }
    }

}
