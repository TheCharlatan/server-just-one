# Just One Game

This is the backend server to the [frontend
client](https://github.com/SOPRA-2020/client-just-one) implementing a game with
the rules of [just one](https://justone-the-game.com/index.php?lang=en). It was
developed as part of an undergrad level group project at the university of
Zurich. The server code handles the main business logic. It implements a chat,
a lobby and the game logic. It also keeps a leaderboard of all achieved scores.

## Tech Stack

The project makes heavy use of java's Spring framework. A RESTful interface is
implemented using a controller/service pattern, while data is stored with JPA
hibernate and a entity based model. The services and controllers are kept as
modular as possible, there is no monolithic main class or function. There is a
controller and service for each of the chat, user, lobby and game entities and
REST endpoints. To facilitate real time state changes on the client side, the
server also implements long polling capabilities for its entities. In practice
the project currently implements it for the chat entity, but the service/worker
pattern can easily be extended to others.

Take the [user
entity](https://github.com/SOPRA-2020/server-just-one/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/entity/User.java)
as an example. Its
[controller](https://github.com/SOPRA-2020/server-just-one/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/controller/UserController.java)
exposes the REST endpoint to create, update, delete and poll changes to a user.
The respective endpoints have their respective [user service
functions](https://github.com/SOPRA-2020/server-just-one/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/service/UserService.java).
To facilitate polling for the chat, we additionally have the [chat polling
service](https://github.com/SOPRA-2020/server-just-one/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/service/ChatPollService.java)
and a [chat polling
worker](https://github.com/SOPRA-2020/server-just-one/blob/master/src/main/java/ch/uzh/ifi/seal/soprafs20/worker/ChatPollWorker.java).

The polling is implemented with a subscription model. By executing a POST
request to `/chatpoll/{chatid}` a consumer is subscribed to the endpoint. The
consumer can then issue GET requests to `/chatpoll/{chatid}` to either receive
data after a change, or a timeout status code after 60 seconds. To unsubscribe
from the resource, the consumer can issue a DELETE request to
`/chatpoll/{chatid}`.

### Technical Debt

To facilitate a user moving for example from a lobby into a game, or from a
lobby to the main overview, we currently define multiple repositories in the
same service. While this has worked fine in production so far, this adds quite
some technical debt in complexity. This is mainly due to the simple
abstractions that the JPA interface provides us. Instead these links should be
implemented with the use of foreign keys in the future.

To enable entities to store arrays (for example for a user to keep track of
invitations), the ElementCollection annotation is currently used. Again, this
should be changed to use true SQL commands and a one-to-many mapping.

The polling service and worker classes could probably be templated. Currently
they are hard to test and and have a lot of code overlap between each other.
Additionally the java servlet seems to have some trouble resolving the
correct `DeferredResult` sometimes. This needs investigation, since it means
that some long polling requests are practically dropped.

For a more uplifting list of TODOs, see the [client
repository](https://github.com/SOPRA-2020/client-just-one).

## Testing

To setup, have a look here at the [spring boot testing
framework](https://www.baeldung.com/spring-boot-testing). The tests are run
automatically when building. Please be sure to write at least one unit test for
each function written.

##  Deployment

The project currently deploys the master branch automatically using Github
pipelines and Heroku. Proper releases should be tagged with a git tag. The project
does not do semantic versioning to this date.

## Setup this Template with an IDE of choice

Download IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)) and make sure Java 13 is installed on the system.

1. File -> Open... -> SoPra Server Template
2. Accept to import the project as a `gradle project`

To build right click the `build.gradle` file and choose `Run Build`

## Building with Gradle

You can use the local Gradle Wrapper to build the application.

Plattform-Prefix:

-   MAC OS X: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

## API Endpoint Testing

### Postman

-   We highly recommend to use [Postman](https://www.getpostman.com) in order to test the API Endpoints.

## Debugging

If something is not working and/or you don't know what is going on. We highly recommend that you use a debugger and step
through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug"Name of the task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

## Contributors

For a list of contributors see the [github
contributors](https://github.com/SOPRA-2020/server-just-one/graphs/contributors).
The Just One game was designed by Ludovic Roudy and Bruno Sautter and was
published by Repos Production.
