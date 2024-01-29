package kg.dev.videoeditor.extensions

import androidx.media3.common.Player
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration


fun Player.currentPositionFlow(
    updateFrequency: Duration = 1.seconds,
) = flow {
    while (true) {
        if (isPlaying) emit(currentPosition.toDuration(DurationUnit.MILLISECONDS))
        delay(updateFrequency)
    }
}.flowOn(Dispatchers.Main)

fun Player.remainingTimeFlow(
    updateFrequency: Duration = 1.seconds,
) = flow {
    while (true) {
        if (isPlaying) emit(
            kotlin.math.abs(duration - currentPosition).toDuration(DurationUnit.MILLISECONDS)
        )
        delay(updateFrequency)
    }
}.flowOn(Dispatchers.Main)