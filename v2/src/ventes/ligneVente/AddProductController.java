package ventes.ligneVente;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
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
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.ListProd;
import model.ListVente;
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
import static util.BaseController.customFormat;
import static util.FXMLPage.ADD_PROD;
import validator.ValidateTF;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class AddProductController extends BaseController implements Initializable {

    @FXML
    private JFXComboBox<String> cbDes;
    @FXML
    private JFXTextField tfQte;
    @FXML
    private TableView<ListVente> tableProd;
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
    private JFXTextField total;

    ObservableList<ListVente> data;
    ObservableList<ListProd> dataProd = FXCollections.observableArrayList();
    FilteredList<ListVente> filteredData;
    @FXML
    private JFXTextField tfSearchProd;
    @FXML
    private Label labPrix;
    @FXML
    private Label labQte;
    @FXML
    private Label labVente;
    @FXML
    private JFXDatePicker tfDate;
    @FXML
    private JFXComboBox<String> cbEmploye;
    @FXML
    private TableColumn<?, ?> colVendeur;
    @FXML
    private JFXComboBox<String> cbType;
    @FXML
    private Label labUnite;
    @FXML
    private JFXToggleButton toggleBtn;
    @FXML
    private MenuItem btnVoir;
    @FXML
    private MenuItem btnExcel;
    @FXML
    private MenuItem btnPdf;
    @FXML
    private MenuItem btnPrint;
    @FXML
    private StackPane rootPane;

    double tot = 0.;
    @FXML
    private AnchorPane mainContainer;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initData();
        cbDes.setDisable(true);
        loadCbEmploye();
        clearFields();
        setCells();
        loadData(LocalDate.now());
    }

    private void initData() {
        initTypeDate();
        cbType.getItems().clear();
        cbType.getItems().addAll(typeData);
        System.out.println("init data prod");
        data = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(data, n -> true);
        tfSearchProd.setOnKeyReleased((KeyEvent e) -> {
            filteredData.setPredicate((ListVente n) -> {
                String term = tfSearchProd.getText().trim();
                if (term == null || term.isEmpty()) {
                    return true;
                }
                return n.getDes().toLowerCase().contains(term.toLowerCase());
            });
        });
        tableProd.setItems(filteredData);
    }

    private void setCells() {
        System.out.println("set cells prod");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDes.setCellValueFactory(new PropertyValueFactory<>("des"));
        colPrixUnit.setCellValueFactory(new PropertyValueFactory<>("pvente"));
        colQte.setCellValueFactory(new PropertyValueFactory<>("qteStock"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colVendeur.setCellValueFactory(new PropertyValueFactory<>("vendeur"));

    }

    /**
     *
     * @param date load data to table for given date
     */
    private void loadData(LocalDate date) {
        tot = 0;
        System.out.println("load data prod");
        data.clear();

        try {
            if (toggleBtn.isSelected()) {
                int empl = getEmployeID();
                statement = con.prepareStatement("SELECT lignesortie.*,employes.nom_employe FROM lignesortie,employes WHERE lignesortie.employe = ? AND employes.id_employe = ? AND date = ?");
                statement.setInt(1, empl);
                statement.setInt(2, empl);
                statement.setDate(3, Date.valueOf(date));
            } else {
                statement = con.prepareStatement("SELECT lignesortie.*,employes.nom_employe FROM lignesortie,employes WHERE lignesortie.employe = employes.id_employe AND date = ?");
                statement.setDate(1, Date.valueOf(date));
            }

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                tot += resultSet.getDouble("total");
                data.add(new ListVente(
                        resultSet.getInt("id"),
                        resultSet.getString("des_prod"),
                        resultSet.getDouble("prix_vente"),
                        resultSet.getInt("qte"),
                        resultSet.getDouble("total"),
                        resultSet.getString("nom_employe")
                ));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        tableProd.setItems(filteredData);
        animationFade(tableProd);
        total.setText(BaseController.customFormat(tot));
    }

    private void loadCbEmploye() {
        try {
            cbEmploye.getItems().clear();
            statement = con.prepareStatement("SELECT id_employe,nom_employe FROM employes WHERE type_employe = 'Barman'");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cbEmploye.getItems().addAll(resultSet.getString(2));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
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
        if (cbDes.getSelectionModel().getSelectedIndex() == -1
                || cbEmploye.getSelectionModel().getSelectedIndex() == -1
                || ValidateTF.isEmpty(tfQte)) {

            AlertMaker.showMaterialError(rootPane, mainContainer,
                    new ArrayList<>(),
                    "Erreur", "Les champs \"employé\" , "
                    + "\"désignation produit\" et \"quantité restante "
                    + "\" sont obligatoire");
            return;
        }

        editProd = dataProd.get(cbDes.getSelectionModel().getSelectedIndex());
        for (ListVente listVente : data) {
            if (listVente.getDes().equals(editProd.getDes())
                    && listVente.getVendeur().equals(cbEmploye.getSelectionModel().getSelectedItem())) {

                AlertMaker.showMaterialError(rootPane, mainContainer,
                        new ArrayList<>(),
                        "Erreur", "Produit déja existant dans la liste  !");

                return;
            }
        }
        int qteSaisi = Integer.parseInt(tfQte.getText());
        int valUnite = 0;
        int empl = getEmployeID();
        try {
            /*
            pour l'unite
             */
            statement = con.prepareStatement("SELECT * FROM unites WHERE nom_unite = ?");
            statement.setString(1, editProd.getUnite());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                valUnite = resultSet.getInt("qte_unite");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        double qte = (double) qteSaisi / valUnite;
        if (qte > editProd.getQteStock()) {
            AlertMaker.showMaterialError(rootPane, mainContainer,
                    new ArrayList<>(),
                    "Erreur", "La valeur saisie est supérieur à la quantité en stock !");

            return;
        }

        try {
            statement = con.prepareStatement(
                    "INSERT INTO lignesortie (des_prod , qte , prix_vente , total , date,employe) "
                    + "VALUES (?,?,?,?,?,?)");
            double prixVente = dataProd.get(cbDes.getSelectionModel().getSelectedIndex()).getPvente();
            statement.setString(1, cbDes.getSelectionModel().getSelectedItem());
            statement.setInt(2, qteSaisi);
            statement.setDouble(3, prixVente);
            statement.setDouble(4, prixVente * qteSaisi);
            statement.setDate(5, Date.valueOf(LocalDate.now()));
            statement.setInt(6, empl);
            int i = statement.executeUpdate();
            if (i > 0) {
                loadData(LocalDate.now());
                clearFields();
                initLab(null);
                System.out.println("Data added successffully ! ");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getSQLState() + " " + ex.getMessage() + " " + ex.getErrorCode());
        }

    }

    private void clearFields() {
        tfQte.clear();
        labPrix.setText("");
        labQte.setText("");
        labVente.setText("");
        labUnite.setText("");
    }

    @FXML
    private void initDes(ActionEvent event) {
        if (cbEmploye.getSelectionModel().getSelectedIndex() >= 0) {
            int emp = getEmployeID();
            loadCombobox(cbType.getSelectionModel().getSelectedItem());
            cbDes.setDisable(false);
            //toggleBtn.setDisable(false);
            initLab(event);
            loadData(LocalDate.now());

        }
    }

    @FXML
    private void initLab(ActionEvent event) {
        if (cbDes.getSelectionModel().getSelectedIndex() >= 0) {
            try {
                statement = con.prepareStatement("SELECT prix_achat,prix_vente,qte_stock,unite ,unites.qte_unite FROM produits,unites WHERE des_prod = ? AND unites.nom_unite = produits.unite");
                statement.setString(1, cbDes.getSelectionModel().getSelectedItem());
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    labPrix.setText("Prix d'achat : " + BaseController.customFormat(resultSet.getDouble("prix_achat")));
                    labVente.setText("Prix de vente : " + BaseController.customFormat(resultSet.getDouble("prix_vente")));
                    labQte.setText("Qte en stock : " + resultSet.getInt("qte_stock"));
                    labUnite.setText("Unité : " + resultSet.getString("unite") + "(" + resultSet.getInt("unites.qte_unite") + ")");

                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }

        }
    }

    @FXML
    private void loadEdit(ActionEvent event) {
        if (cbDes.getSelectionModel().getSelectedIndex() == -1) {
            AlertMaker.showMaterialError(rootPane, mainContainer,
                    new ArrayList<>(),
                    "Erreur", "Vueillez sélectionnez d'abord la désignation de prduit");

            return;
        }
        try {
            editProd = dataProd.get(cbDes.getSelectionModel().getSelectedIndex());
            navigate(event, ADD_PROD.getPage(), "Modifier produit");
        } catch (IOException ex) {
            Logger.getLogger(AddProductController.class.getName()).log(Level.SEVERE, null, ex);
        }
        clearFields();
        loadData(LocalDate.now());
        initLab(event);
        editProd = dataProd.get(cbDes.getSelectionModel().getSelectedIndex());
        loadCombobox(cbType.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void loadHisto(ActionEvent event) {
        loadData(getDate());
    }

    @FXML
    private void loadAdd(ActionEvent event) {
        try {
            editProd = null;
            navigate(event, ADD_PROD.getPage(), "Ajouter produit");
        } catch (IOException ex) {
            Logger.getLogger(AddProductController.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(LocalDate.now());
        initLab(event);

    }

    private int getEmployeID() {
        int empl = 0;

        try {
            String emp = cbEmploye.getSelectionModel().getSelectedItem();
            statement = con.prepareStatement("SELECT * FROM employes WHERE nom_employe = ?");
            statement.setString(1, emp);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                empl = resultSet.getInt("id_employe");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return empl;
    }

    @FXML
    private void loadDes(ActionEvent event) {
        loadCombobox(cbType.getSelectionModel().getSelectedItem());
        cbDes.setDisable(false);

    }

    @FXML
    private void refresh(ActionEvent event) {
        loadData(LocalDate.now());
        initLab(event);
    }

    @FXML
    private void modifier(ActionEvent event) throws IOException {
        editVente = ListVente.class.cast(tableProd.getSelectionModel().getSelectedItem());
        if (!nonNull(editVente)) {
            return;
        }
        navigate(event, getClass().getResource("/updateQteLigneVente/addEdit.fxml"), "Modifier");
    }

    @FXML
    private void supprimer(ActionEvent event) {
        editVente = ListVente.class.cast(tableProd.getSelectionModel().getSelectedItem());
        if (!nonNull(editVente)) {
            return;
        }
        String des = editVente.getDes();
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editVente.getDes() + " ?");
        if (ok) {
            int qteActuel = getQteSortie(des);
            if (qteActuel != -1) {
                AlertMaker.showMaterialError(rootPane, mainContainer,
                        new ArrayList<>(),
                        "Erreur", "Vous ne pouvez pas supprimer le produit\n vous avez comfirmer ça vente !"
                );

                return;
            }
            try {

                String req = "DELETE FROM lignesortie WHERE id = ?";
                statement = con.prepareStatement(req);
                statement.setInt(1, editVente.getId());

                int i = statement.executeUpdate();
                if (i > 0) {
                    AlertMaker.showMaterialDialog(rootPane, mainContainer,
                            new ArrayList<>(),
                            "Supprimer", "Données supprimé avec succés !");

                }
                loadData(LocalDate.now());
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }
    }

    /**
     *
     * @param des Designation product
     * @return quantity deja vendue
     */
    private int getQteSortie(String des) {
        int qte = -1;
        try {

            statement = con.prepareStatement("SELECT qte FROM sortieproduit WHERE des_prod = ?"
                    + "AND date = CURRENT_DATE AND sortieproduit.employe = (SELECT employes.id_employe"
                    + " FROM employes WHERE employes.nom_employe = ?)");
            statement.setString(1, des);
            statement.setString(2, editVente.getVendeur());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                qte = resultSet.getInt("qte");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());

        }
        return qte;
    }

    @FXML
    private void toggleLoad(ActionEvent event) {
        loadData(getDate());
    }

    private LocalDate getDate() {
        return tfDate.getValue() != null ? tfDate.getValue() : LocalDate.now();

    }

    @FXML
    private void printInvoce(ActionEvent event) {
        try {

            //parametres
            int numFacture = 000000;
            String employe = cbEmploye.getSelectionModel().getSelectedItem();
            String qteVendus = String.valueOf(0);
            String total = customFormat(tot);

            String srcFile = System.getProperty("user.dir") + "\\iReportDesign\\facture.jrxml";
            //String srcFile = "C:\\Users\\soffi\\Documents\\NetBeansProjects\\EpiDorge\\src\\report\\facture.jrxml";
            JasperReport jr = JasperCompileManager.compileReport(srcFile);
            HashMap<String, Object> parameters = new HashMap<>();

            parameters.put("NumFacture", "" + numFacture);
            parameters.put("employe", employe);
            parameters.put("qteVendus", qteVendus);
            parameters.put("total", total);

            ArrayList<FactureModel> pData = new ArrayList<>();
            data.forEach((pm) -> {
                pData.add(new FactureModel(pm.getDes(), "" + pm.getQteStock(), customFormat(pm.getPvente()), customFormat(pm.getTotal())));
            });
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(pData);
            JasperPrint jp = JasperFillManager.fillReport(jr, parameters, dataSource);
            String fileName = "Facture" + LocalDate.now();
            if (event.getSource() == btnVoir) {
                JasperViewer.viewReport(jp, false);

            } else if (event.getSource() == btnPrint) {

                JasperPrintManager.printReport(jp, true);

            } else if (event.getSource() == btnPdf) {
                File destFilePdf = new File(chooseDir(mainContainer), fileName + ".pdf");
                JasperExportManager.exportReportToPdfFile(jp, destFilePdf.getPath());

                AlertMaker.showMaterialDialog(rootPane,
                        mainContainer, new ArrayList<>(),
                        "Success !", "Fichier enregistré avec succés ");

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

        }

    }

}
