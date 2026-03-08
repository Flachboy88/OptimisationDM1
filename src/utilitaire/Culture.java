package utilitaire;

public class Culture {

    private String nom;
    private double coutHa;
    private double beneficeHa;

    public Culture(String nom, double coutHa, double beneficeHa) {
        this.nom = nom;
        this.coutHa = coutHa;
        this.beneficeHa = beneficeHa;
    }

    public String getNom() {
        return nom;
    }

    public double getCoutHa() {
        return coutHa;
    }

    public double getBeneficeHa() {
        return beneficeHa;
    }
}