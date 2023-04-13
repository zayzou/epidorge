package stats;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import model.ListStatVente;
import util.BaseController;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class StatsUIController extends BaseController implements Initializable {

    @FXML
    private Label currentYear;
    @FXML
    private Label grandTotal;
    @FXML
    private Label qteTotal;
    @FXML
    private JFXComboBox<String> cbMois;
    @FXML
    private JFXTextField tfAnnee;
    @FXML
    private LineChart<String, Number> benifChart;
    @FXML
    private NumberAxis benifAxis;
    @FXML
    private CategoryAxis dateAxis;
    @FXML
    private Label labRevenu;
    @FXML
    private Label indiceProfit;
    @FXML
    private Label labProfit;
    @FXML
    private Label cout;
    @FXML
    private Label labCout;
    @FXML
    private FontAwesomeIconView iconProfi;
    @FXML
    private Label labQteTotal;
    @FXML
    private FontAwesomeIconView iconQteTot;
    @FXML
    private FontAwesomeIconView iconRevenu;
    @FXML
    private FontAwesomeIconView iconCout;

    private String[] months = new String[12];

    private ObservableList<ListStatVente> data;
    XYChart.Series<String, Number> seriesBenef = new XYChart.Series<>();

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        currentYear.setText(getYear() + "");
        date = LocalDate.now().getYear() + "-0" + LocalDate.now().getMonthValue() + "-01";
        data = FXCollections.observableArrayList();
        months = getMonths();
        initMonths();
        seriesBenef.setName("Benefice");
        benifChart.setTitle("Estimation bénifice "+ getYear());
        loadBenefChart();
        loadPercen();

    }

    private void loadBenefChart() {
        seriesBenef.getData().clear();
        try {
            statement = con.prepareStatement("SELECT EXTRACT(MONTH FROM date) date, "
                    + "SUM(produits.prix_vente*sortieproduit.qte)-SUM(produits.prix_achat*sortieproduit.qte) AS benef"
                    + " FROM produits,sortieproduit WHERE sortieproduit.des_prod = produits.des_prod"
                    + " AND EXTRACT(YEAR FROM date) = ? GROUP BY EXTRACT(MONTH FROM date)");
            statement.setInt(1, getYear());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                seriesBenef.getData().add(new XYChart.Data<>(months[Integer.valueOf(resultSet.getString("date"))], resultSet.getDouble("benef")));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        benifChart.getData().add(seriesBenef);

    }

    private void initMonths() {
        for (String month : months) {
            cbMois.getItems().addAll(month);
        }
    }

    @FXML
    private void loadStat(ActionEvent event) throws IOException {
        date = tfAnnee.getText() + "-0" + (cbMois.getSelectionModel().getSelectedIndex() + 1) + "-" + "01";

        navigate(event, getClass().getResource("/stats/detail/detailMois.fxml"), "détail vente");
    }

    private void loadPercen() {
        double[] current = new double[3];
        double[] past = new double[3];
        for (int i = 0; i < 3; i++) {
            current[i] = 0;
            past[i] = 0;
        }
        String[] lab = {"profit", "qteVendu", "revenuTotal"};
        try {
            String requteUnifie = "SELECT date,\n"
                    + "SUM(produits.prix_vente*sortieproduit.qte)-SUM(produits.prix_achat*sortieproduit.qte) AS profit,\n"
                    + "SUM(sortieproduit.qte)as qteVendu,\n"
                    + "SUM(sortieproduit.prix_vente*sortieproduit.qte) AS revenuTotal\n"
                    + "FROM produits,sortieproduit\n"
                    + "WHERE sortieproduit.des_prod = produits.des_prod AND EXTRACT(YEAR FROM sortieproduit.date) <= EXTRACT(YEAR FROM NOW())\n"
                    + "GROUP BY EXTRACT(YEAR FROM sortieproduit.date) ORDER BY date DESC";
            statement = con.prepareStatement(requteUnifie);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                for (int i = 0; i < 3; i++) {
                    current[i] = resultSet.getDouble(lab[i]);
                }
                if (resultSet.next()) {
                    for (int i = 0; i < 3; i++) {
                        past[i] = resultSet.getDouble(lab[i]);
                    }
                }

                //ini text
                indiceProfit.setText(customFormat(current[0]));
                qteTotal.setText((int) current[1] + "");
                grandTotal.setText(customFormat(current[2]));

                getPercen(current[0], past[0], labProfit, iconProfi);
                getPercen(current[1], past[1], labQteTotal, iconQteTot);
                //faut changer à past de 2
                getPercen(current[2], past[2], labRevenu, iconRevenu);

                statement = con.prepareStatement("SELECT SUM(entreproduit.prix_unit*entreproduit.qte) AS cout"
                        + " FROM `entreproduit`  "
                        + "WHERE EXTRACT(YEAR FROM entreproduit.date) <= EXTRACT(YEAR FROM NOW())"
                        + "GROUP BY EXTRACT(YEAR FROM date) ORDER BY date DESC");
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    double curr = resultSet.getDouble("cout");
                    cout.setText(customFormat(resultSet.getDouble("cout")));
                    resultSet.next();
                    getPercen(curr, resultSet.getDouble("cout"), labCout, iconCout);

                }

            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }

    private void getPercen(double current, double past, Label lab, FontAwesomeIconView icon) {
        double diff = current - past;
        double result = (diff * 100) / past;
        System.out.println(result);
        if (result < 0) {
            lab.getStyleClass().remove("labGain");
            lab.getStyleClass().add("labPert");
            icon.setGlyphName("CARET_DOWN");
        } else if (result == 0) {
            lab.getStyleClass().remove("labZero");
            lab.getStyleClass().add("labZero");
            icon.setGlyphName("CARET_LEFT");
        }
        lab.setText(customPercent(result));

    }
}
