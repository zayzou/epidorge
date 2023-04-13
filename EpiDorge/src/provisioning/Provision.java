package provisioning;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import dialogue.AlertMaker;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import static java.util.Objects.nonNull;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import model.ListApprov;
import model.ListProd;
import util.BaseController;
import static util.BaseController.editProd;
import static util.FXMLPage.ADD_PROD;
import validator.ValidateTF;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class Provision extends BaseController implements Initializable {

    @FXML
    private JFXComboBox<String> cbDes;
    @FXML
    private JFXTextField tfQte;
    @FXML
    private TableView<ListApprov> tableProd;
    @FXML
    private TableColumn<?, ?> colId;
    @FXML
    private TableColumn<?, ?> colDes;
    @FXML
    private TableColumn<?, ?> colQte;
    @FXML
    private TableColumn<?, ?> colPrixUnit;
    @FXML
    private TableColumn<?, ?> colTotal;
    @FXML
    private Text total;
    @FXML
    private JFXTextField tfSearchProd;
    @FXML
    private Label labPrix;
    @FXML
    private Label labQte;
    @FXML
    private Label labVente;
    @FXML
    private JFXDatePicker tfDate;

    ObservableList<ListApprov> data;
    ObservableList<ListProd> dataProd = FXCollections.observableArrayList();
    FilteredList<ListApprov> filteredData;
    @FXML
    private JFXComboBox<String> cbType;
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane mainContainer;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initData();
        cbDes.setDisable(true);
        clearFields();
        setCells();
        loadData(LocalDate.now());
    }

    private void initData() {
        initTypeDate();
        cbType.getItems().clear();
        cbType.getItems().addAll(typeData);
        System.out.println("init data prod");
        data = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(data, n -> true);
        tfSearchProd.setOnKeyReleased((KeyEvent e) -> {
            filteredData.setPredicate((ListApprov n) -> {
                String term = tfSearchProd.getText().trim();
                if (term == null || term.isEmpty()) {
                    return true;
                }
                return n.getDes().toLowerCase().contains(term.toLowerCase());
            });
        });
        tableProd.setItems(filteredData);

    }

    private void setCells() {
        System.out.println("set cells prod");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDes.setCellValueFactory(new PropertyValueFactory<>("des"));
        colPrixUnit.setCellValueFactory(new PropertyValueFactory<>("pachat"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("qteStock"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

    }

    /**
     *
     * @param date load data to table for given date
     */
    private void loadData(LocalDate date) {
        System.out.println("load data prod");
        data.clear();
        double tot = 0.;
        try {
            statement = con.prepareStatement("SELECT * FROM entreproduit WHERE date = ?");
            statement.setDate(1, Date.valueOf(date));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                tot += resultSet.getDouble("total");
                data.add(new ListApprov(
                        resultSet.getInt("id"),
                        resultSet.getString("des_prod"),
                        resultSet.getDouble("prix_unit"),
                        resultSet.getInt("qte"),
                        resultSet.getDouble("total")
                ));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        tableProd.setItems(filteredData);
        animationFade(tableProd);
        total.setText(BaseController.customFormat(tot));
    }

    private void loadCombobox(String type) {
        try {
            dataProd.clear();
            cbDes.getItems().clear();
            statement = con.prepareStatement("SELECT * FROM produits WHERE type = ?");
            statement.setString(1, type);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dataProd.add(new ListProd(
                        resultSet.getInt("id_prod"),
                        resultSet.getString("des_prod"),
                        resultSet.getDouble("prix_achat"),
                        resultSet.getDouble("prix_vente"),
                        resultSet.getInt("qte_stock"),
                        resultSet.getString("commentaire_prod"),
                        resultSet.getString("unite"),
                        resultSet.getString("type"),
                        resultSet.getString("fournisseur")
                ));
                cbDes.getItems().addAll(resultSet.getString(2));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void ajouterProduit(ActionEvent event) {
        if (cbDes.getSelectionModel().getSelectedIndex() == -1 || ValidateTF.isEmpty(tfQte)) {
            AlertMaker.showMaterialError(rootPane, mainContainer,
                    new ArrayList<>(),
                    "Erreur", "Les champs  "
                    + "\"désignation produit\" et \" type "
                    + "\" sont obligatoire");
            return;
        }
        if (Integer.parseInt(tfQte.getText()) <= 0) {
            AlertMaker.showMaterialError(rootPane, mainContainer,
                    new ArrayList<>(),
                    "Erreur", "Le champs  quantité doit être supérieur à zéro ! ");
            return;
        }
        try {
            statement = con.prepareStatement(
                    "INSERT INTO entreproduit (des_prod , qte , prix_unit , total , date) VALUES (?,?,?,?,?)");
            double prixAchat = dataProd.get(cbDes.getSelectionModel().getSelectedIndex()).getPachat();
            statement.setString(1, cbDes.getSelectionModel().getSelectedItem());
            statement.setInt(2, Integer.valueOf(tfQte.getText()));
            statement.setDouble(3, prixAchat);
            statement.setDouble(4, prixAchat * Integer.valueOf(tfQte.getText()));
            statement.setDate(5, Date.valueOf(LocalDate.now()));
            int i = statement.executeUpdate();
            if (i > 0) {
                int qteEnStock = dataProd.get(cbDes.getSelectionModel().getSelectedIndex()).getQteStock() + Integer.valueOf(tfQte.getText());
                String req = "UPDATE produits SET qte_stock = ? WHERE des_prod = ?";
                statement = con.prepareStatement(req);
                statement.setInt(1, qteEnStock);
                statement.setString(2, cbDes.getSelectionModel().getSelectedItem());
                statement.executeUpdate();
                loadData(LocalDate.now());
                clearFields();
                initLab(null);
                System.out.println("Data added successffully ! ");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getSQLState() + " " + ex.getMessage() + " " + ex.getErrorCode());
        }

    }

    private void clearFields() {
        tfQte.clear();
        labPrix.setText("");
        labQte.setText("");
        labVente.setText("");
    }

    @FXML
    private void initLab(ActionEvent event) {
        if (cbDes.getSelectionModel().getSelectedIndex() >= 0) {
            try {
                statement = con.prepareStatement("SELECT prix_achat,prix_vente,qte_stock FROM produits WHERE des_prod = ?");
                statement.setString(1, cbDes.getSelectionModel().getSelectedItem());
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    labPrix.setText("Prix d'achat : " + BaseController.customFormat(resultSet.getDouble("prix_achat")));
                    labVente.setText("Prix de vente : " + BaseController.customFormat(resultSet.getDouble("prix_vente")));
                    labQte.setText("Qte en stock : " + resultSet.getInt("qte_stock"));

                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }

        }

    }

    @FXML
    private void loadEdit(ActionEvent event) {
        if (cbDes.getSelectionModel().getSelectedIndex() == -1) {
            AlertMaker.showDialog("Erreur", "Vueillez sélectionnez d'abord la désignation de prduit");
            return;
        }
        try {
            editProd = dataProd.get(cbDes.getSelectionModel().getSelectedIndex());
            navigate(event, ADD_PROD.getPage(), "Modifier produit");
        } catch (IOException ex) {
            Logger.getLogger(Provision.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(LocalDate.now());
        initLab(event);
        editProd = dataProd.get(cbDes.getSelectionModel().getSelectedIndex());
        loadCombobox(cbType.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void loadHisto(ActionEvent event) {
        loadData(tfDate.getValue());
    }

    @FXML
    private void loadAdd(ActionEvent event) {
        try {
            editProd = null;
            navigate(event, ADD_PROD.getPage(), "Ajouter produit");
        } catch (IOException ex) {
            Logger.getLogger(Provision.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(LocalDate.now());
        initLab(event);
    }

    @FXML
    private void loadDes(ActionEvent event) {
        loadCombobox(cbType.getSelectionModel().getSelectedItem());
        cbDes.setDisable(false);

    }

    @FXML
    private void modifier(ActionEvent event) throws IOException {
        editApprov = ListApprov.class.cast(tableProd.getSelectionModel().getSelectedItem());
        if (!nonNull(editApprov)) {
            return;
        }
        navigate(event, getClass().getResource("/updateQte/addEdit.fxml"), "Modifier");

    }

    @FXML
    private void supprimer(ActionEvent event) {

        editApprov = ListApprov.class.cast(tableProd.getSelectionModel().getSelectedItem());
        if (!nonNull(editApprov)) {
            return;
        }
        int qte = editApprov.getQteStock();
        String des = editApprov.getDes();
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editApprov.getDes() + " ?");
        if (ok) {
            try {

                String req = "DELETE FROM entreproduit WHERE id = ?";
                statement = con.prepareStatement(req);
                statement.setInt(1, editApprov.getId());

                int i = statement.executeUpdate();
                if (i > 0) {
                    AlertMaker.showInfo("Supprimer", "Données supprimé avec succés !");
                    req = "UPDATE produits SET qte_stock = qte_stock-? WHERE des_prod = ?";
                    statement = con.prepareStatement(req);
                    statement.setInt(1, qte);
                    statement.setString(2, des);
                    statement.executeUpdate();
                }
                loadData(LocalDate.now());
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }
    }

    @FXML
    private void refresh(ActionEvent event) {
        loadData(LocalDate.now());
        initLab(event);
    }
}
