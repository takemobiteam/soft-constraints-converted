package ai.mobi.softconstraints

interface Operation {
    /**
     * Returns the assignment with the highest value that has not already been enumerated.
     */
    fun nextBest(): ValuedAssignment?
}