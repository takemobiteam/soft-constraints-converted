package ai.mobi.softconstraints

import java.util.PriorityQueue

class Combine(
    /* Input and output constraints of operator */
    private val input1Constraint: ValuedConstraint,
    private val input2Constraint: ValuedConstraint,

    vcspScope: VCSPScope
) : Operation {
    val name: String = "C${count++}"

    /* True iff the scope of input1 is a superset of the scope of input2 */
    val input1SupersetInput2: Boolean =
        input1Constraint.scope.orderedVars.containsAll(input2Constraint.scope.orderedVars)

    val outputConstraint: ValuedConstraint
    private val queue: PriorityQueue<Triple<Int, Int, Int>> = PriorityQueue(compareBy { it.first })

    init {
        /* Union of input variables, placed in proper order */
        val combinedVariables = extractJointVariables(input1Constraint.scope, input2Constraint.scope)

        /* Create constraint denoting the result of operator and record operator as its producer */
        val constraintDict = ConstraintParameters(
            name,
            combinedVariables,
            emptyList(),
        )
        outputConstraint = ValuedConstraint(constraintDict, vcspScope, this)

        /* Search state for enumeration */
        queue.add(Triple(-1, 1, 1))
    }

    override fun toString(): String {
        return "$name(${input1Constraint.name}, ${input2Constraint.name})"
    }

    override fun nextBest(): ValuedAssignment? {
        while (queue.isNotEmpty()) {
            // Dequeue the next state
            val (priority, i, j) = queue.poll()
            val value = -priority // Convert back to positive value

            // Get assignments from input constraints
            val vasgn1 = input1Constraint.ithBest(i)
            if (vasgn1 != null) {
                val vasgn2 = input2Constraint.ithBest(j)
                if (vasgn2 != null) {
                    // Combine the two assignments
                    val combinedAssignment = vasgn1.combine(vasgn2, outputConstraint.scope)

                    // Add next siblings to the queue
                    if (!input1SupersetInput2) {
                        val vasgn1Sibling = input1Constraint.ithBest(i + 1)
                        if (vasgn1Sibling != null) {
                            val qPriority = -vasgn1Sibling.getValue() * vasgn2.getValue()
                            queue.add(Triple(qPriority.toInt(), i + 1, j))
                        }
                    }

                    if (i == 0) {
                        val vasgn2Sibling = input2Constraint.ithBest(j + 1)
                        if (vasgn2Sibling != null) {
                            val qPriority = -vasgn1.getValue() * vasgn2Sibling.getValue()
                            queue.add(Triple(qPriority.toInt(), i, j + 1))
                        }
                    }

                    // Return the consistent composition
                    if (combinedAssignment != null) {
                        return combinedAssignment
                    }
                }
            }
        }
        return null // No more assignments
    }

    companion object {
        private var count: Int = 1

        /**
         * Return ordered list of combined_variables from scopes 1 and 2.  The result is a list of variables from both,
         * but in the proper order
         */
        fun extractJointVariables(scope1: VCScope, scope2: VCScope): List<Variable> {
            val composedVariables = mutableListOf<Variable>()
            val s1Vars = scope1.orderedVars
            val s2Vars = scope2.orderedVars
            var s1Index = 0
            var s2Index = 0

            while (s1Index < s1Vars.size || s2Index < s2Vars.size) {
                if (s1Index < s1Vars.size && s2Index < s2Vars.size) {
                    val s1IndexPos = s1Vars[s1Index].position
                    val s2IndexPos = s2Vars[s2Index].position

                    if (s1IndexPos < s2IndexPos) {
                        /* Add variable from scope1 */
                        composedVariables.add(s1Vars[s1Index])
                        s1Index++
                    } else if (s1IndexPos == s2IndexPos) {
                        /* Add variable that appears both in scope1 and scope2*/
                        composedVariables.add(s1Vars[s1Index])
                        s1Index++
                        s2Index++
                    } else {
                        /* Add variable from scope2 */
                        composedVariables.add(s2Vars[s2Index])
                        s2Index++
                    }
                } else if (s1Index < s1Vars.size) {
                    /* Scope2 done, add variable from scope1 */
                    composedVariables.add(s1Vars[s1Index])
                    s1Index++
                } else {
                    /* Scope1 done, add variable from scope2 */
                    composedVariables.add(s2Vars[s2Index])
                    s2Index++
                }
            }

            return composedVariables
        }
    }
}