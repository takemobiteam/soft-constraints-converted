package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedEdge

class DecompositionEdge(
    val source: String,
    val target: String,
) {
    override fun toString(): String {
        return "$source -> $target"
    }
}