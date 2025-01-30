package ai.mobi.softconstraints

fun constructBucketTree(
    inducedGraph: Graph<Variable>,
    ordering: List<Variable>,
    constraints: Set<ValuedConstraint>
): Triple<RootedTree<Variable>, Map<Variable, Set<Variable>>, Map<Variable, Set<ValuedConstraint>>> {
    val rootedTree = RootedTree<Variable>()
    inducedGraph.nodes().forEach { rootedTree.addNode(it) }
    inducedGraph.nodes().forEach { n ->
        inducedGraph.closestLowerNeighbor(n, ordering).also { closestLowerNeighbor ->
            if (closestLowerNeighbor != null)
                rootedTree.setParent(n, closestLowerNeighbor)
        }
    }
    val chis = inducedGraph.nodes().associateWith { chi(it, inducedGraph, ordering) }
    val lambdas = inducedGraph.nodes().associateWith { lambda(it, constraints) }
    return Triple(rootedTree, chis, lambdas)
}

fun <V> chi(node: V, inducedGraph: Graph<V>, ordering: List<V>): Set<V> =
    (0 until ordering.indexOf(node))
        .filter { inducedGraph.hasEdge(ordering[it], node) }
        .map { ordering[it] }
        .toSet()

fun lambda(node: Variable, constraints: Set<ValuedConstraint>) =
    constraints.filter { it.scope.orderedVars.last() == node }.toSet()