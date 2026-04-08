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
package org.apache.fineract.portfolio.loanaccount.loanschedule.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import org.apache.fineract.portfolio.common.domain.PeriodFrequencyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultPaymentPeriodsInOneYearCalculatorTest {

    private static final MathContext MC = new MathContext(19, RoundingMode.HALF_EVEN);

    private DefaultPaymentPeriodsInOneYearCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new DefaultPaymentPeriodsInOneYearCalculator();
    }

    // --- calculate() tests ---

    @Test
    void calculate_Days_Returns365() {
        assertEquals(365, calculator.calculate(PeriodFrequencyType.DAYS));
    }

    @Test
    void calculate_Weeks_Returns52() {
        assertEquals(52, calculator.calculate(PeriodFrequencyType.WEEKS));
    }

    @Test
    void calculate_Months_Returns12() {
        assertEquals(12, calculator.calculate(PeriodFrequencyType.MONTHS));
    }

    @Test
    void calculate_Years_Returns1() {
        assertEquals(1, calculator.calculate(PeriodFrequencyType.YEARS));
    }

    @Test
    void calculate_Invalid_Returns0() {
        assertEquals(0, calculator.calculate(PeriodFrequencyType.INVALID));
    }

    @Test
    void calculate_WholeTerm_Returns0() {
        assertEquals(0, calculator.calculate(PeriodFrequencyType.WHOLE_TERM));
    }

    // --- calculatePortionOfRepaymentPeriodInterestChargingGrace() tests ---

    @Test
    void calculatePortion_NullInterestChargedFromDate_ReturnsZero() {
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 2, 1);

        BigDecimal result = calculator.calculatePortionOfRepaymentPeriodInterestChargingGrace(startDate, dueDate, null,
                PeriodFrequencyType.MONTHS, 1, MC);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void calculatePortion_InterestChargedBeforePeriodStart_ReturnsZero() {
        // Period: Jan 1 – Feb 1; interest already started Dec 1 (before the period starts)
        // No grace: the whole period already accrues interest
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 2, 1);
        LocalDate interestFrom = LocalDate.of(2023, 12, 1);

        BigDecimal result = calculator.calculatePortionOfRepaymentPeriodInterestChargingGrace(startDate, dueDate, interestFrom,
                PeriodFrequencyType.MONTHS, 1, MC);

        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void calculatePortion_PeriodEndsBeforeInterestStarts_ReturnsOne() {
        // Period: Jan 1 – Jan 10; interest starts Jan 15 (period ends before interest starts)
        // Full period is a grace period → fraction = 1
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 10);
        LocalDate interestFrom = LocalDate.of(2024, 1, 15);

        BigDecimal result = calculator.calculatePortionOfRepaymentPeriodInterestChargingGrace(startDate, dueDate, interestFrom,
                PeriodFrequencyType.MONTHS, 1, MC);

        assertEquals(0, BigDecimal.ONE.compareTo(result));
    }

    @Test
    void calculatePortion_InterestChargedAfterPeriodEnd_ReturnsOne() {
        // Period: Jan 1 – Feb 1; interest starts Mar 1 (period ends before interest starts)
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 2, 1);
        LocalDate interestFrom = LocalDate.of(2024, 3, 1);

        BigDecimal result = calculator.calculatePortionOfRepaymentPeriodInterestChargingGrace(startDate, dueDate, interestFrom,
                PeriodFrequencyType.MONTHS, 1, MC);

        assertEquals(0, BigDecimal.ONE.compareTo(result));
    }

    @Test
    void calculatePortion_InterestChargedAtPeriodStart_ReturnsZero() {
        // Period: Jan 1 – Feb 1; interest starts Jan 1 (start of period)
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 2, 1);
        LocalDate interestFrom = LocalDate.of(2024, 1, 1);

        BigDecimal result = calculator.calculatePortionOfRepaymentPeriodInterestChargingGrace(startDate, dueDate, interestFrom,
                PeriodFrequencyType.MONTHS, 1, MC);

        // interestFrom equals startDate: 0 days grace → fraction = 0/30 * 1 = 0
        assertEquals(0, BigDecimal.ZERO.compareTo(result));
    }

    @Test
    void calculatePortion_InterestChargedMidPeriod_MonthlyFrequency_ReturnsFraction() {
        // Monthly period Jan 1 – Feb 1; interest starts Jan 16 (15 days into period)
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 2, 1);
        LocalDate interestFrom = LocalDate.of(2024, 1, 16);

        BigDecimal result = calculator.calculatePortionOfRepaymentPeriodInterestChargingGrace(startDate, dueDate, interestFrom,
                PeriodFrequencyType.MONTHS, 1, MC);

        // 15 days grace / 30 days * 1 repayEvery = 0.5
        BigDecimal expected = BigDecimal.valueOf(15).divide(BigDecimal.valueOf(30), MC);
        assertEquals(0, expected.compareTo(result));
    }

    @Test
    void calculatePortion_InterestChargedMidPeriod_WeeklyFrequency_ReturnsFraction() {
        // Weekly period Jan 1 – Jan 8; interest starts Jan 4 (3 days into period)
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 8);
        LocalDate interestFrom = LocalDate.of(2024, 1, 4);

        BigDecimal result = calculator.calculatePortionOfRepaymentPeriodInterestChargingGrace(startDate, dueDate, interestFrom,
                PeriodFrequencyType.WEEKS, 1, MC);

        // 3 days grace / 7 days * 1 repayEvery
        BigDecimal expected = BigDecimal.valueOf(3).divide(BigDecimal.valueOf(7), MC);
        assertEquals(0, expected.compareTo(result));
    }

    @Test
    void calculatePortion_InterestChargedMidPeriod_DailyFrequency_ReturnsDaysGrace() {
        // Daily repayment; interest starts 5 days into period
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate dueDate = LocalDate.of(2024, 1, 31);
        LocalDate interestFrom = LocalDate.of(2024, 1, 6);

        BigDecimal result = calculator.calculatePortionOfRepaymentPeriodInterestChargingGrace(startDate, dueDate, interestFrom,
                PeriodFrequencyType.DAYS, 1, MC);

        // noDaysGrace * repayEvery = 5 * 1 = 5
        assertEquals(0, BigDecimal.valueOf(5).compareTo(result));
    }
}
