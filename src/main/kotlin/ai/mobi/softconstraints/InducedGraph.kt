package ai.mobi.softconstraints

fun inducedGraph(vars: List<Variable>, constraints: Collection<ValuedConstraint>): Graph<Variable> {
    /*
        Initial hypergraph--nodes are variables, each hyperedge is a set of variables involved with a particular
        constraint
    */
    val graph = Hypergraph<Variable, ValuedConstraint>()
    vars.forEach { graph.addNode(it) }
    constraints.forEach { graph.addHyperedge(it, it.scope.orderedVars) }

    /* Process variables in reverse order to create induced graph */
    val inducedGraph = Graph<Variable>()
    graph.nodes().forEach { inducedGraph.addNode(it) }
    vars.reversed().forEach { variable ->
        val lowerNeighbors = graph.lowerNeighbors(variable, vars)
        inducedGraph.addClique(lowerNeighbors)
        val edgesWithVar = inducedGraph.edges.filter { (f, t) -> f == variable || t == variable }
        val relevantConnections = mutableSetOf<Variable>()
        edgesWithVar.forEach { (f, t) ->
            if (f != variable) {
                relevantConnections.add(f)
            }
            if (t != variable) {
                relevantConnections.add(t)
            }
        }
        inducedGraph.addClique(relevantConnections)
        //inducedGraph.removeNode(variable)
    }
    return inducedGraph
}

