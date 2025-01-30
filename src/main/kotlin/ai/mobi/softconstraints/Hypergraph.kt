package ai.mobi.softconstraints

class Hypergraph<V, E> {
    private val vertices = mutableSetOf<V>()
    private val edges = mutableSetOf<Pair<E, Set<V>>>()

    fun addNode(node: V) = vertices.add(node)
    fun addHyperedge(edge: E, connecting: Collection<V>) = edges.add(Pair(edge, connecting.toSet()))

    fun nodes() = vertices.toSet()

    fun sharesHyperEdge(v1: V, v2: V) = edges.any { (e, vs) -> v1 in vs && v2 in vs }

    fun hyperedgesContaining(v: V) = edges.filter { (_, vs) -> v in vs }

    fun lowerNeighbors(v: V, ordering: List<V>): Set<V> {
        /* Not very elegant */

        /* The elements after the vertex */
        val elementsAfter = ordering.elementsBefore(v)

        /* Go through each hyperedge containing our vertex & pull out any lower neighbors from that hyperedge */
        val results = mutableSetOf<V>()
        hyperedgesContaining(v).forEach { (_, vs) ->
            /* vs is a hyperedge containing the target vertex. */
            results.addAll(elementsAfter.intersect(vs))
        }
        return results
    }
}

class Graph<V> {
    private val vertices = mutableSetOf<V>()
    private val edges = mutableSetOf<Pair<V, V>>()

    fun addNode(node: V) = vertices.add(node)
    fun addEdge(fromV: V, toV: V) {
        if (edges.contains(Pair(toV, fromV))) throw IllegalArgumentException()
        else
            edges.add(Pair(fromV, toV))
    }

    fun addClique(vs: Collection<V>) {
        val list = vs.toList()
        (0..list.size - 1).forEach { i ->
            (i + 1..list.size - 1).forEach { j ->
                addEdge(list[i], list[j])
            }
        }
    }

    fun edgeCount() = edges.size

    fun hasEdge(v1: V, v2: V) = edges.contains(Pair(v1, v2)) || edges.contains(Pair(v2, v1))

    fun nodes() = vertices.toSet()

    fun closestLowerNeighbor(node: V, ordering: List<V>): V? {
        (ordering.indexOf(node) - 1 downTo 0).forEach { i ->
            val potentialNeighbor = ordering[i]
            if (hasEdge(node, potentialNeighbor))
                return potentialNeighbor
        }
        return null
    }
}

fun <V> V.after(other: V, ordering: List<V>) = ordering.indexOf(other) > ordering.indexOf(this)
fun <V> List<V>.elementsBefore(v: V): List<V>  = subList(0, indexOf(v))


class RootedTree<V> {
    private val nodes = mutableSetOf<V>()
    private val parents = mutableMapOf<V, V>()  // node to parent

    fun addNode(node: V) = nodes.add(node)
    fun setParent(node: V, parent: V) {
        parents[node] = parent
    }

    fun nodes() = nodes.toSet()
    fun edges() = parents.toMap()

    fun parent(node: V) = parents[node]
}