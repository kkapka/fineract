/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.loanaccount.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class LoanChargeEffectiveDueDateComparatorTest {

    private static final LoanChargeEffectiveDueDateComparator COMPARATOR = LoanChargeEffectiveDueDateComparator.INSTANCE;

    private LoanCharge chargeWithDueDate(LocalDate dueDate) {
        LoanCharge charge = mock(LoanCharge.class);
        when(charge.getEffectiveDueDate()).thenReturn(dueDate);
        return charge;
    }

    @Test
    void compareReturnZeroWhenBothDueDatesAreNull() {
        LoanCharge c1 = chargeWithDueDate(null);
        LoanCharge c2 = chargeWithDueDate(null);
        assertEquals(0, COMPARATOR.compare(c1, c2));
    }

    @Test
    void compareReturnsPositiveWhenFirstDueDateIsNull() {
        LoanCharge nullCharge = chargeWithDueDate(null);
        LoanCharge nonNullCharge = chargeWithDueDate(LocalDate.of(2024, 6, 1));
        assertTrue(COMPARATOR.compare(nullCharge, nonNullCharge) > 0);
    }

    @Test
    void compareReturnsNegativeWhenSecondDueDateIsNull() {
        LoanCharge nonNullCharge = chargeWithDueDate(LocalDate.of(2024, 6, 1));
        LoanCharge nullCharge = chargeWithDueDate(null);
        assertTrue(COMPARATOR.compare(nonNullCharge, nullCharge) < 0);
    }

    @Test
    void compareReturnsZeroForEqualDueDates() {
        LocalDate date = LocalDate.of(2024, 6, 1);
        LoanCharge c1 = chargeWithDueDate(date);
        LoanCharge c2 = chargeWithDueDate(date);
        assertEquals(0, COMPARATOR.compare(c1, c2));
    }

    @Test
    void compareReturnsNegativeWhenFirstDueDateIsEarlier() {
        LoanCharge earlier = chargeWithDueDate(LocalDate.of(2024, 1, 1));
        LoanCharge later = chargeWithDueDate(LocalDate.of(2024, 6, 1));
        assertTrue(COMPARATOR.compare(earlier, later) < 0);
    }

    @Test
    void compareReturnsPositiveWhenFirstDueDateIsLater() {
        LoanCharge later = chargeWithDueDate(LocalDate.of(2024, 12, 1));
        LoanCharge earlier = chargeWithDueDate(LocalDate.of(2024, 6, 1));
        assertTrue(COMPARATOR.compare(later, earlier) > 0);
    }

    @Test
    void sortingChargesPlacesNullDueDatesLast() {
        LoanCharge noDate = chargeWithDueDate(null);
        LoanCharge early = chargeWithDueDate(LocalDate.of(2024, 1, 1));
        LoanCharge late = chargeWithDueDate(LocalDate.of(2024, 12, 31));

        List<LoanCharge> charges = Arrays.asList(noDate, late, early);
        charges.sort(COMPARATOR);

        assertEquals(early.getEffectiveDueDate(), charges.get(0).getEffectiveDueDate());
        assertEquals(late.getEffectiveDueDate(), charges.get(1).getEffectiveDueDate());
        assertEquals(null, charges.get(2).getEffectiveDueDate());
    }
}
