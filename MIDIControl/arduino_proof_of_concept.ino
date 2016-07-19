#include <MIDI.h>
#include <midi_Defs.h>
#include <midi_Message.h>
#include <midi_Namespace.h>
#include <midi_Settings.h>

#define SHRUTHI_OUT_CHANNEL 2
#define MOVING_AVG_LENGTH 5
#define DEBOUNCE_CYCLES 50

#define PROGRAM_CHANGE 'p'
#define CONTROL_CHANGE 'c'
#define INCREMENT_CC 'i'
#define DECREMENT_CC 'd'
#define MIN_CC 127
#define MAX_CC 127

typedef struct {
  int pin;
  int totalPreviousReadings;
  int currentReading;
  int cc;
} AnalogControl;

typedef struct {
  int pin;
  bool lastReading;
  int debounceCyclesRemaining;
  char outputType;
  int cc;
  int value;
} DigitalControl;

#define NUM_ANALOG_CONTROLS 2

#define ENV_1_ATTACK 104
#define ENV_1_DECAY 105
#define ENV_1_SUSTAIN 106
#define ENV_1_RELEASE 107
#define ENV_2_ATTACK 108
#define ENV_2_DECAY 109
#define ENV_2_SUSTAIN 110
#define ENV_2_RELEASE 111
#define OSC_1_RANGE 22
#define OSC_2_RANGE 26
#define OSC_2_DETUNE 27
#define OSC_BALANCE 29
#define SUB_OSC_VOLUME 30
#define LFO_1_RATE 114
#define LFO_2_RATE 117
#define PORTAMENTO 84

AnalogControl defineAnalog(int pin, int cc) {
  pinMode(pin, INPUT);
  int reading = analogRead(pin);
  int total = reading * MOVING_AVG_LENGTH;
  return AnalogControl {pin, total, reading, cc};
}

AnalogControl ANALOG_CONTROLS[NUM_ANALOG_CONTROLS] = {
  defineAnalog(A0, OSC_1_RANGE),
  defineAnalog(A1, OSC_2_RANGE)
//  defineAnalog(A2, OSC_2_DETUNE),
//  defineAnalog(A3, OSC_BALANCE),
//  defineAnalog(A4, SUB_OSC_VOLUME),
//  defineAnalog(A5, LFO_1_RATE),
//  defineAnalog(A6, LFO_2_RATE),
//  defineAnalog(A7, PORTAMENTO),
//  defineAnalog(A8, ENV_1_ATTACK),
//  defineAnalog(A9, ENV_1_DECAY),
//  defineAnalog(A10, ENV_1_SUSTAIN),
//  defineAnalog(A11, ENV_1_RELEASE),
//  defineAnalog(A12, ENV_2_ATTACK),
//  defineAnalog(A13, ENV_2_DECAY),
//  defineAnalog(A14, ENV_2_SUSTAIN),
//  defineAnalog(A15, ENV_2_RELEASE),
};

#define NUM_DIGITAL_CONTROLS 3
#define ALL_NOTES_OFF 123

DigitalControl DIGITAL_CONTROLS[NUM_DIGITAL_CONTROLS] = {
  {2, LOW, 0, PROGRAM_CHANGE, 0, 0},
  {3, LOW, 0, PROGRAM_CHANGE, 0, 1},
  {4, LOW, 0, PROGRAM_CHANGE, 0, 2}
//  {5, LOW, 0, PROGRAM_CHANGE, 0, 3},
//  {6, LOW, 0, PROGRAM_CHANGE, 0, 4},
//  {7, LOW, 0, PROGRAM_CHANGE, 0, 5},
//  {8, LOW, 0, PROGRAM_CHANGE, 0, 6},
//  {9, LOW, 0, PROGRAM_CHANGE, 0, 7},
  
//  {10, LOW, 0, CONTROL_CHANGE, ALL_NOTES_OFF, 0}
};

MIDI_CREATE_DEFAULT_INSTANCE();

void setup() {
  MIDI.begin(SHRUTHI_OUT_CHANNEL);
  for (int i=0; i < NUM_DIGITAL_CONTROLS; i++) {
    pinMode(DIGITAL_CONTROLS[i].pin, INPUT);
  }
}

void loop() {
  MIDI.read();
  for (int i=0; i < NUM_ANALOG_CONTROLS; i++) {
    scanAnalogControl(ANALOG_CONTROLS[i]);
  }
  MIDI.read();
  for (int i=0; i < NUM_DIGITAL_CONTROLS; i++) {
    scanDigitalControl(DIGITAL_CONTROLS[i]);
  }
}

int scale(int reading) {
  long x = reading * 127L;
  return (int) (x / 1024);  
}

void scanAnalogControl(AnalogControl& control) {
  int oldValue = control.currentReading;
  control.totalPreviousReadings -= oldValue;
  control.totalPreviousReadings += analogRead(control.pin);
  control.currentReading = control.totalPreviousReadings / MOVING_AVG_LENGTH;
  
  if (oldValue != control.currentReading) {
    MIDI.sendControlChange(control.cc, scale(control.currentReading), SHRUTHI_OUT_CHANNEL);
  }
}

void scanDigitalControl(DigitalControl& control) {
  if (control.debounceCyclesRemaining) {
    control.debounceCyclesRemaining--;
    return;
  }
  int reading = digitalRead(control.pin);
  if (reading == HIGH && control.lastReading == LOW) {
    performDigitalControl(control);
    control.debounceCyclesRemaining = DEBOUNCE_CYCLES;
  }
  control.lastReading = reading;
}

void performDigitalControl(DigitalControl& control) {
  switch (control.outputType) {
    case CONTROL_CHANGE:
      MIDI.sendControlChange(control.cc, control.value, SHRUTHI_OUT_CHANNEL);
      break;
    case PROGRAM_CHANGE:
      MIDI.sendProgramChange(control.value, SHRUTHI_OUT_CHANNEL);
      break;
    case DECREMENT_CC:
      if (control.value >= MIN_CC) {
        MIDI.sendControlChange(control.cc, --control.value, SHRUTHI_OUT_CHANNEL);
      }
      break;
    case INCREMENT_CC:
      if (control.value < MAX_CC) {
        MIDI.sendControlChange(control.cc, ++control.value, SHRUTHI_OUT_CHANNEL);
      }
      break;
  }
}
