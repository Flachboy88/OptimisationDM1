import solver.BranchAndBoundSolver;
import solver.Solution;
import utilitaire.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

public class Main {

    public static void afficherProbleme(ProblemData data){

        List<Culture> cultures = data.getCultures();
        List<Parcelle> parcelles = data.getParcelles();
        InteractionMatrix interactions = data.getInteractions();

        System.out.println("=== DONNEES DU PROBLEME ===");
        System.out.println("=== CULTURES ===");
        for (Culture c : cultures) {
            System.out.println(
                    c.getNom() +
                            " | cout=" + c.getCoutHa() +
                            " | benef=" + c.getBeneficeHa());
        }


        System.out.println("\n=== PARCELLES ===");
        for (Parcelle p : parcelles) {
            System.out.println(
                    "Parcelle " + p.getId() +
                            " | surface=" + p.getSurface() +
                            " | voisins=" + p.getVoisins());
        }


        System.out.println("\n=== INTERACTIONS ===");

        System.out.println("Effet B -> C : " + interactions.getEffet("B","C"));
        System.out.println("Effet C -> A : " + interactions.getEffet("C","A"));
        System.out.println("Effet E -> D : " + interactions.getEffet("E","D"));



    }
    public static int calculerBudgetMinimale(ProblemData data){
        double budgetMinimal = 0;
        for (Parcelle p : data.getParcelles()) {
            double minCout = Double.MAX_VALUE;
            for (Culture c : data.getCultures()) {
                minCout = Math.min(minCout, p.getSurface() * c.getCoutHa());
            }
            budgetMinimal += minCout;
        }

        System.out.println("Budget minimal requis : " + budgetMinimal);
        return (int)budgetMinimal;
    }


    public static void main(String[] args) throws FileNotFoundException {

        ProblemData data = DataLoader.loadProblem();
        int budget = 700;
        afficherProbleme(data);
        int budgetMinimal = calculerBudgetMinimale(data); // avec nos données ca fait 350
        if (budget < budgetMinimal) {
            System.out.println("Budget insuffisant");
            return;
        }

        BranchAndBoundSolver solver = new BranchAndBoundSolver(data, budget);
        Solution solution = solver.solve();

        System.out.println("Profit max : " + solution.getProfit());
        System.out.println(solution.getAssignment());

    }
}