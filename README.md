# Simple File-Sharing Program
Just a simple P2P system (the program can work both as client and server), sharing and downloading files, implemented in Java with Sockets.

## Comunication Protocol
The programs communicate with each other through messages in JSON (JavaScript Object Notation).

## Sending and Receiving messages
The program sends and receives messages through the UDP protocol. The client sends a message to an IP Broadcast and any server connected to that IP will catch the message.
For send and receive files, the program uses the TCP protocol.
