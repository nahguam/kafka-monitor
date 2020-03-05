package nahguam

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tags
import io.micrometer.core.instrument.Timer
import org.apache.commons.lang3.RandomStringUtils.randomAscii
import org.springframework.stereotype.Component
import java.time.Clock
import java.util.concurrent.ScheduledExecutorService

@Component
class ContextManager(
    private val service: ScheduledExecutorService,
    private val clock: Clock,
    private val registry: MeterRegistry,
    private val generator: () -> Id,
    private val config: Config
) {
  val contexts = mutableMapOf<Id, Context>()

  fun send(send: (Event, (Exception?) -> Unit) -> Unit) {
    val id = generator()
    val event = Event(id, randomAscii(config.payloadSize))
    val context = Context(clock.instant())
    contexts[id] = context
    service.schedule(config.eventTimeout) {
      contexts.remove(id)
      val tags = Tags.of("state", context.stateName)
      Timer.builder("monitor.send").tags(tags)
          .publishPercentiles(0.5, 0.95, 0.99, 0.999)
          .register(registry)
          .record(context.send ?: config.eventTimeout)
      Timer.builder("monitor.transit").tags(tags)
          .publishPercentiles(0.5, 0.95, 0.99, 0.999)
          .register(registry)
          .record(context.transit ?: config.eventTimeout)
    }
    send(event) { context.sent(it, clock.instant()) }
  }

  fun receive(id: Id) {
    contexts[id]?.also { it.received(clock.instant()) }
  }
}

