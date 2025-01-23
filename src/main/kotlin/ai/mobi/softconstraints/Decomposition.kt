package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedDecomposition

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
class Decomposition(
    dictDecomposition: SerializedDecomposition,
    val vcsp: VCSP
) {
    val name: String = dictDecomposition.name
    val vertices: MutableList<DecompositionVertex> = mutableListOf()
    val edges: MutableList<DecompositionEdge> = mutableListOf()
    private val vertexDict: MutableMap<String, DecompositionVertex> = mutableMapOf()

    init {
        /* Check and create vertices */
        val dictVertices = dictDecomposition.vertices
        for (dictVertex in dictVertices) {
            val vertex = DecompositionVertex(dictVertex, this)
            if (getVertexByName(vertex.name) != null)
                throw DuplicateVertexException(vertex, name)
            vertexDict[vertex.name] = vertex
            vertices.add(vertex)
        }

        /* Check and create edges */
        val dictEdges = dictDecomposition.edges
        for (dictEdge in dictEdges) {
            val edge = DecompositionEdge(dictEdge, this)
            edges.add(edge)
        }

        /* Create the enumeration network */
        instantiateEnumerationOperators()
    }

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
        for (vertex in vertices) {
            vertex.display()
        }
    }

    fun displayEnumerationOperators() {
        clearMarks()
        for (vertex in vertices) {
            vertex.displayEnumerationOperators()
        }
    }

    fun displayConstraintProducers() {
        // Display the constraints and their producers for each vertex of decomposition
        println("Constraints and their producers for vertices of decomposition $this.")
        for (vertex in vertices) {
            val outputConstraint = vertex.outputConstraint
            val producer = outputConstraint?.producer
            println("Vertex: $vertex, output constraint $outputConstraint, producer $producer, derived constraints:")

            for (derivedConstraint in vertex.derivedConstraints) {
                val derivedProducer = derivedConstraint.producer
                println("   Constraint $derivedConstraint, producer $derivedProducer")
            }
        }
    }

    fun instantiateEnumerationOperators() {
        clearMarks()

        /* Instantiate enumeration operators for each vertex, in topological order */
        vertices.forEach { it.instantiateEnumerationOperators() }
    }

    private fun clearMarks() = vertices.forEach { it.marked = false }
}

class DuplicateVertexException(vertex: DecompositionVertex, decompositionName: String):
    Exception("Duplicate vertex named ${vertex.name} in decomposition $decompositionName")