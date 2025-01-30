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