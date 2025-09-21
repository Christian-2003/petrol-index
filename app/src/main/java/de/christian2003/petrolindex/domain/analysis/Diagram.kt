package de.christian2003.petrolindex.domain.analysis


/**
 * Models a diagram that is created by the analysis.
 *
 * @param values    Values of the diagram.
 */
data class Diagram(
    val values: List<Double>
) {

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
        fun build(): Diagram {
            return Diagram(
                values = values
            )
        }

    }

}
