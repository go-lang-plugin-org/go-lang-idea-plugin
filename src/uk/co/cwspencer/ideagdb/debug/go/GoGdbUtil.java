/*
Copyright 2013 Florin Patan. All rights reserved.
Use of this source code is governed by a MIT-style
license that can be found in the LICENSE file.
*/
package uk.co.cwspencer.ideagdb.debug.go;

import com.google.common.collect.ImmutableMap;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.CapturingProcessHandler;
import com.intellij.execution.process.ProcessOutput;
import com.intellij.openapi.diagnostic.Logger;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by Florin Patan <florinpatan@gmail.com>
 *
 * 1/14/14
 */
public class GoGdbUtil {
    private static final Logger LOG = Logger.getInstance("uk.co.cwspencer.ideagdb.debug.go.gogdbutil");

    // TODO properly add only types that work here
    private static final Map<String, Boolean> editingSupport = ImmutableMap.<String, Boolean>builder()
            .put("string", false)
            .build();

    public static String getGoObjectType(String originalType) {
        if (originalType.contains("struct string")) {
            return originalType.replace("struct ", "");
        }

        return originalType;
    }

    public static Boolean supportsEditing(String varType) {
        String goType = getGoObjectType(varType);

        if (!editingSupport.containsKey(goType)) {
            return true;
        }

        return editingSupport.get(goType);
    }

    public static Boolean isKnownGdb(String path) {
        try {
            GeneralCommandLine command = new GeneralCommandLine();
            command.setExePath(path);
            command.addParameter("--version");

            ProcessOutput output = new CapturingProcessHandler(
                    command.createProcess(),
                    Charset.defaultCharset(),
                    command.getCommandLineString()).runProcess();

            if (output.getExitCode() != 0) {
                LOG.error("gdb exited with invalid exit code: " + output.getExitCode());
                return false;
            }

            String cmdOutput = output.getStdout();
            return cmdOutput.contains("(GDB) 7.6") || cmdOutput.contains("(GDB) 7.4");
        } catch (Exception e) {
            LOG.error("Exception while executing the process:", e);
            return false;
        }
    }

    public static Boolean isValidGdbPath(String path) {
        try {
            GeneralCommandLine command = new GeneralCommandLine();
            command.setExePath(path);
            command.addParameter("--version");

            ProcessOutput output = new CapturingProcessHandler(
                    command.createProcess(),
                    Charset.defaultCharset(),
                    command.getCommandLineString()).runProcess();

            if (output.getExitCode() != 0) {
                LOG.error("gdb exited with invalid exit code: " + output.getExitCode());
                return false;
            }

            // TODO maybe we should warn the user that his GDB version is not the latest at time of writing (7.6.2)
            return output.getStdout().contains("GDB");
        } catch (Exception e) {
            LOG.error("Exception while executing the process:", e);
            return false;
        }
    }

    public static boolean doesExecutableExist(String executablePath) {
        if(new File(executablePath).exists()) {
          return true;
        }
        String systempath = System.getenv("PATH");
        String[] paths = systempath.split(File.pathSeparator);

        for(String path : paths) {
            if(new File(path, executablePath).exists()) {
                return true;
            }
        }

        return false;
    }
}
