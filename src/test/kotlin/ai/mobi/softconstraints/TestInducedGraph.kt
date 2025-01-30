package ai.mobi.softconstraints

import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.Test

class TestInducedGraph {

    @Test
    fun test() {
        val x1 = Variable("x1", emptyList())    // Domain is not important here
        val x2 = Variable("x2", emptyList())
        val x3 = Variable("x3", emptyList())
        val x4 = Variable("x4", emptyList())
        val x5 = Variable("x5", emptyList())

        val problemScope = listOf(x1, x2, x3, x4, x5)

        val f1 = ValuedConstraint("f1", listOf(x1, x2), emptyList(), null, problemScope)
        val f2 = ValuedConstraint("f2", listOf(x2, x3, x4), emptyList(), null, problemScope)
        val f3 = ValuedConstraint("f3", listOf(x3, x4, x5), emptyList(), null, problemScope)
        val f4 = ValuedConstraint("f4", listOf(x4, x5), emptyList(), null, problemScope)

        val inducedGraph = inducedGraph(problemScope, listOf(f1, f2, f3, f4))

        assertEquals(setOf(x1, x2, x3, x4, x5), inducedGraph.nodes())
        assertEquals(2, inducedGraph.edgeCount())
        assertTrue(inducedGraph.hasEdge(x3, x4))
        assertTrue(inducedGraph.hasEdge(x2, x3))
    }

    @Test
    fun testClosestLowerNeighbor() {
        val problemScope = listOf("x1", "x2", "x3", "x4")

        val inducedGraph = Graph<String>()

        problemScope.forEach { inducedGraph.addNode(it) }
        inducedGraph.addEdge("x1", "x2")
        inducedGraph.addEdge("x2", "x3")
        inducedGraph.addEdge("x3", "x4")

        assertEquals("x3", inducedGraph.closestLowerNeighbor("x4", problemScope))
        assertEquals("x2", inducedGraph.closestLowerNeighbor("x3", problemScope))
        assertEquals("x1", inducedGraph.closestLowerNeighbor("x2", problemScope))
    }

    @Test
    fun testLowerNeighbors() {
        val hypergraph = Hypergraph<String, String>()
        hypergraph.addNode("x1")
        hypergraph.addNode("x2")
        hypergraph.addNode("x3")
        hypergraph.addNode("x4")
        hypergraph.addNode("x5")

        hypergraph.addHyperedge("f1", setOf("x1", "x2"))
        hypergraph.addHyperedge("f2", setOf("x2", "x3", "x4"))
        hypergraph.addHyperedge("f3", setOf("x3", "x4", "x5"))
        hypergraph.addHyperedge("f4", setOf("x4", "x5"))

        assertEquals(setOf("x3", "x4"), hypergraph.lowerNeighbors("x5", listOf("x1", "x2", "x3", "x4", "x5")))
    }

    @Test
    fun testAddClique() {
        val graph = Graph<String>()
        graph.addNode("x1")
        graph.addNode("x2")
        graph.addNode("x3")
        graph.addNode("x4")
        graph.addNode("x5")

        graph.addClique(setOf("x2", "x3", "x4"))

        assertEquals(3, graph.edgeCount())
        assertTrue(graph.hasEdge("x2", "x3"))
        assertTrue(graph.hasEdge("x2", "x4"))
        assertTrue(graph.hasEdge("x3", "x4"))
    }

    @Test
    fun testBucketTree() {
        val x1 = Variable("x1", emptyList())    // Domain is not important here
        val x2 = Variable("x2", emptyList())
        val x3 = Variable("x3", emptyList())
        val x4 = Variable("x4", emptyList())

        val problemScope = listOf(x1, x2, x3, x4)

        val f1 = ValuedConstraint("f1", listOf(x1, x2), emptyList(), null, problemScope)
        val f2 = ValuedConstraint("f2", listOf(x2, x3), emptyList(), null, problemScope)
        val f3 = ValuedConstraint("f3", listOf(x3, x4), emptyList(), null, problemScope)

        val inducedGraph = Graph<Variable>()
        problemScope.forEach { inducedGraph.addNode(it) }
        inducedGraph.addEdge(x1, x2)
        inducedGraph.addEdge(x2, x3)
        inducedGraph.addEdge(x3, x4)

        val (rootedTree, chis, lambdas) = constructBucketTree(inducedGraph, problemScope, setOf(f1, f2, f3))
        assertEquals(x1, rootedTree.parent(x2))
        assertEquals(x2, rootedTree.parent(x3))
        assertEquals(x3, rootedTree.parent(x4))
        assertEquals(null, rootedTree.parent(x1))

        assertEquals(setOf(x1), chis[x1])
        assertEquals(setOf(x1, x2), chis[x2])
        assertEquals(setOf(x3, x3), chis[x3])
        assertEquals(setOf(x3, x4), chis[x4])

        assertEquals(setOf(f1), lambdas[x1])
        assertEquals(setOf(f2), lambdas[x2])
        assertEquals(setOf(f3), lambdas[x3])
        assertEquals(emptySet(), lambdas[x4])
    }
}