package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedConstraintProblem
import ai.mobi.softconstraints.serde.SerializedVariable

/**
 *  Creates a VCSP object corresponding to json description json_vcsp.
 *     <VCSP> ::= “{“ “name” “:” <string_name> “,”
 *                    “scope” “:” <variables> ","
 *                    “constraints” “:” <constraints> "}"
 *     <constraints> ::= “{“ <constraint> ("," <constraint>)*  “}”
 */
class VCSP(
    val name: String,
    val scope: VCSPScope,
    val constraints: List<ValuedConstraint>,
) {
    val constraintDict: MutableMap<String, ValuedConstraint> = mutableMapOf()

    init {
        // Check for duplicate constraint names
        for (constraint in constraints) {
            if (constraintDict.containsKey(constraint.name)) {
                println("Duplicate constraint named ${constraint.name} in VCSP $name - $constraint. Fix and reload.")
            } else {
                constraintDict[constraint.name] = constraint
            }
        }
    }

    override fun toString(): String {
        return "p:$name"
    }

    fun getConstraintByName(name: String): ValuedConstraint? {
        return constraintDict[name]
    }

    fun variableNamed(variableName: String) = scope.varByName(variableName)

    fun display() {
        // Print the VCSP details
        println("$this:")
        println("   Scope: $scope")
        scope.display()
        println("   Constraints:")
        for (constraint in constraints) {
            constraint.display()
        }
    }
}

fun vcsp(name: String) = VCSPBuilder(name)

class VCSPBuilder(val name: String) {
    val vars = mutableListOf<String>()
    val vals = mutableListOf<VariableBuilder>()
    val constraintNames = mutableListOf<String>()
    val constraintBuilders = mutableListOf<ConstraintBuilder>()

    fun variable(varName: VariableName) = VariableBuilder(this).also {
        vars.add(varName)
        vals.add(it)
    }

    fun constraint(constraintName: String) = ConstraintBuilder(this).also {
        constraintNames.add(constraintName)
        constraintBuilders.add(it)
    }

    fun build(): VCSP {
        val problemScope = vars.zip(vals).map { (varName, varBuilder) ->
            Variable(varName, varBuilder.vals!!)
        }

        return VCSP(
            name,
            problemScope,
            constraintNames.zip(constraintBuilders).map { (constraintName, constraintBuilder) ->
                ValuedConstraint(
                    constraintName,
                    constraintBuilder.scopeVars!!.map { problemScope.varByName(it) },
                    constraintBuilder.assignments.zip(constraintBuilder.values).map { (assignments, value) ->
                        assignments + listOf(value.toString())
                    },
                    null,
                    problemScope
                )
            }
        )
    }
}

class ConstraintBuilder(val vcspBuilder: VCSPBuilder)  {
    var scopeVars: List<String>? = null
    var assignments: MutableList<List<VariableValue>> = mutableListOf()
    var values: MutableList<Float> = mutableListOf()

    fun scope(vararg vars: String): ConstraintBuilder{
        scopeVars = vars.toList()
        return this
    }

    fun assignment(vararg vals: VariableValue): ConstraintBuilder {
        assignments.add(vals.toList())
        return this
    }

    fun value(value: Float): ConstraintBuilder {
        values.add(value)
        return this
    }

    fun constraint(name: String) = vcspBuilder.constraint(name)

    fun build() = vcspBuilder.build()
}

class VariableBuilder(val vcspBuilder: VCSPBuilder) {
    var vals: List<String>? = null
    fun domain(vararg vals: String): VCSPBuilder {
        this.vals = vals.toList()
        return vcspBuilder
    }
}