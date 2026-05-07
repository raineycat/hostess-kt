package uk.reddust.hostess

import kotlinx.io.Sink
import kotlinx.io.Source

fun Source.read7BitInt(): Int {
    var shift = 0
    var num = 0U

    while (true) {
        val part = readByte().toUInt() and 255U
        num = num or ((part and 127U) shl shift)
        if ((part and 128U) == 0U) break
        shift += 7
    }

    return num.toInt()
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
