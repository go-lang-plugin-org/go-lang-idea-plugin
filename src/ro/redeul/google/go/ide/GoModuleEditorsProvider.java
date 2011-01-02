package ro.redeul.google.go.ide;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.roots.ui.configuration.DefaultModuleConfigurationEditorFactory;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: 1/2/11
 * Time: 11:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoModuleEditorsProvider implements ModuleConfigurationEditorProvider {

    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
        final Module module = state.getRootModel().getModule();

        if (module.getModuleType() != GoModuleType.getInstance())
            return ModuleConfigurationEditor.EMPTY;

        final DefaultModuleConfigurationEditorFactory editorFactory = DefaultModuleConfigurationEditorFactory.getInstance();
        List<ModuleConfigurationEditor> editors = new ArrayList<ModuleConfigurationEditor>();
        editors.add(editorFactory.createModuleContentRootsEditor(state));
        editors.add(editorFactory.createOutputEditor(state));
        editors.add(editorFactory.createClasspathEditor(state));
//        editors.add(new PluginModuleBuildConfEditor(state));

        return editors.toArray(new ModuleConfigurationEditor[editors.size()]);
    }
}
