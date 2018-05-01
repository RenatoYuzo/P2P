package redes.trab.packet;

import java.util.ArrayList;

public class Packet {

    private String myIP;
    private Integer command;
    private String fileName;
    private ArrayList<String> listOfFiles;

    public Packet(String myIP, Integer command) {
        this.myIP = myIP;
        this.command = command;
    }

    public String getMyIP() {        
        return myIP;
    }

    public void setMyIP(String myIP) {
        this.myIP = myIP;
    }

    public Integer getCommand() {
        return command;
    }

    public void setCommand(Integer command) {
        this.command = command;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<String> getListOfFiles() {
        return listOfFiles;
    }

    public void setListOfFiles(ArrayList<String> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

    @Override
    public String toString() {
        return "Packet [myIP=" + myIP + ", command=" + command + 
                ", fileName=" + fileName + ", listOfFiles="
                + listOfFiles + "]";
    }
}
