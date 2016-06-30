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
package org.optaplanner.workbench.screens.solver.client.editor;

import javax.inject.Inject;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import org.gwtbootstrap3.client.ui.Well;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.widgets.metadata.client.KieEditorViewImpl;
import org.optaplanner.workbench.screens.solver.model.PhaseConfigModel;
import org.optaplanner.workbench.screens.solver.model.ScoreDirectorFactoryConfigModel;
import org.optaplanner.workbench.screens.solver.model.TerminationConfigModel;
import org.uberfire.backend.vfs.Path;

import java.util.List;

public class SolverEditorViewImpl
        extends KieEditorViewImpl
        implements SolverEditorView {


    interface Binder
            extends
            UiBinder<Widget, SolverEditorViewImpl> {

    }

    private static Binder uiBinder = GWT.create( Binder.class );

    @UiField(provided = true)
    TerminationConfigForm terminationConfigForm;

    @UiField(provided = true)
    ScoreDirectorFactoryForm scoreDirectorFactoryForm;

    @UiField(provided = true)
    Well well;

    PhaseConfigForm phaseConfigForm;

    @Inject
    public SolverEditorViewImpl( final ScoreDirectorFactoryForm scoreDirectorFactoryForm,
                                 final TerminationConfigForm terminationConfigForm,
                                 final Well well,
                                 final PhaseConfigForm phaseConfigForm) {

        this.scoreDirectorFactoryForm = scoreDirectorFactoryForm;
        this.terminationConfigForm = terminationConfigForm;
        this.well = well;

        this.phaseConfigForm = phaseConfigForm;

        initWidget( uiBinder.createAndBindUi( this ) );

        well.add( ElementWrapperWidget.getWidget( phaseConfigForm.getElement() ));
    }

    @Override
    public void setTerminationConfigModel( TerminationConfigModel terminationConfigModel ) {
        terminationConfigForm.setModel( terminationConfigModel );
    }

    @Override
    public void setPhaseConfigModel( List<PhaseConfigModel> phaseConfigModel ) {
        phaseConfigForm.setModel( phaseConfigModel );
    }

    @Override
    public void setScoreDirectorFactoryConfig( final ScoreDirectorFactoryConfigModel scoreDirectorFactoryConfig,
                                               final Path path ) {
        scoreDirectorFactoryForm.setModel( scoreDirectorFactoryConfig,
                                           path );
    }

    @Override
    public void onResize() {
        int height = getParent().getOffsetHeight();
        setHeight( height + "px" );
    }
}
