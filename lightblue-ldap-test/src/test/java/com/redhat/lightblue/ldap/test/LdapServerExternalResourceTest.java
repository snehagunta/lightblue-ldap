/*
 Copyright 2014 Red Hat, Inc. and/or its affiliates.

 This file is part of lightblue.

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.redhat.lightblue.ldap.test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.redhat.lightblue.ldap.test.LdapServerExternalResource.InMemoryLdapServer;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;

public class LdapServerExternalResourceTest {

    public static class SimpleTests{

        @Test(expected = IllegalStateException.class)
        public void testApply_WithoutAnnotation_onTestLevel(){
            new LdapServerExternalResource().apply(mock(Statement.class), mock(Description.class));
        }

        @Test(expected = IllegalStateException.class)
        public void testApply_WithoutAnnotation_onClassLevel(){
            new LdapServerExternalResource().apply(
                    mock(Statement.class),
                    Description.createSuiteDescription(Object.class));
        }

    }

    @InMemoryLdapServer
    public static class AnnotationOnClassRule{

        @ClassRule
        public static final LdapServerExternalResource ldapServer =  LdapServerExternalResource.createDefaultInstance();

        @Test
        public void testConnection() throws LDAPException{
            LDAPConnection conn = new LDAPConnection("localhost", LdapServerExternalResource.DEFAULT_PORT);
            assertNotNull(conn.getEntry("dc=example,dc=com"));
        }

    }

    @InMemoryLdapServer
    public static class AnnotationOnRule{

        @Rule
        public LdapServerExternalResource ldapServer =  LdapServerExternalResource.createDefaultInstance();

        @Test
        public void testConnection() throws LDAPException{
            LDAPConnection conn = new LDAPConnection("localhost", LdapServerExternalResource.DEFAULT_PORT);
            assertNotNull(conn.getEntry("dc=example,dc=com"));
        }

    }

}
