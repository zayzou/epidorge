package model;

/**
 *
 * @author sof
 */
public class ListVente {
    private int id;
    private String des;
    private double pvente;
    private int qteStock;
    private double total;
    private String vendeur;

    public ListVente(int id, String des, double pvente, int qteStock, double total,String vendeur) {
        this.id = id;
        this.des = des;
        this.pvente = pvente;
        this.qteStock = qteStock;
        this.total = total;
        this.vendeur = vendeur;
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
     * @return the pvente
     */
    public double getPvente() {
        return pvente;
    }

    /**
     * @param pvente the pvente to set
     */
    public void setPvente(double pvente) {
        this.pvente = pvente;
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
     * @return the vendeur
     */
    public String getVendeur() {
        return vendeur;
    }

    /**
     * @param vendeur the vendeur to set
     */
    public void setVendeur(String vendeur) {
        this.vendeur = vendeur;
    }

}

    

