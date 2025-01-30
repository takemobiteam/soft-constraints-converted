package ai.mobi.softconstraints

typealias VariableName = String
typealias VariableValue = String

class Variable(
    val name: VariableName,
    val domain: List<VariableValue>,
) {
    override fun toString() = name

    fun display() {
        println("$name: ${listToString(domain)}")
    }

    fun positionIn(problemScope: VCSPScope) = problemScope.indexOf(this)
}