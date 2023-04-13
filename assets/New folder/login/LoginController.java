package login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import util.BaseController;

public class LoginController extends BaseController implements Initializable {

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadMain();

    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        if (isValideUser()) {
            closeStage();
            loadMain();

        } else {
            username.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
        }
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private void closeStage() {
        ((Stage) username.getScene().getWindow()).close();
    }

    void loadMain() {
        try {
            StackPane parent = FXMLLoader.load(getClass().getResource("/root/rootUI.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setMaximized(true);
            stage.setMinWidth(1000);
            stage.setMinHeight(600);
            stage.getIcons().add(new Image("/resources/icon.png"));
            stage.setTitle("Epi d'orge");
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());

        }
    }

    public boolean isValideUser() {
        boolean valide = false;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT username,password "
                    + "FROM preferences WHERE username = ? AND password = SHA1(?)");
            ps.setString(1, username.getText());
            ps.setString(2, password.getText());
            ResultSet rs = ps.executeQuery();
            valide = rs.next();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

        return valide;
    }

}
