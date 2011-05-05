/*
  PeggyWriter.h - Peggy 2.0 Text library for Arduino
  DATED 2009-04-04
  
  Character shapes based on the Silkscreen font by Jason Kottke
  http://kottke.org/plus/type/silkscreen/index.html
  
  Copyright (c) 2008 Gareth Lloyd 
  http://www.ragtag.info
  glloyd@gmail.com
  
  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

*/

/*
  The PeggyWriter Class sets up the character shapes in its 
  constructor method. 
  
  The class cannot operate in isolation: the drawCharacter() method 
  must be passed a Peggy2 object so that it has a frame buffer to 
  draw upon.
*/

#ifndef PeggyWriter_h
#define PeggyWriter_h

// Subset of ASCII from '0' through to 'Z' defined
#define NUM_CHARS 43

// Some constants that my be useful
#define LINE_HEIGHT 6
// All chars are defined in 5x6 space
#define CHAR_HEIGHT 6
#define CHAR_WIDTH 5

// this constant maps a literal ASCII character to a position
// in the array of char shapes
#define ASCII_OFFSET 48 


#include <inttypes.h>
#include <Peggy2.h>

class PeggyWriter
{ 
  public:
    PeggyWriter(); 
    
    // Pass in a literal ASCII character between '0' and 'Z', along with a 
	// Peggy2 object, and the x and y coordinates at which to draw. 
    void drawCharacter(char thisChar, Peggy2* targetBuffer, uint8_t xPos, uint8_t yPos);

    // Takes a null-terminated string of characters and outputs to the board,
	// adding spacing between characters as appropriate. Adds line breaks where
	// text flows off right-hand edge, and returns when yPos > (24 - LINE_HEIGHT)
   	void drawCharacterSequence(char* sequence, Peggy2* targetBuffer, uint8_t xPos, uint8_t yPos);
    
	// Returns 
    int getCharacterWidth(char thisChar);
	
  private:
    uint32_t shapes[NUM_CHARS];
};

class PeggyScroller
{
  public:
    PeggyScroller();
	
	// Initialize a scroller with a reference to the target frame, a
	// reference to a writer object, the vertical position of the top
	// of the text, and a sequence of characters. 
	void init(Peggy2* myTarget, PeggyWriter* myWriter, uint8_t yPos, char* sequence);
	
	// Move the sequence of chars one position to the left. Returns 1 if 
	// there is more message to come, or 0 if it has completed a full
	// scrolling cycle. After it has finished a cycle, it will 
	// automatically reset and start from the beginning of the message
	// next time it is called. 
    int scrollLeft();
	
  private:
    char* sequence;
	Peggy2* myTarget;
	PeggyWriter* myWriter;
	uint8_t topOfScroller;
	uint8_t sequenceLength;
	uint8_t progress;
	uint8_t nextCharCountdown;
};


#endif

