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
package com.redhat.lightblue.metadata.ldap.parser;

import java.util.List;

import com.redhat.lightblue.common.ldap.LdapConstant;
import com.redhat.lightblue.metadata.MetadataConstants;
import com.redhat.lightblue.metadata.ldap.model.FieldToAttribute;
import com.redhat.lightblue.metadata.ldap.model.LdapMetadataPropertyImpl;
import com.redhat.lightblue.metadata.parser.MetadataParser;
import com.redhat.lightblue.metadata.parser.PropertyParser;
import com.redhat.lightblue.util.Error;

/**
 * {@link PropertyParser} implementation for LDAP.
 *
 * @author dcrissman
 */
public class LdapPropertyParser <T> extends PropertyParser<T> {

    private static final String FIELDS_TO_ATTRIBUTES = "fieldsToAttributes";
    private static final String FIELD = "field";
    private static final String ATTRIBUTE = "attribute";

    @Override
    public com.redhat.lightblue.metadata.ldap.model.LdapMetadataPropertyImpl parse(String name, MetadataParser<T> p, T node) {
        if (!LdapConstant.BACKEND.equals(name)) {
            throw Error.get(MetadataConstants.ERR_ILL_FORMED_METADATA, name);
        }

        LdapMetadataPropertyImpl ldapMetadataPropertyImpl = new LdapMetadataPropertyImpl();

        List<T> fieldsToAttributesNode = p.getObjectList(node, FIELDS_TO_ATTRIBUTES);
        if(fieldsToAttributesNode != null){
            for(T fieldToAttributeNode : fieldsToAttributesNode){
                ldapMetadataPropertyImpl.addFieldToAttribute(new FieldToAttribute(
                        p.getRequiredStringProperty(fieldToAttributeNode, FIELD),
                        p.getRequiredStringProperty(fieldToAttributeNode, ATTRIBUTE)));
            }
        }

        return ldapMetadataPropertyImpl;
    }

    @Override
    public void convert(MetadataParser<T> p, T emptyNode, Object object) {
        if(!(object instanceof LdapMetadataPropertyImpl)){
            throw new IllegalArgumentException("Source type " + object.getClass() + " is not supported.");
        }

        LdapMetadataPropertyImpl ldapMetadataPropertyImpl = (LdapMetadataPropertyImpl) object;

        if(!ldapMetadataPropertyImpl.getFieldsToAttributes().isEmpty()){
            Object fieldsToAttributesNode = p.newArrayField(emptyNode, FIELDS_TO_ATTRIBUTES);

            for(FieldToAttribute fieldToAttribute : ldapMetadataPropertyImpl.getFieldsToAttributes()){
                T fieldToAttributeNode = p.newNode();
                p.putString(fieldToAttributeNode, FIELD, fieldToAttribute.getFieldName());
                p.putString(fieldToAttributeNode, ATTRIBUTE, fieldToAttribute.getAttributeName());

                p.addObjectToArray(fieldsToAttributesNode, fieldToAttributeNode);
            }
        }
    }

}
