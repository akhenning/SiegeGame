# SiegeGame
Simple game I made for fun using assets from online.
Click on "SiegeGame.jar" to play the game, assuming you have Java. And assuming I actually updated the jar file in the current release.

Note that the thing that makes this project notable, is that I didn't use ANYTHING other than the default Java libraries to create it!  
It's also designed to be very easy to develop new levels for, and fairly modular within itself.

This is a pretty stable build, and I might let it be the final one, unless I get inspired to track down a few of the really elusive bugs or expand on the levels.  
I originally had greater ambitions for this project, but then I tried out Unity, and now I feel like I can't go back to manually suffering with this. Mostly because of the effing Java audio libraries, which are NOT designed for game sound effects, and the fact that nobody but me can really appreciate how much work it took/what's impressive about it.

Controls:  
They are explained in the tutorial, but there are some that are not included in it.  
WASD: Movement  
Space: Jump  
E: Attack  
Up/Down Arrow: Zoom In and Out  
---- Not explained or intended for in-game use ----  
F: Strong attack (game is not designed around it, but is a feature nonetheless)  
R: Respawn (moves to 0,0)  
Q: Toggle Debug mode (displays collission, shows hidden objects)  
Shift: Sprint (Looks terrible, so only disabled outside of Debug mode. Jumping is faster anyway.)  


Issues:  
The biggest issue is that the audio tends to cause lag spikes whenever it plays. If the audio is disabled, the game runs really well, but Java's library audio-playing functions simply are not designed to be used in va game like this, so it's basically stuck like this unless I download some other library to fix it, which would go against the point of the project. To "solve" this, there is now an option on the title screen to disable the normal sfx, to greatly improve performance. The reason it's so slow is that the only audio players I could find ALWAYS manually load the entire file each time it is played. Technically, there are ways to get around this, but I kept running into weird issues with those solutions, such as clips not playing 20% of the time for no reason, and taking up too many audio channels.  
However, it seems like some computers are fast enough that they can handle it without noticable spikes.

Also, I made this a while ago, and upon coming back to it, I am angry at some of the organizational decisions I made when making it. Like, why did I put all of the game logic (other than stuff directly pertaining to the player/game objects) in one file? It isn't too too bloated, but still, should have been split up between loading/graphics/logic. And, why did I use so many globals for things like scroll position? ...It isn't worth trying to go in and fix all of these issues though because it would take so long and the project is old anyway.


(I'm not gonna give a guide for the stage builder, because it's way too complicated, and nobody will ever need it. The labels+the code should mostly tell you what to do, though.)
