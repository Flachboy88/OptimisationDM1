package utilitaire;
import java.util.List;

public class Parcelle {

    private int id;
    private double surface;
    private List<Integer> voisins;

    public Parcelle(int id, double surface, List<Integer> voisins) {
        this.id = id;
        this.surface = surface;
        this.voisins = voisins;
    }

    public int getId() {
        return id;
    }

    public double getSurface() {
        return surface;
    }

    public List<Integer> getVoisins() {
        return voisins;
    }
}