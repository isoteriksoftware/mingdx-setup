package com.isoterik.mgdx.setup;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.*;

public class Utils
{
	public static boolean emptyField (TextField field)
	{ return field.getText().equals(""); }

	public static boolean isActive (Actor actor)
	{ return (actor == null) ? false : actor.hasParent() && actor.isVisible(); }

	public static boolean isInt (String str)
	{
		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isInt (TextField field)
	{ return isInt(field.getText()); }

	public static boolean isFloat (String str)
	{
		try {
			Float.parseFloat(str);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean isFloat (TextField field)
	{ return isFloat(field.getText()); }

	public static int toInt (String str)
	{ return Integer.parseInt(str); }

	public static float toFloat (String str)
	{ return Float.parseFloat(str); }
}
