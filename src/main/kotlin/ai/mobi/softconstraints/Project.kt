package ai.mobi.softconstraints

class Project(
    private val vertex: DecompositionVertex,
    private val opInput: ValuedConstraint,
    opVariables: List<Variable>,
    vcspScope: VCSPScope
) : Operation {
    val name: String = "P${count++}"
    val opVariables: List<Variable> = opVariables.sortedBy { it.position }
    val inputConstraint: ValuedConstraint = opInput
    val outputConstraint: ValuedConstraint

    /* # Search state for enumeration. */
    private var index: Int = 0
    private var enumerationFinished: Boolean = false

    init {
        // Check that opVariables is a subset of the input constraint's scope
        assertVarsInOrder(this.opVariables)
        val isSubset = opVariables.all { it in opInput.scope.orderedVars }
        if (!isSubset) {
            println("Projecting to ${listToString(opVariables)}, some of which are not in constraint scope ${listToString(opInput.scope.orderedVars)}.")
        }

        // Create the output constraint
        val constraintDict = ConstraintParameters(name, this.opVariables, listOf())
        outputConstraint = ValuedConstraint(constraintDict, vcspScope, this)
        outputConstraint.producer = this
    }

    override fun toString(): String {
        return "$name${listToString(opVariables)}(${inputConstraint.name})"
    }

    override fun nextBest(): ValuedAssignment? {
        while (!enumerationFinished) {
            // Generate the ith-best assignment of the input constraint
            val vasgn1 = inputConstraint.ithBest(index)
            if (vasgn1 != null) {
                // Project the assignment
                val projectedAssignment = vasgn1.project(outputConstraint.scope)!!
                index++
                if (outputConstraint.containsAssignment(projectedAssignment)) {
                    /* Projection is already known, go to next */
                    continue
                }
                /* Projection is new, return as next best */
                return projectedAssignment
            } else {
                /* Flag no remaining assignments */
                enumerationFinished = true
            }
        }
        return null
    }

    companion object {
        private var count: Int = 1
    }
}