/*******************************************************************************
 * Copyright (c) 2001, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.validation.internal;


import java.util.Vector;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.validation.internal.plugin.ValidationPlugin;


/**
 * This class manages (queue, invoke, etc.) the Runnables that perform the validation for a
 * particular validator.
 */
public class VThreadManager {
	private static VThreadManager _manager = null;
	private static final int MAX_NUM_OF_RESTART = 5; // the maximum number of times that the thread
	// should attempt to ignore a Throwable and
	// carry on

	private Thread _validationThread = null; // This thread checks if the current Runnable is
	// finished, and if so, loads the first Runnable off of
	// the queue and starts it.
	volatile int restart = 0; // how many times has the thread been restarted?
	volatile Jobs _jobs = null;

	private VThreadManager() {
		_jobs = new Jobs();

		// Start the validation thread to check for queued ValidationOperation
		Runnable validationRunnable = new Runnable() {
			public void run() {
				while (true) {
					try {
						if (restart > MAX_NUM_OF_RESTART) {
							// something has gone seriously, seriously wrong
							String message = "restart = " + restart; //$NON-NLS-1$
							ValidationPlugin.getPlugin().logMessage(IStatus.ERROR, message);
							break;
						}

						Runnable job = getJobs().dequeue(); // If currentRunnable is null, there's
						// nothing on the queue. Shouldn't
						// happen with a semaphore.
						if (job != null) {
							getJobs().setActive(true);
							job.run();
							getJobs().setActive(false);
						}
					} catch (Exception e) {
						restart++;
						getJobs().setActive(false);
						ValidationPlugin.getPlugin().handleException(e);
					} finally {
						//do nothing
					}
				}
			}
		};

		_validationThread = new Thread(validationRunnable, "ValidationThread"); //$NON-NLS-1$
		_validationThread.start();
	}

	public static VThreadManager getManager() {
		if (_manager == null) {
			_manager = new VThreadManager();
		}
		return _manager;
	}

	Jobs getJobs() {
		return _jobs;
	}

	public void queue(Runnable runnable) {
		getJobs().queue(runnable);
	}

	/**
	 * Return true if all of the Runnables have been run.
	 */
	public boolean isDone() {
		return getJobs().isDone();
	}

	private class Jobs {
		private Vector<Runnable> _queuedJobs; // The queued Runnables that need to be run.
		private boolean _isActive; // Is a job being run in the validation thread?

		public Jobs() {
			_queuedJobs = new Vector<Runnable>();
		}

		public synchronized void queue(Runnable runnable) {
			// If there is a thread running already, then it must finish before another validation
			// thread is launched, or the validation messages could reflect the last thread
			// finished,
			// instead of the last state of changes.

			// Have to wait for the current Runnable to finish, so add this to the end of the queue
			_queuedJobs.add(runnable);
			notifyAll();
		}

		/**
		 * Pop the Runnable off of the head of the queue.
		 */
		synchronized Runnable dequeue() {
			while (_queuedJobs.size() == 0) {
				try {
					wait();
				} catch (InterruptedException exc) {
					//Ignore
				}
			} // Block on the semaphore; break when a job has been added to the queue.

			Runnable job = null;
			if (_queuedJobs.size() > 0) {
				job = _queuedJobs.get(0);
				if (job != null) {
					_queuedJobs.remove(0);
				}
			}
			return job;
		}

		public synchronized boolean isActive() {
			return _isActive;
		}

		public synchronized void setActive(boolean active) {
			_isActive = active;
		}

		/**
		 * Return true if all of the Runnables have been run.
		 */
		public synchronized boolean isDone() {
			return ((_queuedJobs.size() == 0) && !isActive());
		}
	}
}
