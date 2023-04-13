package products;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import database.DatabaseUtil;
import dialogue.AlertMaker;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import static java.util.Objects.nonNull;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.ListProd;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.view.JasperViewer;
import report.StockModel;
import util.BaseController;
import static util.FXMLPage.ADD_PROD;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class ProdUIController extends BaseController implements Initializable {

    @FXML
    private JFXTextField tfSearchProd;
    @FXML
    private TableView<ListProd> tableProd;
    @FXML
    private TableColumn<?, ?> colIdProd;
    @FXML
    private TableColumn<?, ?> colDes;
    @FXML
    private TableColumn<?, ?> colUnite;
    @FXML
    private TableColumn<?, ?> colPrixAchat;
    @FXML
    private TableColumn<?, ?> colPrixVente;
    @FXML
    private TableColumn<?, ?> colQteStock;
    @FXML
    private TableColumn<?, ?> colRemarque;

    ObservableList<ListProd> data;
    FilteredList<ListProd> filteredData;
    @FXML
    private TableColumn<?, ?> colType;
    @FXML
    private TableColumn<?, ?> colFour;
    @FXML
    private JFXComboBox<String> cbFournisseur;
    @FXML
    private JFXToggleButton toggleBtn;
    @FXML
    private AnchorPane main;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnPdf;
    @FXML
    private Button btnExcel;
    @FXML
    private Button btnView;
    @FXML
    private StackPane rootPane;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initData();
        setCells();
        loadCbox();
        loadData();
        // TODO
    }

    private void loadCbox() {
        try {
            cbFournisseur.getItems().clear();
            statement = con.prepareStatement("SELECT * FROM fournisseurs");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cbFournisseur.getItems().addAll(resultSet.getString("raison_sociale_four"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void initData() {
        System.out.println("init data prod");
        data = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(data, n -> true);
        tfSearchProd.setOnKeyReleased((KeyEvent e) -> {
            filteredData.setPredicate((ListProd n) -> {
                String term = tfSearchProd.getText().trim();
                if (term == null || term.isEmpty()) {
                    return true;
                }
                return n.getDes().toLowerCase().contains(term.toLowerCase())
                        || n.getType().toLowerCase().contains(term.toLowerCase())
                        || n.getFourn().toLowerCase().contains(term.toLowerCase())
                        || n.getUnite().toLowerCase().contains(term.toLowerCase());
            });
        });
        tableProd.setItems(filteredData);
    }

    private void setCells() {
        System.out.println("set cells prod");
        /*column tablefour        */
        colIdProd.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDes.setCellValueFactory(new PropertyValueFactory<>("des"));
        colPrixAchat.setCellValueFactory(new PropertyValueFactory<>("pachat"));
        colPrixVente.setCellValueFactory(new PropertyValueFactory<>("pvente"));
        colQteStock.setCellValueFactory(new PropertyValueFactory<>("qteStock"));
        colRemarque.setCellValueFactory(new PropertyValueFactory<>("rem"));
        colUnite.setCellValueFactory(new PropertyValueFactory<>("unite"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colFour.setCellValueFactory(new PropertyValueFactory<>("fourn"));
        /* col tableprod */

    }

    private void loadData() {
        System.out.println("load data prod");
        data.clear();
        try {
            if (toggleBtn.isSelected() && cbFournisseur.getSelectionModel().getSelectedIndex() >= 0) {
                statement = con.prepareStatement("SELECT * FROM produits where fournisseur = ?");
                statement.setString(1, cbFournisseur.getSelectionModel().getSelectedItem());
            } else {
                statement = con.prepareStatement("SELECT * FROM produits");
            }

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                data.add(new ListProd(
                        resultSet.getInt("id_prod"),
                        resultSet.getString("des_prod"),
                        resultSet.getDouble("prix_achat"),
                        resultSet.getDouble("prix_vente"),
                        resultSet.getInt("qte_stock"),
                        LocalDate.parse(resultSet.getString("dateAjout")).toString(),
                        resultSet.getString("unite"),
                        resultSet.getString("type"),
                        resultSet.getString("fournisseur")
                ));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        tableProd.setItems(filteredData);
        animationFade(tableProd);

    }

    @FXML
    private void ajouter(ActionEvent event) {
        editProd = null;
        try {
            navigate(event, ADD_PROD.getPage(), "Ajouter Produit");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void modifier(ActionEvent event) {
        editProd = ListProd.class.cast(tableProd.getSelectionModel().getSelectedItem());
        if (!nonNull(editProd)) {
            return;
        }
        try {
            navigate(event, ADD_PROD.getPage(), "Modifier Produit");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    @FXML
    private void supprimer(ActionEvent event) {
        editProd = ListProd.class.cast(tableProd.getSelectionModel().getSelectedItem());
        if (!nonNull(editProd)) {
            return;
        }
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editProd.getDes() + " ?");
        if (ok) {
            try {
                int i = DatabaseUtil.execDelProd(editProd.getId());
                if (i > 0) {
                    AlertMaker.showInfo("Supprimer", "Données supprimé avec succés !");
                }
                loadData();
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }

    }

    @FXML
    private void refresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void loadWithFilter(ActionEvent event) {
        loadData();
    }

    @FXML
    private void toggleLoad(ActionEvent event) {
        loadData();
    }

    @FXML

    private void printInvoice(ActionEvent event) {

        try {

            String srcFile = System.getProperty("user.dir") + "\\iReportDesign\\stock.jrxml";

//            String srcFile = "C:\\Users\\soffi\\Documents\\NetBeansProjects\\logiciel new\\EpiDorge\\src\\report\\stock.jrxml";
            JasperReport jr = JasperCompileManager.compileReport(srcFile);
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("totalProduits", data.size() + "");

            //add database data here
            ArrayList<StockModel> pData = new ArrayList<>();

            data.forEach((pm) -> {
                pData.add(new StockModel(
                        (data.indexOf(pm) + 1) + "",
                        pm.getDes(),
                        pm.getQteStock() + "",
                        customFormat(pm.getPachat()),
                        customFormat(pm.getPvente()),
                        pm.getUnite(),
                        pm.getType(),
                        pm.getFourn())
                );

            });

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pData);
            JasperPrint jp = JasperFillManager.fillReport(jr, parameters, dataSource);
            String fileName = "Stock" + LocalDate.now();
            if (event.getSource() == btnView) {
                JasperViewer.viewReport(jp, false);

            } else if (event.getSource() == btnPrint) {

                JasperPrintManager.printReport(jp, true);

            } else if (event.getSource() == btnPdf) {

                File destFilePdf = new File(chooseDir(main), fileName + ".pdf");
                JasperExportManager.exportReportToPdfFile(jp, destFilePdf.getPath());

                AlertMaker.showMaterialDialog(rootPane, main,
                        new ArrayList<>(),
                        "Success ! ", "Fichier enregistré avec succés ");
            } else if (event.getSource() == btnExcel) {

                File destFileXls = new File(chooseDir(main), fileName + ".xls");
                JRXlsExporter exporter = new JRXlsExporter();
                exporter.setExporterInput(new SimpleExporterInput(jp));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFileXls));
                SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
                configuration.setOnePagePerSheet(true);
                exporter.setConfiguration(configuration);
                exporter.exportReport();

                AlertMaker.showMaterialDialog(rootPane, main,
                        new ArrayList<>(),
                        "Success ! ", "Fichier enregistré avec succés ");
            }

        } catch (JRException ex) {
            AlertMaker.showMaterialError(rootPane, main,
                    new ArrayList<>(),
                    "Erreur ! ", ex.getMessage());
            System.err.println(ex.getMessage());
        }
    }

}
