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

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.DigitalState;

public class Main {
	Context pi4j;

	public static void main(String[] args) throws Exception {
		System.out.println("--- Test Klasse ---");

		Main testEnv = new Main();
		// LCD connect on I2C
		Lcd myLcd = new Lcd(testEnv.pi4j);
		myLcd.displayText("BasisKlassen Test", 1, 0);
		myLcd.displayText("Start", 2, 0);
		Thread.sleep(500);

		// BUZZER connect on PWM
		myLcd.displayText("Buzzer", 2, 0);
		testEnv.testBuzzer();
		Thread.sleep(500);

		// LedButton connect on D5
		myLcd.displayText("LedButton", 2, 0);
		testEnv.testLedButton();
		Thread.sleep(500);

		// testMotionSensor connect on D26
		myLcd.displayText("testMotionSensor", 2, 0);
		testEnv.testMotionSensor();

		testEnv.pi4j.shutdown();
	}

	Main() {
		pi4j = Pi4J.newAutoContext();
	}

	public void testMotionSensor() throws Exception {
		MotionSensor myMs = new MotionSensor(pi4j, 26);
		System.out.println("isUp:" + myMs.isUp());
		Thread.sleep(1000);
		System.out.println("10Sekunden warten für Listener --> Bewegen");
		for (int i = 0; i < 10; i++) {
			myMs.onMotion(() -> System.out.println("BEWEGUNG"));
			myMs.onNoMotion(() -> System.out.println("KEINE BEWEGUNG"));
			Thread.sleep(1000);
			System.out.println("isUp:" + myMs.isUp());
		}
	}

	public void testLcd() {
		System.out.println("LCD");
		Lcd myLcd = new Lcd(this.pi4j);
		myLcd.displayText("Basis Test", 1, 0);
		myLcd.displayText("Hallo Welt", 2, 0);

	}

	public void testBuzzer() throws Exception {

		System.out.println("Buzzer");
		Buzzer myBuzzer = new Buzzer(this.pi4j, 12, true);
		myBuzzer.on(330);
		Thread.sleep(1000);
		myBuzzer.off();
	}

	public void testLedButton() throws Exception {
		// LED Button
		System.out.println("LedButton");
		LedButton myLedButton = new LedButton(this.pi4j, 5, 6);

		myLedButton.on();
		Thread.sleep(1000);
		myLedButton.off();
		Thread.sleep(1000);
		myLedButton.toggle();
		Thread.sleep(1000);
		myLedButton.toggle();
		myLedButton.onDown(() -> System.out.println("BonDown utton gedrückt"));
		myLedButton.onUp(() -> System.out.println("onUp Button losgelassen"));
		System.out.println("Drücke Button");
		Thread.sleep(10000);
	}
}
