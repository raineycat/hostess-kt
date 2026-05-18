package uk.reddust.hostess

import uk.reddust.hostess.packets.*

enum class PacketType(val value: Byte, val ctor: () -> Packet) {
    None(0, ::EmptyPacket),
    FileRequestBlocking(1, ::FileRequestBlockingPacket),
    FileResponseData(2, ::FileResponseDataPacket),
    FileResponseFailure(3, ::FileResponseFailurePacket),
    FileResponseBegin(4, ::FileResponseBeginPacket),
    FileResponseEnd(5, ::FileResponseEndPacket),
    ClientAnnounce(6, ::ClientAnnouncePacket),
    ServerAccept(7, ::ServerAcceptPacket),
    CheatKey(8, ::CheatKeyPacket),
    DirectoryGetFiles(9, ::DirectoryGetFilesPacket),
    DirectoryGetFilesResponse(10, ::DirectoryGetFilesResponsePacket),
    ServerAnnounce(11, ::EmptyPacket),
    ClientConnectionAnnounce(12, ::EmptyPacket),
    FileExistsRequest(13, ::FileExistsRequestPacket),
    FileExistsResponse(14, ::FileExistsResponsePacket),
    FileRequestCachedBlocking(15, ::EmptyPacket),
    FileResponseCachedBegin(16, ::EmptyPacket),
    KeepAlive(17, ::KeepAlivePacket),
    SetClientName(18, ::SetClientNamePacket),
    ServerReject(19, ::EmptyPacket),
    LuaCommand(20, ::LuaCommandPacket),
    DebugLog(127, ::DebugLogPacket);

    companion object {
        fun get(value: Byte): PacketType {
            return PacketType.entries.first { it.value == value }
        }
    }
}