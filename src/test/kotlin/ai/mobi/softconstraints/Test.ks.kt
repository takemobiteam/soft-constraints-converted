package ai.mobi.softconstraints

import kotlin.test.Test
import kotlin.test.assertEquals

class Test {
    val vars = listOf("a1", "a2", "e1", "e2", "o1", "u", "v", "w", "y")
    val constraints = listOf("fa1", "fa2", "fe1", "fe2", "fo1")

    @Test
    fun `Read VSCP`() {
        val vscp = readVCSP("full-adder-constraints")
        assertEquals("full_adder", vscp.name)
        assertEquals(vars, vscp.scope.orderedVars.map { it.name })
        assertEquals(vars.toSet(), vscp.scope.varDict.keys)
        assertEquals(constraints, vscp.constraints.map { it.name })
        constraints.forEach { it -> assertEquals(it, vscp.getConstraintByName(it)!!.name) }
    }

    @Test
    fun `Read decomposition`() {
        val vcsp = readVCSP("full-adder-constraints")
        val decomp = readDecomposition("full-adder-bucket-tree", vcsp)
        assertEquals("adder_bucket_tree", decomp.name)
        assertEquals(9, decomp.vertices.size)
        assertEquals(8, decomp.edges.size)
     }

    @Test
    fun `Next best bucket tree`() {
        val vcsp = readVCSP("full-adder-constraints")
        val decomp = readDecomposition("full-adder-bucket-tree", vcsp)
        assertNextBest("v4",
            listOf(
                listOf("1", "0", "0"),
                listOf("0", "0", "1"),
                listOf("0", "1", "1"),
                null),
            decomp)
        assertNextBest("v3",
            listOf(
                listOf("0", "0", "0"),
                null),
            decomp)
        assertNextBest("v2",
            listOf(
                null),
            decomp)
        assertNextBest("v1",
            listOf(
                null),
            decomp)
    }

    @Test
    fun `Next best decomposition`() {
        val vcsp = readVCSP("full-adder-constraints")
        val decomp = readDecomposition("full-adder-tree-decomposition", vcsp)
        assertNextBest("v3",
            listOf(
                listOf("G", "0"),
                listOf("B", "0"),
                listOf("B", "1"),
                null),
            decomp)
        assertNextBest("v2",
            listOf(
                listOf("B", "0", "0", "0", "1"),
                listOf("B", "0", "0", "1", "1"),
                listOf("B", "0", "1", "0", "1"),
                listOf("B", "0", "1", "1", "1"),
                null),
            decomp)
        assertNextBest("v2",
            listOf(null),
            decomp)
    }

    private fun assertNextBest(vertex: String, expected: List<List<String>?>, decomp: Decomposition) {
        expected.forEach { assertEquals(it, decomp.nextBest(vertex)?.assignment) }
    }

}