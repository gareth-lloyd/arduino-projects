/*
 *  PeggyData.cpp
 */

#include "PeggyData.h"
#include <avr/io.h>
#include <avr/interrupt.h>
#include <inttypes.h>
#include <avr/pgmspace.h>

/*
 * This function is serving in the place of a constructor, as 
 * constructors are not well supported by AVR's compiler. Instead,
 * a new instance must be initialized with an explicit call to its
 * setup function. 
 */
void PeggyData::setup(LEDDisplay* myDisplay, uint8_t x, uint8_t y, uint8_t width, uint8_t height, unsigned int moveIndex, unsigned int moveArraySize, prog_uchar* moves) {
	this->myDisplay = myDisplay;

	this->x = x;
	this->y = y;
	
	// Shapes cannot be smaller than one cell:
	this->height = (height < CELL_SIZE) ? CELL_SIZE : height; 
	this->width = (width < CELL_SIZE) ? CELL_SIZE : width;
	 
	this->moveIndex = moveIndex;
	this->moveArraySize = moveArraySize;
	
	this->moves = moves;
	
	this->landed = false;
	initialFill();
}

/*
 * Function to shade the initial area that the shape will occupy.
 *
 * This function is not especially efficient, but is only ever called
 * once per instance. 
 * 
 */
void PeggyData::initialFill() {
	uint8_t col, row, brightnessVal;

	// find top left corner values
	uint8_t xCell = x / CELL_SIZE;
	uint8_t xPosInCell = x % CELL_SIZE;	
	uint8_t yCell = y / CELL_SIZE;
	uint8_t yPosInCell = y % CELL_SIZE;
	
	// find top left corner values
	uint8_t widthTopLeft = CELL_SIZE - xPosInCell;
	uint8_t heightTopLeft = CELL_SIZE - yPosInCell;
	
	// number of cells between sides
	uint8_t widthInWholeCells = (width - widthTopLeft) / CELL_SIZE;
	uint8_t heightInWholeCells = (height - heightTopLeft) / CELL_SIZE;
	
	// find bottom right corner values
	uint8_t widthBottomRight = width - (CELL_SIZE * widthInWholeCells) - widthTopLeft;
	uint8_t heightBottomRight = height - (CELL_SIZE * heightInWholeCells) - heightTopLeft;
	
	// shade top left corner
	myDisplay->changeCellBrightness(xCell, yCell, (widthTopLeft * heightTopLeft));
	
	// shade top row
	brightnessVal = (heightTopLeft * CELL_SIZE);
	for (col = 1; col <= widthInWholeCells; col++)
		myDisplay->changeCellBrightness(xCell + col, yCell, brightnessVal);
	
	// shade top right corner
	myDisplay->changeCellBrightness(xCell + widthInWholeCells + 1, yCell, 
		(widthBottomRight * heightTopLeft));
		
	// shade body of shape
	uint8_t leftSideBrightness = (widthTopLeft * CELL_SIZE);
	uint8_t rightSideBrightness = (widthBottomRight * CELL_SIZE);
	for (row = 1; row <= heightInWholeCells; row++) {
		// set leftmost
		myDisplay->changeCellBrightness(xCell, yCell + row, leftSideBrightness);
		// set body
		for (col = 1; col <= widthInWholeCells; col++)
			myDisplay->changeCellBrightness(xCell + col, yCell + row, MAX_BRIGHTNESS);
		// set right-most
		myDisplay->changeCellBrightness(xCell + col, yCell + row, rightSideBrightness);
	}
		
	// shade bottom left corner
	myDisplay->changeCellBrightness(xCell, yCell + row, (widthTopLeft * heightBottomRight));
	// shade bottom
	brightnessVal = (heightBottomRight * CELL_SIZE);
	for (col = 1; col <= widthInWholeCells; col++)
		myDisplay->changeCellBrightness(xCell + col, yCell + row, brightnessVal);
	
	//shade bottom right corner
	myDisplay->changeCellBrightness(xCell + col, yCell + row, (widthBottomRight * heightBottomRight));
}


/* 
 * Perform the next move in the array of moves. After the move is performed, 
 * check whether the shape has 'landed'. If it has, return true, otherwise
 * return false. 
 *
 */
bool PeggyData::nextMove() {
	if (landed) 
		return true;

	unsigned int thisIndex = moveIndex / MOVES_PER_BYTE;
	unsigned int segment = (moveIndex % MOVES_PER_BYTE) * 2;
	prog_uchar thisMove = (prog_uchar) pgm_read_byte(moves + thisIndex);
	thisMove = (thisMove >> segment) & 3U;
	
	switch (thisMove) {
		//up
		case 0:
			y--;
			changeRow(x, y + height, DARKEN);
			changeRow(x, y, BRIGHTEN);
			break;
		//right
		case 1:
			changeCol(x, y, DARKEN);
			changeCol(x + width, y, BRIGHTEN);
			x++;
			break;
		//down
		case 2:
			changeRow(x, y, DARKEN);
			changeRow(x, y + height, BRIGHTEN);
			y++;
			break;
		//left
		case 3:
		  if (x <= 0) {
        break;
      }
			x--;
			changeCol(x + width, y, DARKEN);
			changeCol(x, y, BRIGHTEN);
			break;
	}
	
	moveIndex++;
	if (moveIndex >= moveArraySize) 
		moveIndex = 0;
		
	if(isLanded(x / CELL_SIZE, width - (CELL_SIZE - (x % CELL_SIZE)), 
			((y + height) / CELL_SIZE))) {
		setLanded();
		return true;
	}
	return false;
}

/* 
 * Recursively check whether the shape has 'landed' 
 */
bool PeggyData::isLanded(uint8_t xCell, int8_t remainder, uint8_t verticalDisplacement) {
	if (remainder < 0 || xCell >= COLS)
		return false;
	
	if (myDisplay->contour[xCell] <= verticalDisplacement)
		return true;
	else 
		return isLanded(++xCell, remainder - 4, verticalDisplacement);
}

/* 
 * Set the contour information, and set 'landed' flag. 
 */
void PeggyData::setLanded() {
	this->landed = true;
	
	int8_t remainder = width - (CELL_SIZE - (x % CELL_SIZE));
	uint8_t xCell = x / CELL_SIZE;
	uint8_t altitude = y / CELL_SIZE;
	
	while (remainder >= 0) {
		myDisplay->contour[xCell] = altitude;
		xCell++;
		remainder -= 4;
	}
}

void PeggyData::changeRow(uint8_t startX, uint8_t startY, uint8_t brightenOrDarken) {
	// change left corner
	uint8_t xCell = startX / CELL_SIZE;
	uint8_t yCell = startY / CELL_SIZE;
	int8_t widthLeft = CELL_SIZE - (startX % CELL_SIZE);
	myDisplay->changeCellBrightness(xCell, yCell, (brightenOrDarken) ? widthLeft : -widthLeft);

	// change row
	uint8_t limitCell = xCell + ((width - widthLeft) / CELL_SIZE);
	int8_t change = (brightenOrDarken) ? CELL_SIZE : -CELL_SIZE;
	for (xCell += 1; xCell <= limitCell; xCell++)
		myDisplay->changeCellBrightness(xCell, yCell, change);
		
	// change right corner
	xCell = (startX + width) / CELL_SIZE;
	int8_t widthRight = (width - widthLeft) % CELL_SIZE;
	myDisplay->changeCellBrightness(xCell, yCell, (brightenOrDarken) ? widthRight : -widthRight);
}


void PeggyData::changeCol(uint8_t startX, uint8_t startY, uint8_t brightenOrDarken) {
	// change top corner
	uint8_t xCell = startX / CELL_SIZE;
	uint8_t yCell = startY / CELL_SIZE;
	int8_t heightTop = CELL_SIZE - (startY % CELL_SIZE);
	myDisplay->changeCellBrightness(xCell, yCell, (brightenOrDarken) ? heightTop : -heightTop);

	// change side
	uint8_t limitCell = yCell + ((height - heightTop) / CELL_SIZE);
	int8_t change = (brightenOrDarken) ? CELL_SIZE : -CELL_SIZE;
	for (yCell += 1; yCell <= limitCell; yCell++)
		myDisplay->changeCellBrightness(xCell, yCell, change);
		
	// change bottom corner
	yCell = (startY + height) / CELL_SIZE;
	int8_t bottomHeight = (height - heightTop) % CELL_SIZE;
	myDisplay->changeCellBrightness(xCell, yCell, (brightenOrDarken) ? bottomHeight : -bottomHeight);
}
