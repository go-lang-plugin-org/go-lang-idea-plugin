package ro.redeul.google.go.library;

import com.intellij.openapi.roots.libraries.LibraryKind;

/**
 * @author Florin Patan
 */
public interface GoLibrary {
    String GO_LIBRARY_TYPE_ID = "Go";
    String GO_LIBRARY_CATEGORY_NAME = "Go";
    String GO_LIBRARY_KIND_ID = "Go";
    LibraryKind KIND = LibraryKind.create(GO_LIBRARY_KIND_ID);
}
