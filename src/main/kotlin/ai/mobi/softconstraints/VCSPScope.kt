package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedVariable

/**
 * A list of [Variable]s, sorted by their specified order
 */
class VCSPScope(dictScope: List<SerializedVariable>) {
    val orderedVars: MutableList<Variable> = mutableListOf()
    val varDict: MutableMap<String, Variable> = mutableMapOf()

    init {
        var position = 0 // Position of each variable in the ordering
        for (dictVar in dictScope) {
            val varName = dictVar.name
            if (varDict.containsKey(varName)) {
                println("Dropping duplicate variable $dictVar in VCSP scope $dictScope.")
            } else {
                val varDomain = dictVar.domain
                val variable = Variable(varName, varDomain, position)
                orderedVars.add(variable)
                varDict[varName] = variable
            }
            position++
        }
    }

    override fun toString(): String {
        return Utils.listToString(orderedVars)
    }

    fun display() {
        for (variable in orderedVars) {
            variable.display()
        }
    }
}