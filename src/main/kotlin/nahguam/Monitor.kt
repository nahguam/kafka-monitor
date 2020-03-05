package nahguam

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.ByteArrayDeserializer
import org.apache.kafka.common.serialization.ByteArraySerializer
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.lang.Thread.currentThread
import java.time.Clock.systemUTC
import java.util.concurrent.Executors.newScheduledThreadPool
import java.util.concurrent.atomic.AtomicLong

fun main(args: Array<String>) {
  runApplication<Monitor>(*args)
}

@ConfigurationPropertiesScan
@SpringBootApplication
class Monitor {
  @Bean
  fun clock() = systemUTC()!!

  @Bean
  fun service() = newScheduledThreadPool(4)!!

  @Bean
  fun generator(config: Config): () -> Id {
    val id = AtomicLong()
    return { Id(config.group, id.incrementAndGet()) }
  }

  @Bean
  fun producer(config: Config) = KafkaProducer<Void, Event>(mapOf(
      ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to config.bootstrapServers,
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to ByteArraySerializer::class.java,
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to EventSerDe::class.java,
      ProducerConfig.ACKS_CONFIG to config.acks
  ))

  @Bean
  fun consumer(config: Config) = KafkaConsumer<Void, Event>(mapOf(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to config.bootstrapServers,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to ByteArrayDeserializer::class.java,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to EventSerDe::class.java,
      ConsumerConfig.GROUP_ID_CONFIG to config.group
  ))

  @Bean
  fun runner(receiver: Receiver, sender: Sender) = ApplicationRunner {
    receiver.start()
    sender.start()
  }
}
