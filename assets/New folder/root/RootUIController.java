package root;

import com.smattme.MysqlExportService;
import com.smattme.MysqlImportService;
import dialogue.AlertMaker;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import util.BaseController;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class RootUIController extends BaseController implements Initializable {

    @FXML
    private AnchorPane rootAnchorPane;
    @FXML
    private StackPane dataContainer;
    @FXML
    private CheckMenuItem checkfullScreen;
    @FXML
    private MenuItem btnExport;
    @FXML
    private MenuItem btnImport;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //loadUI("/dashboard/home.fxml","Accueil");
        loadStock(null);

    }

    public void loadUI(String ui) {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource(ui));
            dataContainer.getChildren().clear();
            dataContainer.getChildren().add(pane);

        } catch (IOException ex) {
            System.err.println("Error : " + ex.getMessage());

        }
    }

    public void loadUI(String ui, String title) {
        try {
            StackPane pane = FXMLLoader.load(getClass().getResource(ui));
            dataContainer.getChildren().clear();
            dataContainer.getChildren().add(pane);
            ((Stage) dataContainer.getScene().getWindow()).setTitle(title);
        } catch (IOException ex) {
            System.err.println("Error : " + ex.getMessage());

        }
    }

    @FXML
    private void loadHome(ActionEvent event) {
        loadUI("/dashboard/home.fxml", "Accueil");
    }

    @FXML
    private void loadStock(ActionEvent event) {
        loadUI("/stock/stockUI.fxml", "Afficher stock");
    }

    @FXML
    private void quitter(ActionEvent event) {
        Platform.exit();

    }

    @FXML
    private void loadApprov(ActionEvent event) {
        loadUI("/provisioning/addProduct.fxml", "Ajouter produits aux stock");
    }

    @FXML
    private void loadClients(ActionEvent event) {
        loadUI("/clients/clientUI.fxml", "Client");

    }

    @FXML
    private void fullScreen(ActionEvent event) {
        Stage stage = (Stage) rootAnchorPane.getScene().getWindow();
        stage.setFullScreen(checkfullScreen.isSelected());
    }

    @FXML
    private void loadStatVente(ActionEvent event) {
        loadUI("/stats/statsUI.fxml", "Statistiques ventes");
    }

    @FXML
    private void loadSettings(ActionEvent event) throws IOException {
        navigate(event, getClass().getResource("/settings/settings.fxml"), "Paramétres");
    }

    @FXML
    private void loadlogOut(ActionEvent event) throws IOException {
        ((Stage) rootAnchorPane.getScene().getWindow()).close();
        navigate(event, getClass().getResource("/login/login.fxml"), "LOGIN");
    }

    @FXML
    private void loadEmploye(ActionEvent event) {
        loadUI("/personels/ViewEmploye.fxml", "Liste employés");
    }

    @FXML
    private void addEmploye(ActionEvent event) throws IOException {
        navigate(event, getClass().getResource("/personels/ajouter/ajouterUI.fxml"), "Ajouter employé");
    }

    @FXML
    private void loadAbout(ActionEvent event) throws IOException {
        navigate(event, getClass().getResource("/about/about.fxml"), "A propos");
    }

    @FXML
    private void loadLigneVente(ActionEvent event) {
        loadUI("/ventes/ligneVente/addProduct.fxml", "Approvisionner employés");
    }

    @FXML
    private void loadVente(ActionEvent event) {
        loadUI("/ventes/addProduct.fxml", "Les ventes");
    }

    @FXML
    private void buckupDatabase(ActionEvent event) {
        File dir = chooseDir(rootAnchorPane);
        if (event.getSource() == btnExport) {
            exportDb(dir);
        } else if (event.getSource() == btnImport) {
            importDb(dir);
        }

    }

    private void exportDb(File dir) {
        if (dir == null){
            return;
        }
        try {
            //required properties for exporting of db
            Properties properties = new Properties();
            properties.setProperty(MysqlExportService.DB_NAME, "epiDorge");
            properties.setProperty(MysqlExportService.DB_USERNAME, "root");
            properties.setProperty(MysqlExportService.DB_PASSWORD, "2113853211");

            properties.setProperty(MysqlExportService.TEMP_DIR, dir.getPath());
            properties.setProperty(MysqlExportService.PRESERVE_GENERATED_ZIP, "true");

            MysqlExportService mysqlExportService = new MysqlExportService(properties);
            File file = mysqlExportService.getGeneratedZipFile();
            mysqlExportService.export();
            AlertMaker.showDialog("Succés ", "Données sauvegardé avec succés ! ");
        } catch (IOException | SQLException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
        }

    }

    private void importDb(File dir) {
        if (dir == null) {
            return;
        }
        try {
            String sql = new String(Files.readAllBytes(Paths.get(dir.toURI())));

            boolean res = MysqlImportService.builder()
                    .setDatabase("epiDorge")
                    .setSqlString(sql)
                    .setUsername("root")
                    .setPassword("2113853211")
                    .setDeleteExisting(true)
                    .setDropExisting(true)
                    .importDatabase();
            //assertTrue(res);
        } catch (IOException | ClassNotFoundException | SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }

    public File chooseDir(AnchorPane rootPane) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(stage);

        return file;
    }
}
