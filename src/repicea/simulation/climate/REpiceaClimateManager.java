/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2026 His Majesty the King in right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package repicea.simulation.climate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class handling checks on climate-related methods.
 * @author Mathieu Fortin - February
 */
public final class REpiceaClimateManager {

	public static enum ClimateVariableTemporalResolution {
		Normals30Year,
		IntervalAveraged,
		IntervalAveragedStartingBeforeInitialMeasurement, 
		Annual;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface AllowedResolutions {
		public ClimateVariableTemporalResolution[] values();
	}
	
	private static final REpiceaClimateManager SINGLETON = new REpiceaClimateManager();

	private final ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, List<ClimateVariableTemporalResolution>>> controlMap;
	
	private REpiceaClimateManager() {
		controlMap = new ConcurrentHashMap<Class<?>, ConcurrentHashMap<String, List<ClimateVariableTemporalResolution>>>();
	};

	public void checkClimateRelatedMethodResolution(Object o, String methodName, ClimateVariableTemporalResolution resolution) {
		Class<?> clazz = o.getClass();
		if (!controlMap.containsKey(clazz)) {
			controlMap.put(clazz, new ConcurrentHashMap<String, List<ClimateVariableTemporalResolution>>());
		}
		ConcurrentHashMap<String, List<ClimateVariableTemporalResolution>> innerMap = controlMap.get(clazz);
		if (!innerMap.containsKey(methodName)) {
			try {
				Method method = o.getClass().getDeclaredMethod(methodName, resolution.getClass());
				if (method.isAnnotationPresent(AllowedResolutions.class)) {
					AllowedResolutions allowedResolutions = method.getAnnotation(AllowedResolutions.class);
					innerMap.put(methodName, Arrays.asList(allowedResolutions.values()));
				} else {
					innerMap.put(methodName, Arrays.asList(ClimateVariableTemporalResolution.values()));
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		}
		if (!innerMap.get(methodName).contains(resolution)) {
			throw new UnsupportedOperationException("The method " + methodName + " in class " + clazz.getName() + " does not support this resolution: " + resolution.name());
		}
	}

	public static REpiceaClimateManager getInstance() {
		return SINGLETON;
	}
	
}
