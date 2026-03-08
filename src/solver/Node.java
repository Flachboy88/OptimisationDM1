package solver;

import java.util.Map;

public class Node {

    private Map<Integer, String> assignment; // parcelle avec culture
    private int nextParcel;                  // prochaine parcelle à assigner
    private double currentProfit;
    private double currentCost;

    public Node(Map<Integer, String> assignment,
                int nextParcel,
                double currentProfit,
                double currentCost) {

        this.assignment = assignment;
        this.nextParcel = nextParcel;
        this.currentProfit = currentProfit;
        this.currentCost = currentCost;
    }

    public Map<Integer, String> getAssignment() {
        return assignment;
    }

    public int getNextParcel() {
        return nextParcel;
    }

    public double getCurrentProfit() {
        return currentProfit;
    }

    public double getCurrentCost() {
        return currentCost;
    }
}