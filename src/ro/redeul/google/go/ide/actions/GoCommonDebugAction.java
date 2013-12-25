package ro.redeul.google.go.ide.actions;

import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.AnAction;

public abstract class GoCommonDebugAction extends AnAction {
    protected static final String ID = "go Debug Console";
    protected static String TITLE = "";
    protected static ConsoleView consoleView;
}