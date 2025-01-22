package ai.mobi.softconstraints

class Project(
    private val vertex: DecompositionVertex,
    private val opInput: ValuedConstraint,
    opVariables: List<Variable>,
    vcspScope: VCSPScope
) : Operation {
    val name: String = "P${count++}"
    val opVariables: List<Variable> = opVariables.sortedBy { it.getPosition() }
    val inputConstraint: ValuedConstraint = opInput
    val outputConstraint: ValuedConstraint
    private var index: Int = 0
    private var enumerationFinished: Boolean = false
    private val queue: MutableList<Any> = mutableListOf()

    init {
        // Check that opVariables is a subset of the input constraint's scope
        val isSubset = opVariables.all { it in opInput.scope.orderedVars }
        if (!isSubset) {
            println("Projecting to ${Utils.listToString(opVariables)}, some of which are not in constraint scope ${Utils.listToString(opInput.scope.orderedVars)}.")
        }

        // Create the output constraint
        val constraintDict = ConstraintDictionary(name, opVariables, listOf())
        outputConstraint = ValuedConstraint(constraintDict, vcspScope, this)
        outputConstraint.producer = this
    }

    override fun toString(): String {
        return "$name${Utils.listToString(opVariables)}(${inputConstraint.name})"
    }

    override fun nextBest(): ValuedAssignment? {
        while (!enumerationFinished) {
            // Generate the ith-best assignment of the input constraint
            val vasgn1 = inputConstraint.ithBest(index)
            if (vasgn1 != null) {
                // Project the assignment
                val projectedAssignment = vasgn1.project(outputConstraint.scope)
                index++
                if (projectedAssignment != null) {
                    return projectedAssignment
                }
            } else {
                enumerationFinished = true
            }
        }
        return null
    }

    companion object {
        private var count: Int = 1
    }
}