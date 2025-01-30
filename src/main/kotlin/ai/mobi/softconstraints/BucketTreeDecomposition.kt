package ai.mobi.softconstraints

data class BucketTreeEntry(
    val name: String,
    val variable: Variable,
    val chi: Set<Variable>,
    val lambda: Set<ValuedConstraint>,
)

fun constructBucketTree(
    inducedGraph: Graph<Variable>,
    ordering: List<Variable>,
    constraints: Set<ValuedConstraint>
): Triple<RootedTree<Variable>, Map<Variable, Set<Variable>>, Map<Variable, Set<ValuedConstraint>>> {
    var labelCounter = 1
    val rootedTree = RootedTree<BucketTreeEntry>()
    ordering.reversed().forEach { v ->
        val closestLowerNeighbor = inducedGraph.closestLowerNeighbor(v, ordering)
        val chi = chi(v, inducedGraph, ordering)
        val lambda = lambda(v, constraints)
        val node = BucketTreeEntry("v$labelCounter", v, chi, lambda)
        labelCounter++

        rootedTree.addNode(node)


    }
    TODO()
}

fun chi(node: Variable, inducedGraph: Graph<Variable>, ordering: List<Variable>, constraints: Set<ValuedConstraint>): Set<Variable> {
    val edgesFromInducedGraph = inducedGraph.edgesContaining(node)
    if (edgesFromInducedGraph.isEmpty()) {
        val x = mutableSetOf<Variable>()
        constraints.forEach { constraint ->
            if (node in constraint.scope.orderedVars) {
                x.addAll(constraint.scope.orderedVars)
            }
        }
        return constraints
    }


    setOf(node) +
            (0 until ordering.indexOf(node))
                .filter { inducedGraph.hasEdge(ordering[it], node) }
                .map { ordering[it] }
                .toSet()
}

fun lambda(node: Variable, constraints: Set<ValuedConstraint>) =
    constraints.filter { it.scope.orderedVars.first() == node }.toSet()