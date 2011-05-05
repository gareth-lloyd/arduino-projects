#include <LEDDisplay.h>
#include <PeggyData.h>
#include <avr/interrupt.h>
#include <inttypes.h>
#include <avr/pgmspace.h>
#include <Wire.h>


#define PEGGY_ADDRESS 1
#define MAX_DATA_COUNT 10
#define I2C_INPUT_MAX_SIZE 12
#define INPUT_TERMINATOR ';'
#define INPUT_SEPARATOR '&'

// 4 moves per byte, therefore size is 4 * 17, 68
#define FALL_PATH_SIZE 68
static prog_uchar fallPath[17] PROGMEM = {
B11111011, B11101111, B10111110, B11101110, 
B10101001, B10011001, B01100101, B10010110, 
B01010101, B10010110, B01011001, B01100110, 
B01101010, B10111011, B10111110, B11111011, B11101111}; 

/* GLOBAL VARIABLES: UGLY BUT NECESSARY */

// This array of input must be global so that it can 
// be accessed by the I2C library's service function
char i2cInput[I2C_INPUT_MAX_SIZE];
// Same is true of the inputCount
uint8_t inputCount = 0;
// The display object must be global so that it can be
// accessed inside the display refresh interrupt routine
LEDDisplay sd;


void setup() {
  // I 2 C   S E T U P
  //Specify an address to set up Peggy as an I2C slave
  Wire.begin(PEGGY_ADDRESS);
  // Register the receiveEvent function with the library.
  Wire.onReceive(receiveEvent);
  
  // set pull up resistors on 4, 5 on PORTC.
  // These pins handle I2C comms.
  PORTC |= (1 << PC4) | (1 << PC5);
  
  // Call the display's initiatiion routine:
  sd.init();  
}


/*
 * Main Routine. In the background, the I2C functionality of
 * the microcontroller is continually listening for input. 
 * When input is detected, it is parsed and turned into a 
 * 'Social Data' item. 
 */
void loop() {    
  PeggyData data[MAX_DATA_COUNT];
  uint8_t dataCount = 0;
 
  // Continually listen for new input, and move existing items 
  while (1) {
    // Finding a string terminator means input is ready. 
    if (i2cInput[inputCount - 1] == INPUT_TERMINATOR) {
      inputCount = 0;
      createSocialData(data, &dataCount);
    }
    
    uint8_t i;
    for (i = 0; i < dataCount; i++) {
      // A return value true means that the item has 
      // finished its descent and can be discarded.
      if (data[i].nextMove()) {
        removeItemFromDataArray(i, data, &dataCount);
      }  
    }
    delay(50);
  }
}

/*
 * Read from the inputString, attempting to parse values for the 
 * x-position and size of a new Social Data item. If successful,
 * add the new item to the array. 
 */
void createSocialData(PeggyData* data, uint8_t* dataCount) {
  if (*dataCount >= MAX_DATA_COUNT) {
    return;
  }
  
  uint8_t index = 0;
  uint8_t x = getNumberFromString(i2cInput, &index);
  if (x < 5 || x > 95) {
    return;
  }
  
  index++;
  uint8_t sideLength = getNumberFromString(i2cInput, &index);
  if (sideLength < 5 || sideLength > 25) {
    return;
  }
  
  data[*dataCount].setup(&sd, x, 0, sideLength, sideLength, 0, FALL_PATH_SIZE, fallPath);
  (*dataCount)++;
}


/* Calculates and returns the decimal value of a sequence of 
characters, and returns 0 if an invalid char is encountered. */
uint8_t getNumberFromString(char* input, uint8_t* index) {
  uint8_t numericValue = 0;
  char thisDigit = input[(*index)];
  while(thisDigit != INPUT_SEPARATOR && thisDigit != INPUT_TERMINATOR) {
    if (thisDigit < '0' || thisDigit > '9' || numericValue >= 26) {
      return 0;
    }
    numericValue *= 10;
    numericValue += thisDigit - '0';
    thisDigit = input[++(*index)];
  }
  return numericValue;
}

/*
 * Remove one data item from the array of items, and decrement
 * the dataCount accordingly. 
 */
void removeItemFromDataArray(uint8_t itemForRemoval, PeggyData* data, uint8_t* dataCount) {
  if (*dataCount == 0) {
    return;
  }
  
  // Move last item to replace removed item. It is 
  // possible that removed item IS last item, but
  // the effect is the same.
  data[itemForRemoval] = data[(*dataCount) - 1];
  (*dataCount)--;
  
  // ensure that the swapped item gets its move. Again, 
  // if this happens to be the removed item, nextMove()
  // will have no effect.
  data[itemForRemoval].nextMove();
}


/*
 * Service function for the I2C library. When data is available
 * for receipt, this function will read it into a string. 
 */
void receiveEvent(int numBytes)
{
  while (Wire.available())
  {
    i2cInput[inputCount++] = Wire.receive();
    if (inputCount >= I2C_INPUT_MAX_SIZE) {
       inputCount = 0;
    }
  }
}

/* 
 * Interrupt Service Vector to refresh the display. This 
 * interrupt occurs regularly, roughly 300 times per second. 
 */
ISR(TIMER1_OVF_vect) {
  sd.refresh();
  TCNT1 = 25000U;
}
