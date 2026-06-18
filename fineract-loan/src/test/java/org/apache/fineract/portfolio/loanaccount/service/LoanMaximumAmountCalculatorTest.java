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
package org.apache.fineract.portfolio.loanaccount.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanproduct.domain.LoanProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoanMaximumAmountCalculatorTest {

    private LoanMaximumAmountCalculator calculator;
    private Loan loan;
    private LoanProduct loanProduct;

    @BeforeEach
    void setUp() {
        calculator = new LoanMaximumAmountCalculator();
        loan = mock(Loan.class);
        loanProduct = mock(LoanProduct.class);
        when(loan.getLoanProduct()).thenReturn(loanProduct);
    }

    @Test
    void getOverAppliedMax_PercentageType_ReturnsProposedPrincipalMultipliedByFactor() {
        when(loanProduct.getOverAppliedCalculationType()).thenReturn("percentage");
        when(loanProduct.getOverAppliedNumber()).thenReturn(20); // 20%
        when(loan.getProposedPrincipal()).thenReturn(BigDecimal.valueOf(1000));

        BigDecimal result = calculator.getOverAppliedMax(loan);

        // 1000 * (1 + 20/100) = 1000 * 1.20 = 1200
        assertEquals(0, BigDecimal.valueOf(1200).compareTo(result));
    }

    @Test
    void getOverAppliedMax_PercentageTypeUpperCase_ReturnsCorrectResult() {
        when(loanProduct.getOverAppliedCalculationType()).thenReturn("PERCENTAGE");
        when(loanProduct.getOverAppliedNumber()).thenReturn(50); // 50%
        when(loan.getProposedPrincipal()).thenReturn(BigDecimal.valueOf(2000));

        BigDecimal result = calculator.getOverAppliedMax(loan);

        // 2000 * (1 + 50/100) = 2000 * 1.50 = 3000
        assertEquals(0, BigDecimal.valueOf(3000).compareTo(result));
    }

    @Test
    void getOverAppliedMax_FlatType_ReturnsProposedPrincipalPlusFixedAmount() {
        when(loanProduct.getOverAppliedCalculationType()).thenReturn("flat");
        when(loanProduct.getOverAppliedNumber()).thenReturn(500);
        when(loan.getProposedPrincipal()).thenReturn(BigDecimal.valueOf(1000));

        BigDecimal result = calculator.getOverAppliedMax(loan);

        // 1000 + 500 = 1500
        assertEquals(0, BigDecimal.valueOf(1500).compareTo(result));
    }

    @Test
    void getOverAppliedMax_FlatTypeUpperCase_ReturnsCorrectResult() {
        when(loanProduct.getOverAppliedCalculationType()).thenReturn("FLAT");
        when(loanProduct.getOverAppliedNumber()).thenReturn(200);
        when(loan.getProposedPrincipal()).thenReturn(BigDecimal.valueOf(5000));

        BigDecimal result = calculator.getOverAppliedMax(loan);

        // 5000 + 200 = 5200
        assertEquals(0, BigDecimal.valueOf(5200).compareTo(result));
    }

    @Test
    void getOverAppliedMax_ZeroPercentage_ReturnsProposedPrincipalUnchanged() {
        when(loanProduct.getOverAppliedCalculationType()).thenReturn("percentage");
        when(loanProduct.getOverAppliedNumber()).thenReturn(0);
        when(loan.getProposedPrincipal()).thenReturn(BigDecimal.valueOf(1000));

        BigDecimal result = calculator.getOverAppliedMax(loan);

        // 1000 * (1 + 0/100) = 1000 * 1 = 1000
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(result));
    }

    @Test
    void getOverAppliedMax_ZeroFlatAmount_ReturnsProposedPrincipalUnchanged() {
        when(loanProduct.getOverAppliedCalculationType()).thenReturn("flat");
        when(loanProduct.getOverAppliedNumber()).thenReturn(0);
        when(loan.getProposedPrincipal()).thenReturn(BigDecimal.valueOf(1000));

        BigDecimal result = calculator.getOverAppliedMax(loan);

        // 1000 + 0 = 1000
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(result));
    }

    @Test
    void getOverAppliedMax_HundredPercentage_DoublesPrincipal() {
        when(loanProduct.getOverAppliedCalculationType()).thenReturn("percentage");
        when(loanProduct.getOverAppliedNumber()).thenReturn(100);
        when(loan.getProposedPrincipal()).thenReturn(BigDecimal.valueOf(1000));

        BigDecimal result = calculator.getOverAppliedMax(loan);

        // 1000 * (1 + 100/100) = 1000 * 2 = 2000
        assertEquals(0, BigDecimal.valueOf(2000).compareTo(result));
    }
}
