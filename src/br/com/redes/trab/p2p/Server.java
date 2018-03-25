package br.com.redes.trab.p2p;

import java.net.Socket;
import java.awt.List;

/**
 *
 * @author RenatoYuzo
 */
public class Server implements Runnable {

    private final Socket client;
    private final List textArea;
    private final List textError;

    public Server(Socket client, List textArea, List textError) { 
        this.client = client;
        this.textArea = textArea;
        this.textError = textError;
    }

    @Override
    public void run() {
        textArea.add("New connection with Client " + this.client.getInetAddress().getHostAddress());
        
        try {
            
        } catch (Exception e) {
            textError.add("Error: "+e.getMessage());
        }
    }

}
