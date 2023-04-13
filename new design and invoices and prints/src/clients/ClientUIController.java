package clients;

import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
import dialogue.AlertMaker;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.util.Objects.nonNull;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.ListFour;
import util.BaseController;
import static util.BaseController.editFour;
import static util.FXMLPage.ADD;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class ClientUIController extends BaseController implements Initializable {

    @FXML
    private JFXTextField tfSearch;
    @FXML
    private Button bntRefresh;
    @FXML
    private TableView<ListFour> tableClient;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colRs;
    @FXML
    private TableColumn<?, ?> colAdr;
    @FXML
    private TableColumn<?, ?> colTel;
    @FXML
    private TableColumn<?, ?> colSolde;
    @FXML
    private TableColumn<?, ?> colCommentaire;

    private PreparedStatement statement;
    private ResultSet resultSet;
    private final Connection con = DatabaseUtil.getDbCon();
    ObservableList<ListFour> data;
    FilteredList<ListFour> filteredData;
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane mainContainer;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isClient = true;
        initData();
        setCells();
        loadData();
    }

    private void initData() {
        System.out.println("init data ");
        data = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(data, n -> true);
        tfSearch.setOnKeyReleased((KeyEvent e) -> {
            filteredData.setPredicate((ListFour n) -> {
                String term = tfSearch.getText().trim();
                if (term == null || term.isEmpty()) {
                    return true;
                }
                return n.getRs().toLowerCase().contains(term.toLowerCase()) ||
                        n.getTel().contains(term) || 
                        n.getAdr().toLowerCase().contains(term.toLowerCase());
            });
        });
        tableClient.setItems(filteredData);
    }

    private void setCells() {
        System.out.println("set cells");
        /*column tablefour        */
        colID.setCellValueFactory(new PropertyValueFactory<>("cpt"));
        colRs.setCellValueFactory(new PropertyValueFactory<>("rs"));
        colAdr.setCellValueFactory(new PropertyValueFactory<>("adr"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("tel"));
        colSolde.setCellValueFactory(new PropertyValueFactory<>("solde"));
        colCommentaire.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        /* col tableprod */

    }

    private void loadData() {
        System.out.println("load data");
        data.clear();
        try {
            statement = con.prepareStatement("SELECT * FROM clients");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                data.add(new ListFour(
                        resultSet.getString("raison_sociale_client"),
                        resultSet.getString("adr_client"),
                        resultSet.getString("tel_client"),
                        resultSet.getString("commentaire_client"),
                        resultSet.getDouble("sold_client"),
                       resultSet.getInt("id_client")
                ));
            }
        } catch (SQLException ex) {
        }
        tableClient.setItems(filteredData);
    }

    @FXML
    private void ajouter(ActionEvent event) {
        editFour = null;
        try {
            navigate(event,getClass().getResource("/clientsAddEdit/addEditClient.fxml"), "Ajouter Client");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void modifier(ActionEvent event) {
        editFour = ListFour.class.cast(tableClient.getSelectionModel().getSelectedItem());
        if (!nonNull(editFour)) {
            return;
        }
        try {
            navigate(event, getClass().getResource("/clientsAddEdit/addEditClient.fxml"), "Modifier Client");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    @FXML
    private void supprimer(ActionEvent event) {
        editFour = ListFour.class.cast(tableClient.getSelectionModel().getSelectedItem());
        if (!nonNull(editFour)) {
            return;
        }
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editFour.getRs() + " ?");
        if (ok) {
            try {
                DatabaseUtil.execDelClient(editFour.getRs());
                loadData();
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }

    }

    @FXML
    private void refresh(ActionEvent event) {
        loadData();
    }

}
