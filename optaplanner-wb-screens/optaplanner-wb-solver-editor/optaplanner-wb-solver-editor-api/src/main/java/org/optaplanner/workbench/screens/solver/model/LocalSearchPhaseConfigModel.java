/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.core.config.localsearch.LocalSearchType;

@Portable
public class LocalSearchPhaseConfigModel extends PhaseConfigModel {

    private LocalSearchType localSearchType;

    public LocalSearchType getLocalSearchType() {
        return localSearchType;
    }

    public void setLocalSearchType(LocalSearchType localSearchType) {
        this.localSearchType = localSearchType;
    }
}
