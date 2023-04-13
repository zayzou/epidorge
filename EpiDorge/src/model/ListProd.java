package model;

/**
 *
 * @author sof
 */
public class ListProd {

    private int id;
    private String des;
    private double pachat;
    private double pvente;
    private int qteStock;
    private String rem;
    private String unite;
    private String type;
    private String fourn;
    public ListProd(int id, String des, double pachat, double pvente, int qteStock, String rem, String unite, String type, String fourn) {
        this.id = id;
        this.des = des;
        this.pachat = pachat;
        this.pvente = pvente;
        this.qteStock = qteStock;
        this.rem = rem;
        this.unite = unite;
        this.type = type;
        this.fourn = fourn;
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
     * @return the rem
     */
    public String getRem() {
        return rem;
    }

    /**
     * @param rem the rem to set
     */
    public void setRem(String rem) {
        this.rem = rem;
    }

    /**
     * @return the unite
     */
    public String getUnite() {
        return unite;
    }

    /**
     * @param unite the unite to set
     */
    public void setUnite(String unite) {
        this.unite = unite;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the fourn
     */
    public String getFourn() {
        return fourn;
    }

    /**
     * @param fourn the fourn to set
     */
    public void setFourn(String fourn) {
        this.fourn = fourn;
    }

}
