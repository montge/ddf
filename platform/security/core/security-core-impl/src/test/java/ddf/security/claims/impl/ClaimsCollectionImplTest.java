/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package ddf.security.claims.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;

import ddf.security.claims.Claim;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ClaimsCollectionImplTest {

  private ClaimsCollectionImpl claimsCollection;

  @BeforeEach
  public void setUp() {
    claimsCollection = new ClaimsCollectionImpl();
  }

  @Test
  public void testAddClaim() {
    Claim claim = mock(Claim.class);
    claimsCollection.add(claim);

    assertThat(claimsCollection, hasSize(1));
    assertThat(claimsCollection.get(0), is(claim));
  }

  @Test
  public void testAddMultipleClaims() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);
    Claim claim3 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);
    claimsCollection.add(claim3);

    assertThat(claimsCollection, hasSize(3));
    assertThat(claimsCollection, containsInAnyOrder(claim1, claim2, claim3));
  }

  @Test
  public void testRemoveClaim() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    assertThat(claimsCollection, hasSize(2));

    claimsCollection.remove(claim1);

    assertThat(claimsCollection, hasSize(1));
    assertThat(claimsCollection.get(0), is(claim2));
  }

  @Test
  public void testClearClaims() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    assertThat(claimsCollection, hasSize(2));

    claimsCollection.clear();

    assertThat(claimsCollection, hasSize(0));
  }

  @Test
  public void testContainsClaim() {
    Claim claim = mock(Claim.class);
    claimsCollection.add(claim);

    assertThat(claimsCollection.contains(claim), is(true));
  }

  @Test
  public void testDoesNotContainClaim() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);

    assertThat(claimsCollection.contains(claim2), is(false));
  }

  @Test
  public void testIsEmptyWhenEmpty() {
    assertThat(claimsCollection.isEmpty(), is(true));
  }

  @Test
  public void testIsEmptyWhenNotEmpty() {
    Claim claim = mock(Claim.class);
    claimsCollection.add(claim);

    assertThat(claimsCollection.isEmpty(), is(false));
  }

  @Test
  public void testSizeInitiallyZero() {
    assertThat(claimsCollection.size(), is(0));
  }

  @Test
  public void testSizeAfterAddingClaims() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    assertThat(claimsCollection.size(), is(2));
  }

  @Test
  public void testGetClaim() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    assertThat(claimsCollection.get(0), is(claim1));
    assertThat(claimsCollection.get(1), is(claim2));
  }

  @Test
  public void testSetClaim() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.set(0, claim2);

    assertThat(claimsCollection.get(0), is(claim2));
    assertThat(claimsCollection, hasSize(1));
  }

  @Test
  public void testIndexOfClaim() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    assertThat(claimsCollection.indexOf(claim1), is(0));
    assertThat(claimsCollection.indexOf(claim2), is(1));
  }

  @Test
  public void testIndexOfClaimNotInCollection() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);

    assertThat(claimsCollection.indexOf(claim2), is(-1));
  }

  @Test
  public void testAddAllClaims() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.addAll(Arrays.asList(claim1, claim2));

    assertThat(claimsCollection, hasSize(2));
    assertThat(claimsCollection, containsInAnyOrder(claim1, claim2));
  }

  @Test
  public void testRemoveAllClaims() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);
    Claim claim3 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);
    claimsCollection.add(claim3);

    claimsCollection.removeAll(Arrays.asList(claim1, claim2));

    assertThat(claimsCollection, hasSize(1));
    assertThat(claimsCollection.get(0), is(claim3));
  }

  @Test
  public void testRetainAllClaims() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);
    Claim claim3 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);
    claimsCollection.add(claim3);

    claimsCollection.retainAll(Arrays.asList(claim1, claim3));

    assertThat(claimsCollection, hasSize(2));
    assertThat(claimsCollection, containsInAnyOrder(claim1, claim3));
  }

  @Test
  public void testIterator() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    int count = 0;
    for (Claim claim : claimsCollection) {
      assertThat(claim, is(notNullValue()));
      count++;
    }

    assertThat(count, is(2));
  }

  @Test
  public void testToArray() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    Object[] array = claimsCollection.toArray();

    assertThat(array.length, is(2));
    assertThat(array[0], is(claim1));
    assertThat(array[1], is(claim2));
  }

  @Test
  public void testToArrayWithType() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    Claim[] array = claimsCollection.toArray(new Claim[0]);

    assertThat(array.length, is(2));
    assertThat(array[0], is(claim1));
    assertThat(array[1], is(claim2));
  }

  @Test
  public void testAddNullClaim() {
    claimsCollection.add(null);

    assertThat(claimsCollection, hasSize(1));
    assertThat(claimsCollection.get(0), is((Claim) null));
  }

  @Test
  public void testRemoveByIndex() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);
    Claim claim3 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);
    claimsCollection.add(claim3);

    Claim removed = claimsCollection.remove(1);

    assertThat(removed, is(claim2));
    assertThat(claimsCollection, hasSize(2));
    assertThat(claimsCollection, containsInAnyOrder(claim1, claim3));
  }

  @Test
  public void testContainsAllClaims() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    assertThat(claimsCollection.containsAll(Arrays.asList(claim1, claim2)), is(true));
  }

  @Test
  public void testDoesNotContainAllClaims() {
    Claim claim1 = mock(Claim.class);
    Claim claim2 = mock(Claim.class);
    Claim claim3 = mock(Claim.class);

    claimsCollection.add(claim1);
    claimsCollection.add(claim2);

    assertThat(claimsCollection.containsAll(Arrays.asList(claim1, claim3)), is(false));
  }

  @Test
  public void testInheritsFromArrayList() {
    assertThat(claimsCollection instanceof java.util.ArrayList, is(true));
  }

  @Test
  public void testImplementsClaimsCollection() {
    assertThat(claimsCollection instanceof ddf.security.claims.ClaimsCollection, is(true));
  }
}
