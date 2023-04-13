package soldefour;

import dialogue.AlertMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
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

    private final String operation = "ajouter";
    @FXML
    private JFXTextField montant;
    @FXML
    private Label labMontant;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initLabel();
    }

    @FXML
    private void ajouter(ActionEvent event) {
        boolean empty = ValidateTF.isEmpty(montant, labMontant, "le champ montant ne peut pas être vide !");
        boolean sup = editFour.getCpt() != -1 && ValidateTF.isSup(montant, editFour.getSolde(), labMontant, "Le champ montant doit être superieur à "+BaseController.customFormat(editFour.getSolde()));
        
        if (!empty  && !sup) {
            boolean ok = AlertMaker.showDialog(operation.toUpperCase(), "Voulez vous vraiment " + operation + " " + montant.getText() + " ?");
            
            if (ok) {
                try {
                    if (editFour.getCpt() == -1) {
                        editFour.setSolde(editFour.getSolde() + Double.valueOf(montant.getText()));
                    } else {
                        
                        editFour.setSolde(editFour.getSolde() - Double.valueOf(montant.getText()));
                    }
                    int i = DatabaseUtil.addVerssement(editFour);
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
        ((Stage) montant.getScene().getWindow()).close();
        
        
    }

    private void clearFields() {
        montant.clear();

    }

    private void initLabel() {
        labMontant.setVisible(false);

    }

}
