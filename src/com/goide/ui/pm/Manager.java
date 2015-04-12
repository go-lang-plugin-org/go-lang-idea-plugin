/*
 * Copyright 2013-2015 Sergey Ignatov, Alexander Zolotov, Mihai Toader, Florin Patan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.goide.ui.pm;

import com.goide.sdk.GoSdkService;
import com.goide.util.GoExecutor;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.WindowWrapper;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class Manager {
  private JPanel myPanel1;
  private JTextField mySearchText;
  private JButton myGoGetButton;
  private JLabel myPackageName;
  private JTextArea myPackageDescription;
  private JLabel myPackageAuthor;
  private JLabel myPackageURL;
  private JList myPackagesList;
  private JLabel myDocumentationURL;
  private JButton myCloseButton;
  private JLabel poweredBy;
  private String mySearchedPackage = "";

  private static WindowWrapper myWindow;
  private static Manager myInstance;
  private static Project myProject;
  private static Module myModule;

  private final CloseableHttpClient myHttpClient;
  private HttpResponse myHttpResponse;
  private HttpGet myHttpRequest;
  private String myUserAgent = "go plugin for IntelliJ Platform (contact us at https://github.com/go-lang-plugin-org/go-lang-idea-plugin)";
  @NotNull private String mySearchUrl = "http://go-search.org/api?action=search&q=%s";
  private Response myPackages;
  private Task.Backgroundable mySearchTask;

  private Manager() {
    myHttpClient = HttpClientBuilder.create().build();

    mySearchText.addKeyListener(new KeyAdapter() {
      @Override
      public void keyReleased(KeyEvent e) {
        super.keyReleased(e);
        if (mySearchText.getText() != mySearchedPackage) {
          if (myHttpRequest != null) {
            myHttpRequest.abort();
          }
          mySearchedPackage = mySearchText.getText();
          if (mySearchedPackage.length() >= 3) {
            mySearchTask = new Task.Backgroundable(myProject, "searching for packages", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {
              @Override
              public void run(@NotNull ProgressIndicator indicator) {
                searchPackage();
              }
            };

            ProgressManager.getInstance().run(mySearchTask);
          }
        }
      }
    });
    myCloseButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (myWindow != null) {
          myWindow.close();
        }
      }
    });
    myPackagesList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        populateGoPackageDetails(myPackagesList.getSelectedIndex());
      }
    });
    myPackageURL.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (Desktop.isDesktopSupported()) {
          String url = myPackages.getHits().get(myPackagesList.getSelectedIndex()).getProjectUrl();
          try {
            Desktop.getDesktop().browse(URI.create(url));
          }
          catch (IOException ignored) {

          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {

      }

      @Override
      public void mouseReleased(MouseEvent e) {

      }

      @Override
      public void mouseEntered(MouseEvent e) {
        myPackageURL.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        myPackageURL.setCursor(Cursor.getDefaultCursor());
      }
    });
    myDocumentationURL.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (Desktop.isDesktopSupported()) {
          String url = myPackages.getHits().get(myPackagesList.getSelectedIndex()).getPackage();
          try {
            Desktop.getDesktop().browse(URI.create("https://godoc.org/" + url));
          }
          catch (IOException ignored) {

          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {

      }

      @Override
      public void mouseReleased(MouseEvent e) {

      }

      @Override
      public void mouseEntered(MouseEvent e) {
        myDocumentationURL.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        myDocumentationURL.setCursor(Cursor.getDefaultCursor());
      }
    });
    poweredBy.addMouseListener(new MouseListener() {
      @Override
      public void mouseClicked(MouseEvent e) {
        if (Desktop.isDesktopSupported()) {
          try {
            Desktop.getDesktop().browse(URI.create("http://go-search.org"));
          }
          catch (IOException ignored) {

          }
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {

      }

      @Override
      public void mouseReleased(MouseEvent e) {

      }

      @Override
      public void mouseEntered(MouseEvent e) {
        poweredBy.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        poweredBy.setCursor(Cursor.getDefaultCursor());
      }
    });
    myGoGetButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        goGet();
      }
    });
  }

  private void searchPackage() {
    if (myHttpRequest != null) {
      myHttpRequest.abort();
    }
    if (mySearchedPackage == "") {
      myPackagesList.removeAll();
      populateGoPackageDetails(-1);
      return;
    }
    mySearchTask.setTitle(String.format("Searching for Go Package: \"%s\"", mySearchedPackage));
    myHttpRequest = new HttpGet(String.format(mySearchUrl, mySearchedPackage));
    try {
      myHttpRequest.addHeader("Content-Type", "application/json");
      myHttpRequest.addHeader("User-Agent", myUserAgent);
      myHttpResponse = myHttpClient.execute(myHttpRequest);

      ApplicationManager.getApplication().invokeLater(new Runnable() {
        @Override
        public void run() {
          try {
            handlerAnswer();
          }
          catch (IOException ignored) {
          }
        }
      });
    }
    catch (IOException ignored) {
    }
  }

  private void handlerAnswer() throws IOException {
    String json = EntityUtils.toString(myHttpResponse.getEntity(), "UTF-8");

    Gson gson = new Gson();
    myPackages = gson.fromJson(json, Response.class);

    myPackagesList.removeAll();

    DefaultListModel model = new DefaultListModel();
    List<GoPackage> goPackages = myPackages.getHits();
    if (goPackages.isEmpty()) {
      model.addElement("No packages found");
    }
    else {
      for (GoPackage goPackage : goPackages) {
        String description = goPackage.name;
        if (!goPackage.synopsis.isEmpty()) {
          description += " (" + goPackage.synopsis + ")";
        }
        else if (!goPackage.description.isEmpty()) {
          description += " (";
          if (goPackage.description.length() > 30) {
            description += goPackage.description.substring(0, 30);
            description += "...";
          }
          description += ")";
        }
        model.addElement(description);
      }
    }

    myPackagesList.setModel(model);
  }

  private void populateGoPackageDetails(int goPackageId) {
    if (goPackageId == -1) {
      myPackageName.setText("");
      myPackageAuthor.setText("");
      myPackageURL.setText("");
      myDocumentationURL.setText("");
      myPackageDescription.setText("");
      myGoGetButton.setEnabled(false);
      return;
    }

    GoPackage goPackage = myPackages.getHits().get(goPackageId);
    myPackageName.setText(goPackage.getName());
    myPackageAuthor.setText(goPackage.getAuthor());

    myPackageURL.setText(String.format(
      "<html><a href=\"%s\">%s</a></html>",
      goPackage.getProjectUrl(),
      goPackage.getProjectUrl()));

    myDocumentationURL.setText(String.format(
      "<html><a href=\"https://godoc.org/%s\">https://godoc.org/%s</a></html>",
      goPackage.getPackage(),
      goPackage.getPackage()));

    myPackageDescription.setText(goPackage.getDescription());
    myPackageDescription.setCaretPosition(0);
    myGoGetButton.setEnabled(true);

    // TODO check if package is already installed and display / run go get -u instead if so
  }

  private void goGet() {
    final String sdkPath = GoSdkService.getInstance(myProject).getSdkHomePath(myModule);
    if (StringUtil.isEmpty(sdkPath)) {
      return;
    }

    final String packageName = myPackages.getHits().get(myPackagesList.getSelectedIndex()).packageName;

    CommandProcessor.getInstance().runUndoTransparentAction(new Runnable() {
      @Override
      public void run() {
        GoExecutor.in(myModule).withPresentableName("go get " + packageName)
          .withParameters("get", packageName).showNotifications(false).showOutputOnError().executeWithProgress(true);
      }
    });
  }

  public static class GoPackage {
    private String name;
    @SerializedName("package")
    private String packageName;
    private String author;
    private String synopsis;
    private String description;
    @SerializedName("projecturl")
    private String projectUrl;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getPackage() {
      return packageName;
    }

    public void setPackage(String packageName) {
      this.packageName = packageName;
    }

    public String getAuthor() {
      return author;
    }

    public void setAuthor(String author) {
      this.author = author;
    }

    public String getSynopsis() {
      return synopsis;
    }

    public void setSynopsis(String synopsis) {
      this.synopsis = synopsis;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getProjectUrl() {
      return projectUrl;
    }

    public void setProjectUrl(String projectUrl) {
      this.projectUrl = projectUrl;
    }
  }

  public static class Response {
    private String query;
    private List<GoPackage> hits;

    public String getQuery() {
      return query;
    }

    public void setQuery(String query) {
      this.query = query;
    }

    public List<GoPackage> getHits() {
      return hits;
    }

    public void setHits(List<GoPackage> hits) {
      this.hits = hits;
    }
  }

  public JPanel getComponent() {
    return myPanel1;
  }

  public static Manager getInstance() {
    if (myInstance == null) {
      myInstance = new Manager();
    }

    return myInstance;
  }

  public static void setWindow(WindowWrapper windowWrapper) {
    myWindow = windowWrapper;
  }

  public static void setProject(Project project) {
    myProject = project;
  }

  public static void setModule(Module module) {
    myModule = module;
  }

}
