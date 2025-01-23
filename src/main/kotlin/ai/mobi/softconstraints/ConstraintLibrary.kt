package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedConstraintDecomposition
import ai.mobi.softconstraints.serde.SerializedValuedConstraintProblem
import kotlinx.serialization.json.Json
import java.io.BufferedReader

class ConstraintLibrary {
    private val vcspLibrary = mutableMapOf<String, VCSP>()
    private val decompositionLibrary = mutableMapOf<String, Any>()

    fun readVCSP(): VCSP {
        val classLoader = Thread.currentThread().contextClassLoader!!
        val inputStream = classLoader.getResourceAsStream("examples/full-adder-constraints.json")!!
        val jsonContent =  inputStream.bufferedReader().use(BufferedReader::readText)
        val wrappedDictVCSP = Json.decodeFromString<SerializedValuedConstraintProblem>(jsonContent)

        val dictVCSP = wrappedDictVCSP.vscp
        val vcsp = VCSP(dictVCSP)
        vcspLibrary[vcsp.name] = vcsp

        println("Adding valued CSP ${vcsp.name} to library as $vcsp.")
        return vcsp
    }

    fun readDecomposition(relativePath: String): Decomposition {
        val classLoader = Thread.currentThread().contextClassLoader!!
        val inputStream = classLoader.getResourceAsStream("examples/${relativePath}")!!
        val jsonContent =  inputStream.bufferedReader().use(BufferedReader::readText)
        val wrappedDictDecomposition = Json.decodeFromString<SerializedConstraintDecomposition>(jsonContent)

        val dictDecomposition = wrappedDictDecomposition.constraint_decomposition

        val vcspName = dictDecomposition.constraint_problem
        val vcsp = getVCSP(vcspName)

        val decomposition = Decomposition(dictDecomposition, vcsp)
        decompositionLibrary[decomposition.name] = decomposition
        println("Adding valued CSP decomposition ${decomposition.name} to library as $decomposition.")
        return decomposition
    }

    private fun getVCSP(vcspName: String): VCSP = vcspLibrary[vcspName]!!

}