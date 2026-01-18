package servlet.dto;

public class OfFilleRequestDTO {
    private String idIngredients;
    private String remarque;
    private String libelle;
    private String idunite;
    private double qte;
    private String idBcFille;

    // Getters et setters
    public String getIdIngredients() { return idIngredients; }
    public void setIdIngredients(String idIngredients) { this.idIngredients = idIngredients; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getIdunite() { return idunite; }
    public void setIdunite(String idunite) { this.idunite = idunite; }

    public double getQte() { return qte; }
    public void setQte(double qte) { this.qte = qte; }

    public String getIdBcFille() { return idBcFille; }
    public void setIdBcFille(String idBcFille) { this.idBcFille = idBcFille; }
}
