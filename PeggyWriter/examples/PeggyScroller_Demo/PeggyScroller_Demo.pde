/* Simple example code for Peggy 2.0, using the Peggy2 and PeggyWriter libraries
*/

#include <Peggy2.h>
#include <PeggyWriter.h>

Peggy2 frame1;         // Make a frame buffer object, called frame1
PeggyWriter myWriter;  // Make a PeggyWriter object
PeggyScroller world;   // Make a PeggyScroller object
PeggyScroller human;
char scrollerSelect = 1;

void setup()
{
  // Call this once to init the hardware:
  frame1.HardwareInit();
  
  // Now tell the scrollers where the frame and the writer objects are
  // by passing references to them (the '&' prefix means 'the address 
  // of...'), tell them what row to draw on, and give them their messages.
  human.init(&frame1, &myWriter, 2, "HELLO HUMAN      ");
  world.init(&frame1, &myWriter, 12, "HELLO WORLD      ");
}  // End void setup()  


void loop()
{ 
  long delayCount = 0;
  char messageFinished;
  
  if (scrollerSelect == 1) 
  {
    // the scrollLeft() method returns 1 if there's more message
    // to come, otherwise 0.
    messageFinished = world.scrollLeft();
  }
  else 
  {
    messageFinished = human.scrollLeft();
  }
  
  // Refresh Peggy lots of times while we kill time.
  while (delayCount < 200)
  {
    frame1.RefreshAll(1);
    delayMicroseconds(50);
    delayCount++;
  }
  delayCount = 0;
  
  // switch messages if one has completed. 
  if (messageFinished == 0) 
    // using an 'XOR' to toggle. 
    scrollerSelect = scrollerSelect ^ 1;
}
