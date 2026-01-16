package com.ritnalap.controller;

import com.ritnalap.core.states.ButtonStates;
import com.ritnalap.core.states.MotionSensorStates;
import com.ritnalap.core.states.StateMachineStates;
import com.ritnalap.periphery.Buzzer;
import com.ritnalap.periphery.Lcd;
import com.ritnalap.periphery.LedButton;
import com.ritnalap.periphery.MotionSensor;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Controller {
  private final LedButton ledButton;
  private final Buzzer buzzer;
  private final Lcd lcd;
  private final MotionSensor motionSensor;

  public Controller(LedButton ledButton, Buzzer buzzer, Lcd lcd,
                    MotionSensor motionSensor) {
    this.ledButton = ledButton;
    this.buzzer = buzzer;
    this.lcd = lcd;
    this.motionSensor = motionSensor;
  }

  public void displayHelloWorld() {
    lcd.clearText();
    lcd.writeLine("Ritnalap", 0);
    lcd.writeLine("Ready", 1);
  }

  public void displaySense() {
    lcd.clearText();
    lcd.writeLine("Ritnalap", 0);
    lcd.writeLine("Sensing", 1);
  }

  public void displayAlarm() {
    lcd.clearText();

    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    lcd.writeLine(now.format(dateFormatter), 0);
    lcd.writeLine(now.format(timeFormatter), 1);
  }

  public ButtonStates getButtonState() {
    if (ledButton.isDown()) {
      try {
        Thread.sleep(200);
        if (!ledButton.isDown()) {
          return ButtonStates.SINGLE_PRESS;
        } else {
          return ButtonStates.DOUBLE_PRESS;
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return ButtonStates.RELEASED;
  }

  public MotionSensorStates getMotionSensorState() {
    if (motionSensor.isUp()) {
      return MotionSensorStates.MOTION;
    } else {
      return MotionSensorStates.NO_MOTION;
    }
  }

  public void moveIntoIdle() {
    displayHelloWorld();
    turnOffAlarm();
    turnOffLed();
  }

  public void moveIntoSense() {
    displaySense();
    turnOnLed();
  }

  public void moveIntoAlarm() {
    displayAlarm();
    turnOnAlarm();
  }

  public void turnOffAlarm() { buzzer.off(); }

  public void turnOnAlarm() { buzzer.on(150); }

  public void turnOffLed() { ledButton.off(); }

  public void turnOnLed() { ledButton.on(); }
}
