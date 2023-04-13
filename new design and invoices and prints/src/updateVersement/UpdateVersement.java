package updateVersement;

import dialogue.AlertMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
public class UpdateVersement extends BaseController implements Initializable {

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
        try {
            boolean empty = ValidateTF.isEmpty(montant, labMontant, "le champ montant ne peut pas être vide !");
            if (!empty) {
                boolean ok = AlertMaker.showDialog("Modifier".toUpperCase(), "Voulez vous vraiment modifier " + editPaiement.getSolde() + " ?");
                if (ok) {
                    String req = "UPDATE salaires SET solde = ? WHERE id = ?";
                    statement = con.prepareStatement(req);
                    statement.setInt(1, Integer.parseInt(montant.getText()));
                    statement.setInt(2, editPaiement.getId());
                    int i = statement.executeUpdate();
                    if (i > 0) {
                        AlertMaker.showMaterialDialog(rootStackpane, rootAnchorpane, new ArrayList<>(), "Succès !", "Données " + operation.replaceAll("er", "é") + " avec succès ! ");
                    }
                }

            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
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
