package solver;

import utilitaire.*;
import java.util.List;
import java.util.Map;

public class Heuristic {

    public static double estimateUpperBound(Node node, ProblemData data, double budget, List<Parcelle> parcelles) {
        double bound = 0;
        Map<Integer, String> currentAssignment = node.getAssignment();
        double budgetRestant = budget - node.getCurrentCost();

        // 1. On recalcule le profit des parcelles DÉJÀ FIXÉES
        // Mais en étant optimiste sur leurs voisins non encore assignés
        for (Map.Entry<Integer, String> entry : currentAssignment.entrySet()) {
            int pId = entry.getKey();
            String cultNom = entry.getValue();

            Parcelle p = parcelles.stream().filter(pa -> pa.getId() == pId).findFirst().get();
            Culture c = data.getCultures().stream().filter(cu -> cu.getNom().equals(cultNom)).findFirst().get();

            double profitBase = p.getSurface() * c.getBeneficeHa();
            double multiplicateurOptimiste = 1.0;

            for (int voisinId : p.getVoisins()) {
                if (currentAssignment.containsKey(voisinId)) {
                    // Voisin déjà là : on prend le vrai coefficient
                    multiplicateurOptimiste *= data.getInteractions().getEffet(currentAssignment.get(voisinId), cultNom);
                } else {
                    // Voisin pas encore là : on suppose le meilleur bonus possible (1.5 pour E->D)
                    multiplicateurOptimiste *= 1.5;
                }
            }
            bound += (profitBase * multiplicateurOptimiste);
        }

        // 2. On estime le profit des parcelles RESTANTES (non assignées)
        for (Parcelle p : parcelles) {
            if (currentAssignment.containsKey(p.getId())) continue;

            double maxProfitP = 0;
            for (Culture c : data.getCultures()) {
                // On vérifie si on a encore assez de budget théorique
                if (p.getSurface() * c.getCoutHa() > budgetRestant) continue;

                double multiP = 1.0;
                for (int vId : p.getVoisins()) {
                    if (currentAssignment.containsKey(vId)) {
                        multiP *= data.getInteractions().getEffet(currentAssignment.get(vId), c.getNom());
                    } else {
                        multiP *= 1.5; // On est très optimiste
                    }
                }

                double profitPotentiel = p.getSurface() * c.getBeneficeHa() * multiP;
                if (profitPotentiel > maxProfitP) maxProfitP = profitPotentiel;
            }
            bound += maxProfitP;
        }

        return bound;
    }
}