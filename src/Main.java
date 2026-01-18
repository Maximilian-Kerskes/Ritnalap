//usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.slf4j:slf4j-api:1.7.35
//DEPS org.slf4j:slf4j-simple:1.7.35
//DEPS com.pi4j:pi4j-core:2.3.0
//DEPS com.pi4j:pi4j-plugin-raspberrypi:2.3.0
//DEPS com.pi4j:pi4j-plugin-pigpio:2.3.0
//DEPS com.pi4j:pi4j-plugin-linuxfs:2.3.0

//DEPS periphery/Buzzer.java
//DEPS periphery/Lcd.java
//DEPS periphery/LedButton.java
//DEPS periphery/MotionSensor.java
//DEPS ButtonStates.java
//DEPS MotionSensorStates.java
//DEPS StateMachineStates.java

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalState;

public class Main {
	Context pi4j;

	public static void main(String[] args) throws Exception {

		Main main = new Main();
		// LCD connect on I2C
		LedButton ledButton = new LedButton(main.pi4j, 5, 6);
		Buzzer buzzer = new Buzzer(main.pi4j, 12, true);
		Lcd lcd = new Lcd(main.pi4j);
		MotionSensor motionSensor = new MotionSensor(main.pi4j, 26);

		StateMachine stateMachine = new StateMachine(new Controller(ledButton, buzzer, lcd, motionSensor));

		try {

			while (true) {
				stateMachine.checkState();
				Thread.sleep(50);
			}
		} finally {

			main.pi4j.shutdown();
		}

	}

	Main() {
		pi4j = Pi4J.newAutoContext();
	}

}

class Controller {
	private final LedButton ledButton;
	private final Buzzer buzzer;
	private final Lcd lcd;
	private final MotionSensor motionSensor;

	public Controller(LedButton ledButton, Buzzer buzzer, Lcd lcd, MotionSensor motionSensor) {
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

class StateMachine {
	private StateMachineStates state = StateMachineStates.IDLE;
	private final Controller controller;

	public StateMachine(Controller controller) {
		this.controller = controller;
	}

	public void checkState() {
		ButtonStates buttonState = controller.getButtonState();
		MotionSensorStates motionSensorState = controller.getMotionSensorState();
		switch (state) {
			case IDLE:
				if (buttonState == ButtonStates.DOUBLE_PRESS) {
					controller.moveIntoSense();
					state = StateMachineStates.SENSING;
				}
				break;
			case SENSING:
				if (buttonState == ButtonStates.DOUBLE_PRESS) {
					controller.moveIntoIdle();
					state = StateMachineStates.IDLE;
				}
				if (motionSensorState == MotionSensorStates.MOTION) {
					controller.moveIntoAlarm();
					state = StateMachineStates.ALARM;
				}
				break;
			case ALARM:
				if (buttonState == ButtonStates.SINGLE_PRESS) {
					controller.turnOffAlarm();
					controller.moveIntoSense();
					state = StateMachineStates.SENSING;
				} else if (buttonState == ButtonStates.DOUBLE_PRESS) {
					controller.moveIntoIdle();
					state = StateMachineStates.IDLE;
				}
				break;
		}
	}

}
