# JavaFX Chat Room System

**Course:** CSCI 400 – Advanced Applications Development  
**Semester:** Spring 2025  
**Instructor:** Prof. Alec Berenbaum  

A lightweight multi-user chat application written in Java 11 with JavaFX 17.  
The project is split into two GUI programs:

* **ChatServer** – listens on **TCP 5000**, shows connected users, and broadcasts chat lines.  
* **ChatClient** – lets a user connect, send messages, and disconnect gracefully.

---

##  Features

| Category | Server | Client |
|----------|--------|--------|
| **GUI** | `ListView` of usernames; buttons **Drop Selected**, **Drop All**, **Exit** | Username / IP / Port fields; read-only log; enter-to-send text field; **Connect**, **Disconnect**, **Exit** |
| **Networking** | One **acceptor** thread + one `ClientHandler` thread per connection | Reader thread keeps UI responsive |
| **Broadcasts** | `[username]: message` fan-out to every client | Displays incoming lines instantly |
| **Kick control** | Drop one user or everyone | Auto-re-enable **Connect** after disconnect |

---

## Project Structure

```text
chat-room/
├── ChatServer/
│   └── src/application/Main.java
└── ChatClient/
    └── src/application/Main.java
```
---

## Getting Started

### Prerequisites

* **JDK 11** (or newer)  
* **JavaFX SDK 17** (added to Module Path)  
* Eclipse 2021-09 or later – any IDE works if JavaFX is configured

### Build & Run

1. **Run the server**  
   *Import* the **ChatServer** project → **Run As › Java Application**.  
2. **Run one or more clients**  
   *Import* **ChatClient** → run `Main.java` (open multiple instances if desired).  
3. In each client window:  
   * Username → any name  
   * IP → `localhost`  
   * Port → `5000`  
   * Click **Connect**, then type messages and press **Enter**.  
4. Server-side buttons: **Drop Selected**, **Drop All**, **Exit**.

---

## Wire Protocol

CLIENT → <username>\n
SERVER → <username> has joined the chat\n
CLIENT → free-form chat line\n
…repeat…

(disconnect)
SERVER → <username> has left the chat\n

yaml
Copy
Edit

All messages are plain text with a single newline terminator, so Telnet/netcat can be used for quick testing.

---

##  Threading Model

* **Server**  
  * **Acceptor Thread** – loops on `ServerSocket.accept()`  
  * **ClientHandler Thread** – per client, handles blocking I/O  
  * UI updates marshalled with `Platform.runLater()`  
* **Client**  
  * **Reader Thread** – blocks on `BufferedReader.readLine()`  
  * JavaFX Application Thread handles user input & outbound writes

---


##  License

Released under the **MIT License** – see `LICENSE` for details.
