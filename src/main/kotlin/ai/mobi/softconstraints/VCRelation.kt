package ai.mobi.softconstraints

/**
 *  Creates a relation object corresponding to dictionary description dict_relation.
 *     dict_relation ::= <ordered_assignments> ::= "[]" or “[“ <ordered_assignment> ("," <ordered_assignment>)*  “]”
 *     <ordered_assignment> ::= “[“ <string_value> ("," <string_value>)*  “]”
 */
class VCRelation(
    dictRelation: List<List<String>>,
    private val scope: VCScope
) {
    val assignments: MutableList<ValuedAssignment> = mutableListOf()

    init {
        if (dictRelation.isNotEmpty()) {
            for (dictVasn in dictRelation) {
                val (assignment, value) = assignmentParamsFromStrings(dictVasn)
                val vasn = ValuedAssignment(scope, assignment, value)
                scope.checkValuedAssignment(vasn)
                assignments.add(vasn)
            }

            // Sort assignments in descending order of value
            assignments.sortByDescending { it.value }
        }
    }

    fun appendAssignment(assignment: ValuedAssignment) {
        assignments.add(assignment)
    }

    fun ithAssignment(i: Int): ValuedAssignment? {
        return if (i < assignments.size) assignments[i] else null
    }

    fun containsAssignment(vasgn1: ValuedAssignment): Boolean {
        val asgn1 = vasgn1.assignment
        for (vasgn2 in assignments) {
            if (asgn1 == vasgn2.assignment) {
                return true
            }
        }
        return false
    }
}