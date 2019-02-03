package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.apache.commons.io.IOUtils;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import java8.util.function.Predicate;
import java8.util.stream.StreamSupport;

import static org.firstinspires.ftc.robotcore.external.navigation.AngleUnit.DEGREES;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.XYZ;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesOrder.YZX;
import static org.firstinspires.ftc.robotcore.external.navigation.AxesReference.EXTRINSIC;
import static org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit.mmPerInch;
import static org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection.FRONT;

@Autonomous(name = "Autonomous", group = "Autonomous")
public class MandelbotsAutonomous extends MandelbotsOpMode {
	private final ElapsedTime runtime = new ElapsedTime();
	private final Map<Motor, DcMotor> motorMap = new EnumMap<>(Motor.class);
	private final Map<CRServoType, CRServo> crServoMap = new EnumMap<>(CRServoType.class);
	private final Map<ServoType, Servo> servoMap = new EnumMap<>(ServoType.class);
	
	private VuforiaLocalizer vuforia;
	private TFObjectDetector tfod;
	
	private VuforiaTrackables targetsRoverRuckus;
	
	private static final String TFOD_MODEL_ASSET = "MineralDetection.tflite";
	private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
	private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
	
	private OpenGLMatrix lastLocation = null;
	private boolean targetVisible = false;
	private List<Recognition> recognitions = Collections.emptyList();
	
	@Override
	public void runOpMode() throws InterruptedException {
		telemetry.addData("Status", "Ready to avenge last year!");
		
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
		
		for (Motor motor: Motor.values()) {
			getMotor(motor).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
			getMotor(motor).setMode(DcMotor.RunMode.RUN_TO_POSITION);
		}
		for (Motor motor: Motor.getMovementMotors()) {
			getMotor(motor).setMode(DcMotor.RunMode.RUN_USING_ENCODER);
		}
		
		initVuforia();
		if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
			initTfod();
		} else {
			telemetry.addData("Sorry!", "This device is not compatible with TFOD");
		}
		
		telemetry.update();
		
		waitForStart();
		runtime.reset();
		
		// Descent code
		
		if (opModeIsActive()) {
			getMotor(Motor.LIFT).setTargetPosition(-Constants.LIFT_SHIFT);
			getMotor(Motor.LIFT).setPower(-1);
		}
		while (opModeIsActive() && getMotor(Motor.LIFT).isBusy()) {
			telemetry.addData("Status", "You're going down down down! "+getMotor(Motor.LIFT).getCurrentPosition());
			telemetry.update();
		}
		
		// Center gold
		boolean foundGold2 = false;
		if (opModeIsActive()) {
			if (tfod != null) {
				tfod.activate();
			}
			runtime.reset();
			while (opModeIsActive() && runtime.time() < 4) {
				if (tfod != null) {
					List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
					if (updatedRecognitions != null) {
						recognitions = updatedRecognitions;
					}
					if (recognitions != null) {
						telemetry.addData("# Object Detected", recognitions.size());
						if (StreamSupport.stream(recognitions).anyMatch(new Predicate<Recognition>() {
							@Override
							public boolean test(Recognition recognition) {
								return recognition.getLabel().equals(LABEL_GOLD_MINERAL);
							}
						})) {
							telemetry.addData("AU", "gold");
							foundGold2 = true;
							break;
						}
					}
				}
				
				telemetry.update();
			}
		}
		// Rotation code
		
		if (opModeIsActive()) {
			moveRobot(0, 0, +1);
			runtime.reset();
		}
		while (opModeIsActive() && runtime.time() < 0.205) {
			telemetry.addData("Status", "Gradle gradle gradle…");
			telemetry.update();
		}
		if (opModeIsActive()) {
			for (Motor motor: Motor.getMovementMotors()) {
				getMotor(motor).setPower(0);
			}
		}
		
		//targetsRoverRuckus.activate();
		
		boolean foundGold1 = false;
		
		if (!foundGold2) {
			runtime.reset();
			
			while (opModeIsActive() && runtime.time() < 4) {
				if (tfod != null) {
					List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
					if (updatedRecognitions != null) {
						recognitions = updatedRecognitions;
					}
					if (recognitions != null) {
						telemetry.addData("# Object Detected", recognitions.size());
						if (StreamSupport.stream(recognitions).anyMatch(new Predicate<Recognition>() {
							@Override
							public boolean test(Recognition recognition) {
								return recognition.getLabel().equals(LABEL_GOLD_MINERAL);
							}
						})) {
							telemetry.addData("AU", "gold");
							foundGold1 = true;
							break;
						}
					}
				}
				
				telemetry.update();
			}
		}
		
		if (opModeIsActive()) {
			//getMotor(Motor.FRONT_LEFT).setMode(DcMotor.RunMode.RUN_USING_ENCODER);
			runtime.reset();
			/*
			while (opModeIsActive()) {
				telemetry.addData("Status", foundGold1 + " " + foundGold2);
				telemetry.update();
			}*/
			if (foundGold1) {
				moveRobot(0, 1, 0);
				while (opModeIsActive() && runtime.time() < 1) {
					telemetry.addData("Status", "Pursuing first gold");
					telemetry.update();
				}
				if (opModeIsActive()) {
					for (Motor motor : Motor.getMovementMotors()) {
						getMotor(motor).setPower(0);
					}
				}
			}
			if (foundGold2) {
				moveRobot(-0.7, 1, 0);
				while (opModeIsActive() && runtime.time() < 1.27) {
					telemetry.addData("Status", "Pursuing second gold");
					telemetry.update();
				}
				if (opModeIsActive()) {
					for (Motor motor : Motor.getMovementMotors()) {
						getMotor(motor).setPower(0);
					}
				}
			}
			if (!foundGold1 && !foundGold2) {
				moveRobot(0, 0, +1);
				while (opModeIsActive() && runtime.time() < 0.83) {
					telemetry.addData("Status", "Pursuing third gold");
					telemetry.update();
				}
				if (opModeIsActive()) {
					for (Motor motor : Motor.getMovementMotors()) {
						getMotor(motor).setPower(0);
					}
					runtime.reset();
					moveRobot(0, -1, 0);
				}
				while (opModeIsActive() && runtime.time() < 1) {
					telemetry.addData("Status", "Pursuing third gold");
					telemetry.update();
				}
				if (opModeIsActive()) {
					for (Motor motor : Motor.getMovementMotors()) {
						getMotor(motor).setPower(0);
					}
				}
			}
		}
		
		if (tfod != null) {
			tfod.shutdown();
		}
		/*
		// Disengage
		if (opModeIsActive()) {
			getMotor(Motor.LIFT).setTargetPosition(Constants.LIFT_SHIFT);
			getMotor(Motor.LIFT).setPower(1);
		}
		while (opModeIsActive() && getMotor(Motor.LIFT).isBusy()) {
			telemetry.addData("Status", "Up the ****");
			telemetry.update();
		}*/
	}
	
	private void initVuforia() {
		VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
		try {
			parameters.vuforiaLicenseKey = IOUtils.toString(new InputStreamReader(MandelbotsAutonomous.class.getResourceAsStream("/assets/vuforia_key.txt")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
		vuforia = ClassFactory.getInstance().createVuforia(parameters);
		
		OpenGLMatrix phoneLocationOnRobot = OpenGLMatrix
				.translation(Constants.CAM_FORWARD_DISP, Constants.CAM_LEFT_DISP, Constants.CAM_VERT_DISP)
				.multiplied(Orientation.getRotationMatrix(EXTRINSIC, YZX, DEGREES, -90, 0, 90)); // TODO adjust rotation if necessary
		
		targetsRoverRuckus = this.vuforia.loadTrackablesFromAsset("FourImages");
		for (int i=0; i<4; ++i) {
			NavTarget target = NavTarget.getValues().get(i);
			VuforiaTrackable trackable = targetsRoverRuckus.get(i);
			trackable.setName(target.getTrackableName());
			trackable.setLocation(target.getLocation());
			((VuforiaTrackableDefaultListener)trackable.getListener()).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection);
		}
	}
	
	private void initTfod() {
		int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
		TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
		tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
		tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
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
