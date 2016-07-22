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

package org.optaplanner.workbench.screens.solver.backend.server;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Charsets;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.guvnor.common.services.shared.message.Level;
import org.guvnor.common.services.shared.validation.model.ValidationMessage;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.services.backend.builder.LRUBuilderCache;
import org.kie.workbench.common.services.backend.validation.asset.AllKieProjectFilesFilter;
import org.kie.workbench.common.services.backend.validation.asset.NoProjectException;
import org.kie.workbench.common.services.backend.validation.asset.Validator;
import org.kie.workbench.common.services.backend.validation.asset.ValidatorFileSystemProvider;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;

public class SolverValidator {

    private static final long SMOKE_TEST_MILLISECONDS_SPENT_LIMIT = 3000;

    private static final Set<String> SMOKE_TEST_SUPPORTED_PROJECTS = new HashSet<>(  );

    private IOService ioService;

    private KieProjectService projectService;

    @Inject
    private LRUBuilderCache builderCache;

    static {
        SMOKE_TEST_SUPPORTED_PROJECTS.add( "optacloud" );
    }

    public SolverValidator() {
    }

    @Inject
    public SolverValidator( final @Named( "ioStrategy" ) IOService ioService,
                            final KieProjectService projectService ) {
        this.ioService = ioService;
        this.projectService = projectService;
    }

    public List<ValidationMessage> validate( final Path resourcePath,
                                             final String content ) {
        return validate( resourcePath, content, false );
    }

    public List<ValidationMessage> validateAndRun( final Path resourcePath,
                                                   final String content ) {
        return validate( resourcePath, content, true );
    }

    private List<ValidationMessage> validate( final Path resourcePath,
                                              final String content,
                                              final boolean runSolver ) {
        try {

            // Run validation with the changed resource ( == everything builds ).
            final KieProject kieProject = projectService.resolveProject( resourcePath );

            final Validator validator = getValidator( resourcePath,
                                                      content,
                                                      kieProject );

            final List<ValidationMessage> validationMessages = validator.validate();

            if ( validationMessages.isEmpty() ) {
                return buildSolver( resourcePath,
                                    kieProject,
                                    validator,
                                    runSolver );
            } else {
                return validationMessages;
            }
        } catch ( NoProjectException e ) {
            return new ArrayList<ValidationMessage>();
        }
    }

    private List<ValidationMessage> buildSolver( final Path resourcePath,
                                                 final KieProject kieProject,
                                                 final Validator validator,
                                                 final boolean runSolver ) {
        final List<ValidationMessage> validationMessages = new ArrayList<>();

        final ValidationMessage validationMessage = createSolverFactory( resourcePath,
                                                                         validator,
                                                                         kieProject,
                                                                         runSolver );
        if ( validationMessage != null ) {
            validationMessages.add( validationMessage );
        }

        return validationMessages;
    }

    private Validator getValidator( final Path resourcePath,
                                    final String content,
                                    final KieProject kieProject ) throws NoProjectException {
        return new Validator( new ValidatorFileSystemProvider( resourcePath,
                                                               new ByteArrayInputStream( content.getBytes( Charsets.UTF_8 ) ),
                                                               kieProject,
                                                               ioService,
                                                               new AllKieProjectFilesFilter() ) ) {
            @Override
            protected void addMessage( final String destinationBasePath,
                                       final Message message ) {
                validationMessages.add( convertMessage( message ) );
            }
        };
    }

    private ValidationMessage createSolverFactory( final Path resourcePath,
                                                   final Validator validator,
                                                   final KieProject kieWorkbenchProject,
                                                   final boolean runSolver ) {

        final org.drools.compiler.kie.builder.impl.KieProject kieProject = new KieModuleKieProject( ( InternalKieModule ) validator.getKieBuilder().getKieModule(), null );
        final KieContainer kieContainer = new KieContainerImpl( kieProject,
                                                                KieServices.Factory.get().getRepository() );

        try {

            final String solverConfigResource = getSolverConfigResource( resourcePath,
                                                                         kieWorkbenchProject );

            SolverFactory<Object> solverFactory = SolverFactory.createFromKieContainerXmlResource( kieContainer, solverConfigResource );

            if (runSolver) {
                String projectName = resolveProjectName( resourcePath );
                if ( !SMOKE_TEST_SUPPORTED_PROJECTS.contains( projectName ) ) {
                    throw new IllegalStateException( "Running a smoke test for project (" + projectName + ") is not supported." );
                }

                solverFactory.getSolverConfig().getTerminationConfig().shortenTimeMillisSpentLimit( SMOKE_TEST_MILLISECONDS_SPENT_LIMIT );
                if (solverFactory.getSolverConfig().getScoreDirectorFactoryConfig().getKsessionName() == null) {
                    solverFactory.getSolverConfig().getScoreDirectorFactoryConfig().setKsessionName( kieProject.getDefaultKieSession().getName() );
                }
                Solver<Object> solver = solverFactory.buildSolver();

                XStreamSolutionImporter solutionImporter = new XStreamSolutionImporter( kieContainer.getClassLoader() );
                Object solution = solutionImporter.read( getClass().getClassLoader().getResourceAsStream( "org/optaplanner/workbench/screens/solver/backend/server/solution/" + projectName + ".xml" ) );

                solver.solve( solution );
            } else {
                solverFactory.buildSolver();
            }

        } catch ( Exception e ) {
            e.printStackTrace();
            return make( e,
                         resourcePath );
        }

        return null;
    }

    private String resolveProjectName( final Path resourcePath ) {
        KieProject kieProject = projectService.resolveProject( resourcePath );
        if ( kieProject == null ) {
            throw new IllegalStateException( "Failed to resolve KieProject (" + kieProject + ").");
        }
        return kieProject.getProjectName();
    }

    private String getSolverConfigResource( final Path resourcePath,
                                            final KieProject kieWorkbenchProject ) {
        return resourcePath.toURI().substring( kieWorkbenchProject.getRootPath().toURI().length() + "/src/main/resources/".length() );
    }

    private ValidationMessage make( final Exception e,
                                    final Path resourcePath ) {
        ValidationMessage message = new ValidationMessage();

        message.setId( 0 );
        message.setLevel( Level.ERROR );
        message.setPath( resourcePath );
        message.setText( e.getMessage() );

        return message;
    }

}
