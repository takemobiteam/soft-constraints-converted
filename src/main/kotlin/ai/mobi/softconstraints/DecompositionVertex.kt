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
class DecompositionVertex(
    dictDecompositionVertex: SerializedVertex,
    private val decomp: Decomposition
) {
    val name: String = dictDecompositionVertex.name
    val variables: MutableList<Variable> = mutableListOf()
    val constraints: MutableList<ValuedConstraint> = mutableListOf()
    val inputVertices: MutableList<DecompositionVertex> = mutableListOf()
    var outputConstraint: ValuedConstraint? = null
    val operations: MutableList<Any> = mutableListOf()
    val derivedConstraints: MutableList<ValuedConstraint> = mutableListOf()

    init {
        val vcsp = decomp.vcsp

        // Resolve variables
        val variableNames = dictDecompositionVertex.variables
        for (variableName in variableNames) {
            val variable = vcsp.variableNamed(variableName)
            if (variable == null) {
                println("Undefined variable $variableName used in vertex $name of decomposition ${decomp.name}. Fix and reload.")
            } else {
                variables.add(variable)
            }
        }

        // Resolve constraints
        val constraintNames = dictDecompositionVertex.constraints
        for (constraintName in constraintNames) {
            val constraint = vcsp.getConstraintByName(constraintName)
            if (constraint == null) {
                println("Undefined constraint $constraintName used in vertex $name of decomposition ${decomp.name}. Fix and reload.")
            } else {
                constraints.add(constraint)
            }
        }
    }

    override fun toString(): String {
        return name
    }

    fun display() {
        println("$this:")
        println("    Variables: ${listToString(variables)}")
        println("    Constraints: ${listToString(constraints)}")
    }

    fun instantiateEnumerationOperators() {
        /*
            Instantiate operators for all input vertices first, and accumulate their associated (output) constraint.
         */
        val inputConstraints = mutableListOf<ValuedConstraint>()
        for (inputVertex in inputVertices) {
            inputVertex.instantiateEnumerationOperators()
            inputConstraints.add(inputVertex.outputConstraint!!)
        }

        operations.clear()
        derivedConstraints.clear()

        /* Will compose our constraints with the output constraints of our input */
        val opConstraints = constraints.toMutableList()
        opConstraints.addAll(inputConstraints)

        val vcspScope = decomp.vcsp.scope

        /* Compose constraints pairwise */
        var combinedConstraint = opConstraints.removeAt(0)

        for (constraint in opConstraints) {
            val combineOperation = Combine(this, combinedConstraint, constraint, vcspScope)
            operations.add(combineOperation)

            combinedConstraint = combineOperation.outputConstraint
            derivedConstraints.add(combinedConstraint)
        }

        /* Project the composition onto the vertex's variables */
        val projectOperation = Project(this, combinedConstraint, variables, vcspScope)
        operations.add(projectOperation)

        /* Record the constraint of the projection as the constraint of this vertex */
        val projectedConstraint = projectOperation.outputConstraint
        outputConstraint = projectedConstraint
        derivedConstraints.add(projectedConstraint)
    }

    fun nextBest(): ValuedAssignment? {
        // Delegate to the output constraint's nextBest method
        return outputConstraint?.nextBest()
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