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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Gerhard Petracek
 * @author Werner Punz
 */
@Typed
public class DefaultLogger implements Logger
{
    private static final long serialVersionUID = 6112073723716267680L;

    private transient java.util.logging.Logger wrapped;

    private String loggerName = null;

    protected DefaultLogger()
    {
    }

    protected DefaultLogger(String loggerName)
    {
        this.loggerName = loggerName;
        this.wrapped = java.util.logging.Logger.getLogger(this.loggerName);
    }

    public java.util.logging.Logger getWrapped()
    {
        return this.wrapped;
    }

    public ResourceBundle getResourceBundle()
    {
        return this.wrapped.getResourceBundle();
    }

    public String getResourceBundleName()
    {
        return this.wrapped.getResourceBundleName();
    }

    public void setFilter(Filter filter) throws SecurityException
    {
        this.wrapped.setFilter(filter);
    }

    public Filter getFilter()
    {
        return this.wrapped.getFilter();
    }

    public void log(LogRecord logRecord)
    {
        this.wrapped.log(logRecord);
    }

    public void log(Level level, String s)
    {
        this.wrapped.logp(level, this.loggerName, getMethodName(), s);
    }

    public void log(Level level, String s, Object o)
    {
        this.wrapped.logp(level, loggerName, getMethodName(), s, o);
    }

    public void log(Level level, String s, Object[] objects)
    {
        this.wrapped.logp(level, this.loggerName, getMethodName(), s, objects);
    }

    public void log(Level level, String s, Throwable throwable)
    {
        this.wrapped.logp(level, this.loggerName, getMethodName(), s, throwable);
    }

    public void logp(Level level, String s, String s1, String s2)
    {
        this.wrapped.logp(level, s, s1, s2);
    }

    public void logp(Level level, String s, String s1, String s2, Object o)
    {
        this.wrapped.logp(level, s, s1, s2, o);
    }

    public void logp(Level level, String s, String s1, String s2, Object[] objects)
    {
        this.wrapped.logp(level, s, s1, s2, objects);
    }

    public void logp(Level level, String s, String s1, String s2, Throwable throwable)
    {
        this.wrapped.logp(level, s, s1, s2, throwable);
    }

    public void logrb(Level level, String s, String s1, String s2, String s3)
    {
        this.wrapped.logrb(level, s, s1, s2, s3);
    }

    public void logrb(Level level, String s, String s1, String s2, String s3, Object o)
    {
        this.wrapped.logrb(level, s, s1, s2, s3, o);
    }

    public void logrb(Level level, String s, String s1, String s2, String s3, Object[] objects)
    {
        this.wrapped.logrb(level, s, s1, s2, s3, objects);
    }

    public void logrb(Level level, String s, String s1, String s2, String s3, Throwable throwable)
    {
        this.wrapped.logrb(level, s, s1, s2, s3, throwable);
    }

    public void entering(String s, String s1)
    {
        this.wrapped.entering(s, s1);
    }

    public void entering(String s, String s1, Object o)
    {
        this.wrapped.entering(s, s1, o);
    }

    public void entering(String s, String s1, Object[] objects)
    {
        this.wrapped.entering(s, s1, objects);
    }

    public void exiting(String s, String s1)
    {
        this.wrapped.exiting(s, s1);
    }

    public void exiting(String s, String s1, Object o)
    {
        this.wrapped.exiting(s, s1, o);
    }

    public void throwing(String s, String s1, Throwable throwable)
    {
        this.wrapped.throwing(s, s1, throwable);
    }

    public void severe(String s)
    {
        this.wrapped.logp(Level.SEVERE, loggerName, getMethodName(), s);
    }

    public void warning(String s)
    {
        this.wrapped.logp(Level.WARNING, loggerName, getMethodName(), s);
    }

    public void info(String s)
    {
        this.wrapped.logp(Level.INFO, loggerName, getMethodName(), s);
    }

    public void config(String s)
    {
        this.wrapped.config(s);
    }

    public void fine(String s)
    {
        this.wrapped.logp(Level.FINE, loggerName, getMethodName(), s);
    }

    public void finer(String s)
    {
        this.wrapped.logp(Level.FINER, loggerName, getMethodName(), s);
    }

    public void finest(String s)
    {
        this.wrapped.logp(Level.FINEST, loggerName, getMethodName(), s);
    }

    public void setLevel(Level level) throws SecurityException
    {
        this.wrapped.setLevel(level);
    }

    public Level getLevel()
    {
        return this.wrapped.getLevel();
    }

    public boolean isLoggable(Level level)
    {
        return this.wrapped.isLoggable(level);
    }

    public String getName()
    {
        return this.wrapped.getName();
    }

    public void addHandler(Handler handler) throws SecurityException
    {
        this.wrapped.addHandler(handler);
    }

    public void removeHandler(Handler handler) throws SecurityException
    {
        this.wrapped.removeHandler(handler);
    }

    public Handler[] getHandlers()
    {
        return this.wrapped.getHandlers();
    }

    public void setUseParentHandlers(boolean b)
    {
        this.wrapped.setUseParentHandlers(b);
    }

    public boolean getUseParentHandlers()
    {
        return this.wrapped.getUseParentHandlers();
    }

    public java.util.logging.Logger getParent()
    {
        return this.wrapped.getParent();
    }

    public void setParent(java.util.logging.Logger logger)
    {
        this.wrapped.setParent(logger);
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        this.wrapped = java.util.logging.Logger.getLogger(this.loggerName);
    }

    private String getMethodName()
    {
        @SuppressWarnings({"ThrowableInstanceNeverThrown"})
        RuntimeException runtimeException = new RuntimeException();

        for(StackTraceElement element : runtimeException.getStackTrace())
        {
            if(!element.toString().contains(getClass().getName()))
            {
                return element.getMethodName();
            }
        }

        return "";
    }
}
