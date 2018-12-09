import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordNet {
    private final Map<Integer, String> idToNoun;
    private final Map<String, List<Integer>> nounToId;
    private int numOfVertex;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        this.idToNoun = new HashMap<>();
        this.nounToId = new HashMap<>();

        readSynets(synsets);
        readHypernyms(hypernyms);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        if (args.length > 1) {
            WordNet wordNet = new WordNet(args[0], args[1]);

            wordNet.isNoun("a");
        }
    }

    private void readSynets(String synsets) {
        In in = new In(synsets);
        String currentLine;

        while ((currentLine = in.readLine()) != null) {
            String[] segments = currentLine.split(",");

            if (segments.length < 2) {
                continue;
            }

            numOfVertex++;

            int id = Integer.parseInt(segments[0]);
            String synset = segments[1];

            idToNoun.put(id, synset);

            for (String noun : synset.split(" ")) {
                List<Integer> ids = nounToId.get(noun);

                if (ids == null) {
                    nounToId.put(noun, new ArrayList<>(Collections.singletonList(id)));

                    continue;
                }

                ids.add(id);
            }
        }
    }

    private void readHypernyms(String hypernyms) {
        In in = new In(hypernyms);
        Digraph digraph = new Digraph(numOfVertex);
        String currentLine;

        while ((currentLine = in.readLine()) != null) {
            String[] segments = currentLine.split(",");

            if (segments.length < 2) {
                continue;
            }

            int start = Integer.parseInt(segments[0]);

            for (int i = 1; i < segments.length; ++i) {
                digraph.addEdge(start, Integer.parseInt(segments[i]));
            }
        }


        DirectedCycle dc = new DirectedCycle(digraph);

        if (dc.hasCycle()) {
            throw new IllegalArgumentException("Cycle detected");
        }

        int numOfRoot = 0;

        for (int i = 0; i < digraph.V(); ++i) {
            if (digraph.outdegree(i) == 0) {
                numOfRoot++;

                if (numOfRoot > 1) {
                    throw new IllegalArgumentException("More than 1 root");
                }
            }
        }
        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nounToId.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException();
        }
        return nounToId.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        return sap.length(nounToId.get(nounA), nounToId.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }

        return idToNoun.get(sap.ancestor(nounToId.get(nounA), nounToId.get(nounB)));
    }
}
