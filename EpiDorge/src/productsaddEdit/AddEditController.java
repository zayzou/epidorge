package productsaddEdit;

import dialogue.AlertMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import static java.util.Objects.nonNull;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.ListFour;
import model.ListProd;
import model.ListUnit;
import util.BaseController;
import validator.ValidateTF;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class AddEditController extends BaseController implements Initializable {

    @FXML
    private JFXButton btnAjouter;
    @FXML
    private StackPane rootStackpane;
    @FXML
    private AnchorPane rootAnchorpane;
    @FXML
    private JFXTextField des;
    @FXML
    private Label labDes;
    @FXML
    private JFXTextField pAchat;
    @FXML
    private Label labAchat;
    @FXML
    private JFXTextField pVente;
    @FXML
    private JFXComboBox<String> unite;
    @FXML
    private Label labUnite;
    @FXML
    private Label labVente;
    ObservableList<ListUnit> unitData = FXCollections.observableArrayList();
    ObservableList<ListFour> fournisseurData = FXCollections.observableArrayList();
    boolean isSet = false;
    private String operation = "ajouter";
    private int id = -1;
    @FXML
    private JFXComboBox<String> type;
    @FXML
    private Label labType;
    @FXML
    private JFXTextField qte_stock;
    @FXML
    private Label labQte;
    @FXML
    private JFXComboBox<String> fournisseur;
    @FXML
    private Label labFournisseur;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initLabel();
        try {
            loadCbox();
        } catch (SQLException ex) {
            System.err.println("Erreur unités:" + ex.getMessage() + " " + ex.getSQLState());
        }
        if (nonNull(editProd)) {
            id = editProd.getId();
            isSet = true;
            des.setText(editProd.getDes());
            pAchat.setText(String.valueOf(editProd.getPachat()));
            pVente.setText(String.valueOf(editProd.getPvente()));
            unite.setValue(editProd.getUnite());
            fournisseur.setValue(editProd.getFourn());
            type.setValue(editProd.getType());
            qte_stock.setText(String.valueOf(editProd.getQteStock()));
            //commentaire.setText(editProd.getRem());
            btnAjouter.setText("Modifier");
            operation = "modifier";
        }
    }

    private void loadCbox() throws SQLException {
        unitData.clear();
        fournisseurData.clear();
        Connection con = DatabaseUtil.DBCON;
        Statement statement = con.createStatement();
        ResultSet rs = statement.executeQuery("SELECT * FROM unites");
        int cpt = 0;
        while (rs.next()) {
            unitData.add(new ListUnit(rs.getString("nom_unite"), rs.getInt("qte_unite"), rs.getInt(1)));
            unite.getItems().addAll(unitData.get(cpt++).getNom());
        }
        
         rs = statement.executeQuery("SELECT * FROM fournisseurs");
         cpt = 0;
        while (rs.next()) {
            fournisseurData.add(new ListFour(rs.getString("raison_sociale_four"), rs.getString("raison_sociale_four"), rs.getString("raison_sociale_four"), rs.getString("raison_sociale_four"), rs.getDouble("solde_four"), rs.getInt(1)));
            fournisseur.getItems().addAll(fournisseurData.get(cpt++).getRs());
        }
        
        initTypeDate();
        type.getItems().addAll(typeData);

    }

    @FXML
    private void ajouter(ActionEvent event) {
        boolean empty = ValidateTF.isEmpty(des, labDes, "le champ désignation ne peut pas être vide !")
                | ValidateTF.isEmpty(pAchat, labAchat, "le prix achat ne peut pas être vide !")
                | ValidateTF.isEmpty(pVente, labVente, "le prix de vente ne peut pas être vide !")
                | ValidateTF.isEmpty(qte_stock,labQte, "la qte en stock  ne peut pas être vide !")
                | ValidateTF.isNull(type, labType, "le champ type doit être sèlectionner !")
                
                | ValidateTF.isNull(unite, labUnite, "le champ unité doit être sèlectionner !");

        if (!empty) {
            boolean ok = AlertMaker.showDialog(operation.toUpperCase(), "Voulez vous vraiment " + operation + " " + des.getText() + " ?");
            if (ok) {
                editProd = new ListProd(
                        id, 
                        des.getText(),
                        Double.valueOf(pAchat.getText()),
                        Double.valueOf(pVente.getText()),
                        Integer.parseInt(qte_stock.getText()),
                        "",//commentaire.getText(),
                        getNomUnit(),
                        type.getSelectionModel().getSelectedItem(),
                        fournisseurData.get(fournisseur.getSelectionModel().getSelectedIndex()).getRs()
                );
                try {
                    int i = isSet ? DatabaseUtil.updateProd(editProd) : DatabaseUtil.addProd(editProd);
                    if (i > 0) {
                        AlertMaker.showMaterialDialog(rootStackpane, rootAnchorpane, new ArrayList<>(), "Succès !", "Données " + operation.replaceAll("er", "é") + " avec succès ! ");
                    }
                } catch (SQLException ex) {
                    System.out.println("Erreur : " + ex.getSQLState() + " " + ex.getMessage() + " " + ex.getLocalizedMessage());
                    AlertMaker.showMaterialDialog(rootStackpane, rootAnchorpane, new ArrayList<>(), "Erreur SQL", ex.getMessage() + " " + ex.getSQLState());
                }
                clearFields();
                initLabel();
            }
        }

    }

    @FXML
    private void annuler(ActionEvent event) {
        ((Stage) des.getScene().getWindow()).close();
    }

    private void clearFields() {
        des.clear();
        pAchat.clear();
        pVente.clear();
        //commentaire.clear();
    }

    private void initLabel() {
        labQte.setVisible(false);
        labAchat.setVisible(false);
        labDes.setVisible(false);
        labUnite.setVisible(false);
        labVente.setVisible(false);
        labType.setVisible(false);
        labFournisseur.setVisible(false);
    }

    @FXML
    private void onUnit(ActionEvent event) {
        //getNomUnit()+" "+getValUnit();
    }

    private String getNomUnit() {
        return unitData.get(unite.getSelectionModel().getSelectedIndex()).getNom();
    }

    private int getValUnit() {
        return unitData.get(unite.getSelectionModel().getSelectedIndex()).getVal();
    }

    @FXML
    private void onType(ActionEvent event) {
    }

    @FXML
    private void onFournisseur(ActionEvent event) {
    }

}
