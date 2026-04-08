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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoanTransactionComparatorTest {

    private static final LocalDate DATE_JAN = LocalDate.of(2024, 1, 15);
    private static final LocalDate DATE_FEB = LocalDate.of(2024, 2, 15);
    private static final OffsetDateTime OFFSET_JAN = OffsetDateTime.of(2024, 1, 15, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime OFFSET_FEB = OffsetDateTime.of(2024, 2, 15, 0, 0, 0, 0, ZoneOffset.UTC);

    private LoanTransactionComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = LoanTransactionComparator.INSTANCE;
    }

    private LoanTransaction transaction(LocalDate txDate, boolean accrualActivity, LocalDate submittedOnDate,
            Optional<OffsetDateTime> createdDate, boolean incomePosting, boolean waiver, Long id) {
        LoanTransaction t = mock(LoanTransaction.class);
        when(t.getTransactionDate()).thenReturn(txDate);
        when(t.isAccrualActivity()).thenReturn(accrualActivity);
        when(t.getSubmittedOnDate()).thenReturn(submittedOnDate);
        when(t.getCreatedDate()).thenReturn(createdDate);
        when(t.isIncomePosting()).thenReturn(incomePosting);
        when(t.isWaiver()).thenReturn(waiver);
        when(t.getId()).thenReturn(id);
        return t;
    }

    @Test
    void compare_EarlierTransactionDateSortsFirst() {
        LoanTransaction t1 = transaction(DATE_JAN, false, null, Optional.empty(), false, false, 1L);
        LoanTransaction t2 = transaction(DATE_FEB, false, null, Optional.empty(), false, false, 2L);

        assertTrue(comparator.compare(t1, t2) < 0, "Earlier date should sort first");
        assertTrue(comparator.compare(t2, t1) > 0, "Later date should sort last");
    }

    @Test
    void compare_SameTransactionDate_AccrualActivitySortsLast() {
        LoanTransaction normal = transaction(DATE_JAN, false, null, Optional.empty(), false, false, 1L);
        LoanTransaction accrual = transaction(DATE_JAN, true, null, Optional.empty(), false, false, 2L);

        assertTrue(comparator.compare(normal, accrual) < 0, "Normal transaction should precede accrual activity");
        assertTrue(comparator.compare(accrual, normal) > 0, "Accrual activity should sort after normal");
    }

    @Test
    void compare_SameDate_BothAccrualActivity_ComparesBySubmittedDate() {
        LoanTransaction t1 = transaction(DATE_JAN, true, DATE_JAN, Optional.empty(), false, false, 1L);
        LoanTransaction t2 = transaction(DATE_JAN, true, DATE_FEB, Optional.empty(), false, false, 2L);

        assertTrue(comparator.compare(t1, t2) < 0);
    }

    @Test
    void compare_SameDate_SameSubmittedDate_ComparesByCreatedDate() {
        LoanTransaction t1 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, 1L);
        LoanTransaction t2 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_FEB), false, false, 2L);

        assertTrue(comparator.compare(t1, t2) < 0);
    }

    @Test
    void compare_SameDates_IncomePostingSortsFirst() {
        LoanTransaction income = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), true, false, 1L);
        LoanTransaction regular = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, 2L);

        assertTrue(comparator.compare(income, regular) < 0, "Income posting should sort before regular");
        assertTrue(comparator.compare(regular, income) > 0, "Regular should sort after income posting");
    }

    @Test
    void compare_SameDates_WaiverSortsFirst() {
        LoanTransaction waiver = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, true, 1L);
        LoanTransaction regular = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, 2L);

        assertTrue(comparator.compare(waiver, regular) < 0, "Waiver should sort before regular");
        assertTrue(comparator.compare(regular, waiver) > 0, "Regular should sort after waiver");
    }

    @Test
    void compare_AllSameDates_SmallerIdSortsFirst() {
        LoanTransaction t1 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, 1L);
        LoanTransaction t2 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, 2L);

        assertTrue(comparator.compare(t1, t2) < 0, "Smaller id should sort first");
    }

    @Test
    void compare_FirstIdNull_SecondIdPresent_FirstSortsLast() {
        LoanTransaction t1 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, null);
        LoanTransaction t2 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, 1L);

        assertTrue(comparator.compare(t1, t2) > 0, "Null id should sort after non-null id");
    }

    @Test
    void compare_BothIdsNull_ReturnsZero() {
        LoanTransaction t1 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, null);
        LoanTransaction t2 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, null);

        assertEquals(0, comparator.compare(t1, t2));
    }

    @Test
    void compare_IdenticalTransactions_ReturnsZero() {
        LoanTransaction t1 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, 5L);
        LoanTransaction t2 = transaction(DATE_JAN, false, DATE_JAN, Optional.of(OFFSET_JAN), false, false, 5L);

        assertEquals(0, comparator.compare(t1, t2));
    }

    @Test
    void compare_TransactionDateTakesPriorityOverId() {
        // t1 has later date but smaller id: date wins, so t1 sorts after t2
        LoanTransaction t1 = transaction(DATE_FEB, false, null, Optional.empty(), false, false, 1L);
        LoanTransaction t2 = transaction(DATE_JAN, false, null, Optional.empty(), false, false, 2L);

        assertTrue(comparator.compare(t1, t2) > 0, "Transaction date should take priority over id");
    }

    @Test
    void compare_NullTransactionDates_TreatedAsNullsLast() {
        LoanTransaction t1 = transaction(null, false, null, Optional.empty(), false, false, 1L);
        LoanTransaction t2 = transaction(DATE_JAN, false, null, Optional.empty(), false, false, 2L);

        // Null date goes last
        assertTrue(comparator.compare(t1, t2) > 0, "Null transaction date should sort after non-null");
    }
}
