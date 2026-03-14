package solver;

import utilitaire.*;
import java.util.List;
import java.util.Map;

public class Heuristic {

    // calcule une borne supérieure optimiste du profit total atteignable depuis ce nœud
    // pour les voisins non encore assignés, on suppose le meilleur facteur possible (1.5, soit E->D)
    // cela garantit que la borne est toujours >= au vrai optimum, donc on ne coupe jamais une bonne branche
    public static double estimateUpperBound(Node node, ProblemData data, double budget, List<Parcelle> parcelles) {
        double bound = 0;
        Map<Integer, String> currentAssignment = node.getAssignment();
        double budgetRestant = budget - node.getCurrentCost();

        // recalcul optimiste des parcelles déjà fixées :
        // leurs voisins non encore assignés sont supposés avoir le meilleur effet possible
        for (Map.Entry<Integer, String> entry : currentAssignment.entrySet()) {
            int pId = entry.getKey();
            String cultNom = entry.getValue();

            Parcelle p = parcelles.stream().filter(pa -> pa.getId() == pId).findFirst().get();
            Culture c = data.getCultures().stream().filter(cu -> cu.getNom().equals(cultNom)).findFirst().get();

            double profitBase = p.getSurface() * c.getBeneficeHa();
            double multiplicateurOptimiste = 1.0;

            for (int voisinId : p.getVoisins()) {
                if (currentAssignment.containsKey(voisinId)) {
                    // voisin déjà assigné : on prend le vrai facteur
                    multiplicateurOptimiste *= data.getInteractions().getEffet(currentAssignment.get(voisinId), cultNom);
                } else {
                    // voisin pas encore assigné : on suppose le meilleur bonus possible (1.5)
                    multiplicateurOptimiste *= 1.5;
                }
            }
            bound += (profitBase * multiplicateurOptimiste);
        }

        // estimation optimiste des parcelles restantes (non encore assignées)
        for (Parcelle p : parcelles) {
            if (currentAssignment.containsKey(p.getId())) continue;

            // on prend la culture qui maximise le profit potentiel de cette parcelle
            double maxProfitP = 0;
            for (Culture c : data.getCultures()) {
                if (p.getSurface() * c.getCoutHa() > budgetRestant) continue;

                double multiP = 1.0;
                for (int vId : p.getVoisins()) {
                    if (currentAssignment.containsKey(vId)) {
                        multiP *= data.getInteractions().getEffet(currentAssignment.get(vId), c.getNom());
                    } else {
                        multiP *= 1.5; // voisin inconnu : on est optimiste
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