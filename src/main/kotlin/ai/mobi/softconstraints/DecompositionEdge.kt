package ai.mobi.softconstraints

class DecompositionEdge(
    val source: String,
    val target: String,
) {
    override fun toString(): String {
        return "$source -> $target"
    }
}