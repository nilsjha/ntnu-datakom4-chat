[![Build Status](https://ci.gaupne.net/buildStatus/icon?job=ntnu-datakom4-chat)](https://ci.gaupne.net/job/ntnu-datakom4-chat/)
[![Quality Gate Status](https://sq.gaupne.net/api/project_badges/measure?project=no.ntnu.datakomm%3AChatClient&metric=alert_status)](https://sq.gaupne.net/dashboard?id=no.ntnu.datakomm%3AChatClient)
# Assignment A4 
_In this assignment we got the task to implement the TCP connection handling
for a predefined chat application._
	
__The assignment consists of two parts:__

 *  Warmup
 *  Server/client-chat appliction

## Notes
Unfortunatley I ran a rebase of the develop branch, without knowing what
it really did, so I lost all the __merge__-commits for the _git-flow_ 
function branches. Luckly no other changes were lost! I have now learned
the hard way to really read up on what the different commands do.

The completed assignment with the mandatory steps have been _tagged_ with __v1.0.0__.
## Release summary
### v1.0.0 - Mandatory steps
 1. Connection setup  
 1. Logic to transmit commands & public message
 1. Logon with username & parser to read incoming commands 
 1. Connection handling (disconnect) & notifying logic to listeners
 1. Parse list of online users & notify listeners
 1. Transmit private messages
 1. Receive incoming messages
 1. Parse & show supported server commands
 1. Final bugfixes & testing before release
 1. Release v1.0.0 to production

### v1.1.0 - Joke & UTF-8
 * The client now supports the UTF-8 charset (incl nordic chars)
 * The /joke command will now work with servers that support this feature.

### v1.1.10 - Code cleanup & emoji support
 * Small fixes for performance & readability in code
 * Added support for simple emojis parsing & emojicode flags

## Screenshots
### The client GUI
![v1.0.0 client](Images/clientRunning.png)

_Shows a private meassage exchange_
### The debug terminal
![v1.0.0 console](Images/debugTerminal.png)

_The terminal shows responsible method, socket id, timestamp and debug info_ 

