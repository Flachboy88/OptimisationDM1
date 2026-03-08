package utilitaire;

import java.util.Map;

public class InteractionMatrix {

    private Map<String, Map<String, Double>> effets;

    public InteractionMatrix(Map<String, Map<String, Double>> effets) {
        this.effets = effets;
    }

    public double getEffet(String voisin, String centrale) {
        return effets.get(voisin).get(centrale);
    }
    // exemples:
    // effet("B","C") = 0.9
    //effet("C","A") = 1.2
}