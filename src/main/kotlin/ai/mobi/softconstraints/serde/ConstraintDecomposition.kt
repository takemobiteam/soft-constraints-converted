package ai.mobi.softconstraints.serde

import kotlinx.serialization.Serializable

@Serializable
data class SerializedConstraintDecomposition(
    val constraint_decomposition: SerializedDecomposition
)

@Serializable
data class SerializedDecomposition(
    val name: String,
    val constraint_problem: String,
    val vertices: List<SerializedVertex>,
    val edges: List<SerializedEdge>
)

@Serializable
data class SerializedVertex(
    val name: String,
    val variables: List<String>,
    val constraints: List<String>
)

@Serializable
data class SerializedEdge(
    val source: String,
    val target: String
)