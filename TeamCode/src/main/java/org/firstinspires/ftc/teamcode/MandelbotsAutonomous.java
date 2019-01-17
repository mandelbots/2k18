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
		
		getMotor(Motor.LIFT).setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		getMotor(Motor.LIFT).setMode(DcMotor.RunMode.RUN_TO_POSITION);
		
		initVuforia();
		if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
			initTfod();
		} else {
			telemetry.addData("Sorry!", "This device is not compatible with TFOD");
		}
		
		telemetry.update();
		
		waitForStart();
		runtime.reset();
		/*
		if (opModeIsActive()) {
			getMotor(Motor.LIFT).setTargetPosition(-54000);
			getMotor(Motor.LIFT).setPower(-1);
		}
		while (opModeIsActive() && getMotor(Motor.LIFT).isBusy()) {
			telemetry.addData("Status", "You're going down down down! "+getMotor(Motor.LIFT).getCurrentPosition());
			telemetry.update();
		}*/
		targetsRoverRuckus.activate();
		if (opModeIsActive()) {
			if (tfod != null) {
				//tfod.activate();
			}
			
			while (opModeIsActive()) {
				// check all the trackable target to see which one (if any) is visible.
				targetVisible = false;
				for (VuforiaTrackable trackable : targetsRoverRuckus) {
					if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
						telemetry.addData("Visible Target", trackable.getName());
						targetVisible = true;
						
						// getUpdatedRobotLocation() will return null if no new information is available since
						// the last time that call was made, or if the trackable is not currently visible.
						OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
						if (robotLocationTransform != null) {
							lastLocation = robotLocationTransform;
						}
						break;
					}
				}
				
				// Provide feedback as to where the robot is located (if we know).
				if (targetVisible) {
					// express position (translation) of robot in inches.
					VectorF translation = lastLocation.getTranslation();
					telemetry.addData("Pos (in)", "{X, Y, Z} = %.1f, %.1f, %.1f",
							translation.get(0) / mmPerInch, translation.get(1) / mmPerInch, translation.get(2) / mmPerInch);
					
					// express the rotation of the robot in degrees.
					Orientation rotation = Orientation.getOrientation(lastLocation, EXTRINSIC, XYZ, DEGREES);
					telemetry.addData("Rot (deg)", "{Roll, Pitch, Heading} = %.0f, %.0f, %.0f", rotation.firstAngle, rotation.secondAngle, rotation.thirdAngle);
				}
				else {
					telemetry.addData("Visible Target", "none");
				}
				
				if (tfod != null) {
					List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
					if (updatedRecognitions != null) {
						recognitions = updatedRecognitions;
					}
					if (recognitions != null) {
						telemetry.addData("# Object Detected", recognitions.size());
						if (recognitions.size() == 3) {
							int goldMineralX = -1;
							int silverMineral1X = -1;
							int silverMineral2X = -1;
							for (Recognition recognition : recognitions) {
								if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
									goldMineralX = (int) recognition.getLeft();
								} else if (silverMineral1X == -1) {
									silverMineral1X = (int) recognition.getLeft();
								} else {
									silverMineral2X = (int) recognition.getLeft();
								}
							}
							if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
								if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
									telemetry.addData("Gold Mineral Position", "Left");
								} else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
									telemetry.addData("Gold Mineral Position", "Right");
								} else {
									telemetry.addData("Gold Mineral Position", "Center");
								}
							}
						}
					}
				}
				
				telemetry.update();
			}
		}
		
		if (tfod != null) {
			tfod.shutdown();
		}
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
