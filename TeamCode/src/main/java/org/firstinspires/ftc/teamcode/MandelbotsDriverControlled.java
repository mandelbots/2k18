package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.EnumMap;
import java.util.Map;

@TeleOp(name = "TeleOp", group = "TeleOp")
public class MandelbotsDriverControlled extends MandelbotsOpMode {
	private final ElapsedTime runtime = new ElapsedTime();
	private final Map<Motor, DcMotor> motorMap = new EnumMap<>(Motor.class);
	private final Map<CRServoType, CRServo> crServoMap = new EnumMap<>(CRServoType.class);
	private final Map<ServoType, Servo> servoMap = new EnumMap<>(ServoType.class);
	
	private boolean aFlag = false, bFlag = false;
	
	@Override
	public void runOpMode() throws InterruptedException {
		telemetry.addData("Status", "FUUUUUUUUURYYY 7!!!!!");
		telemetry.update();
		
		for (Motor motor : Motor.values()) {
			motorMap.put(motor, hardwareMap.get(DcMotor.class, motor.toString()));
		}
		for (CRServoType servoType: CRServoType.values()) {
			crServoMap.put(servoType, hardwareMap.get(CRServo.class, servoType.toString()));
		}
		for (ServoType servoType: ServoType.values()) {
			servoMap.put(servoType, hardwareMap.get(Servo.class, servoType.toString()));
		}
		
		getMotor(Motor.FRONT_LEFT).setDirection(DcMotor.Direction.REVERSE);
		getMotor(Motor.BACK_LEFT).setDirection(DcMotor.Direction.REVERSE);
		
		getMotor(Motor.LIFT).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		getMotor(Motor.LIFT).setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
		
		getMotor(Motor.ARM).setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		
		waitForStart();
		runtime.reset();
		
		while (opModeIsActive()) {
			if (gamepad1.right_bumper) {
				this.releaseTheSupremeGod();
			}
			
			getServo(ServoType.BASKET).setPosition(getServo(ServoType.BASKET).getPosition() + (gamepad1.a && !aFlag ? 0.1 : 0) + (gamepad1.b && !bFlag ? -0.1 : 0));
			aFlag = gamepad1.a;
			bFlag = gamepad1.b;
			telemetry.addData("Basket pos", getServo(ServoType.BASKET).getPosition());
			
			this.moveRobot(gamepad1.left_stick_x, gamepad1.left_stick_y, gamepad1.right_stick_x);
			
			telemetry.addData("Lift position", getMotor(Motor.LIFT).getCurrentPosition());
			
			getMotor(Motor.LIFT).setPower((gamepad1.x ? 1 : 0) + (gamepad1.y ? -1 : 0));
			getMotor(Motor.ARM).setPower((gamepad1.dpad_up ? 1 : 0) + (gamepad1.dpad_down ? -1 : 0));
			getCRServo(CRServoType.INTAKE).setPower(gamepad1.left_bumper ? 1 : 0.055);
			
			telemetry.addData("CR servo speed", getCRServo(CRServoType.INTAKE).getPower());
			
			// TODO add op mode code
			telemetry.update();
		}
	}
	
	@Override
	public DcMotor getMotor(Motor motor) {
		return motorMap.get(motor);
	}
	@Override
	public Servo getServo(ServoType servoType) {
		return servoMap.get(servoType);
	}
	@Override
	public CRServo getCRServo(CRServoType servoType) {
		return crServoMap.get(servoType);
	}
}
