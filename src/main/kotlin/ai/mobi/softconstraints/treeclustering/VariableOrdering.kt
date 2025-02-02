package ai.mobi.softconstraints.treeclustering

/*
    To Do:
    o Implement Min-Induced_Width (MIW) and Min-Fill (MF), gihutrd 4.3 and 4.4 of Dechter CP, pg. 89.
*/

/**
 * MAX-CARDINALITY-ORDERING (MC)          From [1] pg. 90:
 * Input: A graph G = (V,E), wher V = {V1 ..... Vn}.
 * Output: An ordering of vertices d = (V1 ..... Vn).
 * 1. Place an arbitrary vertex in position 0.
 * 2. for j = 1 to n do
 * 3.   r <-- a vertex in G that is connected to a largest subset of
 *            the preceding vertices, in positions 1 to j - 1.
 *            Break ties arbitrarily.
 *
 * Note: Assumes vertices are indices, starting at 0 (index used by list connect_count).
 *
 * Computes maximum-cardinality ordering (pg 90, Dechter's Constraint Processing).
 * Input: A graph G = (V,E), V = {V1 ..... Vn}.
 *        Each Vi is the index of a vertex, counting up from 0.
 * Output: An ordering of vertices d = (V1 ..... Vn)
 *         that attempts to minimize induced width of the ordered constraint graph.
 * Approach: Greedily adds vertices that maximize the number of neighbors that follows.
 */
fun <V> computeMaxCardinalityOrdering(g: Graph<V>): List<V> {
    /* variable ordering constructed thus far */
    val ordering = mutableListOf<V>()

    /* Vertices to add, listed from max to min count */
    val remainingVertices = g.vertices.toMutableList() // Initially, a copy of vertices

    /* For each vertex, a count of neighbors currently in the candidate ordering */
    val connectedCount = g.vertices.associateWith { 0 }.toMutableMap()

    /*
        Repeatedly add to ordering, the vertex with the most neighbors currently in ordering.
        (first picked arbitrary).
     */
    while (remainingVertices.isNotEmpty()) {
        /* Add to ordering the vertex v_best with the max # number of neighbors already in ordering */
        val vBest = remainingVertices.removeFirst()
        ordering.add(vBest)

        /* Update connect-count of vertices in remaining that are neighbors of v_best */
        g.findNeighbors(vBest).forEach { neighbor ->
            if (neighbor in remainingVertices)
               connectedCount[neighbor] = connectedCount[neighbor]!! + 1  // Assumes the vertex Neighbor is an index.
        }

        /* Reorder remaining by updated count, from max to min, so the first on the list has max count */
        remainingVertices.sortByDescending { connectedCount[it] }
    }

    /* Return this max cardinality order */
    return ordering
}