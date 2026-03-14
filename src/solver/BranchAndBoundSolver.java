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

        // récupérer la copie triée (sans touché au csv)
        List<Parcelle> parcellesTriees = data.getParcellesSortedByNeighbors();

        Node root = new Node(new HashMap<>(), 1, 0, 0);

        explore(root, parcellesTriees);

        return bestSolution;
    }

    private void explore(Node node, List<Parcelle> parcelles) {

        // cas de base : toutes les parcelles ont été assignées, on évalue la solution complète
        if (node.getNextParcel() > parcelles.size()) {
            double profitReel = calculerProfitTotal(node.getAssignment(), parcelles);
            if (bestSolution == null || profitReel > bestSolution.getProfit()) {
                bestSolution = new Solution(node.getAssignment(), profitReel);
                System.out.println("Nouvelle meilleure solution : profit=" + profitReel);
            }
            return;
        }

        Parcelle p = parcelles.get(node.getNextParcel() - 1);

        // on explore les cultures dans l'ordre décroissant de bénéfice pour trouver rapidement une bonne solution
        List<Culture> cultures = new ArrayList<>(data.getCultures());
        cultures.sort((c1, c2) -> Double.compare(c2.getBeneficeHa(), c1.getBeneficeHa()));

        for (Culture c : cultures) {
            double cost = node.getCurrentCost() + p.getSurface() * c.getCoutHa();

            // coupe par contrainte de budget
            if (cost > budget) {
                System.out.println("Branch coupée : Parcelle " + p.getId() +
                        " avec culture " + c.getNom() +
                        " | coût=" + cost +
                        " > budget=" + budget);
                continue;
            }

            Map<Integer, String> newAssign = new HashMap<>(node.getAssignment());
            newAssign.put(p.getId(), c.getNom());

            // profit réel des parcelles déjà assignées (avec interactions multiplicatives)
            double profitReel = calculerProfitTotal(newAssign, parcelles);

            // calcul de la borne supérieure : si elle ne dépasse pas le meilleur connu, on coupe
            double bound = Heuristic.estimateUpperBound(
                    new Node(newAssign, node.getNextParcel() + 1, profitReel, cost), data, budget, parcelles);

            if (bestSolution != null && bound <= bestSolution.getProfit()) {
                System.out.println("Branch coupée (borne trop faible) : Parcelle " + p.getId() +
                        " avec culture " + c.getNom() +
                        " | profit courant=" + profitReel +
                        " | borne=" + bound +
                        " | meilleur trouvé=" + bestSolution.getProfit());
                continue;
            }

            Node child = new Node(newAssign, node.getNextParcel() + 1, profitReel, cost);
            System.out.println("Développement : Parcelle " + p.getId() +
                    " assignée à " + c.getNom() +
                    " | profit=" + profitReel +
                    " | coût=" + cost);

            explore(child, parcelles);
        }
    }

    // calcule le profit réel des parcelles déjà assignées dans l'assignment
    // les interactions sont multiplicatives : chaque voisin assigné multiplie le bénéfice de base
    private double calculerProfitTotal(Map<Integer, String> assignment, List<Parcelle> parcelles) {
        double total = 0;

        for (Parcelle p : parcelles) {
            String cultureCentraleNom = assignment.get(p.getId());
            if (cultureCentraleNom == null) continue; // parcelle pas encore assignée, on skip

            Culture cultureCentrale = data.getCultures().stream()
                    .filter(c -> c.getNom().equals(cultureCentraleNom))
                    .findFirst().get();

            double profitParcelle = p.getSurface() * cultureCentrale.getBeneficeHa();

            // on cumule les facteurs de tous les voisins déjà assignés (multiplication)
            double facteurVoisinage = 1.0;
            for (int voisinId : p.getVoisins()) {
                String cultureVoisineNom = assignment.get(voisinId);
                if (cultureVoisineNom != null) {
                    facteurVoisinage *= data.getInteractions().getEffet(cultureVoisineNom, cultureCentraleNom);
                }
            }

            total += (profitParcelle * facteurVoisinage);
        }
        return total;
    }
}