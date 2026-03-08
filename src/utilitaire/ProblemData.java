package utilitaire;

import java.util.ArrayList;
import java.util.List;

public class ProblemData {

    private List<Culture> cultures;
    private List<Parcelle> parcelles;
    private InteractionMatrix interactions;

    public ProblemData(List<Culture> cultures,
                       List<Parcelle> parcelles,
                       InteractionMatrix interactions) {
        this.cultures = cultures;
        this.parcelles = parcelles;
        this.interactions = interactions;
    }

    public List<Culture> getCultures() {
        return cultures;
    }

    public List<Parcelle> getParcelles() {
        return parcelles;
    }

    public InteractionMatrix getInteractions() {
        return interactions;
    }

    // copie triée du csv
    public List<Parcelle> getParcellesSortedByNeighbors() {
        List<Parcelle> copy = new ArrayList<>(parcelles); // copie de la liste
        copy.sort((p1, p2) -> Integer.compare(p2.getVoisins().size(), p1.getVoisins().size())); // décroissant
        return copy;
    }
}