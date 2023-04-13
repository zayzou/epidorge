package settings;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import dialogue.AlertMaker;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import util.BaseController;
import validator.ValidateTF;

public class SettingsController extends BaseController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField pass;
    @FXML
    private JFXPasswordField nPass;
    @FXML
    private JFXPasswordField nPass2;
    @FXML
    private JFXButton passBtn;
    @FXML
    private JFXButton nPassBtn;
    @FXML
    private JFXButton nPass2Btn;
    @FXML
    private Label labPass;
    @FXML
    private Label labPass2;
    @FXML
    private JFXTextField valeurMinRupture;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        labPass.setVisible(false);
        labPass2.setVisible(false);

    }

    @FXML
    private void handleSaveButtonAction(ActionEvent event) {
        boolean empty = ValidateTF.isEmpty(pass) | ValidateTF.isEmpty(nPass2) | ValidateTF.isEmpty(nPass);
        boolean wrong = isWrong();
        boolean same = isSame();
        if (!empty && !wrong && same) {
            boolean ok = AlertMaker.showDialog("Changer mot de passe", "Voulez vous vraiment Changer le mot de passe ?");
            if (ok) {
                try {
                    statement = con.prepareStatement("UPDATE preferences SET password = SHA1(?)");
                    statement.setString(1, nPass.getText());
                    int i = statement.executeUpdate();
                    if (i > 0) {
                        AlertMaker.showDialog("Changer mot de passe", "Changement réussis  !");
                        pass.clear();
                        nPass.clear();
                        nPass2.clear();
                    }
                } catch (SQLException ex) {
                    System.out.println("Erreur : " + ex.getSQLState() + " " + ex.getMessage() + " " + ex.getLocalizedMessage());
                    AlertMaker.showDialog("Erreur SQL", ex.getMessage() + " " + ex.getSQLState());
                }
            }

        }
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        ((Stage) nPass.getScene().getWindow()).close();
    }

    @FXML
    private void changerUserName(ActionEvent event) {
        boolean empty = ValidateTF.isEmpty(username);
        if (!empty) {
            boolean ok = AlertMaker.showDialog("Changer nom utilisateur", "Voulez vous vraiment Changer nom utilisateur  ?");
            if (ok) {
                try {
                    statement = con.prepareStatement("UPDATE preferences SET username = ?");
                    statement.setString(1, username.getText());
                    int i = statement.executeUpdate();
                    if (i > 0) {
                        AlertMaker.showDialog("Changer nom utilisateur", "Changement réussis  !");
                        username.clear();
                    }
                } catch (SQLException ex) {
                    System.out.println("Erreur : " + ex.getSQLState() + " " + ex.getMessage() + " " + ex.getLocalizedMessage());
                    AlertMaker.showDialog("Erreur SQL", ex.getMessage() + " " + ex.getSQLState());
                }
            }

        }
    }

    @FXML
    private void clear(ActionEvent event) {
        if (event.getSource().toString().contains("passBtn")) {
            pass.clear();
        } else if (event.getSource().toString().contains("nPassBtn")) {
            nPass.clear();
        } else if (event.getSource().toString().contains("nPass2Btn")) {
            nPass2.clear();
        }

    }

    private boolean isWrong() {
        boolean wrong = false;
        try {
            statement = con.prepareStatement("SELECT COUNT(*) FROM preferences WHERE password = SHA1(?)");
            statement.setString(1, pass.getText());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if (resultSet.getInt(1) != 1) {
                    pass.getStyleClass().add("wrong-credentials");
                    labPass.setVisible(true);
                    wrong = true;
                } else {
                    pass.getStyleClass().remove("wrong-credentials");
                    labPass.setVisible(false);
                    wrong = false;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return wrong;
    }

    private boolean isSame() {
        boolean same = false;
        if (!nPass.getText().equals(nPass2.getText())) {
            nPass2.getStyleClass().add("wrong-credentials");
            labPass2.setVisible(true);
            same = false;
        } else {
            nPass2.getStyleClass().remove("wrong-credentials");
            labPass2.setVisible(false);
            same = true;
        }
        return same;
    }

    @FXML
    private void changeValMin(ActionEvent event) {
        boolean empty = ValidateTF.isEmpty(valeurMinRupture) || ValidateTF.isNaN(valeurMinRupture);
        if (!empty) {
            boolean ok = AlertMaker.showDialog("Changer le seuil", "Voulez vous vraiment Changer le seuil de rupture de stock  ?");
            if (ok) {
                try {
                    statement = con.prepareStatement("UPDATE preferences SET val_min_rupture = ?");
                    statement.setInt(1, Integer.parseInt(valeurMinRupture.getText()));
                    int i = statement.executeUpdate();
                    if (i > 0) {
                        AlertMaker.showDialog("Changer le seuil", "Changement réussis  !");
                        valeurMinRupture.clear();
                    }
                } catch (SQLException ex) {
                    System.out.println("Erreur : " + ex.getSQLState() + " " + ex.getMessage() + " " + ex.getLocalizedMessage());
                    AlertMaker.showDialog("Erreur SQL", ex.getMessage() + " " + ex.getSQLState());
                }
            }

        }
    }

}
