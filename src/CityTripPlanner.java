import java.util.*;

public class CityTripPlanner {
    static class City {
        String name;
        int visitCost;
        int reward;

        City(String name, int visitCost, int reward) {
            this.name = name;
            this.visitCost = visitCost;
            this.reward = reward;
        }
    }

    static class Connection {
        String from;
        String to;
        int travelCost;

        Connection(String from, String to, int travelCost) {
            this.from = from;
            this.to = to;
            this.travelCost = travelCost;
        }
    }

    // Graph representation (adjacency list)
    //weighted graph
    private final Map<String, List<Connection>> graph = new HashMap<>();
    private final Map<String, City> cityInfo = new HashMap<>();
    private final Map<String, Map<Integer, Integer>> memo = new HashMap<>();

    // Add city to the city information map
    public void addCity(City city) {
        cityInfo.put(city.name, city); // Store city details
    }

    // Add connection to the graph
    public void addConnection(Connection connection) {
        // Ensure the city exists in the graph and add the connection
        graph.computeIfAbsent(connection.from, k -> new ArrayList<>()).add(connection);
    }

    // Recursive function with memoization to calculate maximum reward
    private int dp(String currentCity, int timeRemaining) {
        // Base case: Negative time means invalid state
        if (timeRemaining < 0) return Integer.MIN_VALUE;
        // Base case: No further connections means no additional reward
        if (!graph.containsKey(currentCity)) return 0;

        // Check if the result is already memoized
        if (memo.containsKey(currentCity) && memo.get(currentCity).containsKey(timeRemaining)) {
            return memo.get(currentCity).get(timeRemaining);
        }

        // Get current city details
        City city = cityInfo.get(currentCity);
        if (timeRemaining < city.visitCost) return Integer.MIN_VALUE; // Insufficient time to visit

        // Deduct the visit cost and start with the city's reward
        int timeAfterVisit = timeRemaining - city.visitCost;
        int maxReward = city.reward;

        // Explore all outgoing connections
        for (Connection connection : graph.getOrDefault(currentCity, Collections.emptyList())) {
            int rewardFromNext = dp(connection.to, timeAfterVisit - connection.travelCost);
            if (rewardFromNext != Integer.MIN_VALUE) {
                maxReward = Math.max(maxReward, city.reward + rewardFromNext);
            }
        }

        // Memoize the result for this city and remaining time
        memo.computeIfAbsent(currentCity, k -> new HashMap<>()).put(timeRemaining, maxReward);
        return maxReward;
    }

    // Main function to calculate the maximum reward
    public int calculateMaxReward(String origin, String destination, int totalTime) {
        // Clear memoization for a fresh calculation
        memo.clear();
        // Start DFS from the origin city with the total time
        int maxReward = dp(origin, totalTime);
        return maxReward == Integer.MIN_VALUE ? -1 : maxReward;
    }

    public static void main(String[] args) {
        CityTripPlanner planner = new CityTripPlanner();

        // Add cities
        planner.addCity(new City("CITY_A", 300, 1000));
        planner.addCity(new City("CITY_B", 500, 900));
        planner.addCity(new City("CITY_C", 250, 1500));
        planner.addCity(new City("CITY_D", 100, 600));

        // Add connections
        planner.addConnection(new Connection("CITY_A", "CITY_B", 250));
        planner.addConnection(new Connection("CITY_A", "CITY_D", 300));
        planner.addConnection(new Connection("CITY_B", "CITY_C", 500));
        planner.addConnection(new Connection("CITY_D", "CITY_B", 200));

        // Calculate maximum reward
        String origin = "CITY_A";
        String destination = "CITY_B";
        int totalTime = 700;

        int result = planner.calculateMaxReward(origin, destination, totalTime);
        if (result == -1) {
            System.out.println("No valid path found within the given time.");
        } else {
            System.out.println("Maximum Reward: " + result);
        }
    }
}
