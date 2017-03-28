package com.domochevsky.quiverbow.AI;

public class Commands 
{
	// Arms Assistant Commands
	public static String cmdStayStationary = "STAY";			// Stay where you are (and compensate for kickback)
	public static String cmdFollowOwner = "FOLLOW";				// Follow the owner around
	public static String cmdTargetFriendly = "TARGET FRIENDLY";	// Reverse targeting (blacklist instead of whitelist)
	public static String cmdBlacklist = "BLACKLIST";			// Different from TARGET FRIENDLY in that they ONLY attack who's on the list, not each other (internal whitelist)
	public static String cmdInjuredOnly = "INJURED ONLY";		// Only aiming at the injured
	public static String cmdStaggerFire = "STAGGER FIRE";		// Firing the second rail only when the first rail is halways done with cooldown
	
	public static String cmdTellAmmo = "TELL AMMO";		// Informing the owner that we're out of ammo
	public static String cmdTellDeath = "TELL DEATH";	// Same with death and health (below 30%)
	public static String cmdTellHealth = "TELL HEALTH";
	
	public static String cmdFireRemote = "REMOTE FIRE";	// Fires at whatever the owner is pointing at when they're holding he AA targeter
	public static String cmdHoldFire = "HOLD FIRE";		// No shooty unless told to do so
	
	public static String cmdSafeRange = "SAFETY RANGE";	// Not firing explosive weapons near yourself
	
	public static String cmdTargetFlying = "TARGET FLYING";
}
