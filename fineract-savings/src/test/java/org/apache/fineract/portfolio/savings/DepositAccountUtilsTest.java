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
package org.apache.fineract.portfolio.savings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.junit.jupiter.api.Test;

class DepositAccountUtilsTest {

    private static final LocalDate BASE_DATE = LocalDate.of(2024, 1, 15);

    @Test
    void calculateNextDepositDateAddsDaysForDailyFrequency() {
        LocalDate result = DepositAccountUtils.calculateNextDepositDate(BASE_DATE, PeriodFrequencyType.DAYS, 10);
        assertEquals(BASE_DATE.plusDays(10), result);
    }

    @Test
    void calculateNextDepositDateAddsWeeksForWeeklyFrequency() {
        LocalDate result = DepositAccountUtils.calculateNextDepositDate(BASE_DATE, PeriodFrequencyType.WEEKS, 2);
        assertEquals(BASE_DATE.plusWeeks(2), result);
    }

    @Test
    void calculateNextDepositDateAddsMonthsForMonthlyFrequency() {
        LocalDate result = DepositAccountUtils.calculateNextDepositDate(BASE_DATE, PeriodFrequencyType.MONTHS, 1);
        assertEquals(BASE_DATE.plusMonths(1), result);
    }

    @Test
    void calculateNextDepositDateAddsYearsForYearlyFrequency() {
        LocalDate result = DepositAccountUtils.calculateNextDepositDate(BASE_DATE, PeriodFrequencyType.YEARS, 1);
        assertEquals(BASE_DATE.plusYears(1), result);
    }

    @Test
    void calculateNextDepositDateReturnsBaseDateForInvalidFrequency() {
        LocalDate result = DepositAccountUtils.calculateNextDepositDate(BASE_DATE, PeriodFrequencyType.INVALID, 5);
        assertEquals(BASE_DATE, result);
    }

    @Test
    void calculateNextDepositDateReturnsBaseDateForWholeTermFrequency() {
        LocalDate result = DepositAccountUtils.calculateNextDepositDate(BASE_DATE, PeriodFrequencyType.WHOLE_TERM, 1);
        assertEquals(BASE_DATE, result);
    }

    @Test
    void calculateNextDepositDateWithRecurringEveryMultiple() {
        LocalDate result = DepositAccountUtils.calculateNextDepositDate(BASE_DATE, PeriodFrequencyType.MONTHS, 6);
        assertEquals(BASE_DATE.plusMonths(6), result);
    }

    @Test
    void calculateNextDepositDateHandlesEndOfMonthForMonthlyFrequency() {
        LocalDate endOfJan = LocalDate.of(2024, 1, 31);
        LocalDate result = DepositAccountUtils.calculateNextDepositDate(endOfJan, PeriodFrequencyType.MONTHS, 1);
        // Feb 29 in 2024 (leap year)
        assertEquals(LocalDate.of(2024, 2, 29), result);
    }
}
