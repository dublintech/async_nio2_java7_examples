Counter Server Program
======================

Author: Alex Staveley
Date:28th October 2012

Table of Contents
=================

1. Running instructions
2. Build Instructions
3. Design Decisions
4. Testing approach
5. Directory structure

========================
1. Running Instructions
========================
* You must have Java 1.7 installed and set as your Java Home.

* To run server:
- CD into dist\server directory.
- This contains server.jar and the log4j.jar
- Type: java -jar server.jar
- Follow on screen instructions.

* To run client:
- CD into dist\client directory.
- This contains client.jar and the log4j.jar
- Type: java -jar client.jar
- Follow on screen instructions.

To run server or client on different machine just copy the dist\server or dist\client directory to that machine
and repeat above.

Example usage instructions:
- start server as described above:
	dist\server>java -jar server.jar
- spin up a new server named srv1 on port 9999:
	dist\server>start srv1 9999
- check server is up
	dist\server>list       -- you'll see something like:
	
server running on MyMachineName/192.168.1.3
Server:srv1,address=MyMachineName/192.168.1.3:9999	
	
- start client as decribed above:
	dist\client>java -jar client.jar
- connect to srv1:
	dist\client>connect localhost 9999  -- note: you connect to servers by specifying machine name and port.
- get the counter value:
	dist\client>get  --  you'll see: **** COUNTER=0
- increase counter value:
	dist\client>increase -- you'll see: **** COUNTER=1
- increase counter value again:
	dist\client>increase -- you'll see: **** COUNTER=2
- spin up another client:
	dist\client2>connect localhost 9999
- get the counter value:
	dist\client2>get     -- you'll see: **** COUNTER=2
- now decrease value from second client
	dist\client2>decrease -- you'll see: **** COUNTER=1
- check value in client 1:
	dist\client>get 	 -- you'll see: **** COUNTER=1
- Now restart the server:
	dist\server> restart srv1  -- check for a file called counterValue.txt in server directory. 
This will have counter value.
- check value again from client 1:
   dist\client>get -- you'll see: **** COUNTER=1 -- Note client does not have to be aware 
of server restart.  Because things are stateless!

=======================
2. Build Instructions
=======================
- You must have Java 1.7 and ANT 1.7.x installed. 
- Type:
> ANT help 
for information on all targets.

Most interesting target is 
> ANT package
This will package client and server.

=======================
3. Design Decisions
=======================
At a high level, there are two key decisions in this project:

3.a. To use core java, another JVM language or a framework?
3.b. Which client / server paradigm to use?

Both are now discussed:

3.a. To use core java, another JVM language or a framework?

There are many frameworks which could be used for basis as solution:
- akka + Scala
- akka + Java
- netty
- Grizzly
- Apache MINA

Usually, in a production environment it would make sense to use a proper framework that 
is used in other production set-ups rather than write your own.  In some cases, 
when the problem is limited in scope it can make more sense to just use Java rather than introduce
a framework. For example, if you only had to update a single counter in a database - hibernate 
is not going to offer many of its rich advantages over using vanilla JDBC. There is no complex O/R,
no complex transactions and no real need for caching. Similarly, a simple counter 
client / server problem should be solvable with vanilla Java.  In addition, 
if a framework can simplify all the complexity of this problem, I would feel 
that is over simplifying this assignment.

That said, I used some of the ideas that are in these frameworks, for example immutable message
in my approach.

3.b. Which client / server paradigm to use?

There are number of different paradigms that can be used here:
- Classical RPC - each client is a spawned thread on the Server.
- Non blocking synchronous - this is similar to the Reactor pattern.
A single server thread is constanting polling a socket checking for input
and when some valid input is found it is fired to appropriate handler.
- Asynchronous I/O - in this model, callbacks are registered and receive events
when kernel I/O has finished and the callback can process. There is no polling. 

I chose Asynchronous I/O. Classical RPC (thread per client) is inefficient. Threads use 
resources on the Server even when there may be no data for them to process.  The more threads,
the more context switching.  More clients means more threads which means even more context switching 
meaning this model does not scale very well. Non-blocking synchronous involves way less threads 
and way less context switching and is much more scalable. The programming model is far more complex though. 

Asynchronous is inefficient with thread usage and handles issues such as network latency by ensuring 
blocking is minimal.  Asynchrous models scale well. Asynchronous I/O (also known as nio2) is one 
of the big additions to Java 7. It is debatable if it is always more 
performant than non-blocking sychronous.  It depends on many factors - including the operating system,
the type of data (bursts or drip feeds).  It is however, a simpler programming model. 

The other design decisions:
- Thread strategy
- Use of atomic classes in Counter 

are discussed as <dev-notes> in their classes. This is to avoid duplication of documentation. 

The key classes are:
* AsyncCounterServer  -- The Server which can change the counter
* ServerManager  -- manages Server instances. Usually there will only be one, but it can handle > 1.
* AsyncCounterClient  -- Make remote requests to the Server.
* Counter - the actual counter class.

The protocol classes are all the Counter package.

====================
4. Testing Stratedgy
====================
I included both positive tests and negative tests.
Usually I would strive for TDD and a classical unit test approach.
There are classical unit tests such as: TestCounterClientMessage and TestCounterServerMessage.
But, I would have liked some more (leveraging a framework like mockito) but was short time.
In this assignment a lot of the complexity is between client / server interaction.
This is captured in: TestAsyncClientServerInteraction. This is not classical unit test but
is a 'bang for the buck' approach, covering client / server complexity.

These are the most important tests.

All tests can be run from the test suire com.alex.AllTests.java or from the ant targets:

> ant junit
> ant junitreport

=======================
5. Directory Structure
=======================

- dist
------server                       // server distribution
------client					   // client distribution
-lib                               // jar files
-report                            // junit reports
-src                               // src code (test and source)
-target                            // build directories







