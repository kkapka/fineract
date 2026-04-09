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
package org.apache.fineract.infrastructure.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringUtilTest {

    @Test
    void maskValueWithDefaultLengthShowsFirstAndLastFourCharacters() {
        // "ABCDEFGHIJ" -> "A*****GHIJ"
        assertEquals("A*****GHIJ", StringUtil.maskValue("ABCDEFGHIJ"));
    }

    @Test
    void maskValueWithDefaultLengthReturnsAsterisksWhenValueTooShort() {
        assertEquals("****", StringUtil.maskValue("AB"));
    }

    @Test
    void maskValueWithDefaultLengthReturnsAsterisksForExactlyFourCharacters() {
        assertEquals("****", StringUtil.maskValue("ABCD"));
    }

    @Test
    void maskValueWithDefaultLengthHandlesFiveCharacters() {
        // Only one character is "not unmasked", prefix is 1 char, suffix is last 4 chars
        // value.length() (5) > unmaskedLength (4), so: "A" + "*".repeat(0) + "BCDE" = "ABCDE"
        assertEquals("ABCDE", StringUtil.maskValue("ABCDE"));
    }

    @Test
    void maskValueWithCustomUnmaskedLengthMasksCorrectly() {
        // "ABCDEFGH" (length=8) with unmaskedLength=2 -> prefix(1) + stars(8-1-2=5) + suffix(2) = "A*****GH"
        assertEquals("A*****GH", StringUtil.maskValue("ABCDEFGH", 2));
    }

    @Test
    void maskValueWithCustomUnmaskedLengthReturnsAsterisksWhenValueTooShort() {
        assertEquals("****", StringUtil.maskValue("AB", 3));
    }

    @Test
    void maskValueWithZeroUnmaskedLengthReturnsSinglePrefixAndNoSuffix() {
        // "ABCDE" with unmaskedLength=0 -> "A****" (prefix 1 char, no suffix)
        assertEquals("A****", StringUtil.maskValue("ABCDE", 0));
    }

    @Test
    void maskValueWithLargeUnmaskedLengthShowsAllCharacters() {
        // unmaskedLength equals value.length(), so value.length() <= unmaskedLength -> "****"
        assertEquals("****", StringUtil.maskValue("ABCD", 4));
    }

    @Test
    void maskValuePreservesLastFourCharsCorrectly() {
        String value = "1234567890";
        String masked = StringUtil.maskValue(value);
        // last 4 chars should be "7890"
        assertEquals("1*****7890", masked);
    }
}
