package ai.mobi.softconstraints.treeclustering

import kotlin.test.Test
import kotlin.test.assertEquals

class TestInducedGraph {
    @Test
    fun `Test 1`() {
        val g1 = Graph<Int>()
        g1.addVertex(0)
        g1.addVertex(1)
        g1.addVertex(2)
        g1.addEdge(0, 1)
        g1.addEdge(1, 2)
        assertEquals(setOf(0, 1, 2), g1.vertices)
        assertEquals(setOf(Pair(0, 1), Pair(1, 0), Pair(1, 2), Pair(2, 1)), g1.edges)

        val igo1 = listOf(0, 1, 2)
        val (ig1, igw1) = inducedGraph(g1, igo1)
        assertEquals(1, igw1)
        assertEquals(setOf(0, 1, 2), ig1.vertices)
        assertEquals(setOf(Pair(0, 1), Pair(1, 0), Pair(1, 2), Pair(2, 1)), ig1.edges)

        val igo2 = listOf(0, 2, 1)
        val (ig2, igw2) = inducedGraph(g1, igo2)
        assertEquals(2, igw2)
        assertEquals(setOf(0, 1, 2), ig2.vertices)
        assertEquals(setOf(Pair(0, 1), Pair(0, 2), Pair(1, 0), Pair(1, 2), Pair(2, 0), Pair(2, 1)), ig2.edges)
    }

    @Test
    fun `Test 2`() {
        val g1 = Graph<Int>()
        g1.addVertex(0) // A
        g1.addVertex(1) // B
        g1.addVertex(2) // C
        g1.addVertex(3) // D
        g1.addVertex(4) // E
        g1.addVertex(5) // F
        g1.addEdge(0, 1) // AB
        g1.addEdge(0, 2) // AC
        g1.addEdge(0, 4) // AE
        g1.addEdge(1, 3) // BD
        g1.addEdge(1, 4) // BE
        g1.addEdge(2, 3) // CD
        g1.addEdge(3, 5) // DF
        assertEquals(setOf(0, 1, 2, 3, 4, 5), g1.vertices)
        assertEquals(
            setOf(
                Pair(0, 1),
                Pair(0, 2),
                Pair(0, 4),
                Pair(1, 0),
                Pair(1, 3),
                Pair(1, 4),
                Pair(2, 0),
                Pair(2, 3),
                Pair(3, 1),
                Pair(3, 2),
                Pair(3, 5),
                Pair(4, 0),
                Pair(4, 1),
                Pair(5, 3),
            ), g1.edges
        )

        val igo1 = listOf(5, 4, 3, 2, 1, 0)
        val (ig1, igw1) = inducedGraph(g1, igo1)
        assertEquals(3, igw1)
        assertEquals(setOf(0, 1, 2, 3, 4, 5), ig1.vertices)
        assertEquals(
            setOf(
                Pair(0, 1),
                Pair(0, 2),
                Pair(0, 4),
                Pair(1, 0),
                Pair(1, 2),
                Pair(1, 3),
                Pair(1, 4),
                Pair(2, 0),
                Pair(2, 1),
                Pair(2, 3),
                Pair(2, 4),
                Pair(3, 1),
                Pair(3, 2),
                Pair(3, 4),
                Pair(3, 5),
                Pair(4, 0),
                Pair(4, 1),
                Pair(4, 2),
                Pair(4, 3),
                Pair(4, 5),
                Pair(5, 3),
                Pair(5, 4),
            ), ig1.edges
        )

        val igo2 = listOf(0, 1, 2, 3, 4, 5)
        val (ig2, igw2) = inducedGraph(g1, igo2)
        assertEquals(2, igw2)
        assertEquals(setOf(0, 1, 2, 3, 4, 5), ig2.vertices)
        assertEquals(
            setOf(
                Pair(0, 1),
                Pair(0, 2),
                Pair(0, 4),
                Pair(1, 0),
                Pair(1, 2),
                Pair(1, 3),
                Pair(1, 4),
                Pair(2, 0),
                Pair(2, 1),
                Pair(2, 3),
                Pair(3, 1),
                Pair(3, 2),
                Pair(3, 5),
                Pair(4, 0),
                Pair(4, 1),
                Pair(5, 3),
            ), ig2.edges
        )

        val igo3 = listOf(5, 3, 2, 1, 0, 4)
        val (ig3, igw3) = inducedGraph(g1, igo3)
        assertEquals(2, igw3)
        assertEquals(setOf(0, 1, 2, 3, 4, 5), ig3.vertices)
        assertEquals(
            setOf(
                Pair(0, 1),
                Pair(0, 2),
                Pair(0, 4),
                Pair(1, 0),
                Pair(1, 2),
                Pair(1, 3),
                Pair(1, 4),
                Pair(2, 0),
                Pair(2, 1),
                Pair(2, 3),
                Pair(3, 1),
                Pair(3, 2),
                Pair(3, 5),
                Pair(4, 0),
                Pair(4, 1),
                Pair(5, 3),
            ), ig3.edges
        )
    }

    @Test
    fun test3() {
        /*
            vertex - neighbors:
            0 - {1, 2, 4}
            1 - {0, 3, 4}
            2 - {0, 3}
            3 - {1, 2, 5}
            4 - {0, 1}
            5 - {3}
        */
        val g1 = Graph<Int>()
        g1.addVertex(0)    // A
        g1.addVertex(1)    // B
        g1.addVertex(2)    // C
        g1.addVertex(3)    // D
        g1.addVertex(4)    // E
        g1.addVertex(5)    // F

        g1.addEdge(0, 1)  // AB
        g1.addEdge(0, 2)  // AC
        g1.addEdge(0, 4)  // AE
        g1.addEdge(1, 3)  // BD
        g1.addEdge(1, 4)  // BE
        g1.addEdge(2, 3)  // CD
        g1.addEdge(3, 5)  // DF

        assertEquals(setOf(0, 1, 2, 3, 4, 5), g1.vertices)
        assertEquals(setOf(
            0 to 1, 0 to 2, 0 to 4,
            1 to 0, 1 to 3, 1 to 4,
            2 to 0, 2 to 3,
            3 to 1, 3 to 2, 3 to 5,
            4 to 0, 4 to 1,
            5 to 3,
        ), g1.edges)

        val mco1 = computeMaxCardinalityOrdering(g1)
        assertEquals(listOf(0, 1, 4, 2, 3, 5), mco1)

        val (ig1, igw) = inducedGraph(g1, mco1)
        assertEquals(2, igw)
        assertEquals(setOf(0, 1, 2, 3, 4, 5), g1.vertices)
        assertEquals(setOf(
            0 to 1, 0 to 2, 0 to 4,
            1 to 0, 1 to 2, 1 to 3, 1 to 4,
            2 to 0, 2 to 1, 2 to 3,
            3 to 1, 3 to 2, 3 to 5,
            4 to 0, 4 to 1,
            5 to 3,
        ), ig1.edges)


        /*
            For the example ordering [0, 1, 4, 2, 3, 5], the induced graph is:
             vertex - neighbors:
               0 - {1, 2, 4}
               1 - {0, 2, 3, 4}
               2 - {0, 1, 3}
               3 - {1, 2, 5}
               4 - {0, 1}
               5 - {3}
        */
        val ig1cliques = orderedMaximalCliques(ig1, mco1)
        assertEquals(listOf(
            setOf(1, 2, 3, 5),
            setOf(0, 1, 2, 3, 4),
        ), ig1cliques)

        /*
            For the induced graph above, the cliques corresponding to each vertex are the following
            Naximal cliques indicated with *:

                5 - {3, 5}
                3 - {1, 2, 3, 5}           *
                2 - {0, 1, 2, 3}
                4 - {0, 1, 4}
                1 - {0, 1, 2, 3, 4}        *
                0 - {0, 1, 2, 4}
        */
        val ig1atree = createClusterTree(ig1cliques)
        assertEquals(setOf(
            setOf(1, 2, 3, 5),
            setOf(0, 1, 2, 3, 4),
        ), ig1atree.vertices)
        assertEquals(setOf(
            setOf(1, 2, 3, 5) to setOf(0, 1, 2, 3, 4),
            setOf(0, 1, 2, 3, 4) to setOf(1, 2, 3, 5)
        ), ig1atree.edges)

        val ig1btree = createClusterTreeUsingMst(ig1cliques)
        ig1btree.display()
        assertEquals(setOf(
            setOf(1, 2, 3, 5),
            setOf(0, 1, 2, 3, 4),
        ), ig1btree.vertices)
        assertEquals(setOf(
            setOf(1, 2, 3, 5) to setOf(0, 1, 2, 3, 4),
            setOf(0, 1, 2, 3, 4) to setOf(1, 2, 3, 5)
        ), ig1btree.edges)
    }
}