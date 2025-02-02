package ai.mobi.softconstraints.serde

import ai.mobi.softconstraints.Combine
import ai.mobi.softconstraints.Decomposition
import ai.mobi.softconstraints.DecompositionEdge
import ai.mobi.softconstraints.DecompositionVertex
import ai.mobi.softconstraints.Operation
import ai.mobi.softconstraints.Project
import ai.mobi.softconstraints.VCSP
import ai.mobi.softconstraints.VCSPScope
import ai.mobi.softconstraints.ValuedConstraint
import ai.mobi.softconstraints.varByName
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

fun createDecomposition(serializedDecomposition: SerializedDecomposition, vcsp: VCSP): Decomposition {
    val allVertices = mutableMapOf<String, DecompositionVertex>()
    val allEdges = serializedDecomposition.edges.map { DecompositionEdge(it.source, it.target) }
    serializedDecomposition.vertices.forEach {
        addVertex(
            it,
            serializedDecomposition,
            allVertices,
            vcsp.scope,
            vcsp.constraintDict
        )
    }
    return Decomposition(serializedDecomposition.name, allVertices, allEdges, vcsp)
}

private fun addVertex(
    vertex: SerializedVertex,
    serializedDecomposition: SerializedDecomposition,
    allVertices: MutableMap<String, DecompositionVertex>,
    scope: VCSPScope,
    constraints: Map<String, ValuedConstraint>,
): DecompositionVertex {
    /* Have I already been processed? */
    if (vertex.name in allVertices)
        return allVertices[vertex.name]!!
    else {
        /* Who am I the target of? */
        val serializedEdges = serializedDecomposition.edges.filter { it.target == vertex.name }
        val inputs = serializedEdges.map { (source, target) ->
            serializedDecomposition.vertices.first { it.name == source }
        }.map { serializedVertex ->
            addVertex(serializedVertex, serializedDecomposition, allVertices, scope, constraints)
        }

        val constraints = vertex.constraints.map { constraints[it]!! }
        val variables = vertex.variables.map { scope.varByName(it) }

        val inputConstraints = inputs.map { it.outputConstraint }

        val operations = mutableListOf<Operation>()
        val derivedConstraints = mutableListOf<ValuedConstraint>()

        val opConstraints = constraints.toMutableList()
        opConstraints.addAll(inputConstraints)

        /* Compose constraints pairwise */
        var combinedConstraint = opConstraints.removeAt(0)

        for (constraint in opConstraints) {
            val combineOperation = Combine(combinedConstraint, constraint, scope)
            operations.add(combineOperation)
            combinedConstraint = combineOperation.outputConstraint
            derivedConstraints.add(combinedConstraint)
        }

        /* Project the composition onto the vertex's variables */
        val projectOperation = Project(combinedConstraint, variables, scope)
        operations.add(projectOperation)

        /* Record the constraint of the projection as the constraint of this vertex */
        val projectedConstraint = projectOperation.outputConstraint
        val outputConstraint = projectedConstraint
        derivedConstraints.add(projectedConstraint)


        val vertex = DecompositionVertex(
            vertex.name,
            variables,
            constraints,
            inputs,
            outputConstraint,
            operations,
            derivedConstraints,
        )
        allVertices[vertex.name] = vertex
        return vertex
    }
}