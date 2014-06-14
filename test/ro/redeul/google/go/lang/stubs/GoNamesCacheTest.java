package ro.redeul.google.go.lang.stubs;

import org.junit.Test;
import ro.redeul.google.go.config.sdk.GoTargetArch;
import ro.redeul.google.go.config.sdk.GoTargetOs;

import java.util.Set;

import static org.junit.Assert.*;

public class GoNamesCacheTest {

    @Test
    public void testGetExcludeOsNames_excludesSelf() throws Exception {
        Set<String> names = GoNamesCache.getExcludeOsNames(GoTargetOs.Windows);
        assertFalse(names.contains("_windows"));
        assertTrue(names.contains("_linux"));
    }

    @Test
    public void testGetExcludeArchNames_excludesSelf() throws Exception {
        Set<String> names = GoNamesCache.getExcludeArchNames(GoTargetArch._386);
        assertFalse(names.contains("_386"));
        assertTrue(names.contains("_arm"));
    }
}