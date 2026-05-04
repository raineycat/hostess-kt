package uk.reddust.hostess

enum class ErrorKind {
    Success,
    EpicFail,
    FileOpenFail,
    FileMasked,
    DirectoryDoesNotExist,
    FileRequestMatchesCache;

    companion object {
        fun get(value: Int): ErrorKind {
            return ErrorKind.entries.first { it.ordinal == value }
        }
    }
}