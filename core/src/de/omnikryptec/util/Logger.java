/*
 *    Copyright 2017 - 2020 Roman Borris (pcfreak9000), Paul Hagedorn (Panzer1119)
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.omnikryptec.util;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Logger {
    
    public static enum LogType {
        /* Verbose(-2, false, 0), */ Debug(-1, false, 2), Info(0, false, 3), Warning(1, true, 0), Error(2, true, 2);
        
        private final int importance;
        private final boolean red;
        private final String dif;
        
        LogType(final int imp, final boolean red, final int dif) {
            this.importance = imp;
            this.red = red;
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < dif; i++) {
                builder.append(' ');
            }
            this.dif = builder.toString();
        }
    }
    
    private static final Map<Class<?>, Logger> loggerCache = new HashMap<>();
    
    private static PrintStream out = System.out;
    private static PrintStream err = System.err;
    
    private static boolean classDebug = false;
    private static LogType minlevel = LogType.Info;
    
    public static Logger getLogger(final Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Logger logger = loggerCache.get(clazz);
        if (logger == null) {
            logger = new Logger(clazz);
            loggerCache.put(clazz, logger);
        }
        return logger;
    }
    
    public static void log(final Class<?> clazz, final LogType type, final Object... msgs) {
        if (meetsMin(type)) {
            final LocalDateTime now = LocalDateTime.now();
            final StringBuilder builder = new StringBuilder();
            builder.append("[ " + type);
            builder.append(type.dif);
            builder.append(" ]");
            builder.append("[ " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + " ]");
            if (clazz != null) {
                String n = "logger has failed";
                if (classDebug) {
                    final StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
                    int index = 1;
                    while (stacktrace[index].toString().contains(Logger.class.getName())) {
                        index++;
                    }
                    n = " " + stacktrace[index] + " ";
                } else {
                    n = clazz.getSimpleName();
                }
                if (n.isEmpty()) {
                    final String[] array = clazz.getTypeName().split("\\.");
                    n = array[array.length - 1];
                }
                builder.append(" <" + n + ">");
            }
            builder.append(' ');
            LogType nextType = type;
            for (final Object m : msgs) {
                if (m instanceof LogType) {
                    nextType = (LogType) m;
                } else if (m != null && nextType.importance >= minlevel.importance) {
                    builder.append(m);
                    builder.append('\n');
                    //nextType = type; //reset so every other LogType must be specified?
                }
            }
            if (type.red) {
                err.print(builder.toString());
            } else {
                out.print(builder.toString());
            }
        }
    }
    
    private static boolean meetsMin(LogType t) {
        return t.importance >= minlevel.importance;
    }
    
    public static void setClassDebug(final boolean b) {
        classDebug = b;
    }
    
    public static void setMinLogType(final LogType type) {
        minlevel = type == null ? LogType.Info : type;
    }
    
    public static LogType getMinLogType() {
        return minlevel;
    }
    
    public static boolean isClassDebug() {
        return classDebug;
    }
    
    private final Class<?> clazz;
    
    private Logger(final Class<?> clazz) {
        this.clazz = clazz;
    }
    
    public void log(final LogType type, final Object... msgs) {
        log(this.clazz, type, msgs);
    }
    
    public void logf(LogType t, String format, Object... args) {
        if (meetsMin(t)) {
            log(t, String.format(format, args));
        }
    }
    
    public void debug(final Object... msgs) {
        log(this.clazz, LogType.Debug, msgs);
    }
    
    public void debugf(String format, Object... data) {
        if (meetsMin(LogType.Debug)) {
            debug(String.format(format, data));
        }
    }
    
    public void info(final Object... msgs) {
        log(this.clazz, LogType.Info, msgs);
    }
    
    public void infof(String format, Object... data) {
        if (meetsMin(LogType.Info)) {
            info(String.format(format, data));
        }
    }
    
    public void warn(final Object... msgs) {
        log(this.clazz, LogType.Warning, msgs);
    }
    
    public void warnf(String format, Object... data) {
        if (meetsMin(LogType.Warning)) {
            warn(String.format(format, data));
        }
    }
    
    public void error(final Object... msgs) {
        log(this.clazz, LogType.Error, msgs);
    }
    
    public void errorf(String format, Object... data) {
        if (meetsMin(LogType.Error)) {
            error(String.format(format, data));
        }
    }
    
    public PrintStream getOut() {
        return out;
    }
    
    public PrintStream getErr() {
        return err;
    }
}
