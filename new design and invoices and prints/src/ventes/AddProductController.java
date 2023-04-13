package ventes;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXSpinner;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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
import static util.BaseController.editVente;
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
    FilteredList<ListVente> filteredData;
    @FXML
    private JFXTextField tfSearchProd;
    @FXML
    private Label labPrix;
    @FXML
    private Label labQte;
    @FXML
    private JFXDatePicker tfDate;
    @FXML
    private JFXComboBox<String> cbEmploye;
    @FXML
    private TableColumn<?, ?> colVendeur;
    @FXML
    private JFXSpinner spinEtat;
    @FXML
    private HBox panDetail;
    @FXML
    private JFXToggleButton toggleBtn;
    @FXML
    private JFXComboBox<String> cbType;
    @FXML
    private Tooltip ttQteSortie;
    @FXML
    private Hyperlink labQteSortie;

    private ArrayList<Integer> qteSortie = new ArrayList<>();
    @FXML
    private MenuItem btnExcel;
    @FXML
    private MenuItem btnPdf;
    @FXML
    private MenuItem btnPrint;
    @FXML
    private StackPane rootPane;

    double tot = 0.;
    int qte1 = 0, qte2 = 0;
    @FXML
    private MenuItem btnVoir;
    @FXML
    private AnchorPane mainContainer;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initData();
        clearFields();
        loadCbEmploye();
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
                return n.getDes().contains(term) || n.getVendeur().contains(term);
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

        int empl = getEmployeID();
        try {
            if (toggleBtn.isSelected()) {
                statement = con.prepareStatement("SELECT sortieproduit.*,employes.nom_employe "
                        + "FROM sortieproduit,employes WHERE sortieproduit.employe = ? AND employes.id_employe = ? AND date = ?");
                statement.setInt(1, empl);
                statement.setInt(2, empl);
                statement.setDate(3, Date.valueOf(date));
            } else {
                statement = con.prepareStatement("SELECT sortieproduit.*,employes.nom_employe"
                        + " FROM sortieproduit,employes WHERE sortieproduit.employe = employes.id_employe AND date = ?");
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
            statement = con.prepareStatement("SELECT nom_employe FROM employes WHERE type_employe = 'Barman'");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cbEmploye.getItems().addAll(resultSet.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void loadCbProd(int emp, String type) {
        try {
            qteSortie.clear();
            cbDes.getItems().clear();
            statement = con.prepareStatement("SELECT DISTINCT lignesortie.des_prod,lignesortie.qte,produits.type"
                    + " FROM lignesortie,produits WHERE produits.des_prod = lignesortie.des_prod AND produits.type = ?"
                    + " AND lignesortie.employe= ? AND lignesortie.date = CURRENT_DATE");
            statement.setString(1, type);
            statement.setInt(2, emp);

            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cbDes.getItems().addAll(resultSet.getString(1));
                qteSortie.add(resultSet.getInt("lignesortie.qte"));
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

        getProd();//selection le prod en cours 
        
          for (ListVente listVente : data) {
            if (listVente.getDes().equals(editProd.getDes())
                    && listVente.getVendeur().equals(cbEmploye.getSelectionModel().getSelectedItem())) {

                AlertMaker.showMaterialError(rootPane, mainContainer,
                        new ArrayList<>(),
                        "Erreur", "Produit déja existant dans la liste  !");

                return;
            }
        }

        int qteRest = Integer.parseInt(tfQte.getText());
        int qteV = 0;
        double prixVente = 0.;
        double totalV = 0.;
        String req = "";
        int empl = getEmployeID();
        try {
            statement = con.prepareStatement("SELECT qte FROM sortieproduit WHERE sortieproduit.des_prod = ?"
                    + " AND sortieproduit.date = CURRENT_DATE AND sortieproduit.employe= ? ");
            statement.setString(1, editProd.getDes());
            statement.setInt(2, empl);
            resultSet = statement.executeQuery();
            int qteVendu = 0;
            if (resultSet.next()) {
                qteVendu = resultSet.getInt("qte");
            }
            statement = con.prepareStatement("SELECT * FROM lignesortie WHERE lignesortie.des_prod = ? "
                    + "AND lignesortie.date = CURRENT_DATE AND lignesortie.employe= ? ");
            statement.setString(1, editProd.getDes());
            statement.setInt(2, empl);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                if ((qteVendu + Integer.parseInt(tfQte.getText())) > resultSet.getInt("qte")) {
                    int qteR = resultSet.getInt("qte") - qteVendu;
                    tfQte.getStyleClass().add(".wrong-credentials");
                    AlertMaker.showMaterialError(rootPane, mainContainer,
                            new ArrayList<>(),
                            "Erreur", "La valeur saisie est superieur à "
                            + "la quantité sortie ( " + resultSet.getInt("qte") + " ) , il reste : " + qteR);
                    return;
                }
                qteV = resultSet.getInt("qte") - qteRest;
                prixVente = resultSet.getDouble("prix_vente");
                totalV = qteV * prixVente;
                req = "INSERT INTO sortieproduit (des_prod , qte , prix_vente , total , "
                        + "date,employe) VALUES (?,?,?,?,CURRENT_DATE,?)";
            }
            statement = con.prepareStatement(req);
            statement.setString(1, editProd.getDes());
            statement.setInt(2, qteV);
            statement.setDouble(3, prixVente);
            statement.setDouble(4, totalV);
            statement.setInt(5, empl);
            int i = statement.executeUpdate();
            if (i > 0) {
                statement = con.prepareStatement("SELECT qte FROM sortieproduit "
                        + "WHERE des_prod = ? AND date = CURRENT_DATE AND employe= ?");
                statement.setString(1, editProd.getDes());
                statement.setInt(2, empl);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    qteV = resultSet.getInt("qte");
                }
                req = "UPDATE produits SET qte_stock = ? WHERE des_prod = ?";
                statement = con.prepareStatement(req);
                statement.setInt(1, editProd.getQteStock() - qteV / editProd.getId());//deviser pour preserver l'unite ! 
                statement.setString(2, editProd.getDes());
                statement.executeUpdate();
                loadData(LocalDate.now());
                System.out.println("Data added successffully ! ");
            }
            clearFields();
            initLab(event);
            animationFade(tableProd);
        } catch (SQLException ex) {
            System.err.println(ex.getSQLState() + " " + ex.getMessage() + " " + ex.getErrorCode());
        }

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

    private void getProd() {
        String des = cbDes.getSelectionModel().getSelectedItem();
        try {
            statement = con.prepareStatement("SELECT produits.*,unites.qte_unite"
                    + " FROM produits,unites WHERE produits.unite = unites.nom_unite AND produits.des_prod = ?");
            statement.setString(1, des);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                editProd = new ListProd(
                        resultSet.getInt("qte_unite"),
                        resultSet.getString("des_prod"),
                        resultSet.getDouble("prix_achat"),
                        resultSet.getDouble("prix_vente"),
                        resultSet.getInt("qte_stock"),
                        resultSet.getString("commentaire_prod"),
                        resultSet.getString("unite"),
                        resultSet.getString("type"),
                        resultSet.getString("fournisseur")
                );
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void clearFields() {
        //toggleBtn.setDisable(true);
        spinEtat.setVisible(false);
        tfQte.clear();
        labPrix.setText("");
        labQte.setText("");
    }

    @FXML
    private void initLab(ActionEvent event) {

        if (cbEmploye.getSelectionModel().getSelectedIndex() >= 0) {
            
            double montant1 = 0, montant2 = 0;
            if (cbDes.getSelectionModel().getSelectedIndex() >= 0) {
                
                labQteSortie.setText("Qte sortie : " + qteSortie.get(cbDes.getSelectionModel().getSelectedIndex()) + " unités");
                ttQteSortie.setText("Qte sortie : " + qteSortie.get(cbDes.getSelectionModel().getSelectedIndex()) + " unités");
            }

            int emp = getEmployeID();
            try {
                statement = con.prepareStatement("SELECT SUM(qte) as qteTot, SUM(total) as grTotal FROM sortieproduit WHERE employe = ? "
                        + "AND sortieproduit.date = ?");
                statement.setInt(1, emp);
                statement.setDate(2, Date.valueOf(getDate()));
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    qte1 = resultSet.getInt("qteTot");
                    montant1 = resultSet.getDouble("grTotal");

                }
                statement = con.prepareStatement("SELECT SUM(qte) as qteTot, SUM(total) as grTotal FROM lignesortie WHERE employe = ? "
                        + "AND lignesortie.date = ?");
                statement.setInt(1, emp);
                statement.setDate(2, Date.valueOf(getDate()));
                resultSet = statement.executeQuery();
                if (resultSet.next()) {

                    qte2 = resultSet.getInt("qteTot");
                    montant2 = resultSet.getDouble("grTotal");
                    double percent = (double) qte1 / qte2;
                    spinEtat.setProgress(percent);
                    spinEtat.setVisible(true);
                    labPrix.setText("Qte vendus : " + qte1 + " / " + qte2);
                    labQte.setText("Montant total : " + BaseController.customFormat(montant1) + " / " + BaseController.customFormat(montant2));

                }

                animationFade(panDetail);
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
            getProd();
            navigate(event, ADD_PROD.getPage(), "Modifier produit");
        } catch (IOException ex) {
            Logger.getLogger(AddProductController.class.getName()).log(Level.SEVERE, null, ex);
        }
        loadData(LocalDate.now());
        initLab(event);
        getProd();
        loadCbProd(getEmployeID(), cbType.getSelectionModel().getSelectedItem());
    }

    @FXML
    private void loadHisto(ActionEvent event) {
        loadData(getDate());
        initLab(event);
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

    @FXML
    private void initDes(ActionEvent event) {

        if (cbEmploye.getSelectionModel().getSelectedIndex() >= 0) {
            int emp = getEmployeID();
            loadCbProd(emp, cbType.getSelectionModel().getSelectedItem());
            cbDes.setDisable(false);
            
            initLab(event);
            loadData(getDate());

        }
    }

    @FXML
    private void toggleLoad(ActionEvent event) {

        loadData(getDate());

    }

    @FXML
    private void loadDes(ActionEvent event) {
        initDes(event);

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
        navigate(event, getClass().getResource("/updateQteVente/addEdit.fxml"), "Modifier");
    }

    @FXML
    private void supprimer(ActionEvent event) {
        editVente = ListVente.class.cast(tableProd.getSelectionModel().getSelectedItem());
        if (!nonNull(editVente)) {
            return;
        }
        int qte = editVente.getQteStock();
        String des = editVente.getDes();
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editVente.getDes() + " ?");
        if (ok) {
            try {

                String req = "DELETE FROM sortieproduit WHERE id = ?";
                statement = con.prepareStatement(req);
                statement.setInt(1, editVente.getId());

                int i = statement.executeUpdate();
                if (i > 0) {

                    AlertMaker.showMaterialDialog(rootPane, mainContainer,
                            new ArrayList<>(),
                            "Supprimer", "Données supprimé avec succés !");
                    req = "UPDATE produits SET qte_stock = qte_stock+? WHERE des_prod = ?";
                    statement = con.prepareStatement(req);
                    statement.setInt(1, qte);
                    statement.setString(2, des);
                    statement.executeUpdate();
                }
                loadData(getDate());
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
                  AlertMaker.showMaterialError(rootPane, mainContainer,
                    new ArrayList<>(),
                    "Erreur ", ex.getMessage());
            }
        }
    }

    @FXML
    private void printInvoce(ActionEvent event) {
        try {

            //parametres 
            int numFacture = getNumFacture() + 1;
            String employe = cbEmploye.getSelectionModel().getSelectedItem();
            String qteVendus = String.valueOf(qte1);
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

            if (event.getSource() == btnVoir) {
                JasperViewer.viewReport(jp, false);

            } else if (event.getSource() == btnPrint) {

                JasperPrintManager.printReport(jp, true);
                setNumFacture(numFacture);

            } else if (event.getSource() == btnPdf) {
                File destFilePdf = new File(chooseDir(mainContainer), jp.getName() + ".pdf");
                JasperExportManager.exportReportToPdfFile(jp, destFilePdf.getPath());
               
                
                  AlertMaker.showMaterialDialog(rootPane, mainContainer,
                    new ArrayList<>(),
                   "Success ! ", "Fichier enregistré avec succés ");
            } else if (event.getSource() == btnExcel) {
                File destFileXls = new File(chooseDir(mainContainer), jp.getName() + ".xls");
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

    private int getNumFacture() {

        int numFacture = -1;
        try {
            statement = con.prepareStatement("SELECT id_facture FROM factures ORDER BY id_facture DESC LIMIT 1");

            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                numFacture = (getYear() % 1000) * 10000 + resultSet.getInt("id_facture");

            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return numFacture;
    }

    private void setNumFacture(int numFact) {

        try {
            statement = con.prepareStatement("INSERT INTO factures ( num_facture, date_facture) VALUES (?, NOW()); ");
            statement.setInt(1, numFact);
            statement.executeUpdate();

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }

    }

    private LocalDate getDate() {
        return tfDate.getValue() != null ? tfDate.getValue() : LocalDate.now();

    }
}
