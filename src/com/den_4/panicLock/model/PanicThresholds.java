/*
 * PanicLock
 *
 * URLs: http://paniclock.den-4.com/
 * Copyright (C) 2009-2012, Den 4
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.den_4.panicLock.model;

/**
 * Defines the timer thresholds for individual alarms that the system can fire.
 * 
 * @author tony (<a href="mailto:tony@den-4.com">tony@den-4.com</a>)
 */
public enum PanicThresholds {
    /**
     * "Quick Panic" alarm that fires for any click under one second.
     */
	QUICK_ALARM_THRESHOLD(0L, 1000L),

	/**
	 * "Dead Man's Switch" alarm that fires upon button release if the duration is over 3 seconds. 
	 */
	DEAD_MANS_SWITCH_THRESHOLD(3000L),
	
	/**
	 * "Panic Lock" mode that enables if the button is pressed between 1 and 3 seconds.
	 */
	PANICLOCK_ENABLE_THRESHOLD(1000L, 3000L);

	private Long tMin;
	private Long tMax;
	
	private PanicThresholds(Long tMin) {
		this.tMin = tMin;
	}
	
	private PanicThresholds(Long tMin, Long tMax) {
		this.tMin = tMin;
		this.tMax = tMax;
	}
	
	public Long getThresholdMin() {
		return tMin;
	}
	
	public Long getThresholdMax() {
		return tMax;
	}
	
	public boolean withinThreshold(Long milliseconds) {
		return getThresholdMin() < milliseconds && (getThresholdMax() == null || getThresholdMax() > milliseconds);
	}
}
