package by.javaguru.util;

import java.sql.Connection;

public class SQLScriptRunner {
    public static void execute(String path, Connection connection) throws Exception {
        org.apache.ibatis.jdbc.ScriptRunner scriptRunner = new org.apache.ibatis.jdbc.ScriptRunner(connection);
        scriptRunner.setSendFullScript(false);
        scriptRunner.setStopOnError(true);
        scriptRunner.setLogWriter(null);
        scriptRunner.runScript(new java.io.FileReader(path));
    }
}