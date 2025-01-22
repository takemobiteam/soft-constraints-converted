package ai.mobi.softconstraints

import java.io.File
import org.everit.json.schema.SchemaLoader
import org.json.JSONObject

object Utils {

    fun <T> listToString(list: List<T>): String {
        return list.joinToString(prefix = "[", postfix = "]", separator = ",") { it.toString() }
    }

    fun checkSchema(directoryPath: String, schemaPath: String) {
        val schemaFile = File(directoryPath, schemaPath)
        if (schemaFile.exists()) {
            val schemaContent = schemaFile.readText()
            try {
                val rawSchema = JSONObject(schemaContent)
                SchemaLoader.builder()
                    .schemaJson(rawSchema)
                    .draftV7Support()
                    .build()
                    .load()
                println("Schema $schemaPath is valid.")
            } catch (e: Exception) {
                println("Invalid JSON schema $schemaPath: ${e.message}")
            }
        } else {
            println("No file $schemaPath in $directoryPath.")
        }
    }
}