package datamerge;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.StringTokenizer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * FXML Controller class
 *
 * @author ryanv
 */
public class ColumnSelectController implements Initializable {

    ObservableList filesList = FXCollections.observableArrayList(); //stores list of files
    ObservableList columns = FXCollections.observableArrayList(); //stores columns
    String scanDirectory; //stores the directory files will be merged from
    
    @FXML
    private Label lbl_status;
    
    @FXML
    private TextField txt_colName;
    
    @FXML
    private TableView<String> tableView_col;
    
    @FXML
    private ChoiceBox choiceBox_columns;
    
    @FXML
    private void handle_btn_addCol(ActionEvent event){
        if(txt_colName.getText().length() == 0){
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "Enter a valid column name!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{
            addColumn(txt_colName.getText());
            txt_colName.clear();
        }
    }
    
    /**
     * this function will rearrange data in the ObservableList, as well as choiceBox according to user changes
     */
    @FXML
    private void handle_tableView_col_rearrange(){
        int numColumns = columns.size();
        columns.clear();
        for(int i = 0; i < numColumns; i++){
            System.out.println(i + ": " + tableView_col.getColumns().get(i).getText());
            columns.add(tableView_col.getColumns().get(i).getText());
            choiceBox_columns.getItems().clear();
            choiceBox_columns.getItems().addAll(columns);
        }
    }
    
    @FXML
    private void handle_btn_deleteCol(ActionEvent event){
        int selectedIndex = choiceBox_columns.getSelectionModel().getSelectedIndex();
        columns.remove(selectedIndex);
        choiceBox_columns.getItems().remove(selectedIndex);
        tableView_col.getColumns().remove(selectedIndex);
    }
    
    /**
     * this function pre-loads the ObservableList of files so that they can be used here.
     * @param data the ObservableList containing files to be merged
     * @param directory this is the directory to scan files from
     */
    public void setModel(ObservableList data, String directory){
        columns.clear();
        filesList = data;
        scanDirectory = directory;
        lbl_status.setText("Currently merging " + data.size() + 
                " files from location: " + scanDirectory);
        
        scanColumns(); //automatically scan for columns from first file
    }
    
    /**
     * this function will automatically scan columns from the first file and add them
     */
    private void scanColumns(){
        Scanner scanFile;
        
        //try adding first file to scanner
        try{ 
            scanFile = new Scanner(new File(scanDirectory + "\\" + 
                                filesList.get(0).toString()));
            
            //get all columns to string
            String firstRow = scanFile.nextLine();
            System.out.println("first line is: " + firstRow);
            
            
            //place string into a tokenizer to separate words(Def. delim. is '|')
            StringTokenizer token = new StringTokenizer(firstRow,"|",false);
            
            while(token.hasMoreTokens()){
                String nextCol = token.nextToken();
                System.out.println(nextCol);
                addColumn(nextCol);
            }
            
        }catch(FileNotFoundException ex){ //if unable to scan file, display error message
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "Could not auto-fill columns. You must manually enter columns.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * this function will add a column to the table
     * @param colName this should be the string value of the column.
     */
    private void addColumn(String colName){
        TableColumn newCol = new TableColumn(colName);
        columns.add(colName); //add new column to ObservableList
        
        choiceBox_columns.getItems().clear();
        choiceBox_columns.getItems().addAll(columns);
        tableView_col.getColumns().add(newCol); //add column to tableview
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
