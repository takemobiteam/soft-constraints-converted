package ai.mobi.softconstraints.serde

import ai.mobi.softconstraints.VCSP
import ai.mobi.softconstraints.VCSPScope
import ai.mobi.softconstraints.ValuedConstraint
import ai.mobi.softconstraints.Variable
import ai.mobi.softconstraints.varByName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SerializedValuedConstraintProblem(
    @SerialName("valued_constraint_problem") val vscp: SerializedConstraintProblem
)

@Serializable
data class SerializedConstraintProblem(
    val name: String,
    val scope: List<SerializedVariable>,
    val constraints: List<SerializedConstraint>
) {
    fun deserialize(): VCSP {
        val problemScope = scope.map { it.deserialize() }
        return VCSP(
            name,
            problemScope,
            constraints.map { it.deserialize(problemScope) },
        )
    }
}

@Serializable
data class SerializedVariable(
    val name: String,
    val domain: List<String>
) {
    fun deserialize() = Variable(name, domain)
}

@Serializable
data class SerializedConstraint(
    val name: String,
    val scope: List<String>,
    val relation: List<List<String>>
) {
    fun deserialize(problemScope: VCSPScope) =
        ValuedConstraint(
            name,
            scope.map { problemScope.varByName(it) },
            relation,
            null,
            problemScope)
}