package ro.redeul.google.go.config.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

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
    }
}
