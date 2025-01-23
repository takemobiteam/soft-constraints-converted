package ai.mobi.softconstraints.serde

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
)

@Serializable
data class SerializedVariable(
    val name: String,
    val domain: List<String>
)

@Serializable
data class SerializedConstraint(
    val name: String,
    val scope: List<String>,
    val relation: List<List<String>>
)