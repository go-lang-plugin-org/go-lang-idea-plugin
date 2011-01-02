package ro.redeul.google.go.config.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.impl.ModuleJdkOrderEntryImpl;
import com.intellij.openapi.roots.impl.ProjectRootManagerImpl;
import com.intellij.openapi.roots.impl.RootModelImpl;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.config.ui.GoFacetTab;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 3:45:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoFacetConfiguration implements FacetConfiguration, PersistentStateComponent<GoFacetConfiguration.State> {

    public String SDK_NAME;

    private GoFacet goFacet;

    public GoFacetConfiguration() {
    }

    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {

        List<Sdk> libraries = ProjectJdkTable.getInstance().getSdksOfType(GoSdkType.getInstance());

        return new FacetEditorTab[]{new GoFacetTab(this, libraries)};
    }

    public void readExternal(Element element) throws InvalidDataException {
//        XmlSerializer.deserializeInto(this, element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
//        XmlSerializer.serializeInto(this, element);
    }

    public Sdk getSdk() {
        return ProjectJdkTable.getInstance().findJdk(SDK_NAME);
    }

    public void setGoSdkName(String name) {
        SDK_NAME = name;

        ApplicationManager.getApplication().runWriteAction(new Runnable(){
            public void run() {

                Module module = goFacet.getModule();

                ModifiableRootModel modifiableModel = ModuleRootManager.getInstance(goFacet.getModule()).getModifiableModel();

                modifiableModel.addOrderEntry(new ModuleJdkOrderEntryImpl(
                        SDK_NAME,
                        GoSdkType.getInstance().getPresentableName(),
                        ((RootModelImpl) ModuleRootManager.getInstance(module).getModifiableModel()),
                        ProjectRootManagerImpl.getInstanceImpl(module.getProject())));

                modifiableModel.commit();
            }
        });
    }

    public GoFacetConfiguration.State getState() {
        final State state = new State();
        state.SDK_NAME = SDK_NAME;
        return state;
    }

    public void loadState(GoFacetConfiguration.State state) {
        SDK_NAME = state.SDK_NAME;
    }

    public void setGoFacet(GoFacet goFacet) {
        this.goFacet = goFacet;
    }

    public static class State {
         String SDK_NAME;
    }

}
