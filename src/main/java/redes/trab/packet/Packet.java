package redes.trab.packet;

import java.util.ArrayList;

public class Packet {

    private String ipAddress;
    private Integer command;
    private String fileName;
    private ArrayList<String> listOfFiles;

    public Packet(String ip, Integer c) {
        this.ipAddress = ip;
        this.command = c;
    }

    public String getIpAddress() {        
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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
        return "Packet [ipAddress=" + ipAddress + ", command=" + command + ", fileName=" + fileName + ", listOfFiles="
                + listOfFiles + "]";
    }
}
