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
        assertEquals(9, decomp.vertexDict.size)

        val v1 = decomp.vertexDict["v1"]!!
        val v2 = decomp.vertexDict["v2"]!!
        val v3 = decomp.vertexDict["v3"]!!
        val v4 = decomp.vertexDict["v4"]!!
        val v5 = decomp.vertexDict["v5"]!!
        val v6 = decomp.vertexDict["v6"]!!
        val v7 = decomp.vertexDict["v7"]!!
        val v8 = decomp.vertexDict["v8"]!!
        val v9 = decomp.vertexDict["v9"]!!

        val fa1 = vcsp.constraintDict["fa1"]!!
        val fa2 = vcsp.constraintDict["fa2"]!!
        val fe1 = vcsp.constraintDict["fe1"]!!
        val fe2 = vcsp.constraintDict["fe2"]!!
        val fo1 = vcsp.constraintDict["fo1"]!!

        val a1 = vcsp.variableNamed("a1")!!
        val a2 = vcsp.variableNamed("a2")!!
        val e1 = vcsp.variableNamed("e1")!!
        val e2 = vcsp.variableNamed("e2")!!
        val o1 = vcsp.variableNamed("o1")!!
        val u = vcsp.variableNamed("u")!!
        val v = vcsp.variableNamed("v")!!
        val w = vcsp.variableNamed("w")!!
        val y = vcsp.variableNamed("y")!!

        assertEquals("v1", v1.name)
        assertEquals(setOf(u), v1.variables.toSet())
        assertEquals(setOf(v2, v8), v1.inputVertices.toSet())
        assertEquals(setOf(), v1.constraints.toSet())

        assertEquals("v2", v2.name)
        assertEquals(setOf(v, u), v2.variables.toSet())
        assertEquals(setOf(v3, v6), v2.inputVertices.toSet())
        assertEquals(setOf(), v2.constraints.toSet())

        assertEquals("v3", v3.name)
        assertEquals(setOf(u, v, w), v3.variables.toSet())
        assertEquals(setOf(v4, v9), v3.inputVertices.toSet())
        assertEquals(setOf(), v3.constraints.toSet())

        assertEquals("v4", v4.name)
        assertEquals(setOf(u, w, y), v4.variables.toSet())
        assertEquals(setOf(v5, v7), v4.inputVertices.toSet())
        assertEquals(setOf(), v4.constraints.toSet())

        assertEquals("v5", v5.name)
        assertEquals(setOf(a1, w, y), v5.variables.toSet())
        assertEquals(setOf(), v5.inputVertices.toSet())
        assertEquals(setOf(fa1), v5.constraints.toSet())

        assertEquals("v6", v6.name)
        assertEquals(setOf(a2, v, u), v6.variables.toSet())
        assertEquals(setOf(), v6.inputVertices.toSet())
        assertEquals(setOf(fa2), v6.constraints.toSet())

        assertEquals("v7", v7.name)
        assertEquals(setOf(e1, y, u), v7.variables.toSet())
        assertEquals(setOf(), v7.inputVertices.toSet())
        assertEquals(setOf(fe1), v7.constraints.toSet())

        assertEquals("v8", v8.name)
        assertEquals(setOf(e2, u), v8.variables.toSet())
        assertEquals(setOf(), v8.inputVertices.toSet())
        assertEquals(setOf(fe2), v8.constraints.toSet())

        assertEquals("v9", v9.name)
        assertEquals(setOf(o1, w, v), v9.variables.toSet())
        assertEquals(setOf(), v9.inputVertices.toSet())
        assertEquals(setOf(fo1), v9.constraints.toSet())


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