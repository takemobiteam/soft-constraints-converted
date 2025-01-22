package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedValuedConstraintProblem
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

class ConstraintLibrary(
    constraintDirectory: String = "",
    private val checkSchema: Boolean = true,
    private val trace: Boolean = true,
    schemaDirectory: String = "json-schemas"
) {
    private val constraintDirectory: File
    private val schemaDirectory: File
    private val vcspLibrary = mutableMapOf<String, Any>()
    private val decompositionLibrary = mutableMapOf<String, Any>()

    init {
        // Initialize constraint directory
        this.constraintDirectory = if (constraintDirectory.isEmpty()) {
            File(System.getProperty("user.dir"))
        } else {
            File(constraintDirectory)
        }
        println(" - JSON problems and decompositions in ${this.constraintDirectory.absolutePath}.")

        // Initialize schema directory
        this.schemaDirectory = File(schemaDirectory)
        println(" - JSON schemas in ${this.schemaDirectory.absolutePath}.")
        println()
    }

    fun readVCSP(vcspRelativePath: String): VCSP {
        val filePath = constraintDirectory.resolve(vcspRelativePath)
        val jsonContent = filePath.readText()
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
        val filePath = constraintDirectory.resolve(relativePath)

        if (!filePath.exists()) throw FileMissingException(filePath, constraintDirectory)

        val jsonContent = filePath.toFile().readText()
        val wrappedDictDecomposition = JSONObject(jsonContent)
        val dictDecomposition = wrappedDictDecomposition.getJSONObject("constraint_decomposition")

        if (!decompositionValidator.isValid(wrappedDictDecomposition)) {
            println("File $filePath is not a valid JSON CSP decomposition description.")
            val validityMessages = decompositionValidator.iterErrors(wrappedDictDecomposition)
            println("     Error messages:")
            validityMessages.forEach { println("          $it") }
            return null
        }

        val vcspName = dictDecomposition.getString("constraint_problem")
        val vcsp = getVCSP(vcspName)
        if (vcsp == null) {
            println("Can't define decomposition specified by $filePath, its VCSP $vcspName isn't in the library.")
            return null
        }

        val decomposition = Decomposition(dictDecomposition.toMap(), vcsp)
        decompositionLibrary[decomposition.name] = decomposition
        if (trace) {
            println("Adding valued CSP decomposition ${decomposition.name} to library as $decomposition.")
        }
        return decomposition
    }
}

class FileMissingException(val filaPath: File, val constraintDirectory: File):
    Exception("File $filaPath, which describes a decomposition, does not exist in $constraintDirectory.")