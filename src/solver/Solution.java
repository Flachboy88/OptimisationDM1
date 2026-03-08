package solver;

import java.util.Map;

public class Solution {

    private Map<Integer,String> assignment;
    private double profit;

    public Solution(Map<Integer,String> assignment, double profit) {
        this.assignment = assignment;
        this.profit = profit;
    }

    public Map<Integer,String> getAssignment() {
        return assignment;
    }

    public double getProfit() {
        return profit;
    }
}