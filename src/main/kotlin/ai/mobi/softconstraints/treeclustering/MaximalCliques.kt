package ai.mobi.softconstraints.treeclustering

import mu.KotlinLogging

private val log = KotlinLogging.logger {}

/**
 * Return maximal cliques of chordal graph g, ordered according to the reverse of ordering.
 *   Each vertex vi and its parents form a clique, denoted Ci.
 *   A clique is maximal if it is not a subset of another clique.
 *   Extracts and returns maximal cliques according to the reverse of ordering.
 * Inputs: induced graph g*, vertex ordering for g*.
 * Output: List of maximal cliques, according to the reverse of ordering.
 */
fun <V> orderedMaximalCliques(g: Graph<V>, ordering: List<V>): List<Set<V>> {

    val rorder = ordering.reversed()

    log.debug { "Extracting cliques from $g by reverse ordering $rorder" }

    val maximalCliques = mutableListOf<Set<V>>()

    /*
        Create cliques, going from children (last) to parents (first) in ordering,
        and saving cliques (in this reverse order) that are not a superset of any preceding clique
    */
    rorder.forEach { vi ->
        /* Find a clique for the current vertex */
        val clique = (g.findNeighbors(vi) intersect ordering) + vi

        /* If not a subset of a maximal clique, it's maximal */
        val maximal = maximalCliques.none { mc -> mc.containsAll(clique) }
        if (maximal) {
            /* Remove from maximal any element that is a subset of the new clique */
            var index = 0
            while (index < maximalCliques.size) {
                val cliqi = maximalCliques[index]
                if (clique.containsAll(cliqi))
                    maximalCliques.removeAt(index)
                else
                    index++
            }

            /* Add the new clique */
            maximalCliques.add(clique)
        }
        log.debug { "   $vi: $clique, max? : $maximal, max cliques: $maximalCliques" }
    }

    return maximalCliques
}