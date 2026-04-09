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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.junit.jupiter.api.Test;

class DateUtilsTest {

    // ---- LocalDate compare ----

    @Test
    void compareLocalDateReturnsZeroForEqualDates() {
        LocalDate d = LocalDate.of(2024, 1, 15);
        assertEquals(0, DateUtils.compare(d, d));
    }

    @Test
    void compareLocalDateReturnsNegativeWhenFirstIsEarlier() {
        LocalDate first = LocalDate.of(2024, 1, 1);
        LocalDate second = LocalDate.of(2024, 6, 1);
        assertTrue(DateUtils.compare(first, second) < 0);
    }

    @Test
    void compareLocalDateReturnsPositiveWhenFirstIsLater() {
        LocalDate first = LocalDate.of(2025, 1, 1);
        LocalDate second = LocalDate.of(2024, 6, 1);
        assertTrue(DateUtils.compare(first, second) > 0);
    }

    @Test
    void compareLocalDateBothNullReturnsZero() {
        assertEquals(0, DateUtils.compare((LocalDate) null, null));
    }

    @Test
    void compareLocalDateNullFirstNullFirstTrueReturnsNegative() {
        LocalDate second = LocalDate.of(2024, 1, 1);
        assertTrue(DateUtils.compare(null, second, true) < 0);
    }

    @Test
    void compareLocalDateNullFirstNullFirstFalseReturnsPositive() {
        LocalDate second = LocalDate.of(2024, 1, 1);
        assertTrue(DateUtils.compare(null, second, false) > 0);
    }

    @Test
    void compareLocalDateNullSecondNullFirstTrueReturnsPositive() {
        LocalDate first = LocalDate.of(2024, 1, 1);
        assertTrue(DateUtils.compare(first, null, true) > 0);
    }

    @Test
    void compareLocalDateNullSecondNullFirstFalseReturnsNegative() {
        LocalDate first = LocalDate.of(2024, 1, 1);
        assertTrue(DateUtils.compare(first, null, false) < 0);
    }

    @Test
    void compareWithNullsLastLocalDatePutsNullAfterNonNull() {
        LocalDate d = LocalDate.of(2024, 1, 1);
        assertTrue(DateUtils.compareWithNullsLast(null, d) > 0);
        assertTrue(DateUtils.compareWithNullsLast(d, null) < 0);
    }

    // ---- LocalDate isEqual / isBefore / isAfter ----

    @Test
    void isEqualLocalDateReturnsTrueForSameDate() {
        LocalDate d = LocalDate.of(2024, 3, 10);
        assertTrue(DateUtils.isEqual(d, d));
    }

    @Test
    void isEqualLocalDateReturnsFalseForDifferentDates() {
        assertFalse(DateUtils.isEqual(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 2)));
    }

    @Test
    void isEqualLocalDateBothNullReturnsTrue() {
        assertTrue(DateUtils.isEqual((LocalDate) null, null));
    }

    @Test
    void isEqualLocalDateOneNullReturnsFalse() {
        assertFalse(DateUtils.isEqual(LocalDate.of(2024, 1, 1), null));
        assertFalse(DateUtils.isEqual(null, LocalDate.of(2024, 1, 1)));
    }

    @Test
    void isBeforeLocalDateReturnsTrueWhenFirstIsEarlier() {
        assertTrue(DateUtils.isBefore(LocalDate.of(2023, 1, 1), LocalDate.of(2024, 1, 1)));
    }

    @Test
    void isBeforeLocalDateReturnsFalseWhenFirstIsLaterOrEqual() {
        assertFalse(DateUtils.isBefore(LocalDate.of(2024, 1, 1), LocalDate.of(2023, 1, 1)));
        assertFalse(DateUtils.isBefore(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1)));
    }

    @Test
    void isBeforeLocalDateFirstNullReturnsTrue() {
        assertTrue(DateUtils.isBefore(null, LocalDate.of(2024, 1, 1)));
    }

    @Test
    void isBeforeLocalDateSecondNullReturnsFalse() {
        assertFalse(DateUtils.isBefore(LocalDate.of(2024, 1, 1), null));
    }

    @Test
    void isAfterLocalDateReturnsTrueWhenFirstIsLater() {
        assertTrue(DateUtils.isAfter(LocalDate.of(2025, 1, 1), LocalDate.of(2024, 1, 1)));
    }

    @Test
    void isAfterLocalDateReturnsFalseWhenFirstIsEarlierOrEqual() {
        assertFalse(DateUtils.isAfter(LocalDate.of(2024, 1, 1), LocalDate.of(2025, 1, 1)));
        assertFalse(DateUtils.isAfter(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 1)));
    }

    @Test
    void isAfterLocalDateFirstNullReturnsFalse() {
        assertFalse(DateUtils.isAfter(null, LocalDate.of(2024, 1, 1)));
    }

    @Test
    void isAfterLocalDateSecondNullReturnsTrue() {
        assertTrue(DateUtils.isAfter(LocalDate.of(2024, 1, 1), null));
    }

    @Test
    void isAfterInclusiveReturnsTrueWhenEqual() {
        LocalDate d = LocalDate.of(2024, 5, 1);
        assertTrue(DateUtils.isAfterInclusive(d, d));
    }

    @Test
    void isAfterInclusiveReturnsTrueWhenAfter() {
        assertTrue(DateUtils.isAfterInclusive(LocalDate.of(2024, 6, 1), LocalDate.of(2024, 5, 1)));
    }

    @Test
    void isAfterInclusiveReturnsFalseWhenBefore() {
        assertFalse(DateUtils.isAfterInclusive(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 5, 1)));
    }

    // ---- getDifference / getDifferenceInDays ----

    @Test
    void getDifferenceInDaysReturnsCorrectPositiveDifference() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 11);
        assertEquals(10L, DateUtils.getDifferenceInDays(from, to));
    }

    @Test
    void getDifferenceInDaysReturnsNegativeDifferenceWhenFromIsAfterTo() {
        LocalDate from = LocalDate.of(2024, 1, 11);
        LocalDate to = LocalDate.of(2024, 1, 1);
        assertEquals(-10L, DateUtils.getDifferenceInDays(from, to));
    }

    @Test
    void getDifferenceInDaysReturnsZeroForSameDate() {
        LocalDate d = LocalDate.of(2024, 3, 1);
        assertEquals(0L, DateUtils.getDifferenceInDays(d, d));
    }

    @Test
    void getDifferenceThrowsForNullDates() {
        assertThrows(IllegalArgumentException.class, () -> DateUtils.getDifference(null, LocalDate.of(2024, 1, 1), ChronoUnit.DAYS));
        assertThrows(IllegalArgumentException.class, () -> DateUtils.getDifference(LocalDate.of(2024, 1, 1), null, ChronoUnit.DAYS));
    }

    @Test
    void minusDateReturnsNullWhenInputIsNull() {
        assertNull(DateUtils.minusDays(null, 5));
    }

    @Test
    void minusDateSubtractsDaysCorrectly() {
        LocalDate base = LocalDate.of(2024, 3, 10);
        assertEquals(LocalDate.of(2024, 3, 5), DateUtils.minusDays(base, 5));
    }

    // ---- min ----

    @Test
    void minReturnsEarlierDate() {
        LocalDate earlier = LocalDate.of(2024, 1, 1);
        LocalDate later = LocalDate.of(2024, 6, 1);
        assertEquals(earlier, DateUtils.min(earlier, later));
        assertEquals(earlier, DateUtils.min(later, earlier));
    }

    @Test
    void minReturnsSameDateWhenEqual() {
        LocalDate d = LocalDate.of(2024, 3, 15);
        assertEquals(d, DateUtils.min(d, d));
    }

    // ---- parseLocalDate / format ----

    @Test
    void parseLocalDateReturnsNullForNullInput() {
        assertNull(DateUtils.parseLocalDate(null));
    }

    @Test
    void parseLocalDateParsesDefaultFormat() {
        LocalDate result = DateUtils.parseLocalDate("2024-05-20");
        assertEquals(LocalDate.of(2024, 5, 20), result);
    }

    @Test
    void parseLocalDateParsesCustomFormat() {
        LocalDate result = DateUtils.parseLocalDate("20/05/2024", "dd/MM/yyyy");
        assertEquals(LocalDate.of(2024, 5, 20), result);
    }

    @Test
    void parseLocalDateThrowsForInvalidFormat() {
        assertThrows(PlatformApiDataValidationException.class, () -> DateUtils.parseLocalDate("not-a-date"));
    }

    @Test
    void formatLocalDateReturnsNullForNullDate() {
        assertNull(DateUtils.format((LocalDate) null));
    }

    @Test
    void formatLocalDateReturnsDefaultFormat() {
        assertEquals("2024-05-20", DateUtils.format(LocalDate.of(2024, 5, 20)));
    }

    @Test
    void formatLocalDateReturnsCustomFormat() {
        assertEquals("20/05/2024", DateUtils.format(LocalDate.of(2024, 5, 20), "dd/MM/yyyy"));
    }

    @Test
    void formatLocalDateTimeReturnsNullForNullInput() {
        assertNull(DateUtils.format((LocalDateTime) null));
    }

    @Test
    void formatLocalDateTimeReturnsDefaultFormat() {
        LocalDateTime dt = LocalDateTime.of(2024, 5, 20, 10, 30, 0);
        assertEquals("2024-05-20 10:30:00", DateUtils.format(dt));
    }

    // ---- isDateInRange methods ----

    @Test
    void isDateWithinRangeReturnsTrueForDateOnBoundary() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        assertTrue(DateUtils.isDateWithinRange(from, from, to));
        assertTrue(DateUtils.isDateWithinRange(to, from, to));
    }

    @Test
    void isDateWithinRangeReturnsTrueForDateInsideRange() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        LocalDate target = LocalDate.of(2024, 6, 15);
        assertTrue(DateUtils.isDateWithinRange(target, from, to));
    }

    @Test
    void isDateWithinRangeReturnsFalseForDateOutsideRange() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        assertFalse(DateUtils.isDateWithinRange(LocalDate.of(2023, 12, 31), from, to));
        assertFalse(DateUtils.isDateWithinRange(LocalDate.of(2025, 1, 1), from, to));
    }

    @Test
    void isDateWithinRangeThrowsForNullDates() {
        assertThrows(IllegalArgumentException.class,
                () -> DateUtils.isDateWithinRange(null, LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
    }

    @Test
    void isDateInRangeInclusiveReturnsTrueOnBoundaries() {
        LocalDate from = LocalDate.of(2024, 3, 1);
        LocalDate to = LocalDate.of(2024, 3, 31);
        assertTrue(DateUtils.isDateInRangeInclusive(from, from, to));
        assertTrue(DateUtils.isDateInRangeInclusive(to, from, to));
    }

    @Test
    void isDateInRangeExclusiveReturnsFalseOnBoundaries() {
        LocalDate from = LocalDate.of(2024, 3, 1);
        LocalDate to = LocalDate.of(2024, 3, 31);
        assertFalse(DateUtils.isDateInRangeExclusive(from, from, to));
        assertFalse(DateUtils.isDateInRangeExclusive(to, from, to));
    }

    @Test
    void isDateInRangeExclusiveReturnsTrueForMiddleDate() {
        LocalDate from = LocalDate.of(2024, 3, 1);
        LocalDate to = LocalDate.of(2024, 3, 31);
        assertTrue(DateUtils.isDateInRangeExclusive(LocalDate.of(2024, 3, 15), from, to));
    }

    @Test
    void isDateInRangeFromExclusiveToInclusiveReturnsFalseOnFromAndTrueOnTo() {
        LocalDate from = LocalDate.of(2024, 3, 1);
        LocalDate to = LocalDate.of(2024, 3, 31);
        assertFalse(DateUtils.isDateInRangeFromExclusiveToInclusive(from, from, to));
        assertTrue(DateUtils.isDateInRangeFromExclusiveToInclusive(to, from, to));
    }

    @Test
    void isDateInRangeFromInclusiveToExclusiveReturnsTrueOnFromAndFalseOnTo() {
        LocalDate from = LocalDate.of(2024, 3, 1);
        LocalDate to = LocalDate.of(2024, 3, 31);
        // signature: (fromInclusive, upToNotInclusive, target)
        assertTrue(DateUtils.isDateInRangeFromInclusiveToExclusive(from, to, from));
        assertFalse(DateUtils.isDateInRangeFromInclusiveToExclusive(from, to, to));
    }

    // ---- LocalDateTime compare ----

    @Test
    void compareLocalDateTimeReturnsZeroForEqual() {
        LocalDateTime dt = LocalDateTime.of(2024, 5, 1, 12, 0);
        assertEquals(0, DateUtils.compare(dt, dt));
    }

    @Test
    void compareLocalDateTimeReturnsNegativeWhenFirstIsEarlier() {
        LocalDateTime first = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime second = LocalDateTime.of(2024, 6, 1, 0, 0);
        assertTrue(DateUtils.compare(first, second) < 0);
    }

    @Test
    void compareLocalDateTimeBothNullReturnsZero() {
        assertEquals(0, DateUtils.compare((LocalDateTime) null, null));
    }

    @Test
    void compareLocalDateTimeNullFirstReturnsNegative() {
        LocalDateTime second = LocalDateTime.of(2024, 1, 1, 0, 0);
        assertTrue(DateUtils.compare(null, second) < 0);
    }

    @Test
    void compareLocalDateTimeNullSecondReturnsPositive() {
        LocalDateTime first = LocalDateTime.of(2024, 1, 1, 0, 0);
        assertTrue(DateUtils.compare(first, null) > 0);
    }

    @Test
    void isEqualLocalDateTimeReturnsTrueForSame() {
        LocalDateTime dt = LocalDateTime.of(2024, 5, 1, 12, 0);
        assertTrue(DateUtils.isEqual(dt, dt));
    }

    @Test
    void isEqualLocalDateTimeWithTruncationIgnoresSeconds() {
        LocalDateTime dt1 = LocalDateTime.of(2024, 5, 1, 12, 0, 5);
        LocalDateTime dt2 = LocalDateTime.of(2024, 5, 1, 12, 0, 50);
        assertTrue(DateUtils.isEqual(dt1, dt2, ChronoUnit.MINUTES));
    }

    @Test
    void isBeforeLocalDateTimeReturnsTrueWhenFirst() {
        LocalDateTime first = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime second = LocalDateTime.of(2024, 6, 1, 0, 0);
        assertTrue(DateUtils.isBefore(first, second));
    }

    @Test
    void isAfterLocalDateTimeReturnsTrueWhenFirst() {
        LocalDateTime first = LocalDateTime.of(2024, 6, 1, 0, 0);
        LocalDateTime second = LocalDateTime.of(2024, 1, 1, 0, 0);
        assertTrue(DateUtils.isAfter(first, second));
    }

    // ---- OffsetDateTime compare ----

    @Test
    void compareOffsetDateTimeReturnsZeroForSameInstant() {
        OffsetDateTime dt1 = OffsetDateTime.of(2024, 5, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime dt2 = OffsetDateTime.of(2024, 5, 1, 14, 0, 0, 0, ZoneOffset.ofHours(2));
        assertEquals(0, DateUtils.compare(dt1, dt2));
    }

    @Test
    void compareOffsetDateTimeReturnsNegativeForEarlierFirst() {
        OffsetDateTime first = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime second = OffsetDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertTrue(DateUtils.compare(first, second) < 0);
    }

    @Test
    void compareOffsetDateTimeBothNullReturnsZero() {
        assertEquals(0, DateUtils.compare((OffsetDateTime) null, null));
    }

    @Test
    void compareOffsetDateTimeNullFirstReturnsNegativeByDefault() {
        OffsetDateTime second = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertTrue(DateUtils.compare(null, second) < 0);
    }

    @Test
    void compareWithNullsLastOffsetDateTimePutsNullAfterNonNull() {
        OffsetDateTime d = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertTrue(DateUtils.compareWithNullsLast(null, d) > 0);
        assertTrue(DateUtils.compareWithNullsLast(d, null) < 0);
    }

    @Test
    void compareWithNullsLastOptionalOffsetDateTimeNullAfterNonNull() {
        OffsetDateTime d = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertTrue(DateUtils.compareWithNullsLast(Optional.empty(), Optional.of(d)) > 0);
        assertTrue(DateUtils.compareWithNullsLast(Optional.of(d), Optional.empty()) < 0);
    }

    @Test
    void isEqualOffsetDateTimeReturnsTrueForSameInstant() {
        OffsetDateTime dt1 = OffsetDateTime.of(2024, 5, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime dt2 = OffsetDateTime.of(2024, 5, 1, 14, 0, 0, 0, ZoneOffset.ofHours(2));
        assertTrue(DateUtils.isEqual(dt1, dt2));
    }

    @Test
    void isBeforeOffsetDateTimeReturnsTrueWhenFirst() {
        OffsetDateTime first = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime second = OffsetDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertTrue(DateUtils.isBefore(first, second));
    }

    @Test
    void isAfterOffsetDateTimeReturnsTrueWhenFirst() {
        OffsetDateTime first = OffsetDateTime.of(2024, 6, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime second = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertTrue(DateUtils.isAfter(first, second));
    }
}
