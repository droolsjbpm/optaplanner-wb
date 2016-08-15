/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.workbench.screens.solver.client.handlers;

import java.util.HashSet;

import com.google.gwt.core.client.GWT;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.security.shared.api.Role;
import org.jboss.errai.security.shared.api.RoleImpl;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.optaplanner.workbench.screens.solver.client.type.SolverResourceType;
import org.optaplanner.workbench.screens.solver.service.SolverEditorService;
import org.uberfire.ext.widgets.common.client.common.BusyIndicatorView;
import org.uberfire.mocks.CallerMock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class )
public class NewSolverHandlerTest {


    @Mock
    private SolverEditorService solverService;

    @Mock
    private BusyIndicatorView busyIndicatorView;

    @Mock
    private User user;

    private NewSolverHandler newSolverHandler;

    private SolverResourceType resourceType;

    @Before
    public void setUp() throws Exception {
        newSolverHandler = new NewSolverHandler( new CallerMock<SolverEditorService>( solverService ),
                                                 resourceType,
                                                 busyIndicatorView,
                                                 user );
        resourceType = GWT.create( SolverResourceType.class );
    }

    @Test
    public void testNoPermissionToCreate() throws Exception {
        assertFalse( newSolverHandler.canCreate() );
    }

    @Test
    public void testHasPermissionToCreate() throws Exception {
        final HashSet<Role> roles = new HashSet<Role>();
        roles.add( new RoleImpl( "plannermgmt" ) );
        when( user.getRoles() ).thenReturn( roles );

        assertTrue( newSolverHandler.canCreate() );
    }
}