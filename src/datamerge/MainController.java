/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamerge;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javax.swing.JFileChooser;
/**
 *
 * @author Ryan Vickramasinghe
 */
public class MainController implements Initializable {
    
    @FXML
    private Button btn_findDirectory;
    @FXML
    private TextField txt_directory;
    
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
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
