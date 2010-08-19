package ro.redeul.google.go.config.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import ro.redeul.google.go.config.ui.GoFacetTab;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 3:45:47 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoFacetConfiguration implements FacetConfiguration {

    public GoFacetConfiguration() {
    }

    public FacetEditorTab[] createEditorTabs(FacetEditorContext editorContext, FacetValidatorsManager validatorsManager) {
        return new FacetEditorTab[] { new GoFacetTab() };
    }

    public void readExternal(Element element) throws InvalidDataException {
        XmlSerializer.deserializeInto(this, element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        XmlSerializer.serializeInto(this, element);
    }
}
