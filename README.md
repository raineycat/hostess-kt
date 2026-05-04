# hostess-kt

## About
Hostess is a protocol used in the game [Transistor](https://store.steampowered.com/app/237930/Transistor/) for remote debugging.
While disabled in release builds, it's not stripped out, which means it only takes a very small (one line) code patch to enable it.
The game can either connect directly to a server, or use UDP broadcasts to scan for a compatible server.
This server implements broadcast listening, so a patched game can automatically connect to it.

This project is written in Kotlin and uses `ktor-sockets` for networking.
Logback handles logging, so you can edit that config file if you want more or less verbosity.

## Project state
- The low level protocol stuff works
- It can receive and print debug logs
- Sending files is currently broken for unknown reasons
- Not all packets are implemented

### Goals
- Configuration system
- Some kind of control interface

## Protocol features
- Asset file loading
  - The game will query the server when it needs to load asset files
  - This overrides using local files on disk
- Debug logs
  - The game will sometimes send debug log messages over the network
  - Not all messages are sent though
  - Code patches could make the logs more verbose
- Lua execution
  - The server can send Lua code to the game to be executed
  - I haven't played around with this yet, I don't know how powerful it is
- Cheat keys
  - Sends keycodes to the game
  - The game simulates them being pressed
