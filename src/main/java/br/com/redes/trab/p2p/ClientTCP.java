package br.com.redes.trab.p2p;

import java.awt.List;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author RenatoYuzo
 */
public class ClientTCP implements Runnable {

    private final List textArea;
    private final List textError;
    private final List listFiles;
    private final int port;
    private String ipAddress;
    private final String fileName;
    private final String command;
    private PrintWriter out1;
    private BufferedReader input1;
    //private final String path = "D:\\Desktop\\Received Files from Server";
    private final String path;

    public ClientTCP(int port, List textArea, List textError, List listFiles, String path, String command, String fileName) {
        this.port = port;
        this.path = path;
        this.command = command;
        this.fileName = fileName;
        this.textArea = textArea;
        this.textError = textError;
        this.listFiles = listFiles;
    }

    @Override
    public void run() {

        try {
            InputStreamReader in = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(in);

            if (command.equals("Get Available Files")) {
                ipAddress = "localhost";

                Socket socket = new Socket(ipAddress, port);

                out1 = new PrintWriter(socket.getOutputStream(), true);
                input1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out1.println(command); //First command

                String numFiles = input1.readLine(); // Pegando a quantidade de arquivos fornecido pelo Server
                //System.out.println("numFiles: "+numFiles);

                for (int i = 0; i < Integer.parseInt(numFiles); i++) {
                    listFiles.add(input1.readLine());
                }
            } else if (command.equals("Download File")) {
                
                System.out.println(listFiles.getSelectedItem());
                String[] split = listFiles.getSelectedItem().split(" ");
                
                ipAddress = split[0];
                
                Socket socket = new Socket(ipAddress, port);

                out1 = new PrintWriter(socket.getOutputStream(), true);
                input1 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                out1.println(command); //First command

                //System.out.println(input1.readLine()); // Witch file do you want to download?
                input1.readLine();
                //fileName = reader.readLine();
                out1.println(fileName); //Sending file name

                InputStream inputByte = socket.getInputStream();
                BufferedInputStream input = new BufferedInputStream(inputByte);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                //send desired filename
                int code = input.read();
                //System.out.println("code:" + code);
                if (code == 1) {
                    BufferedOutputStream outputFile = new BufferedOutputStream(new FileOutputStream(path + "\\" + fileName));
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        System.out.print("|"); //acts as a download indicator
                        outputFile.write(buffer, 0, bytesRead);
                        outputFile.flush();
                    }
                    System.out.println();
                    JOptionPane.showMessageDialog(null, "File: " + fileName + " was successfully downloaded!");
                    //System.out.println("File: " + fileName + " was successfully downloaded!");
                } else {
                    JOptionPane.showMessageDialog(null, "File is not present on the server.");
                    //System.out.println("File is not present on the server!");
                }
            }

        } catch (Exception e) {
            textError.add(e.getMessage());
        }

    }

}
