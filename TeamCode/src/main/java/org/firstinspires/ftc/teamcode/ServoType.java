package org.firstinspires.ftc.teamcode;

import java.util.Locale;

public enum ServoType {
	BASKET, DROP;
	
	@Override
	public String toString() {
		return this.name().toLowerCase(Locale.US);
	}
}
