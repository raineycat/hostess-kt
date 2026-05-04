package uk.reddust.hostess.packets

import uk.reddust.hostess.PacketType
import uk.reddust.hostess.packets.abstract.SimpleStringPacket

class DebugLogPacket : SimpleStringPacket(PacketType.DebugLog) {
}