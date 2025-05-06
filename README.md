# Chatroom-system
JavaFX Chat Room System

Course: CSCI 400 – Advanced Applications Development  ·  Semester: Spring 2025  ·  Instructor: Prof. Alec Berenbaum  ·  Points: 100

A lightweight multi‑user chat application written in Java 11 with JavaFX 17.  It consists of two standalone GUI programs—ChatServer and ChatClient—and demonstrates sockets, multithreading, and reactive UI updates via Platform.runLater().

✨ Key Features

Category

Server

Client

GUI

Live ListView of connected usernames; Drop Selected, Drop All, Exit buttons

Username / IP / Port fields, read‑only chat log, enter‑to‑send text field; Connect, Disconnect, Exit buttons

Networking

Listens on TCP 5000; one ClientHandler thread per connection

Maintains a reader thread so UI never blocks

Broadcasts

[username]: message fan‑out to every client

Displays all server broadcasts instantly

Kick‑out controls

Drop one or all clients from the server window

Auto‑re‑enable Connect when disconnected

🗂 Project Layout

chat-room/
├── ChatServer/
│   └── src/application/Main.java
└── ChatClient/
    └── src/application/Main.java

Each project is an independent Java module ready to import into Eclipse 2021‑09 or later.

🚀 Getting Started

Prerequisites

JDK 11 or newer

JavaFX SDK 17 (add to Module Path in Eclipse)

Tested on Eclipse 2023‑06; any IDE works if you add JavaFX correctly.

Build & Run

Import the ChatServer project → Run As ► Java Application.

Import the ChatClient project and run as many instances as you like.

In each client:

Username → your name

IP → localhost

Port → 5000

Click Connect.

Type in the message field and hit Enter to chat.

🔄 Protocol Overview

CLIENT →  <username>\n
SERVER →  <username> has joined the chat\n
CLIENT →  free‑form message\n
…repeat…

DISCONNECT (socket close)
SERVER →  <username> has left the chat\n

Every line ends with a single \n.  No binary framing means you can test with telnet.

🧵 Threading Model

Server

Acceptor Thread – loops on ServerSocket.accept().

ClientHandler Thread – per‑connection; does blocking I/O.

All GUI updates use Platform.runLater() to touch JavaFX nodes safely.

Client

Reader Thread – waits on BufferedReader.readLine().

JavaFX Application Thread handles user input and sends messages.

📝 Mapping to Grading Rubric

Rubric Item

Where Implemented

GUI layout (30 pts)

Strict VBox/HBox structure, equal‑width buttons, spacing, and Region spacers.

Networking & broadcast (30 pts)

broadcast() method in Main (Server) plus reader thread in Client.

Threading (20 pts)

Dedicated acceptor + per‑client handlers; Platform.runLater() marshaling.

Button state mgmt (10 pts)

Connect/Disconnect enable‑disable toggles; Drop Selected/All logic.

Code style & error handling (10 pts)

Try/catch blocks, graceful socket close, clear comments.

🖼️ Screenshots (placeholders)

+-----------------------------------------------------+
|  Chat Server (localhost:5000)                       |
|  ┌───────────────────────────────┐  Drop Selected   |
|  |  alice                        |  Drop All        |
|  |  bob                          |  Exit            |
|  └───────────────────────────────┘                  |
+-----------------------------------------------------+

+-----------------------------------------+
| Chat Client – alice                     |
| [log]                                    |
| alice: hello                             |
| bob: hi there!                           |
|------------------------------------------|
| [msg box]                                |
+-----------------------------------------+

(Add real screenshots before submission.)

📄 License

MIT License – see LICENSE for full text.
