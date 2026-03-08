package solver;

import utilitaire.*;

import java.util.*;

public class BranchAndBoundSolver {

    private ProblemData data;
    private double budget;

    private Solution bestSolution = null;

    public BranchAndBoundSolver(ProblemData data, double budget) {
        this.data = data;
        this.budget = budget;
    }

    public Solution solve() {

        // récupérer la copie triée (ne touche pas au CSV)
        List<Parcelle> parcellesTriees = data.getParcellesSortedByNeighbors();

        Node root = new Node(new HashMap<>(), 1, 0, 0);

        explore(root, parcellesTriees);

        return bestSolution;
    }

    private void explore(Node node, List<Parcelle> parcelles) {

        if (node.getNextParcel() > parcelles.size()) {
            double profitReel = calculerProfitTotal(node.getAssignment(), parcelles);
            if (bestSolution == null || profitReel > bestSolution.getProfit()) {
                bestSolution = new Solution(node.getAssignment(), profitReel);
                System.out.println("Nouvelle meilleure solution : profit=" + profitReel);
            }
            return;
        }

        Parcelle p = parcelles.get(node.getNextParcel() - 1);

        // trier les cultures par bénéfice décroissant
        List<Culture> cultures = new ArrayList<>(data.getCultures());
        cultures.sort((c1, c2) -> Double.compare(c2.getBeneficeHa(), c1.getBeneficeHa()));

        for (Culture c : cultures) {
            double cost = node.getCurrentCost() + p.getSurface() * c.getCoutHa();

            if (cost > budget) {
                System.out.println("Branch coupée : Parcelle " + p.getId() +
                        " avec culture " + c.getNom() +
                        " | coût=" + cost +
                        " > budget=" + budget);
                continue;
            }

            Map<Integer, String> newAssign = new HashMap<>(node.getAssignment());
            newAssign.put(p.getId(), c.getNom());

            double profitReel = calculerProfitTotal(newAssign, parcelles);
            double bound = Heuristic.estimateUpperBound(
                    new Node(newAssign, node.getNextParcel() + 1, profitReel, cost), data, budget, parcelles);


            if (bestSolution != null && bound <= bestSolution.getProfit()) {
                System.out.println("Branch coupée (borne trop faible) : Parcelle " + p.getId() +
                        " avec culture " + c.getNom() +
                        " | profit courant=" + profitReel  +
                        " | borne=" + bound +
                        " | meilleur trouvé=" + bestSolution.getProfit());
                continue;
            }

            Node child = new Node(newAssign, node.getNextParcel() + 1, profitReel, cost);
            System.out.println("Développement : Parcelle " + p.getId() +
                    " assignée à " + c.getNom() +
                    " | profit=" + profitReel +    // <-- profit partiel affiché
                    " | coût=" + cost);

            // récursion
            explore(child, parcelles);
        }
    }

    private double calculerProfitTotal(Map<Integer, String> assignment, List<Parcelle> parcelles) {
        double total = 0;
        for (Parcelle p : parcelles) {
            String c = assignment.get(p.getId());
            if (c == null) continue; // parcelle pas encore assignée
            double base = p.getSurface() * data.getCultures().stream()
                    .filter(cu -> cu.getNom().equals(c)).findFirst().orElseThrow().getBeneficeHa();
            double facteur = 1.0;
            for (int voisinId : p.getVoisins()) {
                String cv = assignment.get(voisinId);
                if (cv != null) facteur *= data.getInteractions().getEffet(cv, c);
            }
            total += base * facteur;
        }
        return total;
    }
}