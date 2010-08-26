package ro.redeul.google.go.config.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.impl.ProjectMacrosUtil;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.XmlSerializer;
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
public class GoFacetConfiguration implements FacetConfiguration {

    public String SDK_NAME;

    public GoFacetConfiguration() {
    }

    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {


        List<Sdk> libraries = ProjectJdkTable.getInstance().getSdksOfType(GoSdkType.getInstance());

        return new FacetEditorTab[] { new GoFacetTab(this, libraries) };
    }

    public void readExternal(Element element) throws InvalidDataException {
        XmlSerializer.deserializeInto(this, element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        XmlSerializer.serializeInto(this, element);
    }

    public Sdk getSdk() {
        return ProjectJdkTable.getInstance().findJdk(SDK_NAME);
    }
}
