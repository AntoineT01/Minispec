package generated.java;

import java.util.*;

public class Satellite {
    private String nom;
    private Integer id;
    private Flotte parent;

    public Satellite() {}

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Flotte getParent() {
        return parent;
    }

    public void setParent(Flotte parent) {
        this.parent = parent;
    }
}
