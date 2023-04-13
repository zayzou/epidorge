package fournisseurs;

import dialogue.AlertMaker;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
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
import model.ListFour;
import util.BaseController;
import validator.ValidateTF;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class AddEditFournisseurController extends BaseController implements Initializable {

    @FXML
    private JFXTextField raisonSocial;
    @FXML
    private JFXTextField adr;
    @FXML
    private JFXTextField tel;
    @FXML
    private JFXTextField solde;
    @FXML
    private JFXButton btnAjouter;
    @FXML
    private StackPane rootStackpane;
    @FXML
    private AnchorPane rootAnchorpane;
    @FXML
    private JFXTextArea commentaire;
    @FXML
    private Label labRs;
    @FXML
    private Label labAdr;
    @FXML
    private Label labTel;
    @FXML
    private Label labSolde;
    int idFour;
    boolean isSet = false;
    private String operation = "ajouter";

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initLabel();
        if (nonNull(editFour)) {
            isSet = true;
            raisonSocial.setText(editFour.getRs());
            adr.setText(editFour.getAdr());
            tel.setText(editFour.getTel());
            solde.setText(String.valueOf(editFour.getSolde()));
            commentaire.setText(editFour.getCommentaire());
            btnAjouter.setText("Modifier");
            operation = "modifier";
             idFour = editFour.getCpt();
        }
    }

    @FXML
    private void ajouter(ActionEvent event) {
        boolean empty = ValidateTF.isEmpty(tel, labTel, "le champ tel ne peut pas être vide !")
                | ValidateTF.isEmpty(raisonSocial, labRs, "le champ Raison social ne peut pas être vide !")
                | ValidateTF.isEmpty(adr, labAdr, "le champ Adresse ne peut pas être vide !")
                | ValidateTF.isEmpty(solde, labSolde, "le champ solde ne peut pas être vide !");
        if (!empty) {
            boolean ok = AlertMaker.showDialog(operation.toUpperCase(), "Voulez vous vraiment " + operation + " " + raisonSocial.getText() + " ?");
            if (ok) {
                editFour = new ListFour(raisonSocial.getText(), adr.getText(), tel.getText(), commentaire.getText(), Double.valueOf(solde.getText()), idFour);
                try {

                    int i = isSet ? DatabaseUtil.updateFour(editFour) : DatabaseUtil.addFour(editFour);
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
        ((Stage) tel.getScene().getWindow()).close();
    }

    private void clearFields() {
        raisonSocial.clear();
        tel.clear();
        adr.clear();
        solde.clear();
        commentaire.clear();

    }

    private void initLabel() {
        labAdr.setVisible(false);
        labRs.setVisible(false);
        labTel.setVisible(false);
        labSolde.setVisible(false);

    }

}
