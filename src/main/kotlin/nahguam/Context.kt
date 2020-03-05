package nahguam

import nahguam.State.CREATED
import nahguam.State.RECEIVED
import nahguam.State.SEND_FAILURE
import nahguam.State.SEND_SUCCESS
import java.time.Duration
import java.time.Duration.between
import java.time.Instant

class Context(
    private val start: Instant
) {
  private var state: State = CREATED
  var send: Duration? = null
  var transit: Duration? = null

  fun sent(e: Exception?, end: Instant) {
    if (state != RECEIVED) {
      send = between(start, end)
      state = e?.let { SEND_FAILURE } ?: SEND_SUCCESS
    }
  }

  fun received(end: Instant) {
    transit = between(start, end)
    state = RECEIVED
  }

  val stateName
    get() = state.name
}
