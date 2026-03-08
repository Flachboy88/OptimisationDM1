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

        Node root = new Node(new HashMap<>(), 1, 0, 0);

        explore(root);

        return bestSolution;
    }

    private void explore(Node node) {

        if (node.getNextParcel() > data.getParcelles().size()) {

            if (bestSolution == null || node.getCurrentProfit() > bestSolution.getProfit()) {
                bestSolution = new Solution(node.getAssignment(), node.getCurrentProfit());
            }
            return;
        }

        Parcelle p = data.getParcelles().get(node.getNextParcel() - 1);

        for (Culture c : data.getCultures()) {

            double cost = node.getCurrentCost() + p.getSurface() * c.getCoutHa();

            // Couper la branche si budget dépassé
            if (cost > budget) {
                System.out.println("Branch coupée : Parcelle " + p.getId() +
                        " avec culture " + c.getNom() +
                        " | coût=" + cost +
                        " > budget=" + budget);
                continue;
            }

            Map<Integer, String> newAssign = new HashMap<>(node.getAssignment());
            newAssign.put(p.getId(), c.getNom());

            double profit = node.getCurrentProfit() + p.getSurface() * c.getBeneficeHa();

            // interactions avec voisins déjà assignés
            for (int voisinId : p.getVoisins()) {
                String cultureVoisin = node.getAssignment().get(voisinId);
                if (cultureVoisin != null) {
                    double facteur = data.getInteractions().getEffet(cultureVoisin, c.getNom());
                    profit += p.getSurface() * c.getBeneficeHa() * (facteur - 1);
                }
            }

            // Calcul de la borne supérieure
            double bound = Heuristic.estimateUpperBound(new Node(newAssign, node.getNextParcel() + 1, profit, cost), data);

            if (bestSolution != null && bound <= bestSolution.getProfit()) {
                System.out.println("Branch coupée (borne trop faible) : Parcelle " + p.getId() +
                        " avec culture " + c.getNom() +
                        " | profit courant=" + profit +
                        " | borne=" + bound +
                        " | meilleur trouvé=" + bestSolution.getProfit());
                continue;
            }

            Node child = new Node(newAssign, node.getNextParcel() + 1, profit, cost);
            System.out.println("Développement : Parcelle " + p.getId() +
                    " assignée à " + c.getNom() +
                    " | profit=" + profit +
                    " | coût=" + cost);

            explore(child);
        }
    }
}