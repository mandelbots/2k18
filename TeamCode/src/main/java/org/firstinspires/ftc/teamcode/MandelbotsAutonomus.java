package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.EnumMap;
import java.util.Map;

@Autonomous(name = "Autonomous", group = "Autonomous")
public class MandelbotsAutonomus extends LinearOpMode implements IMandelbotsOpMode {
	private final ElapsedTime runtime = new ElapsedTime();
	private final Map<Motor, DcMotor> motorMap = new EnumMap<>(Motor.class);

	@Override
	public void runOpMode() throws InterruptedException {
		telemetry.addData("Status", "Ready to avenge last year!");
		telemetry.update();

		for (Motor motor: Motor.values()) {
			motorMap.put(motor, hardwareMap.get(DcMotor.class, motor.toString()));
		}

		getMotor(Motor.FRONT_RIGHT).setDirection(DcMotor.Direction.REVERSE);
		getMotor(Motor.BACK_RIGHT).setDirection(DcMotor.Direction.REVERSE);

		waitForStart();
		runtime.reset();

		while (opModeIsActive()) {
			// TODO add op mode code
		}
	}

	@Override
	public DcMotor getMotor(Motor motor) {
		return motorMap.get(motor);
	}
}
