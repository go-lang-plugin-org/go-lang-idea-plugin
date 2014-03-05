package ro.redeul.google.go.ide.ui;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.EmptyRunnable;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by d3xter on 03.03.14.
 */
public class GoToolWindow extends AbstractProjectComponent {
    private ConsoleViewImpl consoleViewImpl;
    private JPanel mainPanel;

    private ToolWindow window;

    public GoToolWindow(final Project project) {
        super(project);
    }

    public void initGoToolWindow(final ToolWindow window) {
        window.setToHideOnEmptyContent(true);
        this.window = window;

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(consoleViewImpl.getComponent(), "", false);
        window.getContentManager().addContent(content);
    }

    public void show() {
        if(this.window != null) {
            this.window.activate(EmptyRunnable.getInstance());
        }
    }

    public void setTitle(String title) {
        if(this.window != null) {
            this.window.setTitle(title);
        }
    }

    private void createUIComponents() {
        consoleViewImpl = (ConsoleViewImpl) TextConsoleBuilderFactory.getInstance().createBuilder(myProject).getConsole();
    }

    public static GoToolWindow getInstance(@NotNull Project project) {
        return project.getComponent(GoToolWindow.class);
    }

    public ConsoleView getConsoleView() {
        return this.consoleViewImpl;
    }

    public void clearConsoleView() {
        this.consoleViewImpl.clear();
    }

    /**
     * Prints a message with ContentType NORMAL_OUTPUT
     * @param message Message, which will be printed
     */
    public void printNormalMessage(String message) {
        this.printMessage(message, ConsoleViewContentType.NORMAL_OUTPUT);
    }

    /**
     * Prints a message with ContentType ERROR_OUTPUT
     * @param message Message, which will be printed
     */
    public void printErrorMessage(String message) {
        this.printMessage(message, ConsoleViewContentType.ERROR_OUTPUT);

        //Focus the tool Window now, because we print a error message
        this.show();
    }

    /**
     * Prints a message to the console view
     * @param message Message, which will be printed
     * @param contentType ContentType, which will be used to hightlight the message
     */
    public void printMessage(String message, ConsoleViewContentType contentType) {
        consoleViewImpl.print(message, contentType);
    }

    /**
     * Attaches the underlying consoleView to the process handler
     * @param processHandler ProcessHandler
     */
    public void attachConsoleViewToProcess(ProcessHandler processHandler) {
        consoleViewImpl.attachToProcess(processHandler);
    }
}
