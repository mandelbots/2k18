package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.Image;

import org.apache.commons.io.IOUtils;
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

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.robotcore.external.tfod.TfodRoverRuckus;
/*
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;*/

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Autonomous(name = "Autonomous", group = "Autonomous")
public class MandelbotsAutonomus extends MandelbotsOpMode {
	private final ElapsedTime runtime = new ElapsedTime();
	private final Map<Motor, DcMotor> motorMap = new EnumMap<>(Motor.class);
	private VuforiaLocalizer vuforia;
	private VuforiaTrackables navTargets;
	
	private final Map<String, VuforiaTrackable> nameToNavTarget = new HashMap<>();
	
	private OpenGLMatrix lastLocation = null;
	private boolean targetVisible = false;
	
	private TFObjectDetector tfod;
	
	private Image rgb;
	
	@Override
	public void runOpMode() throws InterruptedException {
		telemetry.addData("Status", "Ready to avenge last year!");
		telemetry.update();
		
		for (Motor motor : Motor.values()) {
			motorMap.put(motor, hardwareMap.get(DcMotor.class, motor.toString()));
		}
		
		getMotor(Motor.FRONT_LEFT).setDirection(DcMotor.Direction.REVERSE);
		getMotor(Motor.BACK_LEFT).setDirection(DcMotor.Direction.REVERSE);
		
		this.initVuforia();
		if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
			initTfod();
		}
		
		waitForStart();
		runtime.reset();
		
		if (opModeIsActive()) {
			if (tfod != null) {
				tfod.activate();
			}
			
			while (opModeIsActive()) {
				targetVisible = false;
				for (VuforiaTrackable trackable : navTargets) {
					if (((VuforiaTrackableDefaultListener) trackable.getListener()).isVisible()) {
						telemetry.addData("Visible Target", trackable.getName());
						targetVisible = true;
						OpenGLMatrix robotLocationTransform = ((VuforiaTrackableDefaultListener) trackable.getListener()).getUpdatedRobotLocation();
						if (robotLocationTransform != null) {
							lastLocation = robotLocationTransform;
						}
						break;
					}
				}
				
				if (targetVisible) {
					VectorF translation = lastLocation.getTranslation();
					Orientation rotation = Orientation.getOrientation(lastLocation, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);
				} else {
					telemetry.addData("Visible Target", "none");
				}
				
				if (tfod != null) {
					//telemetry.addData("TFOD", "yes");
					List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
					if (updatedRecognitions != null) {
						telemetry.addData("# Object Detected", updatedRecognitions.size());
						if (updatedRecognitions.size() == 3) {
							int goldMineralX = -1;
							int silverMineral1X = -1;
							int silverMineral2X = -1;
							for (Recognition recognition : updatedRecognitions) {
								if (recognition.getLabel().equals(TfodRoverRuckus.LABEL_GOLD_MINERAL)) {
									goldMineralX = (int) recognition.getLeft();
								} else if (silverMineral1X == -1) {
									silverMineral1X = (int) recognition.getLeft();
								} else {
									silverMineral2X = (int) recognition.getLeft();
								}
							}
							if (goldMineralX != -1 && silverMineral1X != -1 && silverMineral2X != -1) {
								// TODO dispatch logic
								
								if (goldMineralX < silverMineral1X && goldMineralX < silverMineral2X) {
									telemetry.addData("Gold Mineral Position", "Left");
								} else if (goldMineralX > silverMineral1X && goldMineralX > silverMineral2X) {
									telemetry.addData("Gold Mineral Position", "Right");
								} else {
									telemetry.addData("Gold Mineral Position", "Center");
								}
							}
						}
						telemetry.update();
					}
				}
				
				// BEGIN OPENCV
				/*
				VuforiaLocalizer.CloseableFrame frame = vuforia.getFrameQueue().take(); //takes the frame at the head of the queue
				long numImages = frame.getNumImages();
				
				for (int i = 0; i < numImages; i++) {
					if (frame.getImage(i).getFormat() == PIXEL_FORMAT.RGB565) {
						rgb = frame.getImage(i);
						break;
					}
				}
				
				Bitmap bm = Bitmap.createBitmap(rgb.getWidth(), rgb.getHeight(), Bitmap.Config.RGB_565);
				bm.copyPixelsFromBuffer(rgb.getPixels());
				
				Mat tmp = new Mat(rgb.getWidth(), rgb.getHeight(), CvType.CV_8UC4);
				Utils.bitmapToMat(bm, tmp);
				Mat hsv = new Mat();
				Imgproc.cvtColor(tmp, hsv, Imgproc.COLOR_RGB2HSV);
				
				this.detectContour(hsv);
				
				frame.close();*/
				
				// END OPENCV
				
				
				telemetry.update();
				
				// TODO add op mode code
			}
			
			if (tfod != null) {
				tfod.shutdown();
			}
		}
	}
	/*
	private void detectContour(Mat hsv) {
		Mat filteredHSV = new Mat();
		Core.inRange(hsv, new Scalar(20.0*180/360, 80.0*255/100, 57.0*255/100),
				new Scalar(44.0*180/360, 100.0*255/100, 100.0*255/100), filteredHSV);
		List<MatOfPoint> contours = new ArrayList<>();
		Imgproc.findContours(filteredHSV, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
		contours.stream().max(Comparator.comparingDouble(Imgproc::contourArea))
		.ifPresent(matOfPoint -> {
			Moments moments = Imgproc.moments(matOfPoint);
			double centerX = moments.m10 / moments.m00, centerY = moments.m01 / moments.m00;
			telemetry.addData("CenterX", centerX);
			telemetry.addData("CenterY", centerY);
		});
	}*/
	
	private void initVuforia() {
		int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
		VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
		
		Reader keyReader = new InputStreamReader(MandelbotsAutonomus.class.getResourceAsStream("/assets/vuforia_key.txt"));
		try {
			parameters.vuforiaLicenseKey = IOUtils.toString(keyReader);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
		vuforia = ClassFactory.getInstance().createVuforia(parameters);
		
		navTargets = this.vuforia.loadTrackablesFromAsset("FourImages");
		//System.out.println("YOU PHAT "+navTargets.size());
		
		final NavTarget[] NTVALUES = NavTarget.values();
		
		for (int i=0; i<navTargets.size(); ++i) {
			VuforiaTrackable trackable = navTargets.get(i);
			trackable.setName(NTVALUES[i].getTrackableName());
			nameToNavTarget.put(trackable.getName(), trackable);
			trackable.setLocation(NTVALUES[i].getLocation());
		}
		
		OpenGLMatrix phoneLoc = OpenGLMatrix.translation(Constants.CAM_FORWARD_DISP, Constants.CAM_LEFT_DISP, Constants.CAM_VERT_DISP)
				.multiplied(OpenGLMatrix.rotation(AxesReference.EXTRINSIC, AxesOrder.YZX, AngleUnit.DEGREES,
						-90 /* camera rotation */, 0, 0));
		
		for (VuforiaTrackable trackable: navTargets) {
			((VuforiaTrackableDefaultListener)trackable.getListener()).setPhoneInformation(phoneLoc, parameters.cameraDirection);
		}
	}
	
	private void initTfod() {
		int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
		TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(cameraMonitorViewId);
		tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
		tfod.loadModelFromAsset(TfodRoverRuckus.TFOD_MODEL_ASSET, TfodRoverRuckus.LABEL_GOLD_MINERAL, TfodRoverRuckus.LABEL_SILVER_MINERAL);
	}
	
	@Override
	public DcMotor getMotor(Motor motor) {
		return motorMap.get(motor);
	}
}
