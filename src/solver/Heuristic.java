package solver;

import utilitaire.*;

import java.util.List;

public class Heuristic {

    public static double estimateUpperBound(Node node, ProblemData data, double budget, List<Parcelle> parcelles) {
        double bound = node.getCurrentProfit();
        double coutCumule = node.getCurrentCost();

        // Trouver le meilleur bénéfice/coût ratio parmi toutes les cultures
        for (Parcelle p : parcelles) {
            if (node.getAssignment().containsKey(p.getId())) continue;

            double best = 0;
            double bestCout = 0;

            for (Culture c : data.getCultures()) {
                if (coutCumule + p.getSurface() * c.getCoutHa() > budget) continue;

                // Borne optimiste : on prend le facteur d'interaction MAX possible (1.5)
                // D'après la table, le facteur max qu'une culture peut recevoir d'un voisin est 1.5
                double facteurMax = 1.0;
                for (int voisinId : p.getVoisins()) {
                    // si voisin déjà assigné, on prend le vrai facteur
                    String cv = node.getAssignment().get(voisinId);
                    if (cv != null) {
                        facteurMax *= data.getInteractions().getEffet(cv, c.getNom());
                    } else {
                        // voisin non assigné : on suppose le meilleur facteur possible (1.5)
                        facteurMax *= 1.5;
                    }
                }

                double profit = p.getSurface() * c.getBeneficeHa() * facteurMax;
                if (profit > best) {
                    best = profit;
                    bestCout = p.getSurface() * c.getCoutHa();
                }
            }

            coutCumule += bestCout;
            bound += best;
        }
        return bound;
    }
}