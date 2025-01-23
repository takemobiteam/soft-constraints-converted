package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedConstraint

/**
 * The parameters needed to create a [ValuedConstraint]
 */
data class ConstraintParameters(
    val name: String,
    val scope: List<Variable>,
    val relation: List<List<String>>
) {
    init {
        assertVarsInOrder(scope)
        if (!noDuplicates(scope.map { variable -> variable.name }))
            throw Exception("Duplicates!!!!")
    }
}

fun SerializedConstraint.toConstraintDictionary(vcspScope: VCSPScope) = ConstraintParameters(
    name,
    scope.map { vcspScope.varDict[it]!! },
    relation
)

class ValuedConstraint(
    constraintParams: ConstraintParameters,
    vcspScope: VCSPScope,
    var producer: Operation? = null
) {
    val name: String
    val scope: VCScope
    val relation: VCRelation
    private var index: Int = 0

    init {
        assertVarsInOrder((constraintParams.scope))
        name = constraintParams.name
        scope = VCScope(constraintParams.scope, this, vcspScope)
        relation = VCRelation(constraintParams.relation, scope)
    }

    override fun toString(): String {
        return name
    }

    fun display() {
        println("$this:")
        println("   Scope: ${listToString(scope.orderedVars)}")
        println("   Relation:")
        for (assignment in relation.assignments) {
            println("      $assignment")
        }
    }

    fun nextBest(): ValuedAssignment? {
        val assignment = ithBest(index)
        index++
        return assignment
    }

    fun ithBest(i: Int): ValuedAssignment? {
        val assignment = relation.ithAssignment(i)
        return assignment ?: producer?.nextBest()?.also {
            relation.appendAssignment(it)
        }
    }

    fun containsAssignment(vasgn1: ValuedAssignment): Boolean {
        return relation.containsAssignment(vasgn1)
    }
}