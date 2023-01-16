/*
 * This file is part of the repicea-simulation library.
 *
 * Copyright (C) 2009-2012 Mathieu Fortin for Rouge-Epicea
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
package repicea.simulation.treelogger;

/**
 * The TreeLoggerChangeListener interface should be implemented by instances that listen
 * to a TreeLoggerWrapper instance. Every time the TreeLogger instance that is contained in the
 * wrapper is changed, all the listeners will be notified.
 * @author Mathieu Fortin - January 2013
 */
public interface TreeLoggerChangeListener {

	/**
	 * This method handles an event generated by a TreeLoggerWrapper instance.
	 * @param evt a TreeLoggerEvent instance
	 */
	public void treeLoggerChanged(TreeLoggerEvent evt);
	
	
}
