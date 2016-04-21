/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.optaplanner.workbench.screens.solver.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum ScoreDefinitionTypeModel {
    SIMPLE,
    SIMPLE_LONG,
    SIMPLE_DOUBLE,
    SIMPLE_BIG_DECIMAL,
    HARD_SOFT,
    HARD_SOFT_LONG,
    HARD_SOFT_DOUBLE,
    HARD_SOFT_BIG_DECIMAL,
    HARD_MEDIUM_SOFT,
    HARD_MEDIUM_SOFT_LONG;

}
