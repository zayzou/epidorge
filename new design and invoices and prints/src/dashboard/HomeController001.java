package dashboard;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.ListFour;
import util.BaseController;
import static util.BaseController.getYear;

public class HomeController001 extends BaseController implements Initializable {

    private StackPane personInfoContainer;
    @FXML
    private HBox stock_info;

    private PieChart personChart = new PieChart();

    @FXML
    private VBox dataFournisseur;
    @FXML
    private JFXTextField tfFournisseur;
    @FXML
    private Text soldeFour;
    @FXML
    private Text dateLast;
    @FXML
    private HBox paneAddSolde;

    @FXML
    private FontAwesomeIconView clear;

    @FXML
    private LineChart<String, Number> lineChart;

    private PreparedStatement st;
    private ResultSet rs;
    @FXML
    private StackPane stockInfoContainer;
    @FXML
    private FontAwesomeIconView clear1;
    @FXML
    private JFXTextField tfclient;
    @FXML
    private StackPane clientInfoContainer;
    @FXML
    private VBox dataClient;
    @FXML
    private Text soldeClient;
    @FXML
    private Text dateLastClient;

    boolean isActive = false;
    @FXML
    private NumberAxis valAxis;
    @FXML
    private CategoryAxis prodAxis;

    XYChart.Series<String, Number> seriesVente = new XYChart.Series<>();
    XYChart.Series<String, Number> seriesAchat = new XYChart.Series<>();
    @FXML
    private JFXButton btnAjouter;
    @FXML
    private JFXButton btnDebiter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        seriesAchat.setName("Achats");
        seriesVente.setName("Vente");
        loadBenefChart();
        clear(null);
        clearClient(null);
    }

    private void loadBenefChart() {
        seriesAchat.getData().clear();
        seriesVente.getData().clear();
        lineChart.getData().clear();
        try {
            statement = con.prepareStatement("SELECT des_prod,SUM(qte) AS qteEntre FROM sortieProduit  WHERE EXTRACT(YEAR FROM date) = ? GROUP BY des_prod ");
            statement.setInt(1, getYear());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("des_prod"));
                seriesVente.getData().add(new XYChart.Data<>(resultSet.getString("des_prod"), resultSet.getInt("qteEntre")));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        try {
            statement = con.prepareStatement("SELECT des_prod,SUM(qte) AS qteEntre FROM entreproduit  WHERE EXTRACT(YEAR FROM date) = ? GROUP BY des_prod ");
            statement.setInt(1, getYear());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                seriesAchat.getData().add(new XYChart.Data<>(resultSet.getString("des_prod"), resultSet.getInt("qteEntre")));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        lineChart.getData().add(seriesVente);
        lineChart.getData().add(seriesAchat);

    }

    private void initGraphs() {
        clientInfoContainer.getChildren().clear();
        clientInfoContainer.getChildren().add(personChart);
        personChart.setData(DatabaseUtil.getPersonnelsStat());
    }

    @FXML
    private void loadFournisseur(ActionEvent event) {
        try {
            st = DatabaseUtil.getDbCon().prepareStatement(
                    "SELECT * FROM fournisseurs WHERE id_four = ?");
            st.setInt(1, Integer.parseInt(tfFournisseur.getText()));
            rs = st.executeQuery();
            if (rs.next()) {
                editFour = new ListFour(rs.getString("raison_sociale_four"), "four", "", "", rs.getDouble("solde_four"), 0);
                soldeFour.setText("Reste à règler : " + BaseController.customFormat(rs.getDouble("solde_four")));
                dateLast.setText("Date dérnier paiement : " + rs.getDate("date").toLocalDate().toString());
                if (rs.getDouble("solde_four") <= 0) {
                    btnDebiter.setDisable(true);
                } else {
                    btnDebiter.setDisable(false);
                }
                paneAddSolde.setVisible(true);
                animationFade(paneAddSolde);
            } else {
                soldeFour.setText("Aucune information trouvé");
                dateLast.setText("SVP vérifier la raison social et réessayer");
            }
            lineChart.setVisible(false);
            dataFournisseur.setVisible(true);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void loadClient(ActionEvent event) {
        try {
            st = DatabaseUtil.getDbCon().prepareStatement(
                    "SELECT * FROM clients WHERE id_client = ?");
            st.setInt(1, Integer.parseInt(tfclient.getText()));
            rs = st.executeQuery();
            if (rs.next()) {
                editFour = new ListFour(rs.getString("raison_sociale_client"), "client", "", "", rs.getDouble("sold_client"), 0);
                soldeClient.setText("Reste à règler : " + BaseController.customFormat(rs.getDouble("sold_client")));
                if (Objects.nonNull(rs.getDate("date"))) {
                    dateLastClient.setText("Date dérnier paiement : " + rs.getDate("date").toLocalDate().toString());
                }
                if (rs.getDouble("sold_client") <= 0) {
                    btnDebiter.setDisable(true);
                } else {
                    btnDebiter.setDisable(false);
                }
                paneAddSolde.setVisible(true);
                animationFade(paneAddSolde);
            } else {
                soldeClient.setText("Aucune information trouvé");
                dateLastClient.setText("SVP vérifier la raison social et réessayer");
            }
            clientInfoContainer.getChildren().clear();
            clientInfoContainer.getChildren().add(dataClient);
            dataClient.setVisible(true);
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void clear(ActionEvent event) {
        tfFournisseur.clear();
        dataFournisseur.setVisible(false);
        paneAddSolde.setVisible(false);
        lineChart.setVisible(true);
    }

    @FXML
    private void clearClient(ActionEvent event) {
        tfclient.clear();
        dataClient.setVisible(false);
        paneAddSolde.setVisible(false);
        initGraphs();
    }

    @FXML
    private void verser(ActionEvent event) {
        try {
            navigate(event, getClass().getResource("/soldefour/addEdit.fxml"), "Ajouter versement");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    private void debiter(ActionEvent event) {
        try {
            editFour.setCpt(-1);//pour deferencier debiter de ajouter
            navigate(event, getClass().getResource("/soldefour/addEdit.fxml"), "Ajouter un crédit");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
