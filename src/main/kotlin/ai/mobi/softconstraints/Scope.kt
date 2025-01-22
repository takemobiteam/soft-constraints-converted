package ai.mobi.softconstraints

class VCScope(
    vars: List<Variable>,
    private val constraint: ValuedConstraint,
    vcspScope: VCSPScope
) {
    val orderedVars: MutableList<Variable> = mutableListOf()
    private val varsDict: MutableMap<String, Variable> = mutableMapOf()

    init {
        for (vari in vars) {
            // Directly add variable instance
            orderedVars.add(vari)
            val variName = vari.name
            if (varsDict.containsKey(variName)) throw DuplicateVariableName(variName, vars)
            varsDict[variName] = vari
        }

        // Check variable order consistency (if needed, based on additional functionality)
    }

    override fun toString(): String {
        return Utils.listToString(orderedVars)
    }
}

class DuplicateVariableName(val varName: String, val vars: List<Variable>):
    Exception("Variable $varName is duplicated in constraint scope $vars")