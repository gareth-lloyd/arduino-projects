/* Simple example code for Peggy 2.0, using the Peggy2 and PeggyWriter libraries
*/

#include <Peggy2.h>
#include <stdlib.h> 
#include <PeggyWriter.h>

Peggy2 frame1;     // Make a frame buffer object, called frame1
PeggyWriter myWriter;  // Make a PeggyWriter object

void setup()                    // run once, when the sketch starts
{
     frame1.HardwareInit();   // Call this once to init the hardware
}  // End void setup()  


void loop()                     // run over and over again
{ 
  byte x = 2, y = 1;
  
  myWriter.drawCharacter('H', frame1, x, y);
  x += 4;
  myWriter.drawCharacter('E', frame1, x, y);
  x += 4;
  myWriter.drawCharacter('L', frame1, x, y);
  x += 4;
  myWriter.drawCharacter('L', frame1, x, y);
  x += 5;
  myWriter.drawCharacter('O', frame1, x, y);
  x = 0;
  y += 7;
  myWriter.drawCharacter('W', frame1, x, y);
  x += 6;
  myWriter.drawCharacter('O', frame1, x, y);
  x += 5;
  myWriter.drawCharacter('R', frame1, x, y);
  x += 4;
  myWriter.drawCharacter('L', frame1, x, y);
  x += 5;
  myWriter.drawCharacter('D', frame1, x, y);
  x += 5;
  
  while (1)
  {
    frame1.RefreshAll(1);
    delayMicroseconds(1000);
  }
  
}
