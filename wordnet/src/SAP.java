import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph digraph;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph digraph) {
        this.digraph = digraph;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private BreadthFirstDirectedPaths buildBFS(int i) {
        int numOfVertex = digraph.V();

        if (i < 0 || i >= numOfVertex) {
            throw new IndexOutOfBoundsException(String.format("vertex %d is not between 0 and %d", i, numOfVertex - 1));
        }

        return new BreadthFirstDirectedPaths(digraph, i);
    }

    private BreadthFirstDirectedPaths buildBFS(Iterable<Integer> i) {
        if (i == null) {
            throw new NullPointerException();
        }

        int numOfVertex = digraph.V();

        for (int v : i) {
            if (v < 0 || v >= numOfVertex) {
                throw new IndexOutOfBoundsException(String.format("vertex %d is not between 0 and %d", v, numOfVertex - 1));
            }
        }

        return new BreadthFirstDirectedPaths(digraph, i);
    }

    private int[] getShortestPath(BreadthFirstDirectedPaths bfsv, BreadthFirstDirectedPaths bfsw) {
        int ancestral = -1;
        int shortestLength = Integer.MAX_VALUE;

        for (int vertex = 0; vertex < digraph.V(); vertex++) {
            if (bfsv.hasPathTo(vertex) && bfsw.hasPathTo(vertex)) {
                int length = bfsv.distTo(vertex) + bfsw.distTo(vertex);

                if (length < shortestLength) {
                    ancestral = vertex;
                    shortestLength = length;
                }
            }
        }

        if (ancestral == -1) {
            shortestLength = -1;
        }

        return new int[]{ancestral, shortestLength};
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int[] result = getShortestPath(buildBFS(v), buildBFS(w));

        return result[1];
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        int[] result = getShortestPath(buildBFS(v), buildBFS(w));

        return result[0];
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = getShortestPath(buildBFS(v), buildBFS(w));

        return result[1];
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int[] result = getShortestPath(buildBFS(v), buildBFS(w));

        return result[0];
    }
}
