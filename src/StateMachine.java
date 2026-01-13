//DEPS StateMachineStates.java
//DEPS Controller.java

public class StateMachine {
	private StateMachineStates state = StateMachineStates.IDLE;
	private final Controller controller;

	public StateMachine(Controller controller) {
		this.controller = controller;
	}

	public void checkState() {
		ButtonStates buttonState = controller.checkButton();
		switch (state) {
			case IDLE:
				if (buttonState == ButtonStates.DOUBLE_PRESS) {
					controller.MoveIntoSense();
					state = StateMachineStates.SENSING;
				}
				break;
			case SENSING:
				if (buttonState == ButtonStates.DOUBLE_PRESS) {
					controller.MoveIntoIdle();
					state = StateMachineStates.IDLE;
				}
				break;
			case ALARM:
				if (buttonState == ButtonStates.SINGLE_PRESS) {
					controller.resetAlarm();
					controller.MoveIntoSense();
					state = StateMachineStates.SENSING;
				}
				break;
		}
	}

}
