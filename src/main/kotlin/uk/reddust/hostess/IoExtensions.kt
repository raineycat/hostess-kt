package uk.reddust.hostess

import kotlinx.io.Sink
import kotlinx.io.Source
import kotlin.experimental.and

fun Source.read7BitInt(): Int {
    var num = 0
    var hasMore = true

    while(hasMore) {
        val part = readByte()
        num = num shl 7
        num = num or (part and 127).toInt()
        hasMore = (part and 128.toByte()) > 0
    }

    return num
}

fun Sink.write7BitInt(value: Int) {
    var current = value
    var first = true
    while(first || current > 0) {
        first = false
        var part = current and 127
        current = current shr 7
        if(current > 0) {
            part = part or 128
        }
        writeByte(part.toByte())
    }
}

fun Source.readBool(): Boolean {
    val b = readByte()
    return b > 0
}

fun Sink.writeBool(value: Boolean) {
    writeByte(if (value) 1 else 0)
}
