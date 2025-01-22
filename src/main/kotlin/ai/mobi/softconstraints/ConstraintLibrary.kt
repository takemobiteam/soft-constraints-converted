package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedConstraintDecomposition
import ai.mobi.softconstraints.serde.SerializedValuedConstraintProblem
import kotlinx.serialization.json.Json
import java.io.File
import java.io.BufferedReader

class ConstraintLibrary(
    constraintDirectory: String = "",
    private val checkSchema: Boolean = true,
    private val trace: Boolean = true,
    schemaDirectory: String = "json-schemas"
) {
    private val vcspLibrary = mutableMapOf<String, VCSP>()
    private val decompositionLibrary = mutableMapOf<String, Any>()

    fun readVCSP(vcspRelativePath: String): VCSP {
        val classLoader = Thread.currentThread().contextClassLoader!!
        val inputStream = classLoader.getResourceAsStream("examples/full-adder-constraints.txt")!!
        val jsonContent =  inputStream.bufferedReader().use(BufferedReader::readText)
        val wrappedDictVCSP = Json.decodeFromString<SerializedValuedConstraintProblem>(jsonContent)

        val dictVCSP = wrappedDictVCSP.valued_constraint_problem
        val vcsp = VCSP(dictVCSP)
        vcspLibrary[vcsp.name] = vcsp

        if (trace) {
            println("Adding valued CSP ${vcsp.name} to library as $vcsp.")
        }
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
        if (trace) {
            println("Adding valued CSP decomposition ${decomposition.name} to library as $decomposition.")
        }
        return decomposition
    }

    private fun getVCSP(vcspName: String): VCSP = vcspLibrary[vcspName]!!

}

class FileMissingException(val filaPath: File, val constraintDirectory: File):
    Exception("File $filaPath, which describes a decomposition, does not exist in $constraintDirectory.")