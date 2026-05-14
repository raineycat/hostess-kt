# hostess-kt

## About
Hostess is a protocol used in the game [Transistor](https://store.steampowered.com/app/237930/Transistor/) for remote debugging.
While disabled in release builds, it's not stripped out, which means it only takes a very small (one line) code patch to enable it.
The game can either connect directly to a server, or use UDP broadcasts to scan for a compatible server.
This server implements broadcast listening, so a patched game can automatically connect to it.

This project is written in Kotlin and uses `ktor-sockets` for networking.
Logback handles logging, so you can edit that config file if you want more or less verbosity.

## Usage
When you run the server, it starts a web interface in port 8080, as well as listening on the required ports for Hostess.
You need to first patch the game to connect to the server, or else nothing will happen.
I'm working on an automatic patcher to do this, but for now you need to do it manually.

Using a tool like dnSpy/ILSpy, add the following line into the `App` class constructor.
Null and zero will make it use UDP discovery, which is nice and easy.
```csharp
HostessClient.init(null, 0);
```

When the game boots, it should show up in the client list (http://localhost:8080/clients).
From there, you can click on the link and interact with it.

## Protocol features
- Asset file loading
  - The game will query the server when it needs to load asset files
  - This overrides using local files on disk
- Debug logs
  - The game will sometimes send debug log messages over the network
  - Not all messages are sent by default
  - Code patches are required for more logging
- Lua
  - The server can send Lua code to be executed in the context of the game
- Cheat keys
  - Sends keycodes to the game
  - The game simulates them being pressed
  - This can be used to trigger the built in debug actions if enabled
  - I will compile a list at some point™
