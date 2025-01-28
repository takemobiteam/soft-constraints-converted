package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedVertex

/**
 *  Creates a decomposition vertex object corresponding to json description dict_decomposition_vertex.
 *     <vertex> ::= "{" “name” : <string_name> “,”
 *                      “variables” : <string_names> ","
 *                      “constraints” : <string_names> "}"
 *
 *     <string_names> ::= "[]" | "[" <string_name> ("," <string_name>)* "]"
 */
data class DecompositionVertex(
    val name: String,
    val variables: List<Variable>,
    val constraints: List<ValuedConstraint>,
    val inputVertices: List<DecompositionVertex>,
    var outputConstraint: ValuedConstraint,
    val operations: List<Operation>,
    val derivedConstraints: List<ValuedConstraint>,
) {
    override fun toString(): String {
        return name
    }

    fun display() {
        println("$this:")
        println("    Variables: ${listToString(variables)}")
        println("    Constraints: ${listToString(constraints)}")
    }

    fun bestAssignments() = sequence {
        while (true) {
            val next = outputConstraint.nextBest()
            if (next == null) break
            else yield(next)
        }
    }

    fun displayEnumerationOperators() {
        println("Vertex $this:")
        println("  Inputs: ${listToString(inputVertices)}")
        println("  Operations: ${listToString(operations)}")

        inputVertices.forEach { decompositionVertex ->
            decompositionVertex.displayEnumerationOperators()
        }
    }
}