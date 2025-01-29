package ai.mobi.softconstraints

typealias VariableName = String

class Variable(
    val name: VariableName,
    val domain: List<VariableName>,
) {
    override fun toString() = name

    fun display() {
        println("$name: ${listToString(domain)}")
    }

    fun positionIn(problemScope: VCSPScope) = problemScope.indexOf(this)
}