package com.example.loggertest;

import android.content.Context;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.StatusListenerConfigHelper;



public class Z1Logger {

    private org.slf4j.Logger    main;
    private static String logFile;
    private static String SEPERATOR_PART       = "#########################################";
    private static String SEPERATOR_PART_LONG  = "##################################################################################";
    private static String SEPERATOR_PART_SMALL = "#######";
    private static final String LOG_FOLDER = "/log";

    public Z1Logger(org.slf4j.Logger logger) {
        main = logger;
    }

    public static org.slf4j.Logger loggerFor(Class<?> clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static void configureLogbackDirectly(Context context){

        String loggername  = "Z1 Logger";
        String logFileName = context.getApplicationInfo().dataDir + LOG_FOLDER + "/z1-myLogger";
        logFile = logFileName + ".log";

        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        StatusListenerConfigHelper.addOnConsoleListenerInstance(lc, new OnConsoleStatusListener());

        // setup FileAppender
        PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
        encoder1.setContext(lc);
        encoder1.setPattern("%d{dd.MM.yyyy HH:mm:ss.SSS} \t [%logger{0}] \t %msg%n");
        encoder1.start();



        RollingFileAppender<ILoggingEvent> rollingFileAppender = new RollingFileAppender<>();
        rollingFileAppender.setContext(lc);
        rollingFileAppender.setName(loggername);
        rollingFileAppender.setFile(logFile);
        rollingFileAppender.setEncoder(encoder1);

        SizeAndTimeBasedRollingPolicy rollingPolicy = new SizeAndTimeBasedRollingPolicy();
        rollingPolicy.setContext(lc);
        rollingPolicy.setParent(rollingFileAppender);  // parent and context required!
        rollingPolicy.setFileNamePattern(logFileName + ".%d{yyyy-MM-dd}-%i.zip");
        rollingPolicy.setMaxHistory(5); // no more than n rollover files (delete oldest)
        rollingPolicy.setTotalSizeCap(FileSize.valueOf("50KB"));
        rollingPolicy.setMaxFileSize(FileSize.valueOf("50KB"));

        rollingPolicy.start();

        rollingFileAppender.setRollingPolicy(rollingPolicy);

        rollingFileAppender.start();


        // setup LogcatAppender
        PatternLayoutEncoder encoder2 = new PatternLayoutEncoder();
        encoder2.setContext(lc);
        encoder2.setPattern("%msg");
        encoder2.start();


        PatternLayoutEncoder tagEncoder = new PatternLayoutEncoder();
        tagEncoder.setContext(lc);
        tagEncoder.setPattern("\t%d{HH:mm:ss.SSS} \t [%logger{0}]\t");
        tagEncoder.start();


        LogcatAppender logcatAppender = new LogcatAppender();
        logcatAppender.setContext(lc);
        logcatAppender.setEncoder(encoder2);
        logcatAppender.setTagEncoder(tagEncoder);
        logcatAppender.start();

        // add the newly created appenders to the root logger;
        // qualify Logger to disambiguate from org.slf4j.Logger
        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);

        root.addAppender(rollingFileAppender);
        root.addAppender(logcatAppender);


//        System.out.println(SEPERATOR_PART_LONG);
//        System.out.println(SEPERATOR_PART_SMALL + "   LOGGER INFO");
//        System.out.println(SEPERATOR_PART_LONG);
//
//        StatusPrinter.print(lc);
//
//        System.out.println(SEPERATOR_PART_LONG);
//        System.out.println(SEPERATOR_PART_LONG);

    }

    public void info(String msg) {
        if(isClassOnBlacklist()) return;
        main.info(msg);
    }

    public void info(String msg, Object arg) {
        if(isClassOnBlacklist()) return;
        main.info(msg,arg);
    }

    public void debug(String msg) {
        if(isClassOnBlacklist()) return;
        main.debug(msg);
    }

    public void debug(String format, Object arg) {
        if(isClassOnBlacklist()) return;
        main.debug(format,arg);
    }

    public void debug(String format, Object arg1, Object arg2){
        if(isClassOnBlacklist()) return;
        main.debug(format,arg1,arg2);
    }

    public void debug(String format, Object... arguments){
        if(isClassOnBlacklist()) return;
        main.debug(format,arguments);
    }

    public void trace(String msg) {
        if(isClassOnBlacklist()) return;
        main.trace(msg);
    }

    public void error(Throwable e) {
        if(isClassOnBlacklist()) return;
        main.error("", e);
    }

    public void error(String msg) {
        if(isClassOnBlacklist()) return;
        main.error(msg);
    }

    public void error(String msg, Throwable e) {
        if(isClassOnBlacklist()) return;
        main.error(msg, e);
    }

    public void printStackTrace(Throwable exception) {
        if(BuildConfig.DEBUG){
            exception.printStackTrace();
        }

        error(exception);
    }

    private boolean isClassOnBlacklist(){
        boolean retValue = false;

        if(main == null)            return retValue;
        if(main.getName() == null)  return retValue;

        if(main.getName().contains("_DataSource"))          retValue = true;
        if(main.getName().contains("_DataSource"))          retValue = true;

        return retValue;
    }


    public void debug_addSeparatorLineWithMsgInCenter(String msg){
        debug_addSeparatorLineWithMsgInCenter(msg,(Object)null);
    }

    public void debug_addSeparatorLineWithMsgInCenter(String format, Object arg){
        debug_addSeparatorLineWithMsgInCenter(format,arg,null);
    }

    public void debug_addSeparatorLineWithMsgInCenter(String format, Object arg1, Object arg2){
        Object[] arguments = {arg1,arg2};
        debug_addSeparatorLineWithMsgInCenter(format,arguments);
    }


    public void debug_addSeparatorLineWithMsgInCenter(String format, Object... arguments){
        String newMessage = " \n";

        newMessage += SEPERATOR_PART_LONG  + "\n";
        newMessage += SEPERATOR_PART_SMALL + "\t\t\t" + format + "\n";
        newMessage += SEPERATOR_PART_LONG  + "\n";

        debug(newMessage,arguments);

    }
}
