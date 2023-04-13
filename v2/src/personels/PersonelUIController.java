package personels;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXProgressBar;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
import dialogue.AlertMaker;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import static java.util.Objects.nonNull;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.ListEmploye;
import model.ListPaiement;
import util.BaseController;
import validator.ValidateTF;

/**
 * FXML Controller class
 *
 * @author sof
 */
public class PersonelUIController extends BaseController implements Initializable {

    ObservableList<ListEmploye> data;
    ObservableList<ListPaiement> dataPaiement;
    FilteredList<ListEmploye> filteredData;

    @FXML
    private TableView<ListEmploye> tblEmploye;
    @FXML
    private TableColumn<?, ?> clmEmployeId;
    @FXML
    private TableColumn<?, ?> clmEmployeName;
    @FXML
    private JFXTextArea adresse;
    @FXML
    private JFXTextField nom;
    @FXML
    private JFXTextField tel;
    @FXML
    private JFXTextField salaire;
    @FXML
    private JFXDatePicker date;
    @FXML
    private JFXTextField tfSearch;
    @FXML
    private JFXTextArea remarque;
   
    private int i;
    @FXML
    private TableView<ListPaiement> tablePaiment;

    @FXML
    private TableColumn<?, ?> colsolde;
    @FXML
    private TableColumn<?, ?> coldate;
    @FXML
    private JFXProgressBar pbVerse;
    @FXML
    private JFXProgressBar pbReste;
    @FXML
    private Label labRest;
    @FXML
    private Label labVerse;
    @FXML
    private JFXTextField tfMontant;
    @FXML
    private JFXComboBox<String> cbMois;
    private double reste = 0;
    @FXML
    private ToggleGroup groupe;
    @FXML
    private JFXRadioButton rbResto;
    @FXML
    private JFXRadioButton rbBarman;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private StackPane rootPane;
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
        cbMois.getItems().addAll(Arrays.asList(BaseController.getMonths()));
        setCells();
        loadData();
    }

    private void initData() {
        System.out.println("init data ");
        data = FXCollections.observableArrayList();
        dataPaiement = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(data, n -> true);
        tfSearch.setOnKeyReleased((KeyEvent e) -> {
            filteredData.setPredicate((ListEmploye n) -> {
                String term = tfSearch.getText().trim();
                if (term == null || term.isEmpty()) {
                    return true;
                }
                return n.getRs().contains(term);
            });
        });
        tblEmploye.setItems(filteredData);
    }

    private void setCells() {
        System.out.println("set cells");
       
        clmEmployeName.setCellValueFactory(new PropertyValueFactory<>("rs"));
        clmEmployeId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colsolde.setCellValueFactory(new PropertyValueFactory<>("solde"));
        coldate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colID.setCellValueFactory(new PropertyValueFactory<>("id"));
       

    }

    private void loadData() {
        System.out.println("load data");
        data.clear();
        try {
            statement = con.prepareStatement("SELECT * FROM employes");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                data.add(new ListEmploye(
                        resultSet.getString("nom_employe"),
                        resultSet.getString("adr_employe"),
                        resultSet.getString("tel_employe"),
                        resultSet.getString("commentaire_emp"),
                        resultSet.getDouble("salaire"),
                        resultSet.getInt("id_employe"),
                        resultSet.getDate("date_entrer"), 
                        resultSet.getString("type_employe")
                ));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        tblEmploye.setItems(filteredData);
    }

    @FXML
    private void modifier(ActionEvent event) {
         String type = "";
        if (rbBarman.isSelected()) {
             type = "Barman";
        }else if(rbResto.isSelected()){
             type = "Restaurant";
        }
       editEmploye = new ListEmploye(nom.getText(), adresse.getText(), tel.getText(), remarque.getText(), Double.valueOf(salaire.getText()), editEmploye.getId(), Date.valueOf(date.getValue()), type);
        if (!nonNull(editEmploye)) {
            return;
        }
        boolean ok = AlertMaker.showDialog("Mofifier", "Voulez vous vraiment modifier " + editEmploye.getRs() + " ?");
        if (ok) {
            try {
                i = DatabaseUtil.updateEmp(editEmploye);
                if (i > 0) {
                    AlertMaker.showInfo("Modifier", "Données modifier avec succés !");
                }
                loadData();
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }

    }

    @FXML
    private void supprimer(ActionEvent event) {
        String type = "";
        if (rbBarman.isSelected()) {
             type = "Barman";
        }else if(rbResto.isSelected()){
             type = "Restaurant";
        }
        editEmploye = new ListEmploye(nom.getText(), adresse.getText(), tel.getText(), remarque.getText(), Double.valueOf(salaire.getText()), editEmploye.getId(), Date.valueOf(date.getValue()), type);
        if (!nonNull(editEmploye)) {
            return;
        }
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editEmploye.getRs() + " ?");
        if (ok) {
            try {
                i = DatabaseUtil.execDelEmp(editEmploye.getId());
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
    private void tblEmloyeOnClick(MouseEvent event) {
        editEmploye = ListEmploye.class.cast(tblEmploye.getSelectionModel().getSelectedItem());
        populateFields();
        loadPaiement(LocalDate.now());
        getState();
    }

    @FXML
    private void tblViewOnClick(KeyEvent event) {
        editEmploye = ListEmploye.class.cast(tblEmploye.getSelectionModel().getSelectedItem());
        populateFields();
        loadPaiement(LocalDate.now());
        getState();
    }

    private void populateFields() {
        nom.setText(editEmploye.getRs());
        adresse.setText(editEmploye.getAdr());
        tel.setText(editEmploye.getTel());
        salaire.setText(String.valueOf(editEmploye.getSolde()));
        date.setValue(editEmploye.getDate().toLocalDate());
        remarque.setText(editEmploye.getCommentaire());
        if (editEmploye.getType().equals("Barman")) {
            rbBarman.setSelected(true);
        }else if(editEmploye.getType().equals("Restaurant")) {
            rbResto.setSelected(true);
        }

    }

    private void getState() {
        try {
            statement = con.prepareStatement("SELECT SUM(solde) as verse,employes.salaire-SUM(solde) as reste,employes.salaire , "
                    + "((employes.salaire-SUM(solde)))/employes.salaire  as percentRest,"
                    + "(SUM(solde))/employes.salaire  as percentVerse "
                    + "FROM salaires,employes  WHERE salaires.id_employe = ? AND employes.id_employe = ? "
                    + "AND EXTRACT(YEAR_MONTH FROM date) = EXTRACT(YEAR_MONTH FROM NOW())");
            statement.setInt(1, editEmploye.getId());
            statement.setInt(2, editEmploye.getId());
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                reste = resultSet.getDouble("reste");
                pbReste.setProgress(resultSet.getDouble("percentRest"));
                labRest.setText(BaseController.customFormat(resultSet.getDouble("reste")));
                labVerse.setText(BaseController.customFormat(resultSet.getDouble("verse")));
                pbVerse.setProgress(resultSet.getDouble("percentVerse"));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void setSalaire(ActionEvent event) {
        
        if (!nonNull(editEmploye) && !ValidateTF.isEmpty(tfMontant) && ValidateTF.isSup(tfMontant, reste)) {
            return;
        }
        boolean ok = AlertMaker.showDialog("Régler", "Voulez vous vraiment ajouter " + tfMontant.getText() + " ?");
        if (ok) {
            try {
                statement = con.prepareStatement("INSERT INTO salaires (id_employe,solde,date) VALUES (?,?,NOW())");
                
                statement.setInt(1, editEmploye.getId());
                statement.setDouble(2, Double.parseDouble(tfMontant.getText()));
                i = statement.executeUpdate();

                if (i > 0) {
                    AlertMaker.showInfo("Ajouter ", "Données ajouter avec succés !");
                }
                loadPaiement(LocalDate.now());
                getState();
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }
    }

    private void loadPaiement(LocalDate date) {
        dataPaiement.clear();
        try {
            statement = con.prepareStatement("SELECT * FROM salaires WHERE id_employe = ? "
                    + "AND EXTRACT(YEAR_MONTH FROM date) = EXTRACT(YEAR_MONTH FROM ?)");
            
            statement.setInt(1, editEmploye.getId());
            statement.setDate(2, Date.valueOf(date));
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dataPaiement.add(new ListPaiement(
                        resultSet.getInt("id"),
                        resultSet.getDouble("solde"),
                        resultSet.getDate("date")
                ));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        tablePaiment.setItems(dataPaiement);
        animationFade(tablePaiment);
        
    }

    @FXML
    private void loadDetail(ActionEvent event) {
        int mois = cbMois.getSelectionModel().getSelectedIndex()+1;
        loadPaiement(LocalDate.of(getYear(), mois, mois));
    }

    @FXML
    private void refresh(ActionEvent event) {
        loadData();
        loadPaiement(LocalDate.now());
        getState();
    }

    @FXML
    private void modifierVersement(ActionEvent event) throws IOException {
        editPaiement = ListPaiement.class.cast(tablePaiment.getSelectionModel().getSelectedItem());
        if (!nonNull(editPaiement)) {
            return;
        }
        navigate(event, getClass().getResource("/updateVersement/addEdit.fxml"), "Modifier");

        
        
    }

    @FXML
    private void supprimerVersement(ActionEvent event) {
        editPaiement = ListPaiement.class.cast(tablePaiment.getSelectionModel().getSelectedItem());
        
        if (!nonNull(editPaiement)) {
            return;
        }
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editPaiement.getSolde() + " ?");
        if (ok) {
            try {

                String req = "DELETE FROM salaires WHERE id = ?";
                statement = con.prepareStatement(req);
                statement.setInt(1, editPaiement.getId());

                int i = statement.executeUpdate();
                if (i > 0) {
                    AlertMaker.showInfo("Supprimer", "Données supprimé avec succés !");
                }
                loadPaiement(LocalDate.now());
                getState();
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }
    }

}
