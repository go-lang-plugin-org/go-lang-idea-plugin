package ro.redeul.google.go.config.facet;

import java.util.List;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.annotations.NotNull;
import ro.redeul.google.go.config.sdk.GoSdkType;
import ro.redeul.google.go.sdk.GoSdkUtil;

/**
 * Created by IntelliJ IDEA.
 * User: mtoader
 * Date: Aug 19, 2010
 * Time: 3:45:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class GoFacet extends Facet<GoFacetConfiguration> {

    public GoFacet(@NotNull FacetType facetType, @NotNull Module module, @NotNull String name, @NotNull GoFacetConfiguration configuration, Facet underlyingFacet) {

        super(facetType, module, name, configuration, underlyingFacet);

        configuration.setGoFacet(this);
    }

    public Sdk getGoSdk() {

        List<Sdk> sdks = GoSdkUtil.getSdkOfType(GoSdkType.getInstance());

        for (Sdk sdk : sdks) {
            if ( sdk.getName().equals(getConfiguration().SDK_NAME) ) {
                return sdk;
            }
        }


        return null;
    }
}
