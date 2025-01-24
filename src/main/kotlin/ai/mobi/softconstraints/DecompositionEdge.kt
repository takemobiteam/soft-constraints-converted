package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedEdge

class DecompositionEdge(
    dictDecompositionEdge: SerializedEdge,
    private val decomp: Decomposition
) {
    val source: DecompositionVertex
    val target: DecompositionVertex

    init {
        val sourceName = dictDecompositionEdge.source
        source = decomp.getVertexByName(sourceName)
            ?: throw IllegalStateException("Edge uses undefined vertex $sourceName in decomposition ${decomp.name}. Fix and reload.")

        val targetName = dictDecompositionEdge.target
        target = decomp.getVertexByName(targetName)
            ?: throw IllegalStateException("Edge uses undefined vertex $targetName in decomposition ${decomp.name}. Fix and reload.")

        // Update input vertices of the target
        target.inputVertices.add(source)
    }

    override fun toString(): String {
        return "$source -> $target"
    }
}

fun noteEdge(dictDecompositionEdge: SerializedEdge, decomp: Decomposition) {
    val sourceName = dictDecompositionEdge.source
    val source = decomp.getVertexByName(sourceName)
        ?: throw IllegalStateException("Edge uses undefined vertex $sourceName in decomposition ${decomp.name}. Fix and reload.")

    val targetName = dictDecompositionEdge.target
    val target = decomp.getVertexByName(targetName)
        ?: throw IllegalStateException("Edge uses undefined vertex $targetName in decomposition ${decomp.name}. Fix and reload.")

    // Update input vertices of the target
    target.inputVertices.add(source)
}