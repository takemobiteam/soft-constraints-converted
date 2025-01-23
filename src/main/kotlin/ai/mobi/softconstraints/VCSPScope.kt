package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedVariable

/**
 * A list of [Variable]s, sorted by their specified order
 *
 * @param dictScope a list of variables, which must be in order
 */
class VCSPScope(dictScope: List<SerializedVariable>) {
    val orderedVars: List<Variable>
    val varDict: Map<String, Variable>

    init {
        val orderedVarsTemp = mutableListOf<Variable>()
        val valDictTemp = mutableMapOf<String, Variable>()
        for ((position, dictVar) in dictScope.withIndex()) {
            val varName = dictVar.name
            if (valDictTemp.containsKey(varName)) {
                throw DuplicateVariableInScope(dictVar, dictScope)
            } else {
                val varDomain = dictVar.domain
                val variable = Variable(varName, varDomain, position)
                orderedVarsTemp.add(variable)
                valDictTemp[varName] = variable
            }
        }
        orderedVars = orderedVarsTemp.toList()
        varDict = valDictTemp.toMap()
    }

    override fun toString(): String {
        return listToString(orderedVars)
    }

    fun display() {
        for (variable in orderedVars) {
            variable.display()
        }
    }
}

class DuplicateVariableInScope(dictVar: SerializedVariable, dictScope: List<SerializedVariable>):
    Exception("Duplicate variable $dictVar in VCSP scope $dictScope")