package org.firstinspires.ftc.teamcode;

import java.util.*;

public enum Motor {
	FRONT_LEFT,
	FRONT_RIGHT,
	BACK_LEFT,
	BACK_RIGHT;
	
	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.US);
	}
}
