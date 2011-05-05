/*
 *  LEDDisplay.h
 *  
 *  Created by Gareth Lloyd on 27/07/2009.
 *
 */

#ifndef LEDDisplay_h
#define LEDDisplay_h

#include <inttypes.h>

#define ROWS 25
#define COLS 25
#define MAX_BRIGHTNESS 15


class LEDDisplay {
	// 4 display buffers, for 16 levels of brightness
	private:
		uint32_t buff1[25];
		uint32_t buff2[25];
		uint32_t buff4[25];
		uint32_t buff8[25];
	
		uint8_t interruptNumber;

		void setCellBrightness(uint8_t col, uint8_t row, uint8_t newVal);

	public:
		uint8_t contour[COLS];
	
		void init();
	
		void changeCellBrightness(uint8_t col, uint8_t row, int8_t change); 

		void refresh();
};

#endif
