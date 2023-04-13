package database;

import dialogue.AlertMaker;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.sql.Date;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import model.ListEmploye;
import model.ListFour;
import model.ListProd;
import model.ListUnit;
import util.BaseController;

/**
 *
 * @author sof
 */
public class DatabaseUtil extends BaseController {

    private static Connection dbCon = getDbCon();
    public static final Connection DBCON = dbCon;

    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    private static PreparedStatement statement;
    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    private static ResultSet resultSet;
    private static String req;

    public static Connection getDbCon() {
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/epidorge",
                    "root",
                    "2113853211");
        } catch (ClassNotFoundException | SQLException ex) {
            AlertMaker.showInfo("Erreur SQL", ex.getMessage());
        }
        return con;

    }

    public static ObservableList<PieChart.Data> getPersonnelsStat() {
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try {
            String req2 = "SELECT SUM(solde_four) AS credit_four  FROM  fournisseurs";
            String req3 = "SELECT SUM(sold_client) AS credit_client FROM  clients";
            Statement st = DBCON.createStatement();
            ResultSet rs = st.executeQuery(req2);

            if (rs.next()) {
                data.add(new PieChart.Data("Créances Fournisseurs (" + BaseController.customFormat(rs.getDouble(1)) + ")", rs.getDouble("credit_four")));
            }
            rs = st.executeQuery(req3);
            if (rs.next()) {
                data.add(new PieChart.Data("Crédits Clients (" + BaseController.customFormat(rs.getDouble(1)) + ")", rs.getDouble("credit_client")));
            }

        } catch (SQLException ex) {
            System.err.println("Error : " + ex.getMessage() + " " + ex.getSQLState());
        }
        return data;
    }

    public static int addFour(ListFour four) throws SQLException {
        req = "INSERT INTO fournisseurs (raison_sociale_four, adr_four, tel_four,commentaire_four,solde_four) VALUES (UPPER(?), ?, ?,?,?);";
        statement = DBCON.prepareStatement(req);
        statement.setString(1, four.getRs());
        statement.setString(2, four.getAdr());
        statement.setString(3, four.getTel());
        statement.setString(4, four.getCommentaire());
        statement.setDouble(5, four.getSolde());
        return statement.executeUpdate();

    }

    public static int addClient(ListFour four) throws SQLException {
        req = "INSERT INTO clients (raison_sociale_client, adr_client, tel_client,commentaire_client,sold_client) VALUES (UPPER(?), ?, ?,?,?);";
        statement = DBCON.prepareStatement(req);
        statement.setString(1, four.getRs());
        statement.setString(2, four.getAdr());
        statement.setString(3, four.getTel());
        statement.setString(4, four.getCommentaire());
        statement.setDouble(5, four.getSolde());
        return statement.executeUpdate();
    }

    public static int updateClient(ListFour four) throws SQLException {

        req = "UPDATE clients SET raison_sociale_client = UPPER(?), adr_client = ? ,tel_client = ?,commentaire_client = ? ,sold_client = ? WHERE id_client = ?";
        statement = DBCON.prepareStatement(req);
        statement.setString(1, four.getRs());
        statement.setString(2, four.getAdr());
        statement.setString(3, four.getTel());
        statement.setString(4, four.getCommentaire());
        statement.setDouble(5, four.getSolde());
        statement.setInt(6, four.getCpt());
        return statement.executeUpdate();
    }

    public static int updateFour(ListFour four) throws SQLException {

        req = "UPDATE fournisseurs SET raison_sociale_four = UPPER(?), adr_four = ? ,tel_four = ?,commentaire_four = ? ,solde_four = ? WHERE id_four = ?";

        statement = DBCON.prepareStatement(req);
        statement.setString(1, four.getRs());
        statement.setString(2, four.getAdr());
        statement.setString(3, four.getTel());
        statement.setString(4, four.getCommentaire());
        statement.setDouble(5, four.getSolde());
        statement.setInt(6, four.getCpt());
        return statement.executeUpdate();
    }

    public static int updateEmp(ListEmploye four) throws SQLException {

        req = "UPDATE employes SET nom_employe = UPPER(?), adr_employe = ?, date_entrer = ?, commentaire_emp = ?,"
                + " salaire = ? ,tel_employe = ? , type_employe = ? WHERE employes.id_employe = ?";

        statement = DBCON.prepareStatement(req);
        statement.setString(1, four.getRs());
        statement.setString(2, four.getAdr());
        statement.setDate(3, four.getDate());
        statement.setString(4, four.getCommentaire());
        statement.setDouble(5, four.getSolde());
        statement.setString(6, four.getTel());
        statement.setString(7, four.getType());
        statement.setInt(8, four.getId());

        return statement.executeUpdate();
    }

    public static int addEmp(ListEmploye four) throws SQLException {

        req = "INSERT into employes (nom_employe,adr_employe,date_entrer,commentaire_emp,salaire,tel_employe,type_employe ) VALUES (UPPER(?),?,?,?,?,?,?);";

        statement = DBCON.prepareStatement(req);
        statement.setString(1, four.getRs());
        statement.setString(2, four.getAdr());
        statement.setDate(3, four.getDate());
        statement.setString(4, four.getCommentaire());
        statement.setDouble(5, four.getSolde());
        statement.setString(6, four.getTel());
        statement.setString(7, four.getType());
        return statement.executeUpdate();
    }

    public static int addVerssement(ListFour four) throws SQLException {
        req = "";
        if (four.getAdr().equals("four")) {
            req = "UPDATE fournisseurs SET solde_four = ?,date  = ? WHERE raison_sociale_four = ?";
        } else if (four.getAdr().equals("client")) {
            req = "UPDATE clients SET sold_client = ?,date  = ? WHERE raison_sociale_client = ?";
        }

        statement = DBCON.prepareStatement(req);
        statement.setDouble(1, four.getSolde());
        statement.setDate(2, Date.valueOf(LocalDate.now()));
        statement.setString(3, four.getRs());
        return statement.executeUpdate();
    }

    public static int addProd(ListProd prod) throws SQLException {
        req = "INSERT INTO produits (des_prod, prix_achat, prix_vente,unite,qte_stock,fournisseur,commentaire_prod,dateAjout,type) VALUES (UPPER(?), ?, ?,?,?,?,?,?,?);";
        statement = DBCON.prepareStatement(req);
        statement.setString(1, prod.getDes().toUpperCase());
        statement.setDouble(2, prod.getPachat());
        statement.setDouble(3, prod.getPvente());
        statement.setString(4, prod.getUnite());
        statement.setInt(5, prod.getQteStock());
        statement.setString(6, prod.getFourn());
        statement.setString(7, prod.getRem());
        Date now = Date.valueOf(LocalDate.now());
        statement.setDate(8, now);
        statement.setString(9, prod.getType());
        return statement.executeUpdate();

    }

    public static int addUnite(ListUnit prod) throws SQLException {
        req = "INSERT INTO unites (nom_unite, qte_unite) VALUES (UPPER(?), ?);";

        statement = DBCON.prepareStatement(req);
        statement.setString(1, prod.getNom().toUpperCase());
        statement.setInt(2, prod.getVal());
        return statement.executeUpdate();

    }

    public static int updateProd(ListProd prod) throws SQLException {
        req = "UPDATE produits SET des_prod = UPPER(?), prix_achat = ? "
                + ",prix_vente = ?,unite = ? ,qte_stock = ? ,commentaire_prod=? , type = ? ,fournisseur = ? WHERE id_prod = ?";
        statement = DBCON.prepareStatement(req);
        statement.setString(1, prod.getDes().toUpperCase());
        statement.setDouble(2, prod.getPachat());
        statement.setDouble(3, prod.getPvente());
        statement.setString(4, prod.getUnite());
        statement.setInt(5, prod.getQteStock());
        statement.setString(6, prod.getRem());
        statement.setString(7, prod.getType());
        statement.setString(8, prod.getFourn());
        statement.setInt(9, prod.getId());
        return statement.executeUpdate();

    }

    public static int updateUnite(ListUnit prod) throws SQLException {
        req = "UPDATE unites SET nom_unite = UPPER(?), qte_unite = ? WHERE id = ?";
        statement = DBCON.prepareStatement(req);
        statement.setString(1, prod.getNom());
        statement.setInt(2, prod.getVal());
        statement.setInt(3, prod.getId());

        return statement.executeUpdate();

    }

    public static int execDel(String par) throws SQLException {
        int i;
        req = "DELETE FROM fournisseurs WHERE raison_sociale_four = ?";
        statement = DBCON.prepareCall(req);
        statement.setString(1, par);
        i = statement.executeUpdate();

        return i;
    }
    public static int execDelClient(String par) throws SQLException {
        int i;
        req = "DELETE FROM clients WHERE raison_sociale_client = ?";
        statement = DBCON.prepareCall(req);
        statement.setString(1, par);
        i = statement.executeUpdate();

        return i;
    }

    public static int execDelEmp(int par) throws SQLException {
        int i;
        req = "DELETE FROM employes WHERE id_employe = ?";
        statement = DBCON.prepareCall(req);
        statement.setInt(1, par);
        i = statement.executeUpdate();

        return i;
    }

    public static int execDelProd(int par) throws SQLException {
        int i;
        req = "DELETE FROM produits WHERE id_prod = ?";
        statement = DBCON.prepareStatement(req);
        statement.setInt(1, par);
        i = statement.executeUpdate();
        return i;
    }
    

    public static int execDelUnit(int par) throws SQLException {
        int i;
        req = "DELETE FROM unites WHERE id= ?";
        statement = DBCON.prepareStatement(req);
        statement.setInt(1, par);
        i = statement.executeUpdate();
        return i;
    }

    private static void closeAll() throws SQLException {
        resultSet.close();
        statement.close();
        DBCON.close();
    }

    public static ObservableList<XYChart.Series<String, Number>> createContent() {
        ObservableList<String> dataAxeX = FXCollections.observableArrayList();
        ObservableList<Number> dataEntre = FXCollections.observableArrayList();
        ObservableList<String> dataDes2 = FXCollections.observableArrayList();
        ObservableList<Number> dataSortie = FXCollections.observableArrayList();

        ObservableList<XYChart.Series<String, Number>> ser = FXCollections.observableArrayList();

        XYChart.Series<String, Number> serieEntre = new XYChart.Series<>();
        XYChart.Series<String, Number> seriesSortie = new XYChart.Series<>();
        seriesSortie.setName("Qte vendue");
        serieEntre.setName("Quantité acheté");

        try {
            statement = DatabaseUtil.DBCON.prepareStatement("SELECT des_prod,SUM(qte) AS qteEntre FROM entreproduit GROUP BY des_prod ");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dataAxeX.add(resultSet.getString("des_prod"));
                dataEntre.add(resultSet.getInt("qteEntre"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        int c = 0;
        for (String dataX : dataAxeX) {
            serieEntre.getData().add(new XYChart.Data<>(dataX, dataEntre.get(c++)));
        }

        try {
            statement = DatabaseUtil.DBCON.prepareStatement("SELECT des_prod,SUM(qte) AS qteEntre FROM sortieProduit GROUP BY des_prod ");
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                dataDes2.add(resultSet.getString("des_prod"));
                dataSortie.add(resultSet.getInt("qteEntre"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        c = 0;
        for (String dataDe2 : dataDes2) {
            seriesSortie.getData().add(new XYChart.Data<>(dataDe2, dataSortie.get(c++)));
        }
        ser.add(serieEntre);
        ser.add(seriesSortie);
        return ser;
    }

    public static void main(String[] args) {

    }
}
