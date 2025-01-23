package ai.mobi.softconstraints

class VCScope(
    vars: List<Variable>,
    private val constraint: ValuedConstraint,
    vcspScope: VCSPScope
) {
    val orderedVars: MutableList<Variable> = mutableListOf()
    private val varsDict: MutableMap<String, Variable> = mutableMapOf()

    val numVariables: Int
        get() = orderedVars.size

    init {
        for (vari in vars) {
            // Directly add variable instance
            orderedVars.add(vari)
            val variName = vari.name
            if (varsDict.containsKey(variName))
                throw DuplicateVariableName(variName, vars)
            varsDict[variName] = vari
        }

        /* Check variable order consistency */
        assertVarsInOrder(orderedVars)
    }

    override fun toString(): String {
        return Utils.listToString(orderedVars)
    }

    fun checkValuedAssignment(vasgn1: ValuedAssignment): Boolean {
        // Return true if and only if each variable in the scope is assigned a value in its domain
        val asgn1 = vasgn1.assignment
        if (asgn1.size != numVariables) {
            println("Wrong number of assignments in $vasgn1, expecting ${numVariables}. Fix and reload.")
            return false
        } else {
            var success = true
            for ((value, variable) in asgn1.zip(orderedVars)) {
                val isInDomain = value in variable.domain
                if (!isInDomain) {
                    println("${variable.name} = $value is not in domain ${variable.domain} for $vasgn1. Fix and reload.")
                }
                success = success && isInDomain
            }
            return success
        }
    }
}

class DuplicateVariableName(val varName: String, val vars: List<Variable>):
    Exception("Variable $varName is duplicated in constraint scope $vars")

tailrec fun assertVarsInOrder(vs: List<Variable>): Unit {
    if (vs.size > 1)
        if (vs[0].position >= vs[1].position)
            throw Exception("Variables out of order in $vs")
        else assertVarsInOrder(vs.drop(1))
}