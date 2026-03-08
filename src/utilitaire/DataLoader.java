package utilitaire;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLoader {

    public static List<Culture> loadCultures(String path) {
        List<Culture> cultures = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String line = br.readLine(); // passe l'entete

            while ((line = br.readLine()) != null) {
                String[] parts = line.replace("\"","").split("\t");

                String nom = parts[0];
                double cout = Double.parseDouble(parts[1]);
                double benefice = Double.parseDouble(parts[2]);

                cultures.add(new Culture(nom, cout, benefice));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return cultures;
    }
    public static List<Parcelle> loadParcelles(String path) {

        List<Parcelle> parcelles = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String line1 = br.readLine();
            String line2 = br.readLine();
            String line3 = br.readLine();

            String[] ids = line1.replace("\"","").split("\t");
            String[] surfaces = line2.replace("\"","").split("\t");
            String[] voisins = line3.replace("\"","").split("\t");

            for (int i = 1; i < ids.length; i++) {

                int id = Integer.parseInt(ids[i]);
                double surface = Double.parseDouble(surfaces[i]);

                List<Integer> voisinsList = new ArrayList<>();

                if (!voisins[i].isEmpty()) {
                    String[] v = voisins[i].split(",");
                    for (String s : v) {
                        voisinsList.add(Integer.parseInt(s.trim()));
                    }
                }

                parcelles.add(new Parcelle(id, surface, voisinsList));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return parcelles;
    }

    public static InteractionMatrix loadInteractions(String path) {

        Map<String, Map<String, Double>> effets = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {

            String header = br.readLine();
            String[] cultures = header.replace("\"","").split("\t");

            String line;

            while ((line = br.readLine()) != null) {

                String[] parts = line.replace("\"","").split("\t");

                String cultureVoisine = parts[0];

                Map<String, Double> map = new HashMap<>();

                for (int i = 1; i < parts.length; i++) {

                    String cultureCentrale = cultures[i];
                    double effet = Double.parseDouble(parts[i]) / 100.0;

                    map.put(cultureCentrale, effet);
                }

                effets.put(cultureVoisine, map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new InteractionMatrix(effets);
    }

    private static String getResourcePath(String fileName) {
        try {
            return Paths.get(
                    DataLoader.class
                            .getClassLoader()
                            .getResource(fileName)
                            .toURI()
            ).toString();
        } catch (Exception e) {
            throw new RuntimeException("Impossible de charger la ressource : " + fileName, e);
        }
    }

    public static List<Culture> loadCultures() {
        return loadCultures(getResourcePath("cultures.csv"));
    }

    public static List<Parcelle> loadParcelles() {
        return loadParcelles(getResourcePath("parcelles.csv"));
    }

    public static InteractionMatrix loadInteractions() {
        return loadInteractions(getResourcePath("interactions.csv"));
    }

    public static ProblemData loadProblem() {
        return new ProblemData(
                loadCultures(),
                loadParcelles(),
                loadInteractions()
        );
    }

}

