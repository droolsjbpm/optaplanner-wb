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
package org.optaplanner.workbench.screens.solver.service;

import java.util.List;

import org.guvnor.common.services.shared.file.SupportsUpdate;
import org.guvnor.common.services.shared.validation.ValidationService;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.services.shared.source.ViewSourceService;
import org.optaplanner.workbench.screens.solver.model.SolverConfigModel;
import org.optaplanner.workbench.screens.solver.model.SolverModelContent;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.service.support.SupportsCopy;
import org.uberfire.ext.editor.commons.service.support.SupportsCreate;
import org.uberfire.ext.editor.commons.service.support.SupportsDelete;
import org.uberfire.ext.editor.commons.service.support.SupportsRead;
import org.uberfire.ext.editor.commons.service.support.SupportsRename;

@Remote
public interface SolverEditorService
        extends
        ValidationService<SolverConfigModel>,
        ViewSourceService<SolverConfigModel>,
        SupportsCreate<SolverConfigModel>,
        SupportsRead<SolverConfigModel>,
        SupportsUpdate<SolverConfigModel>,
        SupportsDelete,
        SupportsCopy,
        SupportsRename {

    SolverModelContent loadContent(final Path path);

    List<ValidationMessage> smokeTest(final Path path,
                                      final SolverConfigModel config);
}
