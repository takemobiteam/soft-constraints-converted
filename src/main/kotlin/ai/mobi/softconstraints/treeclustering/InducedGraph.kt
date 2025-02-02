@file:Suppress("UnstableApiUsage")

package ai.mobi.softconstraints.treeclustering

import mu.KotlinLogging
import kotlin.math.max

private val log = KotlinLogging.logger {}

/** Note: Assumes vertices are indices, starting at 0 (used by list parents). */
fun <T> inducedGraph(g: Graph<T>, ordering: List<T>): Pair<Graph<T>, Int> {
    /*
        Create the induced graph of g for ordering using triangulation
        (Dechter Constraint Processing, Pgs 86-76).

        An induced graph is a chordal graph (a graph is chordal if all cycles with 4 or more vertices
        have a chord, which is an edge between two vertices in the cycle that are not adjacent).

        Inputs: undirected graph g, an ordering of g's vertices (max cardinality ordering).
                Neighbors of vi that precede in ordering are parents and those following are children.
                Given ordering, width of vi is its number of parents and
                graph width is max width over all vertices.
                In Dechter, pg. 86-90, trees are depicted upside down (parents below children).
        Outputs: induced graph g* of g based on ordering, maximum width of induced graph g*.
    */
    log.debug { "Creating induced graph for ordering $ordering and graph $g" }
    val gStar = g.copy()
    var maxWidth = 0

    /* Process vertices in reverse order, from descendants to ancestors (top to bottom in upside down graphs) */
    ordering.reversed().forEach { vj ->
        /*
            Find the parents of v_j in induced graph g*.
            These are neighboring vertices in g* that appear before v_j in ordering.
        */
        val parents = gStar.findNeighbors(vj)
            .filter { it in ordering }
            .filter { ordering.indexOf(it) < ordering.indexOf(vj) }
        log.debug { "Connect parents of $vj" }
        log.debug { "  parents $parents" }

        /*
            Update max width with the induced width of v_j, which is the number of (induced) parents
            where induced parents are those added below
        */
        maxWidth = max(maxWidth, parents.size)

        /* Connect the parents of v_j in induced graph g*, (producing induced parents) */
        for (i in parents.indices) {
            for (k in i + 1 until parents.size) {
                /* Add edge between all pairs(i, k) of parents that are not already in g_star. */
                if (parents[k] !in gStar.findNeighbors(parents[i])) {
                    log.debug { "    Adding edge [${parents[i]},${parents[k]}]" }
                    gStar.addEdge(parents[i], parents[k])
                }
            }
        }
    }
    log.debug { "  Width: $maxWidth, Induced graph: $gStar" }
    return Pair(gStar, maxWidth)
}
