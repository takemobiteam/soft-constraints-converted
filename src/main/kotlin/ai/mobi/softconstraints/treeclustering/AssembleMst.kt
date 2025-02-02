package ai.mobi.softconstraints.treeclustering

/**
 * Construct a cluster tree that is a minimum spanning tree.
 *
 * Input: list of maximal cliques [ci] of the induced graph g*,
 *        ordered according to the reverse of a vertex ordering.
 * Output: cluster tree over cliques that forms a minimum spanning tree.
 *         The resulting tree maximizes the number of shared variables
 *         between neighboring cliques.
 */
fun <V> createClusterTreeUsingMst(cliques: List<Set<V>>): Graph<Set<V>> {
    /*
        Check - Do we want to maximize or minimize the number
        of shared variables between neighboring cliques?

        Turn cliques into immutable sets, so that they can be used as graph vertices.
        Graphs require vertices to be immutable objects so they can be hashed by set operations.
    */
    val edges = mutableListOf<WeightedEdge<Set<V>, Int>>()

    /*
        Create a weighted graph from cliques whose edges connect
        overlapping cliques, weighted by size of overlap.
     */
    cliques.indices.forEach { i ->
        /*
            Connect each clique i to successor cliques of ordering
            that have overlapping vertices.
        */
        (i + 1 until cliques.size).forEach { j ->
            val numSharedVars = (cliques[i] intersect cliques[j]).size
            if (numSharedVars > 0) {
                /*
                    Create graph edge that is weighted by the size of the
                    overlap between v1 and v2, where v1 and v2 are sets of vertices
                    that each form a clique.

                    Edge weights are negated so that the minimum spanning tree
                    maximizes the shared variables between neighboring cliques.
                */
                edges.add(WeightedEdge(cliques[i], cliques[j], -numSharedVars))

            }
        }
    }

    /* Extract minimum spanning tree (forest) of this (negatively) weighted graph */
    val joinTreeEdges = minimumSpanningTree(cliques, edges)

    /* Construct a join tree g from the cliques and the spanning tree (forest) edges */
    val g = Graph<Set<V>>()
    for (c in cliques)
        g.addVertex(c)

    joinTreeEdges.forEach { g.addEdge(it.vertices) }

    return g
}