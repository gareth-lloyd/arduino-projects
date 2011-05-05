/*
 *  SocialDisplay.cpp
 *  
 *
 *  Created by Gareth Lloyd on 27/07/2009.
 *  Copyright 2009 __MyCompanyName__. All rights reserved.
 *
 */

#include "LEDDisplay.h"
#include <avr/io.h>
#include <avr/interrupt.h>
#include <inttypes.h>

/*
 * Clear everything, set up the microcontroller
 */ 
void LEDDisplay::init() {
  
  // PORTD governs which row is displaying, so 
  // is set to all output
  DDRD = 255U;
  PORTD = 0;
  
  // S P I   S E T U P  
  //SET MOSI, SCK Output, all other SPI as input: 
  DDRB |= (1 << 5) | (1 << 3) | (1 << 2) | (1 << 1);
  // ENABLE SPI by setting the SPE bit in SP control register
  // SET chip as MASTER by setting MSTR bit 
  // leave SPR1 and SPR0 at 0 to set clock freq fck/4
  SPCR =  (1 << SPE) |  (1 << MSTR);

  // T I M E R   1   S E T U P
  // set timer counter control register 1 A
  TCCR1A = 0;
  // set timer counter control regsiter 1 B
  TCCR1B = 0<<CS12 | 0<<CS11 | 1<<CS10;
  // set timer 1 interrupt mask register to enable 
  // overflow interrupt
  TIMSK1 = 1 << TOIE1;
  
  // initialize LED board: set all off. 
 for (int row = 0; row < ROWS; row++) {
    buff1[row] = 0;
	buff2[row] = 0;
	buff4[row] = 0;
	buff8[row] = 0;
	
	contour[row] = ROWS;
  }
  interruptNumber = 0;
}

/*
 * change can be positive or negative. 
*/
void LEDDisplay::changeCellBrightness(uint8_t col, uint8_t row, int8_t change) {
  if (change == 0 || col > 24 || row > 24)
    return;
	
  int8_t value = 0;
  // calculate existing
  if ((buff1[row] >> col) & 1U)
    value++;
  if ((buff2[row] >> col) & 1U)
    value += 2;
  if ((buff4[row] >> col) & 1U)
    value += 4;
  if ((buff8[row] >> col) & 1U)
    value += 8;
    
  // calculate difference
  value += change;
  // set new value
  if (value < 0)
    setCellBrightness(col, row, 0);
  else if (value >= 16)
    setCellBrightness(col, row, 15);
  else
    setCellBrightness(col, row, value);
}

/*
 * 16 levels of brightness (including 0) are represented in four arrays.
 * To set a position on the LED board to a certain brightness value, 
 * we test whether the value has a binary 1 set in each of its four
 * least significant bits. If it does, we set that position in the 
 * corresponding array to 1. If it does not, we set it to 0. 
 */
void LEDDisplay::setCellBrightness(uint8_t col, uint8_t row, uint8_t newVal) {
  // Does newVal have 1 in least significant bit?
  if (newVal & 1U)
    buff1[row] |= (uint32_t) 1 << col;
  else 
	// if not, set that position in this frame buffer to 0
    buff1[row] &= ~((uint32_t) 1 << col);

  if (newVal & 2U)
    buff2[row] |= (uint32_t) 1 << col;
  else
    buff2[row] &= ~((uint32_t) 1 << col);

  if (newVal & 4U)
    buff4[row] |= (uint32_t) 1 << col;
  else
    buff4[row] &= ~((uint32_t) 1 << col);

  if (newVal & 8U)
    buff8[row] |= (uint32_t) 1 << col;
  else
    buff8[row] &= ~((uint32_t) 1 << col);
}

/*
 * refresh the display
 */
void LEDDisplay::refresh() {
  interruptNumber++;
  uint32_t* thisBuff;
  
  if (interruptNumber & 1U)
    thisBuff = buff8;
  else if (interruptNumber & 2U)
    thisBuff = buff4;
  else if (interruptNumber & 4U)
    thisBuff = buff2;
  else
    thisBuff = buff1;
  
  // technique of using a union borrowed from Peggy2 library
  // written by Wendell Oskay
  union {
    uint8_t rowBytes[4];
    uint32_t wholeRow;
  } thisRow;
  
  for (uint8_t row = 0; row < ROWS; row++) {
    if (row == 0)
      PORTD = 160;
    else if (row < 16)
      PORTD = row;
    else
      PORTD = (row - 15) << 4;

	  uint8_t i = 0;
	  while (i < 5)
	  {
	  asm("nop"); 
	  i++;
	  }

    // get data to output
    thisRow.wholeRow = thisBuff[row];

    // send data to SPI Data register, 8 bits at a time
    SPDR = thisRow.rowBytes[3];
    // wait for end of transmission:
    while (!(SPSR & (1 << SPIF)));
    
    SPDR = thisRow.rowBytes[2];
    while (!(SPSR & (1 << SPIF)));
    
    SPDR = thisRow.rowBytes[1];
    while (!(SPSR & (1 << SPIF)));
    
    PORTD = 0;
    
    SPDR = thisRow.rowBytes[0];
    while (!(SPSR & (1 << SPIF)));

      PORTB |= 2U;   
      PORTB &= 253U;
  }
}
