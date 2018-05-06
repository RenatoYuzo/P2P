package redes.trab.util;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public abstract class Util {

    public static void chooseDirectory(String msg, JTextField tf) {

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new java.io.File("."));
        fileChooser.setDialogTitle(msg);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            tf.setText(fileChooser.getSelectedFile().getPath());
        }

    }

    /*public static String getSelectedFile(java.awt.List listFiles) {
        String[] separated;

        for (int i = 0; i < listFiles.getItemCount(); i++) {
            if (listFiles.isIndexSelected(i)) {
                separated = listFiles.getItem(i).split(":");
                return separated[1];
            }
        }
        return null;
    }*/
    public static ArrayList<String> getFiles(String srcFolder, java.awt.List textError) {

        try {
            File file = new File(srcFolder);
            File[] listOfFiles = file.listFiles();
            ArrayList<String> listOfNameFiles = new ArrayList();

            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    listOfNameFiles.add(listOfFile.getName());
                }
            }
            return listOfNameFiles;
        } catch (Exception e) {
            e.printStackTrace();
            textError.add("Error: " + e.getMessage());
        }
        return null;

    }

    public static ArrayList<String> getFiles(String srcFolder, java.awt.List textError, String fileName) {

        try {
            File file = new File(srcFolder);
            File[] listOfFiles = file.listFiles();
            ArrayList<String> listOfNameFiles = new ArrayList();

            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile()) {
                    if (listOfFile.getName().equals(fileName)) {
                        listOfNameFiles.add(listOfFile.getName());
                    }
                }
            }
            return listOfNameFiles;
        } catch (Exception e) {
            e.printStackTrace();
            textError.add("Error: " + e.getMessage());
        }
        return null;

    }

    public static String[] getSelectedFile(java.awt.List listFiles) {
        String[] separated;

        if (listFiles.getItemCount() == 0) {
            return null;
        }

        for (int i = 0; i < listFiles.getItemCount(); i++) {
            if (listFiles.isIndexSelected(i)) {
                separated = listFiles.getItem(i).split(":");
                return separated;
            }
        }
        return null;
    }

    public static boolean checkIfIHaveFile(String fileName, String destFolder) {
        
        boolean flag = false;
        
        File file = new File(destFolder);
        File[] listOfFiles = file.listFiles();
        
        for (File f : listOfFiles) {
            if (f.isFile()) {
                if (f.getName().equals(fileName)) {
                    flag = true;
                }
            }
        }
        
        if(flag == true) {
            JOptionPane.showMessageDialog(null, "You already have this file on your Dest folder.", "You already have this file", JOptionPane.INFORMATION_MESSAGE);
            return flag;
        }
        else {
            return flag;
        }
        
    }

}
