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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CommandParameterUtilTest {

    @Test
    void isReturnsTrueForExactMatch() {
        assertTrue(CommandParameterUtil.is("create", "create"));
    }

    @Test
    void isReturnsTrueForCaseInsensitiveMatch() {
        assertTrue(CommandParameterUtil.is("CREATE", "create"));
        assertTrue(CommandParameterUtil.is("create", "CREATE"));
        assertTrue(CommandParameterUtil.is("CrEaTe", "create"));
    }

    @Test
    void isReturnsTrueWhenCommandParamHasLeadingAndTrailingSpaces() {
        assertTrue(CommandParameterUtil.is("  create  ", "create"));
    }

    @Test
    void isReturnsFalseForDifferentValues() {
        assertFalse(CommandParameterUtil.is("update", "create"));
    }

    @Test
    void isReturnsFalseForNullCommandParam() {
        assertFalse(CommandParameterUtil.is(null, "create"));
    }

    @Test
    void isReturnsFalseForBlankCommandParam() {
        assertFalse(CommandParameterUtil.is("", "create"));
        assertFalse(CommandParameterUtil.is("   ", "create"));
    }

    @Test
    void isReturnsTrueForDeleteCommand() {
        assertTrue(CommandParameterUtil.is(CommandParameterUtil.DELETE_COMMAND_VALUE, "delete"));
    }

    @Test
    void isReturnsTrueForUpdateCommand() {
        assertTrue(CommandParameterUtil.is(CommandParameterUtil.UPDATE_COMMAND_VALUE, "update"));
    }

    @Test
    void isReturnsTrueForSaleCommand() {
        assertTrue(CommandParameterUtil.is(CommandParameterUtil.SALE_COMMAND_VALUE, "sale"));
    }

    @Test
    void isReturnsFalseForPartialMatch() {
        assertFalse(CommandParameterUtil.is("creat", "create"));
        assertFalse(CommandParameterUtil.is("creates", "create"));
    }
}
