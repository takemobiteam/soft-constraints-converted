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
    println("\nThis file demonstrates constraint library interactions on the full adder example.\n")

    /*
        # 1) Create the Constraint Library, which is the main interface for solving valued CSPS.
        # It loads VCSPs and their decompositions from an example directory.
     */
    val library = ConstraintLibrary(
        constraintDirectory = "path/to/examples/",
        schemaDirectory = "path/to/json-schemas/"
    )

    /* 2) Load and display an example VCSP, the full-adder from the Sachenbacher Williams Paper */
    println("Load and display a full-adder VCSP from the Sachenbacher/Williams paper.")
    val vcsp = library.readVCSP("full-adder-constraints.txt")
    println()
    vcsp.display()

    // 3) Load and display a hyper tree decomposition for the full-adder.  This is an AND/OR decomposition.
    println("Load and display a hyper tree decomposition for the full-adder.\n")

    val decomp2 = library.readDecomposition("full-adder-bucket-tree.txt")

    println()
    decomp2.display()

    // 4) Generate a network of combine/join operations for the hyper tree decomposition
    println("\nGenerate a network of combine/join operations for the hyper tree decomposition.\n")

    decomp2.displayEnumerationOperators()
    decomp2.displayConstraintProducers()

    // 4) Examples of enumeration for the hyper tree decomposition
    println("\nGenerate the best assignment for vertex 4.")
    printNextBest(decomp2, "v4")

    println("\nFull assignments for V4 - V1")
    printBest(decomp2, "v4")
    printBest(decomp2, "v3")
    printBest(decomp2, "v2")
    printBest(decomp2, "v1")

    // 5) Load and display a bucket tree decomposition for the full-adder
    println("\nLoad and display a bucket tree decomposition for the full-adder.")
    val decomp1 = library.readDecomposition("full-adder-tree-decomposition.txt")

    println()
    decomp1.display()
    println()

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