package units;

import dialogue.AlertMaker;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import model.ListUnit;
import util.BaseController;
import static util.FXMLPage.ADD_PROD;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class UnitUIController extends BaseController implements Initializable {

    private PreparedStatement statement;
    private ResultSet resultSet;
    private final Connection con = DatabaseUtil.getDbCon();
    ObservableList<ListUnit> data;
    FilteredList<ListUnit> filteredData;
    @FXML
    private JFXTextField tfSearchUnite;
    @FXML
    private TableView<ListUnit> tableUnite;
    @FXML
    private TableColumn<?, ?> colIdUnite;
    @FXML
    private TableColumn<?, ?> colNom;
    @FXML
    private TableColumn<?, ?> colQte;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initData();
        setCells();
        loadData();
        // TODO
    }

    private void initData() {
        System.out.println("init data prod");
        data = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(data, n -> true);
        tfSearchUnite.setOnKeyReleased((KeyEvent e) -> {
            filteredData.setPredicate((ListUnit n) -> {
                String term = tfSearchUnite.getText().trim();
                if (term == null || term.isEmpty()) {
                    return true;
                }
                return n.getNom().contains(term);
            });
        });
        tableUnite.setItems(filteredData);
    }

    private void setCells() {
        System.out.println("set cells prod");
        /*column tablefour        */
        colIdUnite.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("val"));

        /* col tableprod */
    }

    private void loadData() {
        System.out.println("load data prod");
        data.clear();
        try {
            statement = con.prepareStatement("SELECT * FROM unites");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                data.add(new ListUnit(
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getInt(1)
                ));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        tableUnite.setItems(filteredData);

    }

    @FXML
    private void ajouter(ActionEvent event) {
        editUnit = null;
        try {
            navigate(event, getClass().getResource("/uniteaddEdit/addEdit.fxml"), "Ajouter unité");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void modifier(ActionEvent event) {
        editUnit = ListUnit.class.cast(tableUnite.getSelectionModel().getSelectedItem());
        if (!nonNull(editUnit)) {
            return;
        }
        try {
            navigate(event, getClass().getResource("/uniteaddEdit/addEdit.fxml"), "Modifier unité");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    @FXML
    private void supprimer(ActionEvent event) {
        editUnit = ListUnit.class.cast(tableUnite.getSelectionModel().getSelectedItem());
        if (!nonNull(editUnit)) {
            return;
        }
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editUnit.getNom() + " ?");
        if (ok) {
            try {
                int i = DatabaseUtil.execDelUnit(editUnit.getId());
                if (i > 0) {
                        AlertMaker.showInfo("Supprimer", "Données supprimé avec succés !");
                    }
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
