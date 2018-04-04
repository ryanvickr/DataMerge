package datamerge;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.StringTokenizer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
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

    private ObservableList filesList = FXCollections.observableArrayList(); //stores list of files
    private final ObservableList columns = FXCollections.observableArrayList(); //stores columns
    private String scanDirectory; //stores the directory files will be merged from
    
    @FXML
    private Label lbl_status;
    
    @FXML
    private Label lbl_progress;
    
    @FXML
    private TextField txt_colName;
    
    @FXML
    private Button btn_start;
    
    @FXML
    private Button btn_addCol;
    
    @FXML
    private Button btn_deleteCol;
    
    @FXML
    private TableView<String> tableView_data;
    
    @FXML
    private ChoiceBox<String> choiceBox_columns;
    
    @FXML
    private ProgressBar progressBar_mergeProgress;
    
    @FXML
    private void handle_btn_addCol(ActionEvent event){
        if(txt_colName.getText().length() == 0){
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "Enter a valid column name!", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
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
    private void handle_tableView_data_rearrange(){
        int numColumns = columns.size();
        columns.clear();
        for(int i = 0; i < numColumns; i++){
            System.out.println(i + ": " + tableView_data.getColumns().get(i).getText());
            columns.add(tableView_data.getColumns().get(i).getText());
            choiceBox_columns.getItems().clear();
            choiceBox_columns.getItems().addAll(columns);
        }
    }
    
    @FXML
    private void handle_btn_deleteCol(ActionEvent event){
        int selectedIndex = choiceBox_columns.getSelectionModel().getSelectedIndex();
        lbl_progress.setText("Deleted column \"" + columns.get(selectedIndex).toString() + "\"");
        columns.remove(selectedIndex);
        choiceBox_columns.getItems().remove(selectedIndex);
        tableView_data.getColumns().remove(selectedIndex);
    }
    
    @FXML
    private void handle_btn_start(ActionEvent event){
        //Verify that user is ready to merge(can't change col's after begins)
            //TODO
        
        //lock up the UI (to prevent changes being made during merging)
        txt_colName.disableProperty().set(true);
        btn_addCol.disableProperty().set(true);
        choiceBox_columns.disableProperty().set(true);
        btn_deleteCol.disableProperty().set(true);
        
        //call merge function
        lbl_progress.setText("Beginning merge...");
        merge();
        lbl_progress.setText("Merging complete.");
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
        
        tableView_data.getColumns().add(newCol); //add column to tableview
        lbl_progress.setText("Added column \"" + colName + "\"");
    }
    
    /**
     * this function handles the actual merging of files
     */
    private void merge(){
        int[] columnOrder = new int[columns.size()]; //stores the order of columns
        
        //First check the columns of each file
        for(int i = 0; i < filesList.size(); i++){
            //update progress
            System.out.println("merging file " + i);
            lbl_progress.setText("Merging file " + (i+1));
            progressBar_mergeProgress.setProgress((i+1)/filesList.size());
            
            //get the order of columns from function
            columnOrder = checkColumns(i);
            for(int j = 0; j < columns.size(); j++)
                System.out.println("Col[" + j + "]: " + columnOrder[j]);
            
            //add rows to table
            tableView_data.setItems(columns);
        }
    }
    
    private int[] checkColumns(int fileToCheck){
        int[] returnVal = new int[columns.size()];
        returnVal[0] = -1; //sets error flag (e.g. if file did not process properly)
        
        try{
            Scanner currentFile = new Scanner(new File(scanDirectory + "\\" + 
                                filesList.get(fileToCheck).toString()));
            
            //store first line of file (column headers)
            String columnHeaders = currentFile.nextLine();
            StringTokenizer token = new StringTokenizer(columnHeaders,"|",false);
            
            //check for missing columns********TODO
            
            //find the order of columns
            int index = 0;
            while(token.hasMoreTokens()){
                String currentColumn = token.nextToken();
                
                int i = 0;
                while(!currentColumn.equals(columns.get(i).toString()) && i < columns.size()){
                    i++;
                }
                if(i >= columns.size()){
                    System.out.println("Could not match column!");
                    break;
                }
                else
                    returnVal[index] = i;
                
                index++;
            }
        }catch(FileNotFoundException ex){
            System.out.println("Could not find file!");
        }
        
        return returnVal;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
