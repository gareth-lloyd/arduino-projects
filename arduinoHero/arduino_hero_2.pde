#include <avr/io.h>
#include <avr/interrupt.h>
#include <inttypes.h>

#define LATCH 12
#define ROWS 8
#define COLUMNS 8
#define MAX_INTERRUPT 15

#define TOTAL_LEDS_PER_ROW 24

/* These vars are global so that they're accessible 
to the interrupt service routine*/
// LED states: we only need to store 4 bits per LED, therefore 32 per row.
uint32_t greenRows[ROWS];
uint32_t blueRows[ROWS];
uint32_t redRows[ROWS];
// keep track of interrupts
uint8_t interruptNumber = 0;
// holding vars for colour info
char green, red, blue;

void setup() {
  pinMode(LATCH, OUTPUT); 
  // set PORTD for output
  DDRD = B11111111;
  // set PORTC 0 - 3 for button input
  DDRC &= B11110000;
  PORTC = 0;
  // Set PORTC4 for output (the audio signal)
  DDRC |= B00010000;
 
  // S P I   S E T U P  
  //SET MOSI, SCK Output, all other SPI as input: 
  DDRB |= (1 << 5) | (1 << 3) | (1 << 2) | (1 << 1);
  // ENABLE SPI by setting the SPE bit in SP control register
  // SET chip as MASTER by setting MSTR bit 
  // leave SPR1 and SPR0 at 0 to set clock freq fck/4
  SPCR =  (1 << SPE) |  (1 << MSTR) ;

  // T I M E R   1   S E T U P
  // set timer counter control register 1 A
  TCCR1A = 0;
  // set timer counter control regsiter 1 B
  TCCR1B = 0<<CS12 | 0<<CS11 | 1<<CS10;
  // set timer 1 interrupt mask register to enable 
  // overflow interrupt
  TIMSK1 = 1 << TOIE1;
  
  clearAndSet();
}

void clearAndSet() {
  for (int row = 0; row < ROWS; row++) {
    greenRows[row] = 0;
    blueRows[row] = 0;
    redRows[row] = 0;
  }
}


/* MAIN LOOP
 * repeatedly iterate the game, with a delay between
 * iterations to let the player attempt to hit the 
 * right button. If they do not, end the game.
 */
void loop() {
  boolean stillAlive = true;
  
  while (stillAlive) {
    delay(400);
    iterate();
  }
}

/*
 * Shift everything down the display
 */
void iterate() {
  uint8_t i;
  for (i = 0, uint8_t j = ROWS - 1; i < j; i++) {
    greenRows[i] = greenRows[i + 1];
    blueRows[i] = blueRows[i + 1];
    redRows[i] = redRows[i + 1];
  }
  
  greenRows[i] = random();
  blueRows[i] = random();
  redRows[i] = random();
}


/*
 * Interrupt service routine to refresh the display and
 * play audio when a button is pressed. 
 *
 * This code executes every time Timer1 overflows, 
 * i.e. 16,000,000 / 2^16 times per second, approximately
 * 250 Hz. 
 *
 * The audio implementation is poor because it's forced to
 * share a timer with the display refresh routine. It works
 * acceptably well in spite of itself.
 */
ISR(TIMER1_OVF_vect) {
  if (interruptNumber++ > 
  
  for (char row = 0; row < ROWS; row++) {
    // V I D E O signal

    
    // Deal with each of 5 possible note placements
    switch (gameStateArray[row]) {
      case 0: 
        blue = B11111111;
        red = 0;
        break;
      case 1:
        red =  B00000011;
        blue = B11111100;
        break;
      case 2: 
        red =  B00001100;
        blue = B11110011;
        break;
      case 4:
        red =  B00110000;
        blue = B11001111;
        break;
      case 8:
        red =  B11000000;
        blue = B00111111;
        break;
    }
    
    // ALL ROWS OFF
    PORTD = B11111111;
    
    // set LATCH low:
    PORTB &= B11101111;
    // send data to SPDR register, 8 bits at a time
    SPDR = green;
    // wait for end of transmission:
    while (!(SPSR & (1 << SPIF))) ;
    SPDR = blue;
    while (!(SPSR & (1 << SPIF))) ;
    SPDR = red;
    while (!(SPSR & (1 << SPIF))) ;

    //set LATCH high:
    PORTB |= B00010000;

    //turn on correct row
    PORTD = B11111111 ^ (1 << row);
    delayMicroseconds(2);
  }
  
  delayMicroseconds(6);
  //turn off all rows
  PORTD = B11111111;
}

