package com.goide.jps.model;

import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.annotations.AbstractCollection;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class GoModuleExtensionProperties {
  @NotNull @Tag("includePaths")
  @AbstractCollection(surroundWithTag = false, elementTag = "path")
  //should not contain duplicate elements
  public List<String> myIncludePaths = ContainerUtil.newArrayList();

  @NotNull @Tag("parseTransforms")
  @AbstractCollection(surroundWithTag = false, elementTag = "transform")
  //should not contain duplicate elements
  public List<String> myParseTransforms = ContainerUtil.newArrayList();

  public GoModuleExtensionProperties() {
  }

  public GoModuleExtensionProperties(@NotNull GoModuleExtensionProperties props) {
    myIncludePaths = ContainerUtil.newArrayList(props.myIncludePaths);
    myParseTransforms = ContainerUtil.newArrayList(props.myParseTransforms);
  }
}
