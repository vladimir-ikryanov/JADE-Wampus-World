# JADE-Wumpus-World

This project represents a solution for the "Wumpus World" problem. The solution is implemented using the approach with multi-agent communication implemented using the JADE framework.

## Prerequisites

- macOS 13 or higher
- Java 17 or higher
- JADE 4.6.0

## Running

To run the solution open macOS **Terminal** and execute the following command line:

```
chmod 755 run_agents.sh
./run_agents.sh
```

## Output

You should see the following output in the console that shows communication between navigator, speleologist, and wumpus world:

```
vladimir@JxBM1 JADE-Wampus-World % ./run_agents.sh
Jan 16, 2023 11:09:01 AM jade.core.Runtime beginContainer
INFO: ----------------------------------
    This is JADE 4.6.0 - revision 6869 of 30-11-2022 14:47:03
    downloaded in Open Source, under LGPL restrictions,
    at http://jade.tilab.com/
----------------------------------------
Jan 16, 2023 11:09:01 AM jade.imtp.leap.LEAPIMTPManager initialize
INFO: Listening for intra-platform commands on address:
- jicp://192.168.3.73:1099

Jan 16, 2023 11:09:01 AM jade.core.AgentContainerImpl init
WARNING: Automatic main-detection mechanism initialization failed (Error setting up multicast socket - Caused by:  Can't assign requested address). Mechanism disabled!
Jan 16, 2023 11:09:02 AM jade.core.BaseService init
INFO: Service jade.core.management.AgentManagement initialized
Jan 16, 2023 11:09:02 AM jade.core.BaseService init
INFO: Service jade.core.messaging.Messaging initialized
Jan 16, 2023 11:09:02 AM jade.core.BaseService init
INFO: Service jade.core.resource.ResourceManagement initialized
Jan 16, 2023 11:09:02 AM jade.core.BaseService init
INFO: Service jade.core.mobility.AgentMobility initialized
Jan 16, 2023 11:09:02 AM jade.core.BaseService init
INFO: Service jade.core.event.Notification initialized
Jan 16, 2023 11:09:02 AM jade.mtp.http.HTTPServer <init>
INFO: HTTP-MTP Using XML parser com.sun.org.apache.xerces.internal.jaxp.SAXParserImpl$JAXPSAXParser
Jan 16, 2023 11:09:02 AM jade.core.messaging.MessagingService boot
INFO: MTP addresses:
http://192.168.3.73:7778/acc
[world@192.168.3.73:1099/JADE] agent is ready.
Wampus world map: 
[[], [breeze], [pit], [breeze]]
[[stench], [], [breeze], []]
[[wumpus], [stench, gold], [], [breeze]]
[[stench], [], [breeze], [pit]]
Jan 16, 2023 11:09:02 AM jade.core.AgentContainerImpl joinPlatform
INFO: --------------------------------------
Agent container Main-Container@192.168.3.73 is ready.
--------------------------------------------
World: Speleologist went inside at 0:0 location
Speleologist: Inform navigator about current room: []
Navigator: Speleologist location: 0:0
Navigator: Checking room status: RoomStatus{stench=-1, breeze=-1, pit=-1, wumpus=-1, ok=1, gold=-1, noWay=-1}
Navigator: Propose action: [down, move]
Speleologist: Receive instruction: [down, move]
Speleologist: Notify wampus world about action: down
World: Speleologist is in room: [stench]
Speleologist: Update info about world state [stench]
Speleologist: Inform navigator about current room: [stench]
Navigator: Speleologist location: 1:0
Navigator: Checking room status: RoomStatus{stench=1, breeze=-1, pit=-1, wumpus=-1, ok=1, gold=-1, noWay=-1}
Navigator: Propose action: [right, move]
Speleologist: Receive instruction: [right, move]
Speleologist: Notify wampus world about action: right
World: Speleologist is in room: []
Speleologist: Update info about world state []
Speleologist: Inform navigator about current room: []
Navigator: Speleologist location: 1:1
Navigator: Checking room status: RoomStatus{stench=-1, breeze=-1, pit=-1, wumpus=-1, ok=1, gold=-1, noWay=-1}
Navigator: Propose action: [down, move]
Speleologist: Receive instruction: [down, move]
Speleologist: Notify wampus world about action: down
World: Speleologist is in room: [stench, gold]
Speleologist: Update info about world state [stench, gold]
Speleologist: Inform navigator about current room: [stench, gold]
Navigator: Speleologist location: 2:1
Navigator: Checking room status: RoomStatus{stench=1, breeze=-1, pit=-1, wumpus=-1, ok=1, gold=1, noWay=-1}
Navigator: Propose action: [take]
Speleologist: Receive instruction: [take]
World: Speleologist took gold! Finish.
```
