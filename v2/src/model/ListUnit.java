/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author sof
 */
public class ListUnit {
    
    private String nom;
    private int val;
    private int id;

    public ListUnit(String nom, int val, int id) {
        this.nom = nom;
        this.val = val;
        this.id = id;
    }
   

    /**
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * @param nom the nom to set
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return the val
     */
    public int getVal() {
        return val;
    }

    /**
     * @param val the val to set
     */
    public void setVal(int val) {
        this.val = val;
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
    
    
    
}
