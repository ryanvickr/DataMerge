/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamerge;

import java.io.File;
import java.nio.file.Files;
import java.net.URL;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
/**
 *
 * @author Ryan Vickramasinghe
 */
public class MainController implements Initializable {
    
    @FXML
    private Button btn_findDirectory;
    
    @FXML
    private Button btn_scan;
    
    @FXML
    private TextField txt_directory;
    
    @FXML 
    private Label lbl_status;
    
    @FXML
    private ListView<String> listView_fileDisplay;
    
    final ObservableList data = FXCollections.observableArrayList();
    
    
    /**
     * this function will retrieve the user selected directory and display it
     * @param event 
     */
    @FXML
    private void handle_btn_findDirectory(ActionEvent event) {
        //Open directory selector
        JFileChooser directorySelect = new JFileChooser(); 
        directorySelect.setCurrentDirectory(new java.io.File("."));
        directorySelect.setDialogTitle("Select a directory to merge files from");
        directorySelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); //select directories only
        directorySelect.setAcceptAllFileFilterUsed(false); //remove all files option
        
        //get the response from the finder
        if (directorySelect.showOpenDialog(directorySelect) == JFileChooser.APPROVE_OPTION) { 
          System.out.println("getCurrentDirectory(): " 
             +  directorySelect.getCurrentDirectory());
          System.out.println("getSelectedFile() : " 
             +  directorySelect.getSelectedFile());
          txt_directory.setText(directorySelect.getSelectedFile().toString());
        }
        else {
          System.out.println("No Selection ");
        }
        
    }
    
    /**
     * This function will scan the directory for files to merge
     * @param event 
     */
    @FXML
    private void handle_btn_scan(ActionEvent event){
        Path directoryPath = Paths.get(txt_directory.getText());
        
        //check to ensure directory is valid
        if(!Files.exists(directoryPath, LinkOption.NOFOLLOW_LINKS) || directoryPath.toString().isEmpty()){
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "Could not find directory!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{ //if not valid, show error message:
            scanFiles(directoryPath);
        }
    }
    
    /**
     * this function will read files from a directory
     * @param path the directory to scan files from
     */
    private void scanFiles(Path path){
        lbl_status.setText("Scanning...");
        txt_directory.disableProperty().set(true);
        btn_findDirectory.disableProperty().set(true);
        
        File directory = new File(path.toString());
        File[] fileList = directory.listFiles(); //creates an array of File objects
        
        //display the files found in list
        try{
            data.removeAll(data); //clear the list
            for(int i = 0; i < fileList.length; i++){
                System.out.println("File[" + i + "]: " + fileList[i].getName());
                data.add(fileList[i].getName());
            }
            listView_fileDisplay.getItems().addAll(data); //display files on screen
        } catch (Exception ex){
            lbl_status.setText("Could not scan files!");
            System.out.println("Error scanning files: " + ex.toString());
        }
        
        lbl_status.setText("Completed scan. Found " + fileList.length + " files:");
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lbl_status.setText("Select a directory to scan.");
    }    
    
}
