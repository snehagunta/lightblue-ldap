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
package com.redhat.lightblue.crud.ldap;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.redhat.lightblue.metadata.ArrayElement;
import com.redhat.lightblue.metadata.ArrayField;
import com.redhat.lightblue.metadata.EntityMetadata;
import com.redhat.lightblue.metadata.FieldTreeNode;
import com.redhat.lightblue.metadata.ObjectArrayElement;
import com.redhat.lightblue.metadata.ObjectField;
import com.redhat.lightblue.metadata.ReferenceField;
import com.redhat.lightblue.metadata.SimpleArrayElement;
import com.redhat.lightblue.metadata.SimpleField;
import com.redhat.lightblue.metadata.Type;
import com.redhat.lightblue.util.JsonDoc;
import com.redhat.lightblue.util.JsonNodeCursor;
import com.redhat.lightblue.util.Path;

/**
 * Defines a class that translates lightblue json nodes into
 * something that a specific datasource can understand.
 *
 * @author dcrissman
 *
 * @param <T> - A entity that the specific datastore knows how
 * to interact with.
 */
public abstract class TranslatorFromJson<T> {

    private final EntityMetadata md;

    public TranslatorFromJson(EntityMetadata md){
        this.md = md;
    }

    protected EntityMetadata getEntityMetadata(){
        return md;
    }

    protected Object fromJson(Type type, JsonNode node){
        if (node == null || node instanceof NullNode) {
            return null;
        }
        else {
            return type.fromJson(node);
        }
    }

    protected void translate(JsonDoc document, T target){
        JsonNodeCursor cursor = document.cursor();
        if (!cursor.firstChild()) {
            //TODO throw exception?
            return;
        }

        do {
            translate(cursor, target);
        } while (cursor.nextSibling());
    }

    private void translate(JsonNodeCursor cursor, T target){
        Path path = cursor.getCurrentPath();
        JsonNode node = cursor.getCurrentNode();
        FieldTreeNode fieldNode = md.resolve(path);

        if (fieldNode == null) {
            throw new NullPointerException("No Metadata field found for: " + path.toString());
        }

        if (fieldNode instanceof SimpleField) {
            translate((SimpleField) fieldNode, path, node, target);
        }
        else if (fieldNode instanceof ObjectField) {
            translate((ObjectField) fieldNode, path, node, target);
        }
        else if (fieldNode instanceof ArrayField) {
            translate((ArrayField) fieldNode, cursor, path, target);
        }
        else if (fieldNode instanceof ReferenceField) {
            translate((ReferenceField) fieldNode, path, node, target);
        }
        else{
            throw new UnsupportedOperationException("Field type is not supported: " + fieldNode.getClass().getName());
        }
    }

    private void translate(ArrayField field, JsonNodeCursor cursor, Path path, T target){
        if(!cursor.firstChild()){
            //TODO: throw exception?
            return;
        }

        ArrayElement arrayElement = field.getElement();

        if (arrayElement instanceof SimpleArrayElement) {
            List<Object> items = new ArrayList<Object>();
            do {
                items.add(fromJson(arrayElement.getType(), cursor.getCurrentNode()));
            } while (cursor.nextSibling());
            translateSimpleArray(field, path, items, target);
        }
        else if(arrayElement instanceof ObjectArrayElement){
            translateObjectArray(field, cursor, target);
        }
        else{
            throw new UnsupportedOperationException("ArrayElement type is not supported: " + arrayElement.getClass().getName());
        }

        cursor.parent();
    }

    protected void translate(ReferenceField field, Path path, JsonNode node, T target){
        //Do nothing by default!
    }

    protected abstract void translate(SimpleField field, Path path, JsonNode node, T target);
    protected abstract void translate(ObjectField field, Path path, JsonNode node, T target);
    protected abstract void translateSimpleArray(ArrayField field, Path path, List<Object> items, T target);
    protected abstract void translateObjectArray(ArrayField field, JsonNodeCursor cursor, T target);

}