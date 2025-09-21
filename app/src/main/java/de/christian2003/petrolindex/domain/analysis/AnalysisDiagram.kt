package de.christian2003.petrolindex.domain.analysis


/**
 * Value object models a diagram that is created by the analysis.
 *
 * @param values    Values of the diagram.
 * @param min       Min value from the diagram.
 * @param max       Max value from the diagram.
 */
data class AnalysisDiagram(
    val values: List<Double>,
    val min: Double,
    val max: Double
) {

    /**
     * Initializes the diagram.
     */
    init {
        require(min >= 0) { "Min value cannot be negative" }
        require(max >= 0) { "Max value cannot be negative" }
        require(min <= max) { "Min value cannot be greater than max value" }
    }


    /**
     * Builder for the diagram.
     */
    class Builder() {

        /**
         * Values for the diagram that is being built.
         */
        private val values: MutableList<Double> = mutableListOf()


        /**
         * Adds the specified value to the diagram.
         *
         * @param value Value to add to the diagram.
         * @return      Instance of this builder.
         */
        fun addValue(value: Double): Builder {
            values.add(value)
            return this
        }


        /**
         * Builds the diagram.
         *
         * @return  Diagram.
         */
        fun build(): AnalysisDiagram {
            var min = 0.0
            var max = 0.0
            if (!values.isEmpty()) {
                min = values.min()
                max = values.max()
            }
            return AnalysisDiagram(
                values = values,
                min = min,
                max = max
            )
        }

    }

}
