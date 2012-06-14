package ro.redeul.google.go.template;

import com.intellij.codeInsight.template.impl.DefaultLiveTemplatesProvider;

public class GoTemplatesProvider implements DefaultLiveTemplatesProvider {
    @Override
    public String[] getDefaultLiveTemplateFiles() {
        return new String[] {"liveTemplates/GoLiveTemplates"};
    }

    @Override
    public String[] getHiddenLiveTemplateFiles() {
        return new String[0];
    }
}
