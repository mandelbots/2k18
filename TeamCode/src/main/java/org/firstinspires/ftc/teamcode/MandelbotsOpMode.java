package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

public abstract class MandelbotsOpMode extends LinearOpMode {
	abstract DcMotor getMotor(Motor motor);
	
	public void moveRobot(double x, double y, double rot) {
		// see https://ftcforum.usfirst.org/forum/ftc-technology/android-studio/6361-mecanum-wheels-drive-code-example
		
		double r = Math.hypot(x, y);
		double robotAngle = Math.atan2(y, x) - Math.PI / 4;
		final double v1 = r * Math.cos(robotAngle) + rot;
		final double v2 = r * Math.sin(robotAngle) - rot;
		final double v3 = r * Math.sin(robotAngle) + rot;
		final double v4 = r * Math.cos(robotAngle) - rot;
		
		this.getMotor(Motor.FRONT_LEFT).setPower(v1);
		this.getMotor(Motor.FRONT_RIGHT).setPower(v2);
		this.getMotor(Motor.BACK_LEFT).setPower(v3);
		this.getMotor(Motor.BACK_RIGHT).setPower(v4);
	}
	
	public void grabLatch() {
		// TODO add
	}
	
	public void releaseLatch()  {
		// TODO add
	}
	
	public void moveRackAndPinion() {
		// TODO add
	}
	
	/**
	 * @param multiplier -1 for reverse, 0 for stop, 1 for forward
	 */
	public void runIntake(int multiplier) {
		// TODO add
	}
	
	void dumpContents() {
		// TODO add
	}
}
