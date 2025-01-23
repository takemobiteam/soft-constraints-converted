package ai.mobi.softconstraints

object Utils {

    fun <T> listToString(list: List<T>): String {
        return list.joinToString(prefix = "[", postfix = "]", separator = ",") { it.toString() }
    }
}

fun <T> noDuplicates(xs: Collection<T>) = xs.toList().size == xs.toSet().size