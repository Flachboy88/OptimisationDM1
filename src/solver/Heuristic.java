package solver;

import utilitaire.*;

public class Heuristic {

    public static double estimateUpperBound(Node node, ProblemData data) {

        double bound = node.getCurrentProfit();

        for (Parcelle p : data.getParcelles()) {

            // ignorer les parcelles déjà assignées
            if (node.getAssignment().containsKey(p.getId())) continue;

            double best = 0;

            for (Culture c : data.getCultures()) {

                // profit de base
                double profit = p.getSurface() * c.getBeneficeHa();

                // interactions avec voisins déjà assignés
                for (int voisinId : p.getVoisins()) {
                    String cultureVoisin = node.getAssignment().get(voisinId);
                    if (cultureVoisin != null) {
                        double facteur = data.getInteractions().getEffet(cultureVoisin, c.getNom());
                        profit += p.getSurface() * c.getBeneficeHa() * (facteur - 1); // -1 pour pas prendre en compte 2 fois la meme parcele
                    }
                }

                best = Math.max(best, profit);
            }

            bound += best;
        }

        return bound;
    }
}