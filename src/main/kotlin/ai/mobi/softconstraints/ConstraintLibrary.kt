package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedConstraintDecomposition
import ai.mobi.softconstraints.serde.SerializedValuedConstraintProblem
import kotlinx.serialization.json.Json
import java.io.BufferedReader

class ConstraintLibrary {
    private val vcspLibrary = mutableMapOf<String, VCSP>()
    private val decompositionLibrary = mutableMapOf<String, Any>()

    fun readVCSP(file: String): VCSP {
        val classLoader = Thread.currentThread().contextClassLoader!!
        val inputStream = classLoader.getResourceAsStream("examples/$file.json")!!
        val jsonContent =  inputStream.bufferedReader().use(BufferedReader::readText)
        val wrappedDictVCSP = Json.decodeFromString<SerializedValuedConstraintProblem>(jsonContent)

        val dictVCSP = wrappedDictVCSP.vscp
        val vcsp = VCSP(dictVCSP)
        vcspLibrary[vcsp.name] = vcsp
        return vcsp
    }

    fun readDecomposition(relativePath: String): Decomposition {
        val classLoader = Thread.currentThread().contextClassLoader!!
        val inputStream = classLoader.getResourceAsStream("examples/$relativePath.json")!!
        val jsonContent =  inputStream.bufferedReader().use(BufferedReader::readText)
        val wrappedDictDecomposition = Json.decodeFromString<SerializedConstraintDecomposition>(jsonContent)

        val dictDecomposition = wrappedDictDecomposition.constraint_decomposition

        val vcspName = dictDecomposition.constraint_problem
        val vcsp = getVCSP(vcspName)

        val decomposition = Decomposition(dictDecomposition, vcsp)
        decompositionLibrary[decomposition.name] = decomposition
        return decomposition
    }

    private fun getVCSP(vcspName: String): VCSP = vcspLibrary[vcspName]!!

}