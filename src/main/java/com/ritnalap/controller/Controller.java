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
	private boolean lastButtonDown = false;
	private long lastPressTime = 0;
	private boolean waitingSecondPress = false;
	private final long DOUBLE_PRESS_MS = 500;

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

	public void displayIdle() {
		lcd.clearText();
		lcd.writeLine("Ritnalap", 0);
		lcd.writeLine("Idle", 1);
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

	public void displayCounter(String text) {
		lcd.clearText();
		lcd.writeLine("Ritnalap", 0);
		lcd.writeLine(text, 1);
	}

	public ButtonStates getButtonState() {
		boolean down = ledButton.isDown();
		long now = System.currentTimeMillis();

		if (down && !lastButtonDown) {
			if (waitingSecondPress && (now - lastPressTime <= DOUBLE_PRESS_MS)) {
				waitingSecondPress = false;
				lastPressTime = 0;
				lastButtonDown = down;
				return ButtonStates.DOUBLE_PRESS;
			} else {
				waitingSecondPress = true;
				lastPressTime = now;
			}
		}

		if (!down && waitingSecondPress && (now - lastPressTime > DOUBLE_PRESS_MS)) {
			waitingSecondPress = false;
			lastPressTime = 0;
			lastButtonDown = down;
			return ButtonStates.SINGLE_PRESS;
		}

		lastButtonDown = down;
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
		displayIdle();
		turnOffAlarm();
		turnOffLed();
	}

	public void moveIntoSense() {
		try {
			for (int i = 5; i >= 0; i--) {
				displayCounter(String.valueOf(i));
				Thread.sleep(1000); // wait 1 second
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		displaySense();
		turnOnLed();
	}

	public void moveIntoAlarm() {
		displayAlarm();
		turnOnAlarm();
	}

	public void turnOffAlarm() {
		buzzer.off();
	}

	public void turnOnAlarm() {
		buzzer.on(150);
	}

	public void turnOffLed() {
		ledButton.off();
	}

	public void turnOnLed() {
		ledButton.on();
	}
}
