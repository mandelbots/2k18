package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.EnumMap;
import java.util.Map;

@TeleOp(name = "TeleOp", group = "TeleOp")
public class MandelbotsDriverControlled extends MandelbotsOpMode {
	private final ElapsedTime runtime = new ElapsedTime();
	private final Map<Motor, DcMotor> motorMap = new EnumMap<>(Motor.class);
	
	@Override
	public void runOpMode() throws InterruptedException {
		telemetry.addData("Status", "FUUUUUUUUURYYY 7!!!!!");
		telemetry.update();
		
		for (Motor motor : Motor.values()) {
			motorMap.put(motor, hardwareMap.get(DcMotor.class, motor.toString()));
		}
		
		getMotor(Motor.FRONT_RIGHT).setDirection(DcMotor.Direction.REVERSE);
		getMotor(Motor.BACK_RIGHT).setDirection(DcMotor.Direction.REVERSE);
		
		waitForStart();
		runtime.reset();
		
		while (opModeIsActive()) {
			this.moveRobot(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
			// TODO add op mode code
		}
	}
	
	@Override
	public DcMotor getMotor(Motor motor) {
		return motorMap.get(motor);
	}
}
