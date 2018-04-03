package datamerge;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
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

    ObservableList data = FXCollections.observableArrayList(); //stores list of files
    ObservableList columns = FXCollections.observableArrayList();
    String scanDirectory; //stores the directory files will be merged from
    
    @FXML
    private Label lbl_status;
    
    @FXML
    private Button btn_addCol;
    
    @FXML
    private TextField txt_colName;
    
    @FXML
    private TableView<String> tableView_col;
    
    @FXML
    private void handle_btn_addCol(ActionEvent event){
        if(txt_colName.getText().length() == 0){
            final JPanel panel = new JPanel();
            JOptionPane.showMessageDialog(panel, "Enter a valid column name!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        else{
            TableColumn newCol = new TableColumn(txt_colName.getText());
            tableView_col.getColumns().add(newCol);
        }
    }
    
    /**
     * this function pre-loads the ObservableList of files so that they can be used here.
     * @param data the ObservableList containing files to be merged
     */
    public void setModel(ObservableList data, String directory){
        this.data = data;
        scanDirectory = directory;
        lbl_status.setText("Currently merging " + data.size() + " files from location: " + scanDirectory);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
    
}
