package stats.detail;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.ListStatVente;
import util.BaseController;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class DetailMoisController extends BaseController implements Initializable {

    @FXML
    private TableView<ListStatVente> tbData;
    @FXML
    private TableColumn<?, ?> coldate;
    @FXML
    private TableColumn<?, ?> colQte;
    @FXML
    private TableColumn<?, ?> colTotal;
    @FXML
    private BarChart<String, Number> venteChart;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private CategoryAxis xAxis;

    private ObservableList<ListStatVente> data = FXCollections.observableArrayList();
    XYChart.Series<String, Number> seriesVentes = new XYChart.Series<>();
    XYChart.Series<String, Number> seriesBenef = new XYChart.Series<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        setCells();
        loadDataToTable();
    }

    private void setCells() {
        coldate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("qteStock"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
    }

    private void loadDataToTable() {
        data.clear();
        seriesVentes.getData().clear();

        try {
            statement = con.prepareStatement("SELECT SUM(qte) AS qte , SUM(total) as total ,"
                    + "date FROM sortieproduit "
                    + "WHERE date >= ? AND date  < ? + INTERVAL 1 MONTH GROUP by date");
            statement.setString(1, date);
            statement.setString(2, date);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                seriesVentes.getData().add(new XYChart.Data<>(resultSet.getDate("date").toString(), resultSet.getDouble("total")));
                data.add(new ListStatVente(resultSet.getInt("qte"), resultSet.getDate("date"), resultSet.getDouble("total")));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        tbData.setItems(data);
        venteChart.getData().add(seriesVentes);
    }
}
