package updateQteLigneVente;

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
    private JFXTextField tfQte;
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
    }

    @FXML
    private void ajouter(ActionEvent event) {
        try {
            boolean empty = ValidateTF.isEmpty(tfQte, labQte, "le champ montant ne peut pas être vide !");
            if (!empty) {
                boolean ok = AlertMaker.showDialog("Modifier".toUpperCase(), "Voulez vous vraiment modifier " + editVente.getDes() + " ?");
                if (ok) {
                    String des = editVente.getDes();
                    int qteSortie = getQteSortie(des);
                    int qteActuel = Integer.parseInt(tfQte.getText());
                    int qteAvant = editVente.getQteStock();
                    double newTotal = editVente.getPvente() * qteActuel;
                    String req = "UPDATE lignesortie SET qte = ? , total = ? WHERE id = ?";
                    System.out.println(qteActuel + "   "+qteSortie);
                    if (qteActuel < qteSortie) {
                        AlertMaker.showMaterialError(rootStackpane, rootAnchorpane,
                                new ArrayList<>(),
                                "Erreur", "La valeur saisie (" + qteActuel + ") est inferieur à la quantité déja vendue ( " + qteSortie + " )"
                        );

                        return;
                    }
                    statement = con.prepareStatement(req);
                    statement.setInt(1, qteActuel);
                    statement.setDouble(2, newTotal);
                    statement.setInt(3, editVente.getId());
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
        ((Stage) tfQte.getScene().getWindow()).close();

    }

    private void clearFields() {
        tfQte.clear();

    }

    private void initLabel() {
        labQte.setVisible(false);

    }

   /**
    * 
    * @param des Designation product 
    * @return quantity deja vendue
    */
    private int getQteSortie(String des) {
        int qte = -1;
        try {

            statement = con.prepareStatement("SELECT qte FROM sortieproduit WHERE des_prod = ?"
                    + "AND date = CURRENT_DATE AND sortieproduit.employe = (SELECT employes.id_employe"
                    + " FROM employes WHERE employes.nom_employe = ?)");
            statement.setString(1, des);
            statement.setString(2, editVente.getVendeur());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                qte = resultSet.getInt("qte");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());

        }
        return qte;
    }

}
