package org.goko.core.gcode.execution;

public enum ExecutionState {
	IDLE,
	RUNNING,
	PAUSED,
	STOPPED,
	ERROR,
	FATAL_ERROR,
	COMPLETE;	
}
