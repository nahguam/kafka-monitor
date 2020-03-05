package nahguam

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.apache.kafka.common.serialization.Deserializer
import org.apache.kafka.common.serialization.Serializer

class EventSerDe : Serializer<Event>, Deserializer<Event> {
  private val mapper = jacksonObjectMapper()
  override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {}
  override fun serialize(topic: String?, data: Event?) = mapper.writeValueAsBytes(data)!!
  override fun deserialize(topic: String?, data: ByteArray?) = mapper.readValue<Event>(data!!)
  override fun close() {}
}
