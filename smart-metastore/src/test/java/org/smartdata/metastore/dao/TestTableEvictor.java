/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.smartdata.metastore.dao;

import org.junit.Assert;
import org.junit.Test;
import org.smartdata.metastore.MetaStore;

import static org.mockito.Mockito.mock;

public class TestTableEvictor {
  MetaStore adapter = mock(MetaStore.class);

  @Test
  public void testCountEvictor() {
    TableEvictor evictor = new CountEvictor(adapter, 3);
    AccessCountTableDeque deque = new AccessCountTableDeque(evictor);
    AccessCountTable first = new AccessCountTable(0L, 1L);
    deque.addAndNotifyListener(first);
    Assert.assertTrue(deque.size() == 1);
    AccessCountTable second = new AccessCountTable(1L, 2L);
    deque.addAndNotifyListener(second);
    Assert.assertTrue(deque.size() == 2);
    deque.addAndNotifyListener(new AccessCountTable(2L, 3L));
    Assert.assertTrue(deque.size() == 3);
    deque.addAndNotifyListener(new AccessCountTable(3L, 4L));
    Assert.assertTrue(deque.size() == 3);
    Assert.assertTrue(!deque.contains(first));
    deque.addAndNotifyListener(new AccessCountTable(4L, 5L));
    Assert.assertTrue(deque.size() == 3);
    Assert.assertTrue(!deque.contains(second));
  }

  @Test
  public void testDurationEvictor() {
    TableEvictor evictor = new DurationEvictor(adapter, 10);
    AccessCountTableDeque deque = new AccessCountTableDeque(evictor);
    AccessCountTable first = new AccessCountTable(0L, 3L);
    deque.addAndNotifyListener(first);
    Assert.assertTrue(deque.size() == 1);
    AccessCountTable second = new AccessCountTable(3L, 7L);
    deque.addAndNotifyListener(second);
    Assert.assertTrue(deque.size() == 2);
    deque.addAndNotifyListener(new AccessCountTable(7L, 10L));
    Assert.assertTrue(deque.size() == 3);
    deque.addAndNotifyListener(new AccessCountTable(11L, 12L));
    Assert.assertTrue(deque.size() == 3);
    Assert.assertTrue(!deque.contains(first));
    deque.addAndNotifyListener(new AccessCountTable(12L, 13L));
    Assert.assertTrue(deque.size() == 4);
    Assert.assertTrue(deque.contains(second));
    deque.addAndNotifyListener(new AccessCountTable(13L, 22L));
    Assert.assertTrue(deque.size() == 2);
    Assert.assertTrue(!deque.contains(second));
  }
}
