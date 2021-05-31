# SiegeGame
Simple game I made for fun using assets from online.
Click on "SiegeGame.jar" to play the game, assuming you have Java.

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
Shift: Sprint (Looks terrible, so only usable during Debug mode.)  


Issues:  
The biggest issue, that I ABSOLUTELY HATE, is that the audio tends to cause lag spikes whenever it plays. If the audio is disabled, the game runs really well, but Java's library audio-playing functions simply are not designed to be used in va game like this, so it's basically stuck like this unless I download some other library to fix it, which would go against the point of the project. To "solve" this, there is now an option on the title screen to disable the normal sfx, to greatly improve performance. The reason it's so slow is that the only audio players I could find ALWAYS manually load the entire file each time it is played. Technically, there are ways to get around this, but I kept running into weird issues with those solutions, such as clips not playing 20% of the time for no reason, and taking up too many audio channels.  
There are also some really difficult to reproduce bugs I haven't been able to fix, such as W's exit malfunctioning or playing twice, and it's possible that the music could start to play over itself, though I'm pretty sure I fixed that last one.   
There's also the possibility it could suddenly freeze upon loading a level, which is because of some concurrency stuff that's easy to fix but annoying to track down. It's super duper rare, though, and I might have fixed it already. (It's so rare that I don't know if I fixed it or not.)  
There's some sort of offset when you zoom the camera in, which I can't pin down how to fix. It's basically never a problem, though, unless you're standing on the very edge of a platform and get the scroll set up in a certain way, where it can shunt you off of the platform.  

I suppose it's fitting that W is the cause of the most elusive bug, at least. I hate that bug. So humiliating that I have no idea what's causing it.  


I'm not gonna give a guide for the stage builder, because it's way too complicated, and nobody will ever need it. The labels should mostly tell you what to do, though.  
