package ai.mobi.softconstraints

typealias VariableName = String

class Variable(
    val name: VariableName,
    val domain: List<VariableName>,
    val position: Int
) {
    override fun toString() = name

    fun display() {
        println("$name: ${listToString(domain)}")
    }
}