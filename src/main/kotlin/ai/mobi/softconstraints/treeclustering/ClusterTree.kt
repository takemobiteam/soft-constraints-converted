package ai.mobi.softconstraints.treeclustering



const val useMst = false    // Flag that specifies whether to construct a regular join tree or a max-span join tree.

/**
 * Background:
 *
 * References:
 *
 *   [1] R. Dechter. Constraint Processing. Morgan Kaufmann, San Francisco,
 *   California, 2003.
 *   [2] R. Dechter and J. Pearl. Tree clustering for constraint networks.
 *   Artificial Intelligence, 38(3):353-366, April 1989.
 *
 * Tree Clustering Algorithm: from [1] pg. 255:
 *
 * Input:
 *   A constraint problem: R = (X,D,C) and its primal graph G = (X,E).
 *
 * Output:
 *   An equivalent acyclic constraint problem and its join-tree: T = (X,D,C')
 *
 * 1 Select a variable ordering, d = (X1 ..... Xn)
 *
 * 2 Triangulation (create the induced graph along d and call it G*):
 *   for j = n to 1 by -1 do
 *   E <-- E U {(i,k) | (i,j) \in E, (k,j) \in E}
 *
 * 3 Create a join-tree of the induced graph G*:
 *   a. Identify all maximal cliques in the chordal graph
 *      (chordal if each vertex and its parents form a clique).
 *      Let C1 ..... Cr be all such cliques, numbered from last
 *      to first vertex in the ordering.
 *
 *   b. Create a tree structure T over the cliques:
 *      Connect each Ci to a Cj (j < i) that shares the
 *      largest subset of vertices.
 *
 * Input: constraint graph g,
 * Output: cluster (join) tree of g
 */
fun <V> clusterTree(g: Graph<V>, ordering: List<V>? = null): Graph<Set<V>> {
    /*
        1. Select a variable ordering that keeps the size of the cliques small
           (corresponds to number of subproblem variables).
           (Any ordering works. max_cardinality is preferred, which is used to detect when
           a graph is already chordal - connects each vertex to its parents).
    */
    val d = if (ordering == null) computeMaxCardinalityOrdering(g) else ordering

    /*
        2. Make g chordal by creating the induced graph of g
           along vertex ordering d (using triangulation).
    */
    val (gStar, _) = inducedGraph(g, d) // Only need induced graph, ignore returned width.

    /*
        3. Create a join-tree from the chordal (induced) graph.
           a. extract maximal cliques acdording to reverse of ordering d.
              Cliques are tightly coupled subgraphs, which correspond to constraint sub-problems.
     */
    val cliques = orderedMaximalCliques(gStar, d)

    /*
           b. Construct a tree that connects these cliques.
              Tree specifies order in which sub-problems are solved.
     */
    val tree = if (useMst) createClusterTreeUsingMst(cliques) else createClusterTree(cliques)
    return tree
}
