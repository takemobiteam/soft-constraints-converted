@file:Suppress("UnstableApiUsage")

package ai.mobi.softconstraints.treeclustering

import ai.mobi.softconstraints.display


fun main() {
    test1()
    test2()
    test3()
}

private fun test1() {
    println("Test 1: graph creation and induced graphs (identical to graph for any ordering):")
    val g1 = Graph<Int>()
    g1.addVertex(0)
    g1.addVertex(1)
    g1.addVertex(2)
    g1.addEdge(0, 1)
    g1.addEdge(1, 2)
    g1.display()

    val igo1 = listOf(0, 1, 2)
    val (ig1, igw1) = inducedGraph(g1, igo1)
    ig1.display()

    val igo2 = listOf(0, 2, 1)
    val (ig2, igw2) = inducedGraph(g1, igo2)
    ig2.display()
}

private fun test2() {
    println("\nTest 2: graph and induced graphs from Dechter pg 87,Figure 4.1:")
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
    g1.display()

    val igo1 = listOf(5, 4, 3, 2, 1, 0)
    val (ig1, _) = inducedGraph(g1, igo1)
    ig1.display()

    val igo2 = listOf(0, 1, 2, 3, 4, 5)
    val (ig2, _) = inducedGraph(g1, igo2)
    ig2.display()

    val igo3 = listOf(5, 3, 2, 1, 0, 4)
    val (ig3, igw3) = inducedGraph(g1, igo3)
    ig3.display()
}

private fun test3() {
    println("\nTest 3: max cardinality ordering for graph from Dechter pg 87, Figure 4.1a:")

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
    g1.display()
    
    val mco1 = computeMaxCardinalityOrdering(g1)
    println("\nAn example max cardinality ordering is  [0, 1, 4, 2, 3, 5]")
    println("Max cardinality ordering found $mco1")
    println("\nMany solutions.  Check that each vertex maximizes number of preceding neighbors.")

    println("\nInduced graph of max cardinality ordering $mco1")
    val (ig1, igw) = inducedGraph(g1, mco1)
    ig1.display()

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
    println("\nExtracting the maximal cliques from this induced graph.")
    val ig1cliques = orderedMaximalCliques(ig1, mco1)
    println("\nMaximal Cliques")
    ig1cliques.forEach { cliq -> println("   $cliq") }

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
    println("\nConstruct join tree for cliques $ig1cliques:")
    val ig1atree = createClusterTree(ig1cliques)
    ig1atree.display()

    println("\nConstruct minimum spanning join tree for $ig1cliques:")
    val ig1btree = createClusterTreeUsingMst(ig1cliques)
    ig1btree.display()
}