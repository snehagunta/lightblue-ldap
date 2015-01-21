/*
 Copyright 2015 Red Hat, Inc. and/or its affiliates.

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
package com.redhat.lightblue.metadata.ldap.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.junit.Test;

public class LdapMetadataPropertyImplTest {

    @Test
    public void testGetAttributeNameForFieldName(){
        String fieldName = "fakeFieldName";
        String attributeName = "fakeAttributeName";

        LdapMetadataPropertyImpl property = new LdapMetadataPropertyImpl();
        property.addFieldToAttribute(new FieldToAttribute(fieldName, attributeName));
        property.addFieldToAttribute(new FieldToAttribute("anotherField", "anotherAttribute"));

        assertEquals(attributeName, property.getAttributeNameForFieldName(fieldName));
    }

    @Test
    public void testGetAttributeNameForFieldName_ValueNotPresent(){
        assertNull(new LdapMetadataPropertyImpl().getAttributeNameForFieldName("fake"));
    }

    @Test
    public void testGetFieldNameForAttributeName(){
        String fieldName = "fakeFieldName";
        String attributeName = "fakeAttributeName";

        LdapMetadataPropertyImpl property = new LdapMetadataPropertyImpl();
        property.addFieldToAttribute(new FieldToAttribute(fieldName, attributeName));
        property.addFieldToAttribute(new FieldToAttribute("anotherField", "anotherAttribute"));

        assertEquals(fieldName, property.getFieldNameForAttributeName(attributeName));
    }

    @Test
    public void testGetFieldNameForAttributeName_ValueNotPresent(){
        assertNull(new LdapMetadataPropertyImpl().getFieldNameForAttributeName("fake"));
    }

    @Test
    public void testGetFieldsToAttributes_AssertImmutable(){
        LdapMetadataPropertyImpl property = new LdapMetadataPropertyImpl();
        property.addFieldToAttribute(new FieldToAttribute("anotherField", "anotherAttribute"));

        Set<FieldToAttribute> fieldsToAttributes = property.getFieldsToAttributes();
        assertNotNull(fieldsToAttributes);
        assertNotSame(fieldsToAttributes, property.getFieldsToAttributes());
    }

}
