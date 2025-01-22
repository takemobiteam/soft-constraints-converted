package ai.mobi.softconstraints

object Utils {

    fun <T> listToString(list: List<T>): String {
        return list.joinToString(prefix = "[", postfix = "]", separator = ",") { it.toString() }
    }
}