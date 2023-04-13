package model;

import java.sql.Date;

/**
 *
 * @author sof
 */
public class ListStatVente {
   
    private int qteStock;
    private Date date;
    private double total;

    public ListStatVente(int qteStock, Date date, double total) {
        this.qteStock = qteStock;
        this.date = date;
        this.total = total;
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
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
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