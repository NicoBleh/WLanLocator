package com.locator.wlan.interfaces;

/**
 * Interface to discribe a subject in a observer pattern.
 *
 * @author Nico Bleh
 * @version 1.0
 */
public interface Subject {

	/**
	 * Registers a observer at the subject
	 * 
	 * @return void
	 * @param Observer the observer to be notified
	 */
	public void registerObserver(Observer o);

	/**
	 * Removes a observer from the subject
	 * 
	 * @return void
	 * @param Observer the observer who not wants to be notified any more
	 */
	 public void removeObserver(Observer o);

	/**
	 * 	Notifies all the observers registered
	 */
	void notifyObservers();

}
