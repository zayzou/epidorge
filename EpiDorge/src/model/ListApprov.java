package model;

/**
 *
 * @author sof
 */
public class ListApprov {
    private int id;
    private String des;
    private double pachat;
    private int qteStock;
    private double total;
    

    public ListApprov(int id, String des, double pachat, int qteStock, double total) {
        this.id = id;
        this.des = des;
        this.pachat = pachat;
        this.qteStock = qteStock;
        this.total = total;
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

}

    

