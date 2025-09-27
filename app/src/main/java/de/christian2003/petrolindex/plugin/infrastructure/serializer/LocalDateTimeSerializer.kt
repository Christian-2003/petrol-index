package de.christian2003.petrolindex.plugin.infrastructure.serializer

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


/**
 * Serializer to serialize a LocalDateTime instance with the Kotlin serialization API.
 */
@Serializer(forClass = LocalDateTime::class)
class LocalDateTimeSerializer: KSerializer<LocalDateTime> {

    /**
     * Formatter used for formatting local date times.
     */
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME //Format "yyyy-MM-ddTHH:mm:ss"


    /**
     * Serializes the local date time instance to the encoder passed.
     *
     * @param encoder   Encoder into which to encode the serialized local date.
     * @param value     Local date time to serialize.
     */
    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }


    /**
     * Deserializes a local date time instance from the decoder passed.
     *
     * @param decoder   Decoder to decode a value.
     * @return          Deserialized local date time.
     */
    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }

}
