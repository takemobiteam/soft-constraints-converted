package ai.mobi.softconstraints.treeclustering

/**
 * Check: Are cliques being processed in the proper order?
 *
 * Input: list of maximal cliques [ci] of g* according to the reverse order.
 * Output: cluster tree over cliques.
 */
fun <V> createClusterTree(cliques: List<Set<V>>): Graph<Set<V>> {
    /*
        The vertices of a cluster tree are the cliques. .
        Tree connects each Ci to a Cj (j < i) with whom it
        shares the largest subset of vertices in G*.
    */
    val g = Graph<Set<V>>() // Cluster tree g, which starts as an empty graph.

    /*
        Turn cliques into immutable sets, so that they can be used as graph vertices.
        Graphs require vertices to be immutable objects so they can be hashed.
     */
    for (ci in cliques) {
        /*
            Connect each clique Ci to a clique in max cliques (g.vertices)
            with the largest number of shared vertices.
         */
        var cj: Set<V>? = null       // The clique to connect to.
        var maxShared = 0   // Max num vertices shared with ci.

        /*
            Find a clique Cj in g that has the maximum overlap of shared vertices with Ci.
            Repeatedly updates Cj and max_shared as each clique is visited.
         */
        g.vertices.forEach { existing ->
            val shared = (ci intersect existing).size
            if (shared > maxShared) {
                maxShared = shared
                cj = existing
            }
        }

        /* Add Ci as a vertex of cluster tree g and an edge <Ci,Cj> if an overlapping Cj in max cliques was found */
        if (cj == null)
            g.addVertex(ci)
        else
            g.addEdge(Pair(ci, cj))
    }
    return g
}