package personels.ajouter;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
import dialogue.AlertMaker;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import static java.util.Objects.nonNull;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import model.ListEmploye;
import util.BaseController;
import static util.BaseController.editEmploye;
import validator.ValidateTF;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class AjouterUIController extends BaseController implements Initializable {

    @FXML
    private JFXTextField nom;
    @FXML
    private JFXTextField tel;
    @FXML
    private JFXDatePicker date;
    @FXML
    private JFXTextField salaire;
    @FXML
    private JFXTextArea adresse;
    @FXML
    private JFXTextArea remarque;
    @FXML
    private JFXRadioButton rbBarman;
    @FXML
    private ToggleGroup groupe;
    @FXML
    private JFXRadioButton rbRestaurant;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void modifier(ActionEvent event) {

        boolean empty = ValidateTF.isEmpty(nom) | ValidateTF.isEmpty(salaire);
        String type = "";
        if (rbBarman.isSelected()) {
            type = "Barman";
        } else if (rbRestaurant.isSelected()) {
            type = "Restaurant";
        }
        editEmploye = new ListEmploye(nom.getText(), adresse.getText(), tel.getText(), remarque.getText(), Double.valueOf(salaire.getText()), 0, Date.valueOf(date.getValue()), type);
        if (!nonNull(editEmploye) || empty) {
            return;
        }
        boolean ok = AlertMaker.showDialog("Ajouter", "Voulez vous vraiment ajouter " + editEmploye.getRs() + " ?");
        if (ok) {
            try {
                int i = DatabaseUtil.addEmp(editEmploye);
                if (i > 0) {
                    AlertMaker.showInfo("Ajouter", "Données ajoutées avec succés !");
                }
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }
    }

}
