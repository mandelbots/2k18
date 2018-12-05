package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;

import java.util.concurrent.CompletableFuture;

/**
 * Common interface for op modes to consolidate common functions.
 * Implementors should also extend {@link com.qualcomm.robotcore.eventloop.opmode.OpMode}.
 */
public interface IMandelbotsOpMode {
	DcMotor getMotor(Motor motor);
	
	default void moveRobot(double x, double y, double rot) {
		// see https://ftcforum.usfirst.org/forum/ftc-technology/android-studio/6361-mecanum-wheels-drive-code-example
		
		double r = Math.hypot(x, y);
		double robotAngle = Math.atan2(y, x) - Math.PI / 4;
		final double v1 = r * Math.cos(robotAngle) + rot;
		final double v2 = r * Math.sin(robotAngle) - rot;
		final double v3 = r * Math.sin(robotAngle) + rot;
		final double v4 = r * Math.cos(robotAngle) - rot;
		
		getMotor(Motor.FRONT_LEFT).setPower(v1);
		getMotor(Motor.FRONT_RIGHT).setPower(v2);
		getMotor(Motor.BACK_LEFT).setPower(v3);
		getMotor(Motor.BACK_RIGHT).setPower(v4);
	}
	
	/**
	 * @return a completeable future triggered upon task completion
	 */
	default CompletableFuture<Void> grabLatch() {
		CompletableFuture<Void> promise = new CompletableFuture<>();
		// TODO add
		return promise;
	}
	
	/**
	 * @return a completeable future triggered upon task completion
	 */
	default CompletableFuture<Void> releaseLatch() {
		CompletableFuture<Void> promise = new CompletableFuture<>();
		// TODO add
		return promise;
	}
	
	default void moveRackAndPinion(double power) {
		// TODO add
	}
	
	/**
	 * Only use this for autonomous.
	 * @return a completeable future triggered upon task completion
	 */
	default CompletableFuture<Void> descendFully() {
		CompletableFuture<Void> promise = new CompletableFuture<>();
		// TODO add
		return promise;
	}
	
	/**
	 * @param multiplier -1 for reverse, 0 for stop, 1 for forward
	 */
	default void runIntake(int multiplier) {
		// TODO add
	}
	
	default void dumpContents() {
		// TODO add
	}
}
