package model;

/**
 *
 * @author sof
 */
public class ListApprov {

    private int id;
    private String des;
    private int idFournisseur;
    private String numFacture;
    private double pachat;
    private int qteStock;
    private double total;
    private String date;

    public ListApprov(int id, String des, String numFacture, double pachat, int qteStock, double total, String date, int idFournisseur) {
        this.id = id;
        this.des = des;
        this.pachat = pachat;
        this.qteStock = qteStock;
        this.total = total;
        this.numFacture = numFacture;
        this.date = date;
        this.idFournisseur = idFournisseur;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the des
     */
    public String getDes() {
        return des;
    }

    /**
     * @param des the des to set
     */
    public void setDes(String des) {
        this.des = des;
    }

    /**
     * @return the pachat
     */
    public double getPachat() {
        return pachat;
    }

    /**
     * @param pachat the pachat to set
     */
    public void setPachat(double pachat) {
        this.pachat = pachat;
    }

    /**
     * @return the qteStock
     */
    public int getQteStock() {
        return qteStock;
    }

    /**
     * @param qteStock the qteStock to set
     */
    public void setQteStock(int qteStock) {
        this.qteStock = qteStock;
    }

    /**
     * @return the total
     */
    public double getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(double total) {
        this.total = total;
    }

    /**
     * @return the numFacture
     */
    public String getNumFacture() {
        return numFacture;
    }

    /**
     * @param numFacture the numFacture to set
     */
    public void setNumFacture(String numFacture) {
        this.numFacture = numFacture;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the idFournisseur
     */
    public int getIdFournisseur() {
        return idFournisseur;
    }

    /**
     * @param idFournisseur the idFournisseur to set
     */
    public void setIdFournisseur(int idFournisseur) {
        this.idFournisseur = idFournisseur;
    }

}
