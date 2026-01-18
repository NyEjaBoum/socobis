// FabricationFilleRequestDTO.java
package servlet.dto;

public class FabricationFilleRequestDTO {
    private String idIngredients;
    private String remarque;
    private String libelle;
    private String idunite;
    private Double qte;
    private String idBcFille;
    private String idMachine;
    private String operateur;
    private Integer niveau;
    private Double pu;

    // Getters et Setters
    public String getIdIngredients() { return idIngredients; }
    public void setIdIngredients(String idIngredients) { this.idIngredients = idIngredients; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getIdunite() { return idunite; }
    public void setIdunite(String idunite) { this.idunite = idunite; }

    public Double getQte() { return qte; }
    public void setQte(Double qte) { this.qte = qte; }

    public String getIdBcFille() { return idBcFille; }
    public void setIdBcFille(String idBcFille) { this.idBcFille = idBcFille; }

    public String getIdMachine() { return idMachine; }
    public void setIdMachine(String idMachine) { this.idMachine = idMachine; }

    public String getOperateur() { return operateur; }
    public void setOperateur(String operateur) { this.operateur = operateur; }

    public Integer getNiveau() { return niveau; }
    public void setNiveau(Integer niveau) { this.niveau = niveau; }

    public Double getPu() { return pu; }
    public void setPu(Double pu) { this.pu = pu; }
}