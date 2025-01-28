package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedDecomposition
import ai.mobi.softconstraints.serde.SerializedVertex

/**
Constraint Decomposition Format:

<constraint_decomposition> ::= “{“ “name” “:” <string_name> “,”
“constraint_problem” “:” <string_name> “,”
“vertices” “:” <vertices> “,”
“edges” “:” <edges> "}"

<vertices> ::= "[" <vertex> ("," <vertex>)* "]"
<edges> ::= "[" <edge> ("," <edge>)* "]"

<vertex> ::= "{" “name” : <string_name> “,”
“variables” : <string_names> ","
“constraints” : <string_names> "}"

<string_names> ::= "[]" | "[" <string_name> ("," <string_name>)* "]"

<edge> ::= "{" "source" : <string_name>
"target" : <string_name> "}"

library is of type clb.ConstraintLibrary

Creates a decomposition object corresponding to json description json_decomposition.
<constraint_decomposition>  ::= “{“ “name” “:” <string_name> “,”
“constraint_problem” “:” <string_name> “,”
“vertices” “:” <vertices> “,”
“edges” “:” <edges> "}"
<vertices> ::= "[" <vertex> ("," <vertex>)* "]"
<edges> ::= "[" <edge> ("," <edge>)* "]"
 */
data class Decomposition(
    val name: String,
    val vertexDict: Map<String, DecompositionVertex>,
    val edges: List<DecompositionEdge>,
    val vcsp: VCSP
) {
    fun nextBest(vertexName: String): ValuedAssignment? {
        // Find the vertex by its name
        val vertex = getVertexByName(vertexName)
            ?: throw IllegalArgumentException("Vertex with name $vertexName not found in decomposition $name.")

        // Delegate to the vertex's nextBest method
        return vertex.nextBest()
    }

    fun getVertexByName(name: String) = vertexDict[name]

    override fun toString() = "d:$name"

    fun display() {
        println("$this:")
        println("   Decomposes $vcsp")
        println("   Edges: ${listToString(edges)}")
        println("   Vertices:")
        for (vertex in vertexDict.values) {
            vertex.display()
        }
    }

    fun displayEnumerationOperators() {
        vertexDict.values.forEach { it.displayEnumerationOperators() }
    }

    fun displayConstraintProducers() {
        // Display the constraints and their producers for each vertex of decomposition
        println("Constraints and their producers for vertices of decomposition $this.")
        for (vertex in vertexDict.values) {
            val outputConstraint = vertex.outputConstraint
            val producer = outputConstraint?.producer
            println("Vertex: $vertex, output constraint $outputConstraint, producer $producer, derived constraints:")

            for (derivedConstraint in vertex.derivedConstraints) {
                val derivedProducer = derivedConstraint.producer
                println("   Constraint $derivedConstraint, producer $derivedProducer")
            }
        }
    }

//    fun instantiateEnumerationOperators() {
//        vertexIterator().forEach { it.instantiateEnumerationOperators() }
//    }

}

class DuplicateVertexException(vertex: DecompositionVertex, decompositionName: String) :
    Exception("Duplicate vertex named ${vertex.name} in decomposition $decompositionName")


fun createDecomposition(serializedDecomposition: SerializedDecomposition, vcsp: VCSP): Decomposition {
    val allVertices = mutableMapOf<String, DecompositionVertex>()
    val allEdges = serializedDecomposition.edges.map { DecompositionEdge(it.source, it.target) }
    serializedDecomposition.vertices.forEach {
        addVertex(
            it,
            serializedDecomposition,
            allVertices,
            vcsp.scope,
            vcsp.constraintDict
        )
    }
    return Decomposition(serializedDecomposition.name, allVertices, allEdges, vcsp)
}

private fun addVertex(
    vertex: SerializedVertex,
    serializedDecomposition: SerializedDecomposition,
    allVertices: MutableMap<String, DecompositionVertex>,
    scope: VCSPScope,
    constraints: Map<String, ValuedConstraint>,
): DecompositionVertex {
    /* Have I already been processed? */
    if (vertex.name in allVertices)
        return allVertices[vertex.name]!!
    else {
        /* Who am I the target of? */
        val serializedEdges = serializedDecomposition.edges.filter { it.target == vertex.name }
        val inputs = serializedEdges.map { (source, target) ->
            serializedDecomposition.vertices.first { it.name == source }
        }.map { serializedVertex ->
            addVertex(serializedVertex, serializedDecomposition, allVertices, scope, constraints)
        }

        val constraints = vertex.constraints.map { constraints[it]!! }
        val variables = vertex.variables.map { scope.varDict[it]!! }

        val inputConstraints = inputs.map { it.outputConstraint }

        val operations = mutableListOf<Operation>()
        val derivedConstraints = mutableListOf<ValuedConstraint>()

        val opConstraints = constraints.toMutableList()
        opConstraints.addAll(inputConstraints)

        /* Compose constraints pairwise */
        var combinedConstraint = opConstraints.removeAt(0)

        for (constraint in opConstraints) {
            val combineOperation = Combine(combinedConstraint, constraint, scope)
            operations.add(combineOperation)
            combinedConstraint = combineOperation.outputConstraint
            derivedConstraints.add(combinedConstraint)
        }

        /* Project the composition onto the vertex's variables */
        val projectOperation = Project(combinedConstraint, variables, scope)
        operations.add(projectOperation)

        /* Record the constraint of the projection as the constraint of this vertex */
        val projectedConstraint = projectOperation.outputConstraint
        val outputConstraint = projectedConstraint
        derivedConstraints.add(projectedConstraint)


        val vertex = DecompositionVertex(
            vertex.name,
            variables,
            constraints,
            inputs,
            outputConstraint,
            operations,
            derivedConstraints,
        )
        allVertices[vertex.name] = vertex
        return vertex
    }
}
