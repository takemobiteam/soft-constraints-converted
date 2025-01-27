package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedEdge


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