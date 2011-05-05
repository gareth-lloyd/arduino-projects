/*
  PeggyWriter.cpp - Peggy 2.0 Text library for Arduino
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



#include "PeggyWriter.h" 

extern "C" {
  #include <string.h>
  #include <inttypes.h> 
  #include <stdlib.h>
}

// Constructor ////////////////////////////////////////////////////////////////
PeggyWriter::PeggyWriter()
{
  // Note only a very limited subset of ASCII implemented.
  
  // Characters are 5 pixels wide by 6 tall, and are defined by the 
  // 30 least significant bits of a 4-byte integer. The least 
  // significant bit represents the top left corner. This is a bit
  // counter-intuitive, but it makes the drawing routine simpler. A 
  // binary 1 represents a turned-on pixel

  // shapes of digits 0 - 9
	shapes[0] = 6595878U;
	shapes[1] = 7407683U;
	shapes[2] = 15767815U;
	shapes[3] = 7608583U;
	shapes[4] = 4691109U;
	shapes[5] = 7609391U;
	shapes[6] = 6593574U;
	shapes[7] = 2167055U;
	shapes[8] = 6592806U;
	shapes[9] = 6568230U;
	shapes[10] = 32800U;
	shapes[11] = 1114176U;
	shapes[12] = 4260932U;
	shapes[13] = 229600U;
	shapes[14] = 1118273U;
	shapes[15] = 2103559U;
	shapes[16] = 14726830U;
	shapes[17] = 9747750U;
	shapes[18] = 7642407U;
	shapes[19] = 6587686U;
	shapes[20] = 7644455U;
	shapes[21] = 7380007U;
	shapes[22] = 1088551U;
	shapes[23] = 6599726U;
	shapes[24] = 9747753U;
	shapes[25] = 1082401U;
	shapes[26] = 6594824U;
	shapes[27] = 9604265U;
	shapes[28] = 7373857U;
	shapes[29] = 18405233U;
	shapes[30] = 18667121U;
	shapes[31] = 6595878U;
	shapes[32] = 1088807U;
	shapes[33] = 275031334U;
	shapes[34] = 9608487U;
	shapes[35] = 7608366U;
	shapes[36] = 2164807U;
	shapes[37] = 6595881U;
	shapes[38] = 4532785U;
	shapes[39] = 11196081U;
	shapes[40] = 18157905U;
	shapes[41] = 4329809U;
	shapes[42] = 7374983U;
}


// Draw one Character, passed as an ASCII code. 
void PeggyWriter::drawCharacter(char thisChar, Peggy2* targetBuffer, uint8_t xPos, uint8_t yPos)
{
  uint8_t xOrigin = xPos;

  uint8_t yLimit = yPos + CHAR_HEIGHT;
  uint32_t thisCharShape;
  
  // The literal character that was supplied corresponds to 
  // a position in the array of character shapes, but we must
  // translate from the ASCII to the real position
  uint8_t shapeIndex = thisChar - ASCII_OFFSET;

  // We are going to modify the shape with bitshifts in order
  // to print it, so we make a copy. Spaces are simple to handle.
  if (thisChar != ' ')
    thisCharShape = shapes[shapeIndex];
  else
    thisCharShape = 0;
  

  // Not using an inner loop, for a touch more speed.
  while (yPos < yLimit) 
  {
    // if the leftmost bit is a 1, set a pixel.
    if (1 & thisCharShape)
	  targetBuffer->SetPoint(xPos, yPos);
    // move to the next pixel
	thisCharShape = thisCharShape >> 1;
	xPos++;
	
	// Do this four more times
    if (1 & thisCharShape)
	  targetBuffer->SetPoint(xPos, yPos);
    thisCharShape = thisCharShape >> 1;
	xPos++;
	
    if (1 & thisCharShape)
	  targetBuffer->SetPoint(xPos, yPos);
    thisCharShape = thisCharShape >> 1;
	xPos++;
	
	if (1 & thisCharShape)
	  targetBuffer->SetPoint(xPos, yPos);
    thisCharShape = thisCharShape >> 1;
	xPos++;
    
	if (1 & thisCharShape)
	  targetBuffer->SetPoint(xPos, yPos);
    thisCharShape = thisCharShape >> 1;
	
	// Now, go back to the left hand side, and the next row
	xPos = xOrigin;
    yPos++;
  }
}

// Takes a null-terminated string and prints to Peggy frame
// passed to the argument 'targetBuffer', adding spacing
// according to the width of characters
void PeggyWriter::drawCharacterSequence(char* sequence, Peggy2* targetBuffer, uint8_t xPos, uint8_t yPos)
{
  int i = 0;
  
  //temporary var saves several lookups from sequence[] during loop
  char thisChar = sequence[i];
  
  while (thisChar != '\0')
  {	
    //skip spaces
	if (thisChar != ' ')
	  drawCharacter(thisChar, targetBuffer, xPos, yPos);
	
	// proportional spacing: move left based on char width
    xPos += getCharacterWidth(thisChar) + 1;
    if (xPos > 25)
	{
	  yPos += LINE_HEIGHT;
	  xPos = 0;
	  if (yPos > (24 - LINE_HEIGHT))
		return;
	}
	
    // get next
	thisChar = sequence[++i];
  }
}


int PeggyWriter::getCharacterWidth(char thisChar)
{
  switch (thisChar) 
  { 
    case ':':
	case 'I':
	  return 1;
	case ';':
	  return 2;
    case '1':
    case '<':
    case '=':
    case '>':
    case 'E':
    case 'F':
    case 'L':
    case 'T':
    case 'Z':
	  return 3;
	case '@':
	case 'M':
    case 'N':
    case 'V':
    case 'W':
    case 'X':
    case 'Y':
	  return 5; 
	default:
	  return 4;
  }
}



// Constructor ////////////////////////////////////////////////////////////////
PeggyScroller::PeggyScroller()
{
}

// Set up all instance variables, and assign memory sufficient for sequence
void PeggyScroller::init(Peggy2* targetBuffer, PeggyWriter* writer, uint8_t yPos, char* displayMessage)
{
  myTarget = targetBuffer;
  myWriter = writer;
  topOfScroller = yPos;
  progress = 0;
  nextCharCountdown = 0;

  sequenceLength = strlen(displayMessage);
  sequence = (char*)malloc(sequenceLength);
  strcpy(sequence, displayMessage);
}

// Move the sequence to the left. 
int PeggyScroller::scrollLeft()
{
  uint8_t rowNum;
  uint8_t rowLimit = topOfScroller + CHAR_HEIGHT;

  
  // if countdown is at zero, add another character.
  if (nextCharCountdown == 0) 
  {
    char nextChar = sequence[progress++];
	myWriter->drawCharacter(nextChar, myTarget, 25, topOfScroller);

	// Reset the countdown for the next letter.
	nextCharCountdown = myWriter->getCharacterWidth(nextChar) + 1;
  }
  nextCharCountdown--;
  
  // Shift all buffer rows one place left. This necessitates rightwad
  // shift, because LSB is on left hand side. Unrolled the loop for speed.
  for (rowNum = topOfScroller; rowNum < rowLimit; rowNum++)
    myTarget->buffer[rowNum] = myTarget->buffer[rowNum] >> 1;
		    
  // If we are at the end of the sequence, reset and return 0
  if (progress >= sequenceLength)
  {
    progress = 0;
    return 0;
  }
  else
	return 1;
}


