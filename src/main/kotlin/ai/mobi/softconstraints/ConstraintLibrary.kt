package ai.mobi.softconstraints

import ai.mobi.softconstraints.serde.SerializedConstraintDecomposition
import ai.mobi.softconstraints.serde.SerializedValuedConstraintProblem
import kotlinx.serialization.json.Json
import java.io.BufferedReader

fun readVCSP(file: String): VCSP {
    val classLoader = Thread.currentThread().contextClassLoader!!
    val inputStream = classLoader.getResourceAsStream("examples/$file.json")!!
    val jsonContent = inputStream.bufferedReader().use(BufferedReader::readText)
    val serializedWrappedVCSP = Json.decodeFromString<SerializedValuedConstraintProblem>(jsonContent)

    val dictVCSP = serializedWrappedVCSP.vscp
    val vcsp = VCSP(dictVCSP)
    return vcsp
}

fun readDecomposition(relativePath: String, vcsp: VCSP): Decomposition {
    val classLoader = Thread.currentThread().contextClassLoader!!
    val inputStream = classLoader.getResourceAsStream("examples/$relativePath.json")!!
    val jsonContent = inputStream.bufferedReader().use(BufferedReader::readText)
    val serializedWrappedDecomp = Json.decodeFromString<SerializedConstraintDecomposition>(jsonContent)

    val dictDecomposition = serializedWrappedDecomp.constraint_decomposition
    val decomposition = createDecomposition(dictDecomposition, vcsp)
    println("Read valued CSP decomposition ${decomposition.name}")
    return decomposition
}
