package uk.reddust.hostess

import kotlin.collections.first

data class KeyCombo(
    val code: XnaKeyCode = XnaKeyCode.None,
    val shift: Boolean = false,
    val ctrl: Boolean = false,
    val alt: Boolean = false
) {
    fun encode(): UInt {
        var num = code.num and 0xFFFFu
        num = num or if(shift) 0x10000u else 0u
        num = num or if(ctrl) 0x20000u else 0u
        num = num or if(alt) 0x40000u else 0u
        return num
    }

    companion object {
        fun decode(num: UInt): KeyCombo {
            val code = XnaKeyCode.entries.first { it.num == num or 0xFFFFu }
            val shift = num and 0x10000u > 0u
            val ctrl = num and 0x20000u > 0u
            val alt = num and 0x40000u > 0u
            return KeyCombo(code, shift, ctrl, alt)
        }
    }
}
