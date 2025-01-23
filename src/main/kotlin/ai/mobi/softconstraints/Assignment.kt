package ai.mobi.softconstraints

/**
 * Parameters to an assignment.  This includes a list variable assignments, and a value.
 */
data class AssignmentParameters(val assignments: List<String>, val value: Float) {

    /**
     * Create an assignment from the input format.  This is a list of strings, where the last element in the list is
     * the value.
     */
    constructor(assignmentsAndValue: List<String>):
            this(assignmentsAndValue.dropLast(1), assignmentsAndValue.last().toFloat())
}

class ValuedAssignment(
    private val scope: VCScope,
    dictVAssignment: AssignmentParameters
) {
    val assignment = dictVAssignment.assignments
    private val value = dictVAssignment.value

    override fun toString(): String {
        return "${Utils.listToString(assignment)}:$value"
    }

    fun getValue(): Float {
        return value
    }

    fun project(outputScope: VCScope): ValuedAssignment {
        val inputVars = scope.orderedVars
        val inputAssignment = assignment
        val outputVars = outputScope.orderedVars

        val outputAssignment = mutableListOf<String>()
        var inputIndex = 0

        for (outputVar in outputVars) {
            // Find the matching variable in the input scope
            while (inputVars[inputIndex] != outputVar) {
                inputIndex++
                if (inputIndex >= inputVars.size) {
                    throw IllegalStateException(
                        "Input scope does not contain output variable: $outputVar"
                    )
                }
            }
            outputAssignment.add(inputAssignment[inputIndex])
        }

        return ValuedAssignment(outputScope, AssignmentParameters(outputAssignment, value))
    }

    /**
     *  Outputs an assignment to the variables of output_scope that is the composition of
     *  valued assignments self (input1) and vasgn2 (input2).  Assumes that output_scope is the union of
     *  the scopes of self and vasgn2.
     *
     *  Produces composition by looping through the variables of output_scope,
     *  interleaving corresponding values from the two input assignments.
     */
    fun combine(
        vasgn2: ValuedAssignment,
        outputScope: VCScope
    ): ValuedAssignment? {
        val input1Scope = scope
        val input1Vars = input1Scope.orderedVars
        val input1Assignment = assignment

        val input2Scope = vasgn2.scope
        val input2Vars = input2Scope.orderedVars
        val input2Assignment = vasgn2.assignment

        /* assignment being created */
        val outputVars = outputScope.orderedVars
        val outputAssignment = mutableListOf<String>()

        var input1Index = 0
        var input2Index = 0

        for (outputVar in outputVars) {
            val input1Value = if (input1Index < input1Vars.size && input1Vars[input1Index] == outputVar) {
                input1Assignment[input1Index++]
            } else {
                null
            }

            val input2Value = if (input2Index < input2Vars.size && input2Vars[input2Index] == outputVar) {
                input2Assignment[input2Index++]
            } else {
                null
            }

            val outputValue = when {
                input1Value == null -> input2Value!!
                input2Value == null -> input1Value
                input1Value == input2Value -> input1Value
                else -> return null // Conflict: different values assigned by inputs
            }

            outputAssignment.add(outputValue)
        }

        // Compose the value for the combined assignment
        val composedValue = this.getValue() + vasgn2.getValue()

        return ValuedAssignment(outputScope, AssignmentParameters(outputAssignment, composedValue))
    }
}