package updateQte;

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
public class UpdateQteController extends BaseController implements Initializable {

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
                boolean ok = AlertMaker.showDialog("Modifier".toUpperCase(), "Voulez vous vraiment modifier " + editApprov.getDes() + " ?");
                if (ok) {
                    int qteAvant = editApprov.getQteStock();
                    String des = editApprov.getDes();
                    double newTotal = editApprov.getPachat() * Integer.parseInt(montant.getText());
                    String req = "UPDATE entreproduit SET qte = ?, total = ? WHERE id = ?";
                    statement = con.prepareStatement(req);
                    statement.setInt(1, Integer.parseInt(montant.getText()));
                    statement.setDouble(2, newTotal);
                    statement.setInt(3, editApprov.getId());
                    int i = statement.executeUpdate();
                    if (i > 0) {
                        AlertMaker.showMaterialDialog(rootStackpane, rootAnchorpane, new ArrayList<>(), "Succès !", "Données " + operation.replaceAll("er", "é") + " avec succès ! ");
                        req = "UPDATE produits SET qte_stock = (qte_stock-? )+ ? WHERE des_prod = ?";
                        statement = con.prepareStatement(req);
                        statement.setInt(1, qteAvant);
                        statement.setInt(2, Integer.parseInt(montant.getText()));
                        statement.setString(3, des);
                        statement.executeUpdate();
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
