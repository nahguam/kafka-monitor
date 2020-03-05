package nahguam

import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import java.time.Duration.ofSeconds
import java.util.concurrent.ScheduledExecutorService

@Component
class Sender(
    private val service: ScheduledExecutorService,
    private val manager: ContextManager,
    private val producer: Producer<Void, Event>,
    private val config: Config
) {
  fun start() {
    service.scheduleAtFixedRate(ofSeconds(1).dividedBy(config.messageHz)) {
      manager.send { event, onComplete ->
        producer.send(ProducerRecord<Void, Event>(config.topic, event)) { _, e ->
          onComplete(e)
        }
      }
    }
  }
}
