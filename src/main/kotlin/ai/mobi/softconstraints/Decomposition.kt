package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedDecomposition

class Decomposition(
    dictDecomposition: SerializedDecomposition,
    val vcsp: VCSP
) {
    val name: String = dictDecomposition.name
    val vertices: MutableList<DecompositionVertex> = mutableListOf()
    private val vertexDict: MutableMap<String, DecompositionVertex> = mutableMapOf()
    val edges: MutableList<DecompositionEdge> = mutableListOf()

    init {
        /*
                # library is of type clb.ConstraintLibrary

                # Creates a decomposition object corresponding to json description json_decomposition.
                #    <constraint_decomposition> ::= “{“ “name” “:” <string_name> “,”
                #                               “constraint_problem” “:” <string_name> “,”
                #                               “vertices” “:” <vertices> “,”
                #                               “edges” “:” <edges> "}"
                #    <vertices> ::= "[" <vertex> ("," <vertex>)* "]"
                #    <edges> ::= "[" <edge> ("," <edge>)* "]"
         */
        // Check and create vertices
        val dictVertices = dictDecomposition.vertices
        for (dictVertex1 in dictVertices) {
            val vertex1 = DecompositionVertex(dictVertex1, this)
            if (getVertexByName(vertex1.name) != null) {
                println("Duplicate vertex named ${vertex1.name} in decomposition $name. Fix and reload.")
            }
            vertexDict[vertex1.name] = vertex1
            vertices.add(vertex1)
        }

        // Check and create edges
        val dictEdges = dictDecomposition.edges
        for (dictEdge1 in dictEdges) {
            val edge1 = DecompositionEdge(dictEdge1, this)
            edges.add(edge1)
        }

        // Create the enumeration network
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
        println("   Edges: ${Utils.listToString(edges)}")
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