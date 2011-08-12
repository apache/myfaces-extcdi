/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.myfaces.extensions.cdi.core.impl.logging;

import org.apache.myfaces.extensions.cdi.core.api.logging.Logger;

import javax.enterprise.inject.Typed;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Gerhard Petracek
 * @author Werner Punz
 */
@Typed()
public class DefaultLogger implements Logger
{
    private static final long serialVersionUID = 6112073723716267680L;

    private transient java.util.logging.Logger wrapped;

    private String loggerName = null;
    private String resourceBundleName; //TODO add support at the injection point
    private boolean anonymous = false;

    protected DefaultLogger()
    {
        this.anonymous = true;
    }

    protected DefaultLogger(String loggerName)
    {
        this.loggerName = loggerName;
    }

    protected DefaultLogger(String loggerName, String resourceBundleName, boolean anonymous)
    {
        this.loggerName = loggerName;
        this.resourceBundleName = resourceBundleName;
        this.anonymous = anonymous;

        if("".equals(this.loggerName))
        {
            this.loggerName = null;
        }

        if("".equals(this.resourceBundleName))
        {
            this.resourceBundleName = null;
        }
    }

    boolean isValid()
    {
        return !(!this.anonymous && this.loggerName == null && this.resourceBundleName == null);
    }

    /**
     * {@inheritDoc}
     */
    public java.util.logging.Logger getWrapped()
    {
        if(this.wrapped == null)
        {
            lazyInitLogger();
        }
        return this.wrapped;
    }

    /**
     * {@inheritDoc}
     */
    public ResourceBundle getResourceBundle()
    {
        return getWrapped().getResourceBundle();
    }

    /**
     * {@inheritDoc}
     */
    public String getResourceBundleName()
    {
        return getWrapped().getResourceBundleName();
    }

    /**
     * {@inheritDoc}
     */
    public void setFilter(Filter filter)
    {
        getWrapped().setFilter(filter);
    }

    /**
     * {@inheritDoc}
     */
    public Filter getFilter()
    {
        return getWrapped().getFilter();
    }

    /**
     * {@inheritDoc}
     */
    public void log(LogRecord logRecord)
    {
        getWrapped().log(logRecord);
    }

    /**
     * {@inheritDoc}
     */
    public void log(Level level, String s)
    {
        if(!isLoggable(level))
        {
            return;
        }
        getWrapped().logp(level, this.loggerName, getMethodName(), s);
    }

    /**
     * {@inheritDoc}
     */
    public void log(Level level, String s, Object o)
    {
        if(!isLoggable(level))
        {
            return;
        }
        getWrapped().logp(level, this.loggerName, getMethodName(), s, o);
    }

    /**
     * {@inheritDoc}
     */
    public void log(Level level, String s, Object[] objects)
    {
        if(!isLoggable(level))
        {
            return;
        }
        getWrapped().logp(level, this.loggerName, getMethodName(), s, objects);
    }

    /**
     * {@inheritDoc}
     */
    public void log(Level level, String s, Throwable throwable)
    {
        if(!isLoggable(level))
        {
            return;
        }
        getWrapped().logp(level, this.loggerName, getMethodName(), s, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public void logp(Level level, String s, String s1, String s2)
    {
        getWrapped().logp(level, s, s1, s2);
    }

    /**
     * {@inheritDoc}
     */
    public void logp(Level level, String s, String s1, String s2, Object o)
    {
        getWrapped().logp(level, s, s1, s2, o);
    }

    /**
     * {@inheritDoc}
     */
    public void logp(Level level, String s, String s1, String s2, Object[] objects)
    {
        getWrapped().logp(level, s, s1, s2, objects);
    }

    /**
     * {@inheritDoc}
     */
    public void logp(Level level, String s, String s1, String s2, Throwable throwable)
    {
        getWrapped().logp(level, s, s1, s2, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public void logrb(Level level, String s, String s1, String s2, String s3)
    {
        getWrapped().logrb(level, s, s1, s2, s3);
    }

    /**
     * {@inheritDoc}
     */
    public void logrb(Level level, String s, String s1, String s2, String s3, Object o)
    {
        getWrapped().logrb(level, s, s1, s2, s3, o);
    }

    /**
     * {@inheritDoc}
     */
    public void logrb(Level level, String s, String s1, String s2, String s3, Object[] objects)
    {
        getWrapped().logrb(level, s, s1, s2, s3, objects);
    }

    /**
     * {@inheritDoc}
     */
    public void logrb(Level level, String s, String s1, String s2, String s3, Throwable throwable)
    {
        getWrapped().logrb(level, s, s1, s2, s3, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public void entering(String s, String s1)
    {
        getWrapped().entering(s, s1);
    }

    /**
     * {@inheritDoc}
     */
    public void entering(String s, String s1, Object o)
    {
        getWrapped().entering(s, s1, o);
    }

    /**
     * {@inheritDoc}
     */
    public void entering(String s, String s1, Object[] objects)
    {
        getWrapped().entering(s, s1, objects);
    }

    /**
     * {@inheritDoc}
     */
    public void exiting(String s, String s1)
    {
        getWrapped().exiting(s, s1);
    }

    /**
     * {@inheritDoc}
     */
    public void exiting(String s, String s1, Object o)
    {
        getWrapped().exiting(s, s1, o);
    }

    /**
     * {@inheritDoc}
     */
    public void throwing(String s, String s1, Throwable throwable)
    {
        getWrapped().throwing(s, s1, throwable);
    }

    /**
     * {@inheritDoc}
     */
    public void severe(String s)
    {
        if(!isLoggable(Level.SEVERE))
        {
            return;
        }
        getWrapped().logp(Level.SEVERE, this.loggerName, getMethodName(), s);
    }

    /**
     * {@inheritDoc}
     */
    public void warning(String s)
    {
        if(!isLoggable(Level.WARNING))
        {
            return;
        }
        getWrapped().logp(Level.WARNING, this.loggerName, getMethodName(), s);
    }

    /**
     * {@inheritDoc}
     */
    public void info(String s)
    {
        if(!isLoggable(Level.INFO))
        {
            return;
        }
        getWrapped().logp(Level.INFO, this.loggerName, getMethodName(), s);
    }

    /**
     * {@inheritDoc}
     */
    public void config(String s)
    {
        getWrapped().config(s);
    }

    /**
     * {@inheritDoc}
     */
    public void fine(String s)
    {
        if(!isLoggable(Level.FINE))
        {
            return;
        }
        getWrapped().logp(Level.FINE, this.loggerName, getMethodName(), s);
    }

    /**
     * {@inheritDoc}
     */
    public void finer(String s)
    {
        if(!isLoggable(Level.FINER))
        {
            return;
        }
        getWrapped().logp(Level.FINER, this.loggerName, getMethodName(), s);
    }

    /**
     * {@inheritDoc}
     */
    public void finest(String s)
    {
        if(!isLoggable(Level.FINEST))
        {
            return;
        }
        getWrapped().logp(Level.FINEST, this.loggerName, getMethodName(), s);
    }

    /**
     * {@inheritDoc}
     */
    public void setLevel(Level level)
    {
        getWrapped().setLevel(level);
    }

    /**
     * {@inheritDoc}
     */
    public Level getLevel()
    {
        return getWrapped().getLevel();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLoggable(Level level)
    {
        return getWrapped().isLoggable(level);
    }

    /**
     * {@inheritDoc}
     */
    public String getName()
    {
        return getWrapped().getName();
    }

    /**
     * {@inheritDoc}
     */
    public void addHandler(Handler handler)
    {
        getWrapped().addHandler(handler);
    }

    /**
     * {@inheritDoc}
     */
    public void removeHandler(Handler handler)
    {
        getWrapped().removeHandler(handler);
    }

    /**
     * {@inheritDoc}
     */
    public Handler[] getHandlers()
    {
        return getWrapped().getHandlers();
    }

    /**
     * {@inheritDoc}
     */
    public void setUseParentHandlers(boolean b)
    {
        getWrapped().setUseParentHandlers(b);
    }

    /**
     * {@inheritDoc}
     */
    public boolean getUseParentHandlers()
    {
        return getWrapped().getUseParentHandlers();
    }

    /**
     * {@inheritDoc}
     */
    public java.util.logging.Logger getParent()
    {
        return getWrapped().getParent();
    }

    /**
     * {@inheritDoc}
     */
    public void setParent(java.util.logging.Logger logger)
    {
        getWrapped().setParent(logger);
    }

    /**
     * {@inheritDoc}
     */
    public Factory getFactory()
    {
        return new DefaultLoggerFactory();
    }

    private void lazyInitLogger()
    {
        if(this.anonymous)
        {
            if(this.resourceBundleName == null)
            {
                this.wrapped = java.util.logging.Logger.getAnonymousLogger();
            }
            else
            {
                this.wrapped = java.util.logging.Logger.getAnonymousLogger(this.resourceBundleName);
            }
        }
        else
        {
            if(this.resourceBundleName == null)
            {
                this.wrapped = java.util.logging.Logger.getLogger(this.loggerName);
            }
            else
            {
                this.wrapped = java.util.logging.Logger.getLogger(this.loggerName, this.resourceBundleName);
            }
        }
    }

    private String getMethodName()
    {
        @SuppressWarnings({"ThrowableInstanceNeverThrown"})
        RuntimeException runtimeException = new RuntimeException();

        StackTraceElement[] stackTrace = runtimeException.getStackTrace();

        for(int i = 2; i < stackTrace.length - 1; i++)
        {
            if(!stackTrace[i].getClassName().equals(getClass().getName()))
            {
                return stackTrace[i].getMethodName();
            }
        }

        return "";
    }
}
