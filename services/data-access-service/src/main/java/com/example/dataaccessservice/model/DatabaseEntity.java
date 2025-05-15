package com.example.dataaccessservice.model;

import java.util.Map;

/**
 * Database Entity Model
 * 
 * This class represents a generic database entity in the system and is used for:
 * - Abstracting database operations
 * - Representing data from any table
 * - Providing a common interface for database entities
 * 
 * In our microservices architecture:
 * - This model is used by the Data Access Service for database operations
 * - It provides a generic representation of any database entity
 * - It's used for both input and output of database operations
 * - It allows for flexible handling of different entity types
 */
public class DatabaseEntity {
    /**
     * The name of the table this entity belongs to
     */
    private String tableName;
    
    /**
     * The primary key field name for the entity
     */
    private String primaryKeyField;
    
    /**
     * The primary key value for the entity
     */
    private Object primaryKeyValue;
    
    /**
     * The data fields and values for the entity
     */
    private Map<String, Object> data;

    /**
     * Default constructor
     * Required for object creation
     */
    public DatabaseEntity() {}

    /**
     * Constructor with table name and data
     * 
     * @param tableName The name of the table this entity belongs to
     * @param data The data fields and values for the entity
     */
    public DatabaseEntity(String tableName, Map<String, Object> data) {
        this.tableName = tableName;
        this.data = data;
    }

    /**
     * Constructor with table name, primary key field, primary key value, and data
     * 
     * @param tableName The name of the table this entity belongs to
     * @param primaryKeyField The primary key field name for the entity
     * @param primaryKeyValue The primary key value for the entity
     * @param data The data fields and values for the entity
     */
    public DatabaseEntity(String tableName, String primaryKeyField, Object primaryKeyValue, Map<String, Object> data) {
        this.tableName = tableName;
        this.primaryKeyField = primaryKeyField;
        this.primaryKeyValue = primaryKeyValue;
        this.data = data;
    }

    /**
     * Gets the table name
     * @return The name of the table this entity belongs to
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the table name
     * @param tableName The name of the table this entity belongs to
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Gets the primary key field name
     * @return The primary key field name for the entity
     */
    public String getPrimaryKeyField() {
        return primaryKeyField;
    }

    /**
     * Sets the primary key field name
     * @param primaryKeyField The primary key field name for the entity
     */
    public void setPrimaryKeyField(String primaryKeyField) {
        this.primaryKeyField = primaryKeyField;
    }

    /**
     * Gets the primary key value
     * @return The primary key value for the entity
     */
    public Object getPrimaryKeyValue() {
        return primaryKeyValue;
    }

    /**
     * Sets the primary key value
     * @param primaryKeyValue The primary key value for the entity
     */
    public void setPrimaryKeyValue(Object primaryKeyValue) {
        this.primaryKeyValue = primaryKeyValue;
    }

    /**
     * Gets the data fields and values
     * @return The data fields and values for the entity
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * Sets the data fields and values
     * @param data The data fields and values for the entity
     */
    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
