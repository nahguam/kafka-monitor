package nahguam

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import java.time.Duration
import java.time.Duration.ofMillis
import java.time.Duration.ofMinutes
import java.util.UUID
import java.util.UUID.randomUUID

@ConstructorBinding
@ConfigurationProperties("monitor")
data class Config(
    val bootstrapServers: String,
    val topic: String,
    val group: String = randomUUID().toString(),
    val messageHz: Long = 100,
    val payloadSize: Int = 1024,
    val eventTimeout: Duration = ofMinutes(1),
    val acks: String = "all",
    val pollTimeout: Duration = ofMillis(100)
)
