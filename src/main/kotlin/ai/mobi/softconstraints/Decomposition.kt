package ai.mobi.softconstraints

class Decomposition(
    dictDecomposition: Map<String, Any>,
    val vcsp: VCSP
) {
    val name: String = dictDecomposition["name"] as String
    val vertices: MutableList<DecompositionVertex> = mutableListOf()
    private val vertexDict: MutableMap<String, DecompositionVertex> = mutableMapOf()
    val edges: MutableList<DecompositionEdge> = mutableListOf()

    init {
        // Initialize vertices
        val vertexList = dictDecomposition["vertices"] as List<Map<String, Any>>
        for (dictVertex in vertexList) {
            val vertex = DecompositionVertex(dictVertex, this)
            vertices.add(vertex)
            if (vertexDict.containsKey(vertex.name)) {
                println("Duplicate vertex name: ${vertex.name}")
            } else {
                vertexDict[vertex.name] = vertex
            }
        }

        // Initialize edges
        val edgeList = dictDecomposition["edges"] as List<Map<String, String>>
        for (dictEdge in edgeList) {
            val edge = DecompositionEdge(dictEdge, this)
            edges.add(edge)
        }
    }

    fun nextBest(vertexName: String): ValuedAssignment? {
        // Find the vertex by its name
        val vertex = getVertexByName(vertexName)
            ?: throw IllegalArgumentException("Vertex with name $vertexName not found in decomposition $name.")

        // Delegate to the vertex's nextBest method
        return vertex.nextBest()
    }

    fun getVertexByName(name: String): DecompositionVertex? {
        return vertexDict[name]
    }

    override fun toString(): String {
        return "Decomposition(name=$name, vertices=${vertices.size}, edges=${edges.size})"
    }

    fun display() {
        println("$this:")
        println("   Decomposes ${vcsp}")
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
}