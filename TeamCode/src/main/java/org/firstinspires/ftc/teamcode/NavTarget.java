package org.firstinspires.ftc.teamcode;

import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum NavTarget {
	ROVER("Rover"), MOON_SURFACE("MoonSurface"), MARS_SURFACE("MarsSurface"), NEBULA("Nebula");
	
	private static final Map<String, NavTarget> nameToTarget = new HashMap<>();
	static {
		for (NavTarget val: NavTarget.values()) {
			nameToTarget.put(val.getTrackableName(), val);
		}
	}
	
	private static final NavTarget[] VALUES = values();
	
	public static List<NavTarget> getValues() {
		return Collections.unmodifiableList(Arrays.asList(VALUES));
	}
	
	public static NavTarget fromTrackableName(String name) {
		System.out.println("THE TEEHEE IS "+name);
		return nameToTarget.get(name);
	}
	
	private final String trackableName;
	NavTarget(String trackableName) {
		this.trackableName = trackableName;
	}
	
	public String getTrackableName() {
		return trackableName;
	}
	
	public int xComponent() {
		switch (this) {
			case MARS_SURFACE:
				return -1;
			case NEBULA:
				return 1;
		}
		return 0;
	}
	
	public int yComponent() {
		switch (this) {
			case MOON_SURFACE:
				return -1;
			case ROVER:
				return 1;
		}
		return 0;
	}
	
	public OpenGLMatrix getLocation() {
		return OpenGLMatrix.translation(Constants.FIELD_BREADTH * xComponent(),
				Constants.FIELD_BREADTH * yComponent(),
				Constants.TARGET_HEIGHT)
				.multiplied(Orientation.getRotationMatrix(AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.RADIANS,
						Constants.HALF_PI, 0, (float)Math.atan2(xComponent(), yComponent())));
	}
}
