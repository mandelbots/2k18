package org.firstinspires.ftc.teamcode;

import java.util.*;

public enum Motor {
	FRONT_LEFT,
	FRONT_RIGHT,
	BACK_LEFT,
	BACK_RIGHT,
	ARM,
	APPLE,
	LIFT;
	
	private static final Motor[] mvmtMotors = new Motor[] { FRONT_LEFT, FRONT_RIGHT, BACK_LEFT, BACK_RIGHT };
	
	public static List<Motor> getMovementMotors() { return Collections.unmodifiableList(Arrays.asList(mvmtMotors)); }
	
	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.US);
	}
}
