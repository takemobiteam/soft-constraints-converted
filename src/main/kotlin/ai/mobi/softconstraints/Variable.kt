package ai.mobi.softconstraints

class Variable(
    val name: String,
    val domain: List<String>,
    val position: Int
) {

    override fun toString(): String {
        return name
    }

    fun display() {
        println("$name: ${Utils.listToString(domain)}")
    }

    fun after(var2: Variable): Boolean {
        return this.position < var2.position
    }
}