package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import java.util.Arrays;

public abstract class MandelbotsOpMode extends LinearOpMode {
	abstract DcMotor getMotor(Motor motor);
	abstract Servo getServo(ServoType servoType);
	abstract CRServo getCRServo(CRServoType servoType);
	
	public boolean isAutonomous() {
		return this.getClass().getAnnotation(Autonomous.class) != null;
	}
	
	public void moveRobot(double x, double y, double rot) {
		if (!isAutonomous()) {
			telemetry.addData("x", x);
			telemetry.addData("y", y);
			telemetry.addData("rot", rot);
		}
		
		double[] vals = new double[] { -y + x + rot, -y - x - rot, -y - x + rot, -y + x - rot};
		telemetry.addData("powers", Arrays.toString(vals));
		
		double scale = 1;
		for (double val: vals) {
			scale = Math.max(scale, Math.abs(val));
		}
		for (int i=0; i<4; ++i) {
			vals[i] /= scale;
		}
		
		for (int i=0; i<4; ++i) {
			this.getMotor(Motor.getMovementMotors().get(i)).setPower(vals[i]);
		}
		
		if (!isAutonomous()) {
			//telemetry.addData("Rotation", robotAngle * 180 / Math.PI);
		}
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
	
	public void dumpContents() {
		// TODO add
	}
}
