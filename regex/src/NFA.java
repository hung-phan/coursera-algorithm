import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedDFS;
import edu.princeton.cs.algs4.Stack;

public class NFA {
    private char[] re;
    private int numOfState;
    private Digraph G;

    public NFA(String regex) {
        re = regex.toCharArray();
        numOfState = regex.length();
        G = buildEpsilonTransitionDigraph();
    }

    public static void main(String[] args) {
        NFA nfa = new NFA("(AA|BB)*C");

        System.out.println(nfa.recognize("AABBAABBAAAAC"));
    }

    public boolean recognize(String txt) {
        // all states that are reachable from first transition
        Bag<Integer> reachableState = getReachableState(new DirectedDFS(G, 0));

        for (int i = 0; i < txt.length(); i++) {
            Bag<Integer> match = new Bag<>();

            for (int v : reachableState) {
                if (v == numOfState) {
                    continue;
                }

                // find all states that are reachable after scanning past txt.charAt(i)
                if ((re[v] == txt.charAt(i)) || re[v] == '.') {
                    match.add(v + 1);
                }
            }

            // follow e-transition
            reachableState = getReachableState(new DirectedDFS(G, match));
        }

        for (int v : reachableState) {
            if (v == numOfState) {
                return true;
            }
        }

        return false;
    }

    private Digraph buildEpsilonTransitionDigraph() {
        Stack<Integer> ops = new Stack<>();
        Digraph digraph = new Digraph(numOfState + 1);

        for (int i = 0; i < numOfState; i++) {
            int lp = i;

            if (re[i] == '(' || re[i] == '|') {
                ops.push(i);
            } else if (re[i] == ')') {
                int or = ops.pop();

                if (re[or] == '|') {
                    lp = ops.pop();

                    digraph.addEdge(lp, or + 1);
                    digraph.addEdge(or, i);
                } else {
                    lp = or;
                }
            }

            if (i < numOfState - 1 && re[i + 1] == '*') {
                digraph.addEdge(lp, i + 1);
                digraph.addEdge(i + 1, lp);
            }

            if (re[i] == '(' || re[i] == '*' || re[i] == ')') {
                digraph.addEdge(i, i + 1);
            }
        }

        return digraph;
    }

    private Bag<Integer> getReachableState(DirectedDFS dfs) {
        Bag<Integer> state = new Bag<>();

        for (int v = 0; v < G.V(); v++) {
            if (dfs.marked(v)) {
                state.add(v);
            }
        }

        return state;
    }
}
