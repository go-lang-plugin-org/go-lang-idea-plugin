package ro.redeul.google.go.ide.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * User: Florin Patan <florinpatan@gmail.com>
 * Date: 12/01/14
 */
public class GoAppEngineStopUpload extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        GoAppEngineUpload.stopCurrentUpload();
    }

}
