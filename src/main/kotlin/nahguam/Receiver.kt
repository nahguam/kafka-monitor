package nahguam

import org.apache.kafka.clients.consumer.Consumer
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService

@Component
class Receiver(
    private val service: ExecutorService,
    private val manager: ContextManager,
    private val consumer: Consumer<Void, Event>,
    private val config: Config
) {
  fun start() {
    val latch = CountDownLatch(1)
    service.submit {
      consumer.subscribe(config.topic) { latch.countDown() }
      while (true) {
        consumer.poll(config.pollTimeout).forEach { manager.receive(it.value().id) }
      }
    }
    latch.await()
  }
}
