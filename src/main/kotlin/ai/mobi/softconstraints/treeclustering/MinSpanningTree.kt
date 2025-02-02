package ai.mobi.softconstraints.treeclustering

import java.util.PriorityQueue

/*
    Find a minimum spanning tree or forest using Kruskal's algorithm:
    (from wiki article "Kurskal's algorithm"):

    Generates a minimum spanning tree of a connected graph
    or a forest if the graph is not connected.

     Complexity O(|E|log|V|)

    A spanning tree of a graph is a tree that connects all vertices of a connected graph.

    A minimum spanning tree is a spanning tree of a weighted graph
    that minimizes the sum of the edge weights in the graph.

    Kruskal's algorithm is a greedy algorithm that constructs a minimum spanning tree
    (or forest) by repeatedly adding to a tree an edge with lowest weight,
    while not creating a cycle.

    The algorithm performs the following steps:

    1) Create a forest (a set of trees) initially consisting of a
       separate single-vertex tree for each vertex in the input graph.

    2) Sort the graph edges by weight.

    3) Loop through the edges of the graph, in ascending sorted order by their weight.
       For each edge:
       - Test whether adding the edge to the current forest would create a cycle.
       - If not, add the edge to the forest, which combines two trees into a single tree.

    Detailed pseudo code

    KRUSKAL(G):
    Create an initial forest, where each vertex is its own tree.
    1 A = ∅
    2 foreach v ∈ G.V:
    3    MAKE-SET(v)

    4 foreach (u, v) in G.E ordered by increasing weight(u, v):
    5    if FIND-SET(u) ≠ FIND-SET(v): # Edge (u, v) does not form a cycle iff u and v are in disjoint trees.
    6       A = A ∪ {(u, v)}
    7       UNION(FIND-SET(u), FIND-SET(v))
    8 return A

    To Do:
    - Reformulate to minimize rather than maximize.
*/

/** Edge of a weighted graph */
data class WeightedEdge<V, W : Comparable<W>>(val vertices: Pair<V, V>, val weight: W) :
    Comparable<WeightedEdge<V, W>> {
    constructor(v1: V, v2: V, weight: W) : this(Pair(v1, v2), weight)

    override fun compareTo(other: WeightedEdge<V, W>) = weight.compareTo(other.weight)
}

/**
 * Create Minimum Spanning Tree of a weighted graph (Kruskal's Algorithm).
 * Inputs:   vertices and edges of a weighted graph.
 *          vertices is a list of integers in the range of 0 to len(vertices) - 1.
 *          edges is a list of WeightedEdge objects.
 * Output: list of those edges that denote a minimum spanning tree or forest.
 *
 * Note - assumes vertices are indices, in the range of 0 to len(vertices) - 1.
 */
fun <V, W : Comparable<W>> minimumSpanningTree(
    vertices: Collection<V>,
    edges: Collection<WeightedEdge<V, W>>
): List<WeightedEdge<V, W>> {
    /*
        Parent is an array specifying the parent of each vertex.
        A vertex v is a root if its parent is itself.
        Initially every vertex is a root.
    */
    val parent = vertices.associateWith { it }.toMutableMap()

    fun root(v: V): V {
        /*
            Follow the parents of vertex v up to its root.
            A vertex is a root if its parent is itself.
        */
        if (parent[v] != v)
            parent[v] = root(parent[v]!!)
        return parent[v]!!
    }

    fun combineTrees(v1: V, v2: V) {
        /* Share the same root between the vertices of v1 and v2's trees */
        val (root1, root2) = Pair(root(v1), root(v2))
        if (root1 != root2)
            parent[root2] = root1
    }

    val mst = mutableListOf<WeightedEdge<V, W>>()

    /* Turn edges into a priority Q, implemented as a heap */
    val edgesHeap = PriorityQueue(edges)

    while (edgesHeap.isNotEmpty() && mst.size < vertices.size - 1) {
        /* Get the next edge of (remaining) edges with minimum weight */
        val edge = edgesHeap.poll()
        val (v1, v2) = edge.vertices

        /*
        Edge expands a tree if it connects two trees,
        but creates a cycle if it connects two vertices of the same tree
        */
        if (root(v1) != root(v2)) {
            combineTrees(v1, v2)
            mst.add(edge)
        }
    }
    return mst
}