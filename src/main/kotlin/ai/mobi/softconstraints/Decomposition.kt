package ai.mobi.softconstraints

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
    fun bestAssignments(vertexName: String) = sequence {
        val vertex = getVertexByName(vertexName)

        /* Delegate to the vertex's nextBest method */
        yieldAll(vertex.bestAssignments())
    }

    fun getVertexByName(name: String) = vertexDict[name]!!

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
}

class DuplicateVertexException(vertex: DecompositionVertex, decompositionName: String) :
    Exception("Duplicate vertex named ${vertex.name} in decomposition $decompositionName")


