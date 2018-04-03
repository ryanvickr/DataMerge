/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datamerge;

import java.io.File;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Ryan Vickramasinghe
 */
public class MainController implements Initializable {
    
    //Stores file list
    final ObservableList data = FXCollections.observableArrayList();
    
    //begin FXML declarations************************
    @FXML
    private Button btn_findDirectory;
    
    @FXML
    private Button btn_scan;
    
    @FXML
    private Button btn_remove;
    
    @FXML
    private Button btn_next;
    
    @FXML
    private TextField txt_directory;
    
    @FXML 
    private Label lbl_status;
    
    @FXML
    private ListView<String> listView_fileDisplay;
    
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
        else{ //if valid, call scan function (end enable/disable certain buttons):
            //enable/disable UI features
            lbl_status.setText("Scanning...");
            txt_directory.disableProperty().set(true);
            btn_findDirectory.disableProperty().set(true);
            btn_remove.disableProperty().set(false);
            btn_next.disableProperty().set(false);
            
            //call scan function
            scanFiles(directoryPath);
        }
    }
    
    /**
     * this function will remove a selected file from the scan list
     * @param event 
     */
    @FXML
    private void handle_btn_remove(ActionEvent event){
        try{
            int selectedIndex = listView_fileDisplay.getSelectionModel().getSelectedIndex(); //gets selected index
            data.remove(selectedIndex);
            listView_fileDisplay.getItems().remove(selectedIndex);
        }catch(Exception ex){
            //show error message if no selection
            System.out.println("Could not remove file: " + ex.toString());
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "Could not remove item!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * this function will reset the program to its initial state
     * @param event 
     */
    @FXML
    private void handle_btn_reset(ActionEvent event){
        //reset buttons/textfield/labels
        lbl_status.setText("Select a directory to scan.");
        txt_directory.clear();
        txt_directory.disableProperty().set(false);
        btn_findDirectory.disableProperty().set(false);
        btn_remove.disableProperty().set(true);
        btn_next.disableProperty().set(true);
        
        //reset the file listing
        data.clear();
        listView_fileDisplay.getItems().clear();
    }
    
    /**
     * this function will display a new window with the next steps
     * @param event 
     */
    @FXML
    private void handle_btn_next(ActionEvent event)throws IOException{
        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ColumnSelect.fxml"));
        Parent root = (Parent) fxmlLoader.load();
        ColumnSelectController columnselectcontroller = (ColumnSelectController) fxmlLoader.getController();
        columnselectcontroller.setModel(data, txt_directory.getText());
        
        Scene scene = new Scene(root);
        Stage stage = new Stage();

        stage.setScene(scene);
        //stage.getIcons().add(new Image("file:src/iPAYROLL/WesternLogo.png"));
        stage.setTitle("DataMerge - Step 2");
        stage.initModality(Modality.APPLICATION_MODAL);

        stage.show();	
    }
    //end FXML declarations**************************
    
    //begin private member functions*****************
    
    /**
     * this function will read files from a directory
     * @param path the directory to scan files from
     */
    private void scanFiles(Path path){     
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
