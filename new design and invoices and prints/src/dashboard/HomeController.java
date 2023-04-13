package dashboard;

import database.DatabaseUtil;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Rupture;
import util.BaseController;

/**
 * FXML Controller class
 *
 * @author soffi
 */
public class HomeController implements Initializable {

    public PreparedStatement statement;
    public ResultSet resultSet;
    public final Connection con = DatabaseUtil.DBCON;

    @FXML
    private TableView<Rupture> tableRupture;
    @FXML
    private TableColumn<?, ?> colIcon;
    @FXML
    private TableColumn<?, ?> colRef;
    @FXML
    private TableColumn<?, ?> colQte;
    @FXML
    private TableColumn<?, ?> colFournisseur;

    ObservableList<Rupture> data = FXCollections.observableArrayList();
    @FXML
    private Label labNbreProdRuptur;
    @FXML
    private Label labDay;
    @FXML
    private Label labDate;
    @FXML
    private Label labProdEnStock;
    @FXML
    private BarChart<String, Number> venteChart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;

    XYChart.Series<String, Number> seriesVentes = new XYChart.Series<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setCells();
        loadData();
        setDashboardInfo();
        loadChart();

    }

    private void setCells() {
        colIcon.setCellValueFactory(new PropertyValueFactory<>("icon"));
        colRef.setCellValueFactory(new PropertyValueFactory<>("ref"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("qte"));
        colFournisseur.setCellValueFactory(new PropertyValueFactory<>("fournisseur"));
    }

    private void loadData() {
        int i = 0;
        Image img = new Image("/resources/icon/error_35px.png");
        try {
            data.clear();
            String requete = "SELECT * FROM `produits` WHERE qte_stock <="
                    + " (SELECT preferences.val_min_rupture FROM preferences) ";
            statement = con.prepareStatement(requete);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                i++;
                data.add(new Rupture(
                        new ImageView(img),
                        resultSet.getString("des_prod"),
                        resultSet.getInt("qte_stock"),
                        resultSet.getString("fournisseur")
                ));
                System.out.println(img.getWidth());
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        tableRupture.setItems(data);
        labNbreProdRuptur.setText(i + "");
    }

    private void setDashboardInfo() {
        try {
            labDay.setText(LocalDate.now().getDayOfWeek().name());
            labDate.setText(LocalDate.now() + "");
            String reqCount = "SELECT COUNT(*) as qte FROM produits WHERE 1";
            resultSet = statement.executeQuery(reqCount);
            if (resultSet.next()) {
                labProdEnStock.setText(resultSet.getInt("qte") + "");
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }

    private void loadChart() {
        String[] months = BaseController.getMonths();
        seriesVentes.getData().clear();
        try {
            statement = con.prepareStatement(
                    "SELECT SUM(total) as total ,date \n"
                    + "FROM `sortieproduit` \n"
                    + "WHERE EXTRACT(year FROM date)  = ?\n"
                    + "GROUP BY EXTRACT(month FROM date) \n"
                    + "ORDER BY  EXTRACT(month FROM date) ");
            statement.setString(1, LocalDate.now().getYear() + "");

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int i = resultSet.getDate("date").toLocalDate().getMonth().getValue();
                seriesVentes.getData().add(new XYChart.Data<>(months[i], resultSet.getDouble("total")));

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        venteChart.getData().add(seriesVentes);
    }

    @FXML
    private void hoverFinished(MouseEvent event) {

    }

    @FXML
    private void hoverEffect(MouseEvent event) {
    }

}
