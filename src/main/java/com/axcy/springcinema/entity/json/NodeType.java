package com.axcy.springcinema.entity.json;

/**
 * @author Aleksei_Cherniavskii
 */
public class NodeType {
    public static final String NODE_NAME = "type";
    private TypeField typeField;

    public NodeType() {}

    public NodeType(TypeField typeField) {
        this.typeField = typeField;
    }

    public TypeField getTypeField() {
        return typeField;
    }

    public void setTypeField(TypeField typeField) {
        this.typeField = typeField;
    }
}
