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
import org.junit.jupiter.api.Test;

class LoanChargeEffectiveDueDateComparatorTest {

    private final LoanChargeEffectiveDueDateComparator comparator = LoanChargeEffectiveDueDateComparator.INSTANCE;

    @Test
    void compare_BothDatesNull_ReturnsZero() {
        LoanCharge c1 = mock(LoanCharge.class);
        LoanCharge c2 = mock(LoanCharge.class);
        when(c1.getEffectiveDueDate()).thenReturn(null);
        when(c2.getEffectiveDueDate()).thenReturn(null);

        assertEquals(0, comparator.compare(c1, c2));
    }

    @Test
    void compare_FirstDateNull_ReturnsPositive() {
        LoanCharge c1 = mock(LoanCharge.class);
        LoanCharge c2 = mock(LoanCharge.class);
        when(c1.getEffectiveDueDate()).thenReturn(null);
        when(c2.getEffectiveDueDate()).thenReturn(LocalDate.of(2024, 1, 15));

        assertTrue(comparator.compare(c1, c2) > 0, "Null first date should sort after non-null");
    }

    @Test
    void compare_SecondDateNull_ReturnsNegative() {
        LoanCharge c1 = mock(LoanCharge.class);
        LoanCharge c2 = mock(LoanCharge.class);
        when(c1.getEffectiveDueDate()).thenReturn(LocalDate.of(2024, 1, 15));
        when(c2.getEffectiveDueDate()).thenReturn(null);

        assertTrue(comparator.compare(c1, c2) < 0, "Non-null first date should sort before null");
    }

    @Test
    void compare_FirstDateBeforeSecond_ReturnsNegative() {
        LoanCharge c1 = mock(LoanCharge.class);
        LoanCharge c2 = mock(LoanCharge.class);
        when(c1.getEffectiveDueDate()).thenReturn(LocalDate.of(2024, 1, 1));
        when(c2.getEffectiveDueDate()).thenReturn(LocalDate.of(2024, 6, 1));

        assertTrue(comparator.compare(c1, c2) < 0);
    }

    @Test
    void compare_FirstDateAfterSecond_ReturnsPositive() {
        LoanCharge c1 = mock(LoanCharge.class);
        LoanCharge c2 = mock(LoanCharge.class);
        when(c1.getEffectiveDueDate()).thenReturn(LocalDate.of(2024, 6, 1));
        when(c2.getEffectiveDueDate()).thenReturn(LocalDate.of(2024, 1, 1));

        assertTrue(comparator.compare(c1, c2) > 0);
    }

    @Test
    void compare_EqualDates_ReturnsZero() {
        LoanCharge c1 = mock(LoanCharge.class);
        LoanCharge c2 = mock(LoanCharge.class);
        LocalDate date = LocalDate.of(2024, 3, 15);
        when(c1.getEffectiveDueDate()).thenReturn(date);
        when(c2.getEffectiveDueDate()).thenReturn(date);

        assertEquals(0, comparator.compare(c1, c2));
    }
}
