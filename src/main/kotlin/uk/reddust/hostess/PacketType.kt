package uk.reddust.hostess

import kotlin.reflect.KClass
import uk.reddust.hostess.packets.*

enum class PacketType(val value: Byte, val clazz: KClass<out Packet>) {
    None(0, EmptyPacket::class),
    FileRequestBlocking(1, FileRequestBlockingPacket::class),
    FileResponseData(2, EmptyPacket::class),
    FileResponseFailure(3, FileResponseFailurePacket::class),
    FileResponseBegin(4, EmptyPacket::class),
    FileResponseEnd(5, EmptyPacket::class),
    ClientAnnounce(6, ClientAnnouncePacket::class),
    ServerAccept(7, ServerAcceptPacket::class),
    CheatKey(8, EmptyPacket::class),
    DirectoryGetFiles(9, EmptyPacket::class),
    DirectoryGetFilesResponse(10, EmptyPacket::class),
    ServerAnnounce(11, EmptyPacket::class),
    ClientConnectionAnnounce(12, EmptyPacket::class),
    FileExistsRequest(13, FileExistsRequestPacket::class),
    FileExistsResponse(14, EmptyPacket::class),
    FileRequestCachedBlocking(15, EmptyPacket::class),
    FileResponseCachedBegin(16, EmptyPacket::class),
    KeepAlive(17, KeepAlivePacket::class),
    SetClientName(18, SetClientNamePacket::class),
    ServerReject(19, EmptyPacket::class),
    LuaCommand(20, LuaCommandPacket::class),
    DebugLog(127, DebugLogPacket::class);

    companion object {
        fun get(value: Byte): PacketType {
            return PacketType.entries.first { it.value == value }
        }
    }
}