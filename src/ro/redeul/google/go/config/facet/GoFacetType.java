package ro.redeul.google.go.config.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GoFacetType extends FacetType<GoFacet, GoFacetConfiguration> {

    public static final FacetTypeId<GoFacet> GO_FACET_TYPE_ID = new FacetTypeId<GoFacet>("Google Go Facet");

    public GoFacetType() {
        super(GO_FACET_TYPE_ID, "id", "google go");
    }

    @Override
    public GoFacetConfiguration createDefaultConfiguration() {
        return new GoFacetConfiguration();
    }

    @Override
    public GoFacet createFacet(@NotNull Module module, String name, @NotNull GoFacetConfiguration configuration, @Nullable Facet underlyingFacet) {
        return new GoFacet(this, module, name, configuration, null);
    }

    @Override
    public boolean isSuitableModuleType(ModuleType moduleType) {
        return true;
    }
}
