/**************************************************************************
 * Esta classe implementa um Cliente para conexao TCP para troca de
 * arquivos entre o Cliente e o Servidor 
 **************************************************************************/

package redes.trab.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import javax.swing.JOptionPane;
import redes.trab.util.Variables;

public class ClientTCP implements Runnable {

    private final String ipAddress;
    private final String fileName;
    private PrintWriter out1;
    private BufferedReader input1;
    private InputStreamReader in;
    private BufferedReader reader;
    private Socket socket;
    private BufferedInputStream input;
    private PrintWriter out;
    private BufferedOutputStream outputFile;
    private Variables v = new Variables();

    public ClientTCP(String fileName, String ipAddress) {
        this.ipAddress = ipAddress;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        try {
            
            System.out.println("Client IpAddress: " + ipAddress);
            System.out.println("Client port: " + v.portTCP);
            socket = new Socket();
            socket.setReuseAddress(true);
            
            // Conecta o ClientTCP no ip e porta para receber pacotes de um especifico ServerTCP
            // ClientTCP espera por 6 segundos pela conexao com o especifico ServerTCP
            socket.connect(new InetSocketAddress(ipAddress, v.portTCP), 6000);
            System.out.println("Client Conectado!");

            // Inicializacao das variaveis para utilizacao de troca de mensagens e troca de arquivos
            in = new InputStreamReader(System.in);
            reader = new BufferedReader(in);
            out1 = new PrintWriter(socket.getOutputStream(), true);
            input1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            input = new BufferedInputStream(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);

            // Recebe um Byte (0 ou 1) do ServerTCP, 0 se arquivo nao existe, 1 se arquivo existe
            int code = input.read();
            System.out.println("code: " + code);

            // Entrara em um While, recebendo partes do arquivo com 1024 bytes
            if (code == 1) {
                outputFile = new BufferedOutputStream(new FileOutputStream(v.destFolder + "\\" + fileName));
                byte[] buffer = new byte[1024];
                int bytesRead = 0;
                while ((bytesRead = input.read(buffer)) != -1) {
                    System.out.print("|"); //acts as a download indicator
                    outputFile.write(buffer, 0, bytesRead);
                    outputFile.flush();
                }
                System.out.println("");

                JOptionPane.showMessageDialog(null, "File: " + fileName.toUpperCase() + " was successfully downloaded!", "File Downloaded.", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "File is not present on the server.", "No File", JOptionPane.WARNING_MESSAGE);
            }
            closeConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
            v.textError.add(ex.getMessage());
            closeConnection();
        }
    }

    public void closeConnection() {

        try {
            if (in != null) {
                in.close();
            }
            if (reader != null) {
                reader.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (out1 != null) {
                out1.close();
            }
            if (input1 != null) {
                input1.close();
            }
            if (input != null) {
                input.close();
            }
            if (out != null) {
                out.close();
            }
            if (outputFile != null) {
                outputFile.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            v.textError.add(ex.getMessage());
        }

    }

}
