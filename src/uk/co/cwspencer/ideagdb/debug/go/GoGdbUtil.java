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
import ro.redeul.google.go.sdk.GoSdkUtil;

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

    public static Boolean isValidGdbPath(String path) {
        if (!GoSdkUtil.checkFileExists(path)) {
            return false;
        }

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
}
