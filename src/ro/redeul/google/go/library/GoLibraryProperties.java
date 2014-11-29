package ro.redeul.google.go.library;

import com.intellij.openapi.roots.libraries.LibraryProperties;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author Florin Patan
 */
public class GoLibraryProperties extends LibraryProperties {
    private Object comparable = new Object();

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public Object getState() {
        return null;
    }

    @Override
    public void loadState(Object o) {
    }
}
