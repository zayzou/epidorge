package provisioning;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
import dialogue.AlertMaker;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import static java.util.Objects.nonNull;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.ListApprov;
import model.ListFour;
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
import report.FactureModel;
import util.BaseController;
import static util.BaseController.editFour;
import static util.FXMLPage.ADD_PROD;
import validator.ValidateTF;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class Provision extends BaseController implements Initializable {

    @FXML
    private JFXComboBox<String> cbDes;
    @FXML
    private JFXTextField tfQte;
    @FXML
    private TableView<ListApprov> tableProd;
    @FXML
    private TableColumn<?, ?> colId;
    @FXML
    private TableColumn<?, ?> colDes;
    @FXML
    private TableColumn<?, ?> colQte;
    @FXML
    private TableColumn<?, ?> colPrixUnit;
    @FXML
    private TableColumn<?, ?> colTotal;
    @FXML
    private TableColumn<?, ?> colNumFacture;

    @FXML
    private JFXTextField total;

    @FXML
    private JFXDatePicker tfDate;

    ObservableList<ListApprov> data;
    ObservableList<ListProd> dataProd = FXCollections.observableArrayList();

    @FXML
    private JFXComboBox<String> cbType;
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane mainContainer;
    @FXML
    private JFXTextField tfFacture;
    @FXML
    private TableColumn<?, ?> colDate;
    double tot = 0.;
    @FXML
    private JFXTextField tfSearchFacture;
    @FXML
    private Button btnPrint;
    @FXML
    private Button btnPdf;
    @FXML
    private Button btnExcel;
    @FXML
    private Button btnView;
    @FXML
    private Label labNumFacture;
    @FXML
    private JFXTextField tfFournisseur;
    @FXML
    private Button btnCredit;
    @FXML
    private Button btnVersement;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initData();
        cbDes.setDisable(true);
        clearFields();
        setCells();
//        loadData(LocalDate.now());
    }

    private void initData() {
        initTypeDate();
        cbType.getItems().clear();
        cbType.getItems().addAll(typeData);
        System.out.println("init data prod");
        data = FXCollections.observableArrayList();

    }

    private void setCells() {
        System.out.println("set cells prod");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDes.setCellValueFactory(new PropertyValueFactory<>("des"));
        colNumFacture.setCellValueFactory(new PropertyValueFactory<>("numFacture"));
        colPrixUnit.setCellValueFactory(new PropertyValueFactory<>("pachat"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("qteStock"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));

    }

    /**
     *
     * @param date load data to table for given date
     */
    private void loadData(LocalDate date) {
        System.out.println("load data prod");
        data.clear();
        tot = 0;
        String factureToFind = tfSearchFacture.getText();
        try {

            if (!factureToFind.isEmpty()) {
                statement = con.prepareStatement("SELECT * FROM entreproduit WHERE num_facture  = ?");
                statement.setInt(1, Integer.parseInt(factureToFind));
            } else {
                statement = con.prepareStatement("SELECT * FROM entreproduit WHERE date  = ?");
                statement.setDate(1, Date.valueOf(date));

            }

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                tot += resultSet.getDouble("total");
                data.add(new ListApprov(
                        resultSet.getInt("id"),
                        resultSet.getString("des_prod"),
                        resultSet.getString("num_facture"),
                        resultSet.getDouble("prix_unit"),
                        resultSet.getInt("qte"),
                        resultSet.getDouble("total"),
                        resultSet.getDate("date").toLocalDate().toString(),
                        resultSet.getInt("id_fournisseur")
                ));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        tableProd.setItems(data);
        animationFade(tableProd);
        total.setText(BaseController.customFormat(tot));
    }

    private void loadCombobox(String type) {
        try {
            dataProd.clear();
            cbDes.getItems().clear();
            statement = con.prepareStatement("SELECT * FROM produits WHERE type = ?");
            statement.setString(1, type);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dataProd.add(new ListProd(
                        resultSet.getInt("id_prod"),
                        resultSet.getString("des_prod"),
                        resultSet.getDouble("prix_achat"),
                        resultSet.getDouble("prix_vente"),
                        resultSet.getInt("qte_stock"),
                        resultSet.getString("commentaire_prod"),
                        resultSet.getString("unite"),
                        resultSet.getString("type"),
                        resultSet.getString("fournisseur")
                ));
                cbDes.getItems().addAll(resultSet.getString(2));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void ajouterProduit(ActionEvent event) {
        if (cbDes.getSelectionModel().getSelectedIndex() == -1 || ValidateTF.isEmpty(tfQte) || ValidateTF.isEmpty(tfFacture)) {
            AlertMaker.showMaterialError(rootPane, mainContainer,
                    new ArrayList<>(),
                    "Erreur", "Les champs  "
                    + "\"désignation produit\" et \" type "
                    + "\" sont obligatoire");
            return;
        }
        try {
            labNumFacture.setText(tfFacture.getText());

            statement = con.prepareStatement(
                    "INSERT INTO entreproduit (des_prod  ,num_facture,qte , prix_unit , total , date,id_fournisseur) VALUES (?,?,?,?,?,?,?)");
            double prixAchat = dataProd.get(cbDes.getSelectionModel().getSelectedIndex()).getPachat();
            statement.setString(1, cbDes.getSelectionModel().getSelectedItem());
            statement.setString(2, tfFacture.getText());
            statement.setInt(3, Integer.valueOf(tfQte.getText()));
            statement.setDouble(4, prixAchat);
            statement.setDouble(5, prixAchat * Integer.valueOf(tfQte.getText()));
            statement.setDate(6, Date.valueOf(LocalDate.now()));
            statement.setInt(7, Integer.valueOf(tfFournisseur.getText()));
            int i = statement.executeUpdate();
            if (i > 0) {
                int qteEnStock = dataProd.get(cbDes.getSelectionModel().getSelectedIndex()).getQteStock() + Integer.valueOf(tfQte.getText());
                String req = "UPDATE produits SET qte_stock = ? WHERE des_prod = ?";
                statement = con.prepareStatement(req);
                statement.setInt(1, qteEnStock);
                statement.setString(2, cbDes.getSelectionModel().getSelectedItem());
                statement.executeUpdate();
                loadData(LocalDate.now());
                clearFields();
                initLab(null);
                System.out.println("Data added successffully ! ");
                loadFournisseur();
            }
        } catch (SQLException ex) {
            System.err.println(ex.getSQLState() + " " + ex.getMessage() + " " + ex.getErrorCode());
        }

    }

    private void loadFournisseur() {
        int idFour = 0;
        if (!tfFournisseur.getText().isEmpty()) {
            idFour = Integer.parseInt(tfFournisseur.getText());
        } else if (!data.isEmpty()) {
            idFour = data.get(0).getIdFournisseur();
        }

        try {
            statement = DatabaseUtil.getDbCon().prepareStatement(
                    "SELECT * FROM fournisseurs WHERE id_four = ?");
            statement.setInt(1, idFour);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                editFour = new ListFour(resultSet.getString("raison_sociale_four"), "four", "", "", resultSet.getDouble("solde_four"), 0);
                btnCredit.setDisable(false);
                btnVersement.setDisable(false);
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void clearFields() {
        tfQte.clear();

    }

    @FXML
    private void initLab(ActionEvent event) {
        if (cbDes.getSelectionModel().getSelectedIndex() >= 0) {
            try {
                statement = con.prepareStatement("SELECT prix_achat,prix_vente,qte_stock FROM produits WHERE des_prod = ?");
                statement.setString(1, cbDes.getSelectionModel().getSelectedItem());
                resultSet = statement.executeQuery();
                if (resultSet.next()) {

                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }

        }

    }

    @FXML
    private void loadEdit(ActionEvent event) {
        if (cbDes.getSelectionModel().getSelectedIndex() == -1) {
            AlertMaker.showDialog("Erreur", "Vueillez sélectionnez d'abord la désignation de prduit");
            return;
        }
        try {
            editProd = dataProd.get(cbDes.getSelectionModel().getSelectedIndex());
            navigate(event, ADD_PROD.getPage(), "Modifier produit");
        } catch (IOException ex) {
            Logger.getLogger(Provision.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(LocalDate.now());
        initLab(event);
        editProd = dataProd.get(cbDes.getSelectionModel().getSelectedIndex());
        loadCombobox(cbType.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void loadHisto(ActionEvent event) {
        loadData(tfDate.getValue());
    }

    @FXML
    private void loadAdd(ActionEvent event) {
        try {
            editProd = null;
            navigate(event, ADD_PROD.getPage(), "Ajouter produit");
        } catch (IOException ex) {
            Logger.getLogger(Provision.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(LocalDate.now());
        initLab(event);
    }

    @FXML
    private void loadDes(ActionEvent event) {
        loadCombobox(cbType.getSelectionModel().getSelectedItem());
        cbDes.setDisable(false);

    }

    @FXML
    private void modifier(ActionEvent event) throws IOException {
        editApprov = ListApprov.class.cast(tableProd.getSelectionModel().getSelectedItem());
        if (!nonNull(editApprov)) {
            return;
        }
        navigate(event, getClass().getResource("/updateQte/addEdit.fxml"), "Modifier");

    }

    @FXML
    private void supprimer(ActionEvent event) {

        editApprov = ListApprov.class.cast(tableProd.getSelectionModel().getSelectedItem());
        if (!nonNull(editApprov)) {
            return;
        }
        int qte = editApprov.getQteStock();
        String des = editApprov.getDes();
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editApprov.getDes() + " ?");
        if (ok) {
            try {

                String req = "DELETE FROM entreproduit WHERE id = ?";
                statement = con.prepareStatement(req);
                statement.setInt(1, editApprov.getId());

                int i = statement.executeUpdate();
                if (i > 0) {
                    AlertMaker.showInfo("Supprimer", "Données supprimé avec succés !");
                    req = "UPDATE produits SET qte_stock = qte_stock-? WHERE des_prod = ?";
                    statement = con.prepareStatement(req);
                    statement.setInt(1, qte);
                    statement.setString(2, des);
                    statement.executeUpdate();
                }
                loadData(LocalDate.now());
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }
    }

    @FXML
    private void refresh(ActionEvent event) {
        tfSearchFacture.setText("");
        tfFacture.setText("");
        loadData(LocalDate.now());
        initLab(event);
    }

    @FXML
    private void searchFacture(ActionEvent event) {
        labNumFacture.setText(tfSearchFacture.getText());
        loadData(LocalDate.now());
        loadFournisseur();
    }

    @FXML
    private void printInvoice(ActionEvent event) {

        try {
            loadFournisseur();
            //parametres
            String numFacture = labNumFacture.getText();
            String oldCredit = customFormat(editFour.getSolde());
            String grandTotal = customFormat(tot);
            String newSolde = customFormat(editFour.getSolde() + tot);

            String srcFile = System.getProperty("user.dir") + "\\iReportDesign\\FactureIn.jrxml";
//            String srcFile = "C:\\Users\\soffi\\Documents\\NetBeansProjects\\logiciel new\\EpiDorge\\src\\report\\FactureIn.jrxml";
            JasperReport jr = JasperCompileManager.compileReport(srcFile);
            HashMap<String, Object> parameters = new HashMap<>();

            parameters.put("numFacture", numFacture);
            parameters.put("oldCredit", oldCredit);
            parameters.put("newSolde", newSolde);
            parameters.put("grandTotal", grandTotal);

            //add database data here
            ArrayList<FactureModel> pData = new ArrayList<>();
            data.forEach((pm) -> {
                pData.add(new FactureModel(pm.getDes(), "" + pm.getQteStock(), customFormat(pm.getPachat()), customFormat(pm.getTotal())));
            });

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pData);
            JasperPrint jp = JasperFillManager.fillReport(jr, parameters, dataSource);
            String fileName = "Facture" + LocalDate.now();
            if (event.getSource() == btnView) {
                JasperViewer.viewReport(jp, false);

            } else if (event.getSource() == btnPrint) {

                JasperPrintManager.printReport(jp, true);

            } else if (event.getSource() == btnPdf) {
                File destFilePdf = new File(chooseDir(mainContainer), fileName + ".pdf");
                JasperExportManager.exportReportToPdfFile(jp, destFilePdf.getPath());

                AlertMaker.showMaterialDialog(rootPane, mainContainer,
                        new ArrayList<>(),
                        "Success ! ", "Fichier enregistré avec succés ");
            } else if (event.getSource() == btnExcel) {
                File destFileXls = new File(chooseDir(mainContainer), fileName + ".xls");
                JRXlsExporter exporter = new JRXlsExporter();
                exporter.setExporterInput(new SimpleExporterInput(jp));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(destFileXls));
                SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
                configuration.setOnePagePerSheet(true);
                exporter.setConfiguration(configuration);
                exporter.exportReport();

                AlertMaker.showMaterialDialog(rootPane, mainContainer,
                        new ArrayList<>(),
                        "Success ! ", "Fichier enregistré avec succés ");
            }

        } catch (JRException ex) {
            AlertMaker.showMaterialError(rootPane, mainContainer,
                    new ArrayList<>(),
                    "Erreur ! ", ex.getMessage());
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void ajouterVersement(ActionEvent event) {
        try {
            navigate(event, getClass().getResource("/soldefour/addEdit.fxml"), "Ajouter un versement");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    private void ajouterCredit(ActionEvent event) {
        try {
            editFour.setCpt(-1);//pour deferencier debiter de ajouter
            navigate(event, getClass().getResource("/soldefour/addEdit.fxml"), "Ajouter un crédit");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

}
