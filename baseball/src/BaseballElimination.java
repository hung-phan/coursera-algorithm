import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class BaseballElimination {
    private final int numOfTeams;
    private final int numOfGames;
    private final int numOfVertices;
    private final int src;
    private final int sink;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] against;
    private final Map<String, Integer> nameToId;
    private final Map<Integer, String> idToName;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        In input = new In(filename);

        numOfTeams    = Integer.parseInt(input.readLine());
        numOfGames    = numOfTeams * (numOfTeams - 1) / 2;
        numOfVertices = numOfGames + numOfTeams + 2;
        src           = numOfVertices - 2;
        sink          = numOfVertices - 1;
        wins          = new int[numOfTeams];
        losses        = new int[numOfTeams];
        remaining     = new int[numOfTeams];
        against       = new int[numOfTeams][numOfTeams];
        nameToId      = new LinkedHashMap<>();
        idToName      = new LinkedHashMap<>();

        String[] lines = input.readAllLines();

        for (int i = 0; i < lines.length; i++) {
            String[] fields = lines[i].trim().split("\\s+", 5);

            nameToId.put(fields[0], i);
            idToName.put(i, fields[0]);

            wins[i]      = Integer.parseInt(fields[1]);
            losses[i]    = Integer.parseInt(fields[2]);
            remaining[i] = Integer.parseInt(fields[3]);

            String[] competeNums = fields[4].split("\\s+");

            for (int j = 0; j < competeNums.length; j++) {
                against[i][j] = Integer.parseInt(competeNums[j]);
            }
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);

        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return nameToId.keySet();
    }

    // number of wins for given team
    public int wins(String team) {
        if (!nameToId.containsKey(team)) {
            throw new IllegalArgumentException();
        }

        return wins[nameToId.get(team)];
    }

    // number of losses for given team
    public int losses(String team) {
        if (!nameToId.containsKey(team)) {
            throw new IllegalArgumentException();
        }

        return losses[nameToId.get(team)];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!nameToId.containsKey(team)) {
            throw new IllegalArgumentException();
        }

        return remaining[nameToId.get(team)];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!nameToId.containsKey(team1) || !nameToId.containsKey(team2)) {
            throw new IllegalArgumentException();
        }

        return against[nameToId.get(team1)][nameToId.get(team2)];
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        return certificateOfElimination(team) != null;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        LinkedList<String> result = new LinkedList<>();
        int teamX = nameToId.get(team);

        for (int currentTeam = 0; currentTeam < numOfTeams; currentTeam++) {
            if (currentTeam == teamX) {
                continue;
            }

            if (wins(team) + remaining(team) < wins[currentTeam]) {
                result.addFirst(idToName.get(currentTeam));
            }
        }

        if (!result.isEmpty()) {
            return result;
        }

        FlowNetwork network = new FlowNetwork(numOfVertices);
        int totalGames = 0;

        for (int team1 = 0, offset = 0; team1 < numOfTeams; team1++) {
            for (int team2 = team1 + 1; team2 < numOfTeams; team2++, offset++) {
                network.addEdge(new FlowEdge(src, offset, against[team1][team2]));
                network.addEdge(new FlowEdge(offset, numOfGames + team1, Double.POSITIVE_INFINITY));
                network.addEdge(new FlowEdge(offset, numOfGames + team2, Double.POSITIVE_INFINITY));
                totalGames += against[team1][team2];
            }

            int capacity = wins(team) + remaining(team) - wins[team1];

            network.addEdge(new FlowEdge(numOfGames + team1, sink, Math.max(capacity, 0)));
        }

        FordFulkerson ff = new FordFulkerson(network, src, sink);

        if ((int) ff.value() == totalGames) {
            return null;
        }

        for (int i = 0; i < numOfTeams; i++) {
            if (ff.inCut(numOfGames + i)) {
                result.add(idToName.get(i));
            }
        }

        return result;
    }
}
