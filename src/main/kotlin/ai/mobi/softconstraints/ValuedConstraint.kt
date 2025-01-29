package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedConstraint
import kotlin.collections.map

class ValuedConstraint(
    val name: String,
    scope: List<Variable>,
    relation: List<List<String>>,
    val producer: Operation? = null,
    vcspScope: VCSPScope,
) {
    val scope: VCScope
    val relation: VCRelation
    private var index: Int = 0

    init {
        if (!noDuplicates(scope.map { variable -> variable.name }))
            throw Exception("Duplicates!!!!")
        assertVarsInOrder(scope, vcspScope)
        this.scope = VCScope(scope, this, vcspScope)
        this.relation = VCRelation(relation, this.scope)
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