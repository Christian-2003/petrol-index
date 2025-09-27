package de.christian2003.petrolindex.plugin.infrastructure.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter


/**
 * Serializer to serialize a LocalDate instance with the Kotlin serialization API.
 */
@Serializer(forClass = LocalDate::class)
class LocalDateSerializer: KSerializer<LocalDate> {

    /**
     * Formatter used for formatting local dates.
     */
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")


    /**
     * Serializes the local date instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized local date.
     * @param value     Local date to serialize.
     */
    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }


    /**
     * Deserializes a local date instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized local date.
     */
    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }

}
