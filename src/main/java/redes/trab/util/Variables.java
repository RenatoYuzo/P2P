package redes.trab.util;

import java.awt.List;
import redes.trab.view.MainView;

public class Variables {
    public List textArea;
    public List textAreaClient;
    public List textError;
    public List listFiles;
    public String broadcastIP;
    public String destFolder;
    public String askedFile;
    public String fileName;
    public String myIP;
    public String srcFolder;
    public Integer command;
    public Integer portUDPsend;
    public Integer portUDPrecv;
    public Integer portTCP;
    
    public Variables() {
        this.textArea = MainView.textArea;
        this.textAreaClient = MainView.textAreaClient;
        this.textError = MainView.textError;
        this.listFiles = MainView.listFiles;
        this.broadcastIP = MainView.tfBroadcastIP.getText();
        this.destFolder = MainView.tfDestFolder.getText();
        this.askedFile = MainView.tfDownload.getText();
        this.myIP = MainView.tfIP.getText();
        this.srcFolder = MainView.tfSrcFolder.getText();        
        this.command = MainView.cbCommand.getSelectedIndex()+1;
        this.portUDPsend = Integer.parseInt(MainView.tfPortUDPsend.getText());
        this.portUDPrecv = Integer.parseInt(MainView.tfPortUDPrecv.getText());
        this.portTCP = Integer.parseInt(MainView.tfPortTCP.getText());
    }

    @Override
    public String toString() {
        return "Variables[broadcastIP=" + broadcastIP + " | destFolder=" + destFolder + 
                " | askedFile=" + askedFile + " | myIP=" + myIP + " | srcFolder=" + srcFolder + 
                " | command=" + command + " | portUDPsend:" + portUDPsend +
                " | portUDPrecv:" + portUDPrecv + " | portTCP:" + portTCP +']';
    }
    
    
}