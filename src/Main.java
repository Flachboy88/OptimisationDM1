import utilitaire.*;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ProblemData data = DataLoader.loadProblem();
        List<Culture> cultures = data.getCultures();
        List<Parcelle> parcelles = data.getParcelles();
        InteractionMatrix interactions = data.getInteractions();

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


        System.out.println("\n=== TEST INTERACTIONS ===");

        System.out.println("Effet B -> C : " + interactions.getEffet("B","C"));
        System.out.println("Effet C -> A : " + interactions.getEffet("C","A"));
        System.out.println("Effet E -> D : " + interactions.getEffet("E","D"));


    }
}