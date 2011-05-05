/*
 *  PeggyData.h
 *  
 *
 *  Created by Gareth Lloyd on 27/07/2009.
 *
 */
 
 
#ifndef PeggyData_h
#define PeggyData_h

#include <LEDDisplay.h>
#include <inttypes.h>
#include <avr/pgmspace.h>

#define MOVES_PER_BYTE 4
#define CELL_SIZE 4

#define BRIGHTEN 1
#define DARKEN 0

class PeggyData {
private:
	LEDDisplay* myDisplay;
	
	uint8_t x;
	uint8_t y; 
	uint8_t width;
	uint8_t height;
	
	unsigned int moveIndex;
	unsigned int moveArraySize;
	prog_uchar* moves;
	
	bool landed;
	
	bool isLanded(uint8_t thisX, int8_t remainder, uint8_t verticalDisplacement);
	void initialFill();
	void setLanded();
	void changeCol(uint8_t startX, uint8_t startY, uint8_t brightenOrDarken);
	void changeRow(uint8_t startX, uint8_t startY, uint8_t brightenOrDarken);
	
public:
	void setup(LEDDisplay* myDisplay, uint8_t x, uint8_t y, uint8_t width, uint8_t height, unsigned int moveIndex, unsigned int moveArraySize, prog_uchar* moves);
	bool nextMove();
};

#endif
