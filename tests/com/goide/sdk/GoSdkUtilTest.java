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

package com.goide.sdk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GoSdkUtilTest {
  @Test
  public void testCompareVersions() throws Exception {
    assertEquals(-1, GoSdkUtil.compareVersions("1.1.2", "1.1.3"));
    assertEquals(-1, GoSdkUtil.compareVersions("1.1.2", "1.2.1"));
    assertEquals(-1, GoSdkUtil.compareVersions("1.1.2", "2.1.1"));
    assertEquals(-1, GoSdkUtil.compareVersions("1.2", "1.2.1"));
    assertEquals(-1, GoSdkUtil.compareVersions("1", "1.1"));
    assertEquals(-1, GoSdkUtil.compareVersions("1.4rc2", "1.4"));
    assertEquals(0, GoSdkUtil.compareVersions("1.2.3", "1.2.3"));
    assertEquals(0, GoSdkUtil.compareVersions("1.2", "1.2"));
    assertEquals(0, GoSdkUtil.compareVersions("1", "1"));
    assertEquals(0, GoSdkUtil.compareVersions("1.4rc2", "1.4rc2"));
    assertEquals(1, GoSdkUtil.compareVersions("1.2.4", "1.2.3"));
    assertEquals(1, GoSdkUtil.compareVersions("1.3.3", "1.2.4"));
    assertEquals(1, GoSdkUtil.compareVersions("2.2.3", "1.4.4"));
    assertEquals(1, GoSdkUtil.compareVersions("1.2.1", "1.2"));
    assertEquals(1, GoSdkUtil.compareVersions("1.4rc2", "1.3"));
  }

  @Test
  public void testGetSrcLocation() {
    assertEquals("src/pkg", GoSdkUtil.getSrcLocation("1.1.2"));
    assertEquals("src/pkg", GoSdkUtil.getSrcLocation("1.2.1"));
    assertEquals("src/pkg", GoSdkUtil.getSrcLocation("1.3"));
    assertEquals("src/pkg", GoSdkUtil.getSrcLocation("1.3.1"));
    assertEquals("src", GoSdkUtil.getSrcLocation("1.4"));
    assertEquals("src", GoSdkUtil.getSrcLocation("1.4.1"));
    assertEquals("src", GoSdkUtil.getSrcLocation("1.5"));
  }

  @Test
  public void testParseGoVersion() {
    assertEquals("1.4rc2", GoSdkUtil.parseGoVersion("const theVersion = `go1.4rc2`"));
    assertEquals("1.4.2", GoSdkUtil.parseGoVersion("const theVersion = `go1.4.2`"));
    assertEquals("1.4.1 (appengine-1.9.18)", GoSdkUtil.parseGoVersion("const theVersion = `go1.4.1 (appengine-1.9.18)`"));
    assertEquals("devel +e8057df Sun May 3 05:34:01 2015 +0000",
                 GoSdkUtil.parseGoVersion("const theVersion = `devel +e8057df Sun May 3 05:34:01 2015 +0000`"));
  }
}
