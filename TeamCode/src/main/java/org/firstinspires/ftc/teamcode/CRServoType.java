package org.firstinspires.ftc.teamcode;

import java.util.Locale;

public enum CRServoType {
	INTAKE;
	
	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.US);
	}
}
