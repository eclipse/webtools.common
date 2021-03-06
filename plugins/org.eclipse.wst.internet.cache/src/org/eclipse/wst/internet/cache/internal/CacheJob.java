/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.internet.cache.internal;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A cache job runs once an hour to cache any prespecified resources which
 * should be cached and any resources for which an attempt was previously 
 * made to cache them but they were unable to be cached.
 */
public class CacheJob extends Job
{
  private static final long SCHEDULE_TIME = 3600000;

  private static CacheJob job = null;
  /**
   * Constructor.
   */
  public CacheJob()
  {
    super(CacheMessages._UI_CACHE_MONITOR_NAME);
  }

  /**
   * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
   */
  protected IStatus run(IProgressMonitor monitor)
  {
	boolean allSuccessful = true;
    Cache cache = Cache.getInstance();
    String[] uncachedURIs = cache.getUncachedURIs();
    int numUncachedURIs = uncachedURIs.length;

    cache.clearUncachedURIs();
    monitor.beginTask(CacheMessages._UI_CACHE_MONITOR_NAME, numUncachedURIs);
    try
    {
      for(int i = 0; i < numUncachedURIs; i++)
      {
        if (monitor.isCanceled())
        {
          for(int j = i; j < numUncachedURIs; j++)
          {
            cache.addUncachedURI(uncachedURIs[j]);
          }
          return Status.CANCEL_STATUS;
        }
        String uri = uncachedURIs[i];
        monitor.setTaskName(MessageFormat.format(CacheMessages._UI_CACHE_MONITOR_CACHING, new Object[]{uri}));
       
        String cachedURI = cache.getResource(uri);
        if(cachedURI == null)
    	{
    	  allSuccessful = false;
    	}
        monitor.worked(1);
      }
      monitor.done();
      return Status.OK_STATUS;
    } 
    finally
    {
      // If all the uncached URIs could not be cached 
      // schedule the next time the job should run.
      if(!allSuccessful)
      {
    	  startJob(SCHEDULE_TIME); 
      }
    }
  }
  
  /**
   * Start the cache job. The cache job caches resources that were not able to be previously
   * downloaded. Only one job is run at a time.
   */
  protected static void startJob() 
  {
	if(job == null)
	{
	  startJob(0);
	}
  }
  
  /**
   * Start a new cache job with the specified delay.
   * 
   * @param delay
   * 		The start delay for the cache job.
   */
  private static void startJob(long delay)
  {
	job = new CacheJob();
	job.setPriority(CacheJob.DECORATE);
	job.schedule(delay); // start as soon as possible
  }

  /**
   * Stop the current cache job. The cache job caches resources that were not able to be previously
   * downloaded.
   */
  protected static void stopJob() 
  {
	if (job != null) 
	{
	  job.cancel();
	}
	job = null;
  }

}


