MindControlledUAV
=================

## This project is meant to work with either the Emotiv SDK's EmoKey program or part of my thesis, the Emotiv-JSON API.

### What is EmoKey?
EmoKey is meant to work with the EPOC/EPOC+ in order to  turn EEG events into emulated keystrokes. This is done by setting up certain triggers for certain events and thresholds in the EmoKey program. The emulated keystrokes can be single characters, a string of characters, repeated characters, etc. These keystrokes can be sent to the application in focus.

### What is the Emotiv-JSON API?
This is an unfinished project that allows a raw data-enabled EPOC/EPOC+ to have EEG events serialized into JSON objects (Although I've also seen this work with an EPOC that supposedly didn't have raw data enabled). This uses a TCP socket connection to send the JSON object constructed each time the headset sends a data packet to the computer, creating a stream of JSON objects. Currently, clients tell the server what EEG events they're interested in and those events are included in the JSON object when the server makes a connection to the headset.

### How to Use (For Owners of the Emotiv Xavier Research Edition of the SDK)
If you have the research edition of the Emotiv SDK, you're in luck. First you need to have your libraries from the research edition in the appropriate directory so Eclipse won't complain that it can't find them. In my case, since I'm on a Mac, they are dylibs that need to be moved to the /usr/lib directory. Then, you'll need to use my other project Emotiv-JSON-API (use at your own discretion since it's a work in progress). This will use API_Main.java as the server and the ImprovedMain.java in this project will act as the client.

You'll need to make sure you're using an A.R. Parrot Drone 2, have it on with your computer connected to its Wi-Fi, have your headset on properly (all green sensors if you can), and have the dongle for the headset connected to the computer. When running ImprovedMain.java (the only program that you'll need to run for the project to work), it'll expect you to supply arguments in the console which are the liftoff and land commands. The following is a list of all possible commands separated by suite, but I recommend using Expressive suite commands since they're the most reliable:

#### Expressive:
- Blink
- LeftWink
- RightWink
- LookingLeft
- LookingRight
- LookingDown
- LookingUp
- EyesOpen
- Clench
- Smile
- EyebrowRaise

#### Cognitive:
- Push
- Pull
- Lift
- Drop
- Left
- Right
- RotateLeft
- RotateRight
- RotateClockwise
- RotateCounterclockwise
- RotateForwards
- RotateReverse
- Disappear

#### Affective:
- Frustration	
- Meditation
- EngagementBoredom
- ExcitementShortTerm
- ExcitementLongTerm

Next, it'll ask if you want to use gyros to control the drone's movement while in air. Unless you have an EPOC+, this will not work for you, so respond 'n'. If you do happen to have an EPOC+, you can use the gyros to make the drone move up and down with head nods and turn left and right with head shakes.

### How to Use (For Everyone Else)
If you don't have the research edition, you won't be able to use the Emotiv-JSON API. The workaround option I had for this before I upgraded to the research edition was to use the EmoKey program that comes with both editions of the SDK to send emulated keystrokes to a Java window, which has a KeyEventListener. On a certain key, a different command would be sent to the drone.

This is very messy since it requires the Java pop-up window that listens for the key events to be in focus in order for the drone to receive commands and the keys need to be configured every time EmoKey is launched or loaded from a preexisting key mapping EmoKey file. The characters are set in the Main.java file so EmoKey needs to be configured to send these keys for the corresponding drone command. The EEG event chosen doesn't matter.
