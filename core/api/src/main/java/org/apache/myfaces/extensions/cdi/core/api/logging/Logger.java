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
package org.apache.myfaces.extensions.cdi.core.api.logging;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author Gerhard Petracek
 */
public interface Logger extends Serializable
{
    ResourceBundle getResourceBundle();

    String getResourceBundleName();

    void setFilter(Filter filter) throws SecurityException;

    Filter getFilter();

    void log(LogRecord logRecord);

    void log(Level level, String s);

    void log(Level level, String s, Object o);

    void log(Level level, String s, Object[] objects);

    void log(Level level, String s, Throwable throwable);

    void logp(Level level, String s, String s1, String s2);

    void logp(Level level, String s, String s1, String s2, Object o);

    void logp(Level level, String s, String s1, String s2, Object[] objects);

    void logp(Level level, String s, String s1, String s2, Throwable throwable);

    void logrb(Level level, String s, String s1, String s2, String s3);

    void logrb(Level level, String s, String s1, String s2, String s3, Object o);

    void logrb(Level level, String s, String s1, String s2, String s3, Object[] objects);

    void logrb(Level level, String s, String s1, String s2, String s3, Throwable throwable);

    void entering(String s, String s1);

    void entering(String s, String s1, Object o);

    void entering(String s, String s1, Object[] objects);

    void exiting(String s, String s1);

    void exiting(String s, String s1, Object o);

    void throwing(String s, String s1, Throwable throwable);

    void severe(String s);

    void warning(String s);

    void info(String s);

    void config(String s);

    void fine(String s);

    void finer(String s);

    void finest(String s);

    void setLevel(Level level) throws SecurityException;

    Level getLevel();

    boolean isLoggable(Level level);

    String getName();

    void addHandler(Handler handler) throws SecurityException;

    void removeHandler(Handler handler) throws SecurityException;

    Handler[] getHandlers();

    void setUseParentHandlers(boolean b);

    boolean getUseParentHandlers();

    java.util.logging.Logger getParent();

    void setParent(java.util.logging.Logger logger);

    /**
     * Just use this method if the original logger is really needed.
     * It's used by CODI internally to get a better performance.
     *
     * @return the wrapped logger
     */
    java.util.logging.Logger getWrapped();
}
