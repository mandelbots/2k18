package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Autonomous(name = "Autonomous", group = "Autonomous")
public class MandelbotsAutonomus extends LinearOpMode implements IMandelbotsOpMode {
	private final ElapsedTime runtime = new ElapsedTime();
	private final Map<Motor, DcMotor> motorMap = new EnumMap<>(Motor.class);
	private VuforiaLocalizer vuforia;
	private final Map<String, VuforiaTrackable> nameToNavTarget = new HashMap<>();
	
	private OpenGLMatrix lastLocation = null;
	private boolean targetVisible = false;
	
	@Override
	public void runOpMode() throws InterruptedException {
		telemetry.addData("Status", "Ready to avenge last year!");
		telemetry.update();
		
		for (Motor motor : Motor.values()) {
			motorMap.put(motor, hardwareMap.get(DcMotor.class, motor.toString()));
		}
		
		getMotor(Motor.FRONT_RIGHT).setDirection(DcMotor.Direction.REVERSE);
		getMotor(Motor.BACK_RIGHT).setDirection(DcMotor.Direction.REVERSE);
		
		int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
		VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
		parameters.vuforiaLicenseKey = Meta.VUFORIA_KEY;
		parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
		vuforia = ClassFactory.getInstance().createVuforia(parameters);
		
		VuforiaTrackables navTargets = this.vuforia.loadTrackablesFromAsset("FourImages");
		for (VuforiaTrackable trackable: navTargets) {
			nameToNavTarget.put(trackable.getName(), trackable);
			// TODO add code for each trackable
			trackable.setLocation(NavTarget.fromTrackableName(trackable.getName()).getLocation());
		}
		
		OpenGLMatrix phoneLoc = OpenGLMatrix.translation(Constants.CAM_FORWARD_DISP, Constants.CAM_LEFT_DISP, Constants.CAM_VERT_DISP)
				.multiplied(OpenGLMatrix.rotation(AxesReference.EXTRINSIC, AxesOrder.YZX, AngleUnit.DEGREES,
						-90 /* camera rotation */, 0, 0));
		
		for (VuforiaTrackable trackable: navTargets) {
			((VuforiaTrackableDefaultListener)trackable.getListener()).setPhoneInformation(phoneLoc, parameters.cameraDirection);
		}
		
		waitForStart();
		runtime.reset();
		
		while (opModeIsActive()) {
			targetVisible = false;
			for (VuforiaTrackable trackable: navTargets) {
				if (((VuforiaTrackableDefaultListener)trackable.getListener()).isVisible()) {
					telemetry.addData("Visible Target", trackable.getName());
					targetVisible = true;
					OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener)trackable.getListener()).getUpdatedRobotLocation();
					if (robotLocationTransform != null) {
						lastLocation = robotLocationTransform;
					}
					break;
				}
			}
			
			if (targetVisible) {
				VectorF translation = lastLocation.getTranslation();
				Orientation rotation = Orientation.getOrientation(lastLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
			}
			
			// TODO add op mode code
		}
	}
	
	@Override
	public DcMotor getMotor(Motor motor) {
		return motorMap.get(motor);
	}
}
