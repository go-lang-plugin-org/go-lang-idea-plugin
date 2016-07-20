package com.goide;

import com.goide.inspections.GoBoolExpressionsInspection;
import com.goide.quickfix.GoQuickFixTestBase;
import com.intellij.testFramework.EdtTestUtil;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.TestRunnerUtil;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public abstract class GoParametrizedTestBase extends GoQuickFixTestBase {

  protected abstract void doTest();

  @Test
  public void test() {
    safeEdt(new ThrowableRunnable<Throwable>() {
      @Override
      public void run() {
        doTest();
      }
    });
  }

  @Before
  @Override
  public void setUp() throws Exception {
    safeEdt(new ThrowableRunnable<Throwable>() {
      @Override
      public void run() throws Throwable {
        GoParametrizedTestBase.super.setUp();
        setUpProjectSdk();
        myFixture.enableInspections(GoBoolExpressionsInspection.class);
      }
    });
  }

  @After
  @Override
  public void tearDown() {
    safeEdt(new ThrowableRunnable<Throwable>() {
      @Override
      public void run() throws Throwable {
        GoParametrizedTestBase.super.tearDown();
      }
    });
  }

  @Override
  protected LightProjectDescriptor getProjectDescriptor() {
    return createMockProjectDescriptor();
  }

  private void safeEdt(@NotNull ThrowableRunnable<Throwable> r) {
    if (runInDispatchThread()) {
      TestRunnerUtil.replaceIdeEventQueueSafely();
      EdtTestUtil.runInEdtAndWait(new ThrowableRunnable<Throwable>() {
        @Override
        public void run() throws Throwable {
          r.run();
        }
      });
    }
    else {
      try {
        r.run();
      }
      catch (Throwable t) {
        throw new RuntimeException(t);
      }
    }
  }
}