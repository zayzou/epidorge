package uniteaddEdit;

import dialogue.AlertMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import static java.util.Objects.nonNull;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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

    boolean isSet = false;
    private String operation = "ajouter";
    private int id = -1;
    @FXML
    private JFXTextField nom;
    @FXML
    private Label labNom;
    @FXML
    private JFXTextField qteUnite;
    @FXML
    private Label labQte;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initLabel();

        if (nonNull(editUnit)) {
            id = editUnit.getId();
            isSet = true;
            nom.setText(editUnit.getNom());
            qteUnite.setText(String.valueOf(editUnit.getVal()));
            btnAjouter.setText("Modifier");
            operation = "modifier";
        }
    }

    @FXML
    private void ajouter(ActionEvent event) {
        boolean empty = ValidateTF.isEmpty(nom, labNom, "le champ désignation ne peut pas être vide !")
                | ValidateTF.isEmpty(qteUnite, labQte, "le prix de vente ne peut pas être vide !");
        if (!empty) {
            boolean ok = AlertMaker.showDialog(operation.toUpperCase(), "Voulez vous vraiment " + operation + " " + nom.getText() + " ?");
            if (ok) {

                editUnit = new ListUnit(nom.getText(), Integer.parseInt(qteUnite.getText()), id);
                try {
                    int i = isSet ? DatabaseUtil.updateUnite(editUnit) : DatabaseUtil.addUnite(editUnit);
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
        ((Stage) nom.getScene().getWindow()).close();
    }

    private void clearFields() {
        nom.clear();
        qteUnite.clear();

    }

    private void initLabel() {
        labQte.setVisible(false);
        labNom.setVisible(false);

    }

}
