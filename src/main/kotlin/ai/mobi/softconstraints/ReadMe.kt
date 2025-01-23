package ai.mobi.softconstraints

fun printNextBest(decomposition: Decomposition, vertexName: String) {
    // Print the next best assignment for the output of vertexName in decomposition
    val assignment = decomposition.nextBest(vertexName)
    println()
    println("$vertexName: next best $assignment")
}

fun printBest(decomposition: Decomposition, vertexName: String) {
    // Print all best assignments for the output of vertexName in decomposition
    println()
    println("Assignments of $vertexName:")
    while (true) {
        val assignment = decomposition.nextBest(vertexName)
        if (assignment != null) {
            println(assignment)
        } else {
            break
        }
    }
}

fun main() {
    println("This file demonstrates constraint library interactions on the full adder example.\n")

    println("""
        
        1) Create the Constraint Library, which is the main interface for solving valued CSPS.
        It loads VCSPs and their decompositions from an example directory.
        
    """.trimIndent())

    println("""
        
        2) Load and display an example VCSP (\"Semiring-based Constraint OptimizationProblem\"), 
        the full-adder from the 
        [Sachenbacher Williams Paper](https://mers-papers.csail.mit.edu/Publications/2004/Sachenbacher-Williams_SOFT04/Sachenbacher-Williams_SOFT04_distribution.pdf)
        
    """.trimIndent())
    val vcsp = readVCSP("full-adder-constraints")
    vcsp.display()

    println("""
        
        3) Load and display a hyper tree (bucket tree) decomposition for the full-adder.
        This is an AND/OR decomposition.
        
    """.trimIndent())
    val decomp = readDecomposition("full-adder-bucket-tree", vcsp)
    decomp.display()

    println("""
        
        4a) Generate a network of combine/join operations for the hyper tree decomposition
        
    """.trimIndent())
    decomp.displayEnumerationOperators()
    decomp.displayConstraintProducers()

    println("""
        
        4b) Examples of enumeration for the hyper tree decomposition
        Generate the best assignment for vertex 4
        
    """.trimIndent())
    printNextBest(decomp, "v4")

    println("\nFull assignments for V4 - V1")
    printBest(decomp, "v4")
    printBest(decomp, "v3")
    printBest(decomp, "v2")
    printBest(decomp, "v1")

    println("""
        
        5) Load and display a bucket tree decomposition for the full-adder
        
    """.trimIndent())
    val decomp1 = readDecomposition("full-adder-tree-decomposition", vcsp)
    decomp1.display()

    // 6) Generate a network of combine/join operations for the bucket tree decomposition
    println("Generate a network of combine/join operations for the bucket tree decomposition.")
    decomp1.displayEnumerationOperators()
    decomp1.displayConstraintProducers()

    // 7) Enumerate a few vertices of the bucket tree
    println("\nEnumerate V1-V3 of the bucket tree.")
    printBest(decomp1, "v3")
    printBest(decomp1, "v2")
    printBest(decomp1, "v1")
}