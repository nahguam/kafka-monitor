package nahguam

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener
import org.apache.kafka.common.TopicPartition
import java.time.Duration
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit.NANOSECONDS

fun ScheduledExecutorService.scheduleAtFixedRate(period: Duration, command: () -> Unit): ScheduledFuture<*> {
  return scheduleAtFixedRate(command, 0L, period.toNanos(), NANOSECONDS)
}

fun ScheduledExecutorService.schedule(delay: Duration, command: () -> Unit): ScheduledFuture<*> {
  return schedule(command, delay.toNanos(), NANOSECONDS)
}

fun Consumer<*, *>.subscribe(topic: String, onAssigned: () -> Unit) {
  subscribe(listOf(topic), object : ConsumerRebalanceListener {
    override fun onPartitionsRevoked(partitions: MutableCollection<TopicPartition>?) {
      //noop
    }

    override fun onPartitionsAssigned(partitions: MutableCollection<TopicPartition>?) {
      onAssigned()
    }
  })
}
