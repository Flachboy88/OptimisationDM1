package utilitaire;

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
}