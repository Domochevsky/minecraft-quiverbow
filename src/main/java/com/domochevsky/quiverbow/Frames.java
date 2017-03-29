package com.domochevsky.quiverbow;

import org.lwjgl.Sys;

public class Frames 
{	
	private static long lastFPS;
	
	private static int tickCounter = 30;
	private static int tickCount;			// The number of ticks we did per second
	
	// Get the time in milliseconds
	private static long getTime() { return (Sys.getTime() * 1000) / Sys.getTimerResolution(); }
	
	
	// Produces 30 ticks at 30 FPS and 31 ticks at 60 FPS
	public static boolean isTickReady()
	{
		if (getTime() - lastFPS > tickCounter)
		{
			tickCounter += 33;	// 30 ms more on the stack
			
			return true;
		}
		else
		{
			if (getTime() - lastFPS > 1000) // A second has passed. Time to update 
			{
		        tickCounter = 0;	// Reset the time counter, to determine when to do a game tick
		        
		        lastFPS += 1000; 	// Add one second
		    }
		    // else, current time counter is less than 1000, meaning a second has not passed yet. Not updating
		    
			return false;	// Not ready yet
		}
	}
}
