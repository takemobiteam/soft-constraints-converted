package ai.mobi.softconstraints

class DecompositionEdge(
    dictDecompositionEdge: Map<String, String>,
    private val decomp: Decomposition
) {
    val source: DecompositionVertex
    val target: DecompositionVertex

    init {
        val sourceName = dictDecompositionEdge["source"] ?: throw IllegalArgumentException("Source not specified.")
        source = decomp.getVertexByName(sourceName)
            ?: throw IllegalStateException("Edge uses undefined vertex $sourceName in decomposition ${decomp.name}. Fix and reload.")

        val targetName = dictDecompositionEdge["target"] ?: throw IllegalArgumentException("Target not specified.")
        target = decomp.getVertexByName(targetName)
            ?: throw IllegalStateException("Edge uses undefined vertex $targetName in decomposition ${decomp.name}. Fix and reload.")

        // Update input vertices of the target
        target.inputVertices.add(source)
    }

    override fun toString(): String {
        return "$source -> $target"
    }
}