package ai.mobi.softconstraints.treeclustering

/** Graph of undirected edges */
class Graph<V> private constructor(vertices: Set<V>, edges: Map<V, Set<V>>) {

    /** vertices is a set of vertices */
    private val _vertices = vertices.toMutableSet()
    val vertices: Set<V>
        get() = _vertices

    /** edges is a dictionary that maps a vertex to a set of neighboring vertices */
    private val _edges = edges.mapValues { it.value.toMutableSet() }.toMutableMap()
    val edges: Set<Pair<V, V>>
        get() = sequence {
            _edges.entries.forEach { (v, vs) ->
                vs.forEach { vv ->
                    yield(Pair(v, vv))
                    if (v != vv) yield(Pair(vv, v))
                }
            }
        }.toSet()

    constructor() : this(emptySet(), emptyMap())

    fun addVertex(v: V) = _vertices.add(v)

    fun addEdge(edge: Pair<V, V>) {
        val (v1, v2) = edge
        addEdge(v1, v2)
    }

    fun addEdge(v1: V, v2: V) {
        _edges.getOrPut(v1) { mutableSetOf() }.add(v2)
        _edges.getOrPut(v2) { mutableSetOf() }.add(v1)
        _vertices.add(v1)
        _vertices.add(v2)
    }

    fun findNeighbors(v: V) = _edges[v] ?: emptySet<V>()

    fun copy() = Graph(_vertices, _edges)

    /** Print vertices and their neighbors of graph */
    fun display() {
        println(this)
        println("  vertex - neighbors:")
        _vertices.forEach {
            println("    $it - ${findNeighbors(it)}")
        }
    }
}
