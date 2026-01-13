//DEPS periphery/Buzzer.java
//DEPS periphery/Lcd.java
//DEPS periphery/LedButton.java
//DEPS periphery/MotionSensor.java
//DEPS ButtonStates.java

public class Controller {
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

	public ButtonStates checkButton() {
		return ButtonStates.RELEASED;
	}

	public void MoveIntoIdle() {
		displayHelloWorld();
	}

	public void MoveIntoSense() {

	}

	public void MoveIntoAlarm() {

	}

	public void resetAlarm() {
	}

	public void turnOffAlarm() {
	}

	public void turnOnAlarm() {
	}

}
