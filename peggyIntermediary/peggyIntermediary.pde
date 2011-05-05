#include <Wire.h>
#include <inttypes.h>
#define PEGGY_ADDRESS 1
#define INPUTSIZE 200;


void setup ()
{
  Serial.begin(9600);
  Wire.begin();
  PORTC |=  (1<<PC4) | (1<<PC5); 
}

// If any data becomes available, send it on to Peggy
void loop()
{
  uint8_t count = Serial.available();
  
  if (count > 0)
  {
    // dont send too many bytes at once
    if (count > 16) count = 16;
  
    Wire.beginTransmission(PEGGY_ADDRESS); 
    while (count-- > 0 )
    {
      uint8_t c = Serial.read(); 
      Wire.send(c);
    }
    Wire.endTransmission();   
  }
}
