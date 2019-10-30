# Distributed-Flight-Reservation-Application


## Authors
Collaborator: [@abbylululu](https://github.com/abbylululu) [@JadeWang96](https://github.com/JadeWang96)

## I. File Structure
We implemented a distributed flight reservation system by Java and it includes 6 files:
1. Host.java: 
>Implement the JSON input and user interface
>Implement multithreaded UDP server function
2. ReservationSys.java:
>Keep local information on dictionary, log, and timetable. Implement insert, delete and update using the Wuu-Berstein algorithm.
3. Listener.java:
>Implement the receiving function of UDP server
4. Reservation.java
5. CommunicateInfo.java
6. EventRecord.java

## II. Project Design
### Algorithm:
Wuu Bernstein Replicated Dictionary Algorithm

### Data structures:
1. EventRecord (Reservation reservation, String operation, String siteId, int siteTimestamp)
  >Used to record the details, operation, and timestamp of the event.
2. CommunicationInfo (ArrayList\<EventRecord\> eventRecords, Integer[][] timeTable, boolean smallFlag, Integer[] timeRow)
  >Used to encapsulate the communication message.
3. Reservation (String clientName, ArrayList\<Integer\> flights, String status)
  >Used to record each reservation information.
4. ReservationSys (ArrayList\<Reservation\> dict, ArrayList\<EventRecord\> log, Integer[][] timeTable, Integer siteTimeStamp, String siteId)
  >Used to construct the site and record the status and information of the site

### Stable storage: 
To deal with site crash or failure, the log, dictionary, and timestamp of each site are saved to 3 different txt files, “log.txt”, “dictionary.txt” and “timetable.txt” in local storage.\
\
Upon updating log, dictionary or timestamp will be saved to the txt file, which lets the backup file be the latest information.
When a site is crashed, and the Reservation system detects all 3 txt files. It reads data from them to initialize the site so that the site will keep the same information as before the crash.

### Sockets and Threads: 
We implemented UDP send in the main thread and receive in a child thread to make sure a concurrent operation, in order to avoid the conflict of concurrent send and receive behaviors.


## III. Implementation
### Determine the events to send:
For \<send site_id\>: we check the specific row of matrix timestamp of the current site to see whether the current site has knowledge that the target site knows about a certain event. If the target site doesn’t know about the event, the current site will send the event records.\
\
For \<sendall\>: Since a message will be sent to all sites and cover the unknown event records of all other sites, we check the whole timestamp except the row of the current site. If a certain site is unaware of an event record, we check whether the send message has contained this event record(it is possible different sites are unaware of the same event records) and decide whether to add it in the message.

### Updating local information
Insert: on receiving the “reserve” command from the local site, the insert method will be called. We first check if the new record is a conflict with local reservations. After making sure there is no conflict, we update local log, dictionary, and timetable about this event.\
\
Delete: on receiving “cancel” command from the local site, the delete method will be called. We delete information on the denoted reservations in local log, dictionary, and timetable.\
\
Update: upon receiving messages from other sites, the update method will be called. We process each event record in the message, to update the local log, dictionary, and timetable accordingly. If an event’s operation is “insert”, we also need to make sure there is no conflict with reservations in the local dictionary. When there is conflict, we use conflict solution to decide which record should be kept.

### Local event conflict solution: 
When the current site receive the conflict records, we collect all conflict event records from other sites and the current site. 
Then, we sort these events by timestamp firstly and by lexicographical order secondly. The first two event records after sorting will be taken as the valid results and rest of the event records will be deleted.

### Local event confirmation solution: 
The event record is inserted into the dictionary and aware by all other sites will be confirmed.


### Implementation of the message size reduction feature:
#### Truncating log
After updating local record according to every message received from other sites, we check every event record saved in log, and truncate records that are aware by all other sites, by looking up the local timetable.

#### Implementing smallSend
As for \<smallsend\> and \<smallsendall\>, we only send the row of the timestamp that represents the current site to the target site and event record selection is the same as \<send\> and \<sendall\>. Since the timestamp of site is only updated by the direct message, the site may know less than before and will send more event records than before.


## IV. Build
```shell
#!/bin/bash
rm -rf bin
mkdir bin

# complie java
javac *.java -cp ./json-simple-1.1.1.jar -Xlint:unchecked -d ./bin

# cp shell and library
cp run.sh bin/
cp json-simple-1.1.1.jar bin/

echo Done!
exit 0
```

## V. Run
```shell
#!/bin/bash
# Run my program.

java -classpath .:json-simple-1.1.1.jar Host $1

exit 0
```

## Acknowledgments

* Professor P.
* Coffee
* People that we love :)

