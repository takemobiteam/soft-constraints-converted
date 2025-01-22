package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedConstraintProblem

/**
 *         # Creates a VCSP object corresponding to json description json_vcsp.
 *         #    <VCSP> ::= “{“ “name” “:” <string_name> “,”
 *         #                   “scope” “:” <variables> ","
 *         #                   “constraints” “:” <constraints> "}"
 *         #    <constraints> ::= “{“ <constraint> ("," <constraint>)*  “}”
 */
class VCSP(dictVCSP: SerializedConstraintProblem) {
    val name = dictVCSP.name
    val scope: VCSPScope = VCSPScope(dictVCSP.scope)
    val constraints: MutableList<ValuedConstraint> = mutableListOf()
    private val constraintDict: MutableMap<String, ValuedConstraint> = mutableMapOf()

    init {
        val constraintList = dictVCSP.constraints
        for (dictValuedConstraint in constraintList) {
            val vConstraint = ValuedConstraint(dictValuedConstraint.toConstraintDictionary(scope), scope)
            constraints.add(vConstraint)

            // Check for duplicate constraint names
            if (constraintDict.containsKey(vConstraint.name)) {
                println("Duplicate constraint named ${vConstraint.name} in VCSP $name - $dictValuedConstraint. Fix and reload.")
            } else {
                constraintDict[vConstraint.name] = vConstraint
            }
        }
    }

    override fun toString(): String {
        return "p:$name"
    }

    fun getConstraintByName(name: String): ValuedConstraint? {
        return constraintDict[name]
    }

    fun variableNamed(variableName: String?) = scope.varDict[variableName]

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