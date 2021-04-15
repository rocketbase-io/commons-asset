package io.rocketbase.commons;

import io.rocketbase.commons.model.AssetFileEntity;
import io.rocketbase.commons.model.AssetJpaEntity;
import io.rocketbase.commons.model.ColorPaletteEntity;
import io.rocketbase.commons.model.ResolutionEntity;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class GenerateSchema {

    @Disabled
    @Test
    public void generateEntity() {
        Map<String, String> settings = new HashMap<>();
        settings.put("connection.driver_class", "org.mariadb.jdbc.Driver");

        settings.put("dialect", "org.hibernate.dialect.MariaDB103Dialect");
        settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/generate");

        settings.put("hibernate.connection.username", "root");
        settings.put("hibernate.connection.password", "my-secret-pw");
        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");

        ServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().applySettings(settings).build();

        MetadataSources metadata =
                new MetadataSources(serviceRegistry);
        metadata.addAnnotatedClass(AssetJpaEntity.class);
        metadata.addAnnotatedClass(ColorPaletteEntity.class);
        metadata.addAnnotatedClass(ResolutionEntity.class);

        EnumSet<TargetType> enumSet = EnumSet.of(TargetType.SCRIPT);
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setDelimiter(";");
        schemaExport.setFormat(true);
        schemaExport.setOutputFile("schema-entity.sql");
        schemaExport.execute(enumSet, SchemaExport.Action.CREATE, metadata.buildMetadata());
    }

    @Disabled
    @Test
    public void generateStorage() {
        Map<String, String> settings = new HashMap<>();
        settings.put("connection.driver_class", "org.mariadb.jdbc.Driver");

        settings.put("dialect", "org.hibernate.dialect.MariaDB103Dialect");
        settings.put("hibernate.connection.url", "jdbc:mysql://localhost:3306/generate");

        settings.put("hibernate.connection.username", "root");
        settings.put("hibernate.connection.password", "my-secret-pw");
        settings.put("hibernate.show_sql", "true");
        settings.put("hibernate.format_sql", "true");

        ServiceRegistry serviceRegistry =
                new StandardServiceRegistryBuilder().applySettings(settings).build();

        MetadataSources metadata =
                new MetadataSources(serviceRegistry);
        metadata.addAnnotatedClass(AssetFileEntity.class);

        EnumSet<TargetType> enumSet = EnumSet.of(TargetType.SCRIPT);
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.setDelimiter(";");
        schemaExport.setFormat(true);
        schemaExport.setOutputFile("schema-storage.sql");
        schemaExport.execute(enumSet, SchemaExport.Action.CREATE, metadata.buildMetadata());
    }
}
