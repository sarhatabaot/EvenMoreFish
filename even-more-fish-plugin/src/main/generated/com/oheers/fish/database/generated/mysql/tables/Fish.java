/*
 * This file is generated by jOOQ.
 */
package com.oheers.fish.database.generated.mysql.tables;


import com.oheers.fish.database.generated.mysql.DefaultSchema;
import com.oheers.fish.database.generated.mysql.Keys;
import com.oheers.fish.database.generated.mysql.tables.records.FishRecord;

import java.time.LocalDateTime;
import java.util.Collection;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Name;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Fish extends TableImpl<FishRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>${table.prefix}fish</code>
     */
    public static final Fish FISH = new Fish();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<FishRecord> getRecordType() {
        return FishRecord.class;
    }

    /**
     * The column <code>${table.prefix}fish.FISH_NAME</code>.
     */
    public final TableField<FishRecord, String> FISH_NAME = createField(DSL.name("FISH_NAME"), SQLDataType.VARCHAR(256).nullable(false), this, "");

    /**
     * The column <code>${table.prefix}fish.FISH_RARITY</code>.
     */
    public final TableField<FishRecord, String> FISH_RARITY = createField(DSL.name("FISH_RARITY"), SQLDataType.VARCHAR(256).nullable(false), this, "");

    /**
     * The column <code>${table.prefix}fish.FIRST_FISHER</code>.
     */
    public final TableField<FishRecord, String> FIRST_FISHER = createField(DSL.name("FIRST_FISHER"), SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * The column <code>${table.prefix}fish.TOTAL_CAUGHT</code>.
     */
    public final TableField<FishRecord, Integer> TOTAL_CAUGHT = createField(DSL.name("TOTAL_CAUGHT"), SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>${table.prefix}fish.LARGEST_FISH</code>.
     */
    public final TableField<FishRecord, Float> LARGEST_FISH = createField(DSL.name("LARGEST_FISH"), SQLDataType.REAL.nullable(false), this, "");

    /**
     * The column <code>${table.prefix}fish.LARGEST_FISHER</code>.
     */
    public final TableField<FishRecord, String> LARGEST_FISHER = createField(DSL.name("LARGEST_FISHER"), SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * The column <code>${table.prefix}fish.SHORTEST_LENGTH</code>.
     */
    public final TableField<FishRecord, Float> SHORTEST_LENGTH = createField(DSL.name("SHORTEST_LENGTH"), SQLDataType.REAL.nullable(false), this, "");

    /**
     * The column <code>${table.prefix}fish.SHORTEST_FISHER</code>.
     */
    public final TableField<FishRecord, String> SHORTEST_FISHER = createField(DSL.name("SHORTEST_FISHER"), SQLDataType.VARCHAR(36).nullable(false), this, "");

    /**
     * The column <code>${table.prefix}fish.FIRST_CATCH_TIME</code>.
     */
    public final TableField<FishRecord, LocalDateTime> FIRST_CATCH_TIME = createField(DSL.name("FIRST_CATCH_TIME"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");

    /**
     * The column <code>${table.prefix}fish.DISCOVERER</code>.
     */
    public final TableField<FishRecord, String> DISCOVERER = createField(DSL.name("DISCOVERER"), SQLDataType.VARCHAR(128), this, "");

    private Fish(Name alias, Table<FishRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Fish(Name alias, Table<FishRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>${table.prefix}fish</code> table reference
     */
    public Fish(String alias) {
        this(DSL.name(alias), FISH);
    }

    /**
     * Create an aliased <code>${table.prefix}fish</code> table reference
     */
    public Fish(Name alias) {
        this(alias, FISH);
    }

    /**
     * Create a <code>${table.prefix}fish</code> table reference
     */
    public Fish() {
        this(DSL.name("${table.prefix}fish"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public UniqueKey<FishRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_E;
    }

    @Override
    public Fish as(String alias) {
        return new Fish(DSL.name(alias), this);
    }

    @Override
    public Fish as(Name alias) {
        return new Fish(alias, this);
    }

    @Override
    public Fish as(Table<?> alias) {
        return new Fish(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Fish rename(String name) {
        return new Fish(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Fish rename(Name name) {
        return new Fish(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Fish rename(Table<?> name) {
        return new Fish(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Fish where(Condition condition) {
        return new Fish(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Fish where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Fish where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Fish where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Fish where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Fish where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Fish where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Fish where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Fish whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Fish whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
