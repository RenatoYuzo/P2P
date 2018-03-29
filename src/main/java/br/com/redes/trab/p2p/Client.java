package br.com.redes.trab.p2p;

import java.awt.List;
import java.io.File;

/**
 *
 * @author RenatoYuzo
 */
public class Client implements Runnable{
    
    private final List textArea;
    private final List textError;
    private final List listFiles;
    
    public Client(List textArea, List textError, List listFiles) {
        this.textArea = textArea;
        this.textError = textError;
        this.listFiles = listFiles;
    }

    @Override
    public void run() {
        getFiles();
    }       
    
    public void getFiles() {
        
        try {
            File directory = new File("D:\\Desktop\\Shared Files");
            File[] listOfFiles = directory.listFiles();
            
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    listFiles.add(listOfFile.getName());
                }
            }
        } catch (Exception e) {
            textError.add("Error: " + e.getMessage());
        }
        
    }
}
