Written and maintained by Gareth Lloyd. Please contact glloyd@gmail.com with any issues. Disclaimer: I'm afraid this code comes without any promises or support, but I'll do my best to help if you have a problem.


ABOUT

A library to display static or animated text on a Peggy2. For more about the Peggy 2, see: http://www.evilmadscientist.com/article.php?story=peggy2


REQUIREMENTS

The Arduino IDE (or another way of uploading code), plus a Peggy 2 board.


INSTALLATION

To use the library, unzip it to the folder ‘/hardware/libraries/’ under your Arduino installation. To include it in a sketch, select ‘Sketch > Import Library > PeggyWriter’, or just write #include <PeggyWriter.h> at the top of the program.


USAGE 

Set up a writer object like so: 

PeggyWriter myWriter;

Then create a character by calling its drawCharacter method: 

myWriter.drawCharacter('A', myBuffer, xPos, yPos); 

where ‘A’ is the literal character you want to see on the LEDs, myBuffer is the Peggy2-type frame buffer you want to draw on, and xPos, yPos define the top left corner of the character.

Do note that creating a PeggyWriter object will set up an array of all the defined character shapes ready to be drawn. This array will take up 172 bytes of RAM, so you’ve got slightly less space for frame buffers.

You can create a sequence of characters using the drawSequence() method. See PeggyWriter.h for details.


To create scrolling text, you need to set up another object of type 'PeggyScroller' like so:

PeggyScroller myScroller;

Before you can use this, you need to give it a few bits of info. It needs to know where to find the frame to draw on, and how to draw characters. You also need to tell it what height on the Peggy to draw at, and what to write. You can give it all this information with the init() method:

myScroller.init(&myFrame, &myWriter, yPos, "THIS IS WHAT I WANT TO WRITE")

In this function call, &myFrame and &myWriter must refer to pre-existing Peggy2 and PeggyWriter objects respectively. Putting an '&' symbol in front of these objects is like saying 'The address of...', and it means you're passing a reference to the object rather than the whole thing. The PeggyScroller stores these references so that it knows where to draw and it can use the PeggyWriter object to make the letters.

After your PeggyScroller is initialized, call myScroller.scrollLeft repeatedly to cause the message to move across the screen. It will always start from the right-hand side and scroll until the whole message has passed. This function will return 1 as long as there are further characters in the message to display, or 0 when the end of the message is reached. At this point, the PeggyScroller automatically resets itself, and further calls to scrollLeft() will cause the message to repeat from the start. 


EXAMPLES

To see an example of static text, open ‘File > Sketchbook > Examples > PeggyWriter > PeggyWriter_Hello’.


CHARACTERS SUPPORTED

Only a limited subset of ASCII characters are supported. These run from '0' through to 'Z' in the standard ASCII table. 

Specifically:
- Numbers '0' up to '9'
- punctuation marks: < > = : ; ? @
- Capital letters: 'A' through to 'Z'
- You can also output spaces

If you tell these functions to output characters outside of this set, the behaviour is undefined, and likely to crash.


ADDITIONAL CREDITS:
Jason Kottke’s lovely little Silkscreen pixel font, made it very simple to map all the ASCII characters to 5×6 pixel blocks. This means each one can fit in a 32-bit integer.
kottke.org/plus/type/silkscreen/index.html

