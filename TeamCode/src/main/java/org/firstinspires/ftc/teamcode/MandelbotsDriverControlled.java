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
		
		getMotor(Motor.FRONT_LEFT).setDirection(DcMotor.Direction.REVERSE);
		getMotor(Motor.BACK_LEFT).setDirection(DcMotor.Direction.REVERSE);
		
		getMotor(Motor.LIFT).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		getMotor(Motor.LIFT).setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		
		waitForStart();
		runtime.reset();
		
		while (opModeIsActive()) {
			this.moveRobot(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
			
			telemetry.addData("Lift position", getMotor(Motor.LIFT).getCurrentPosition());
			
			getMotor(Motor.LIFT).setPower((gamepad1.x ? 1 : 0) + (gamepad1.y ? -1 : 0));
			getMotor(Motor.ARM).setPower((gamepad1.a ? 1 : 0) + (gamepad1.b ? -1 : 0));
			// TODO add op mode code
			telemetry.update();
		}
	}
	
	@Override
	public DcMotor getMotor(Motor motor) {
		return motorMap.get(motor);
	}
}
