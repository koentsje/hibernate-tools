# Hibernate Tools Maven Plugin - Reverse Engineering Example

This example demonstrates how to use the `hibernate-tools-maven` plugin to
reverse-engineer a database schema into different output formats and to
transform existing `hbm.xml` mappings into JPA `mapping.xml` files.

## Database schema

```
PERSON  1───*  ITEM      (one-to-many via OWNER_ID foreign key)
PERSON  1───1  ADDRESS   (one-to-one via unique PERSON_ID foreign key)
```

## What the build does

| Step | Plugin Goal       | Input                    | Output                                   |
|------|-------------------|--------------------------|------------------------------------------|
| 0    | `sql:execute`     | `schema.sql`             | H2 database in `target/exampledb`        |
| 1    | `hbm2java`        | database                 | `target/generated-sources/*.java`        |
| 2    | `generateHbm`     | database                 | `target/generated-hbm/*.hbm.xml`         |
| 3    | `generateMapping` | database                 | `target/generated-mapping/*.mapping.xml` |
| 4    | `hbm2orm`         | `src/.../hbm/*.hbm.xml`  | `src/.../hbm/*.mapping.xml`              |

Steps 1-3 reverse-engineer the live database into three different formats.
Step 4 transforms hand-written `hbm.xml` files into JPA `mapping.xml` —
no database connection needed.

## Running

```bash
mvn clean generate-sources generate-resources
```

## Inspecting the output

After a successful build:

- **`target/generated-sources/`** — annotated `@Entity` Java classes
  with `@Table`, `@Id`, `@Column`, `@ManyToOne`, `@OneToMany`, etc.
- **`target/generated-hbm/`** — Hibernate native `.hbm.xml` mapping files
- **`target/generated-mapping/`** — JPA `.mapping.xml` files (generated
  directly from the database)
- **`src/main/resources/hbm/`** — JPA `.mapping.xml` files (transformed
  from the hand-written `.hbm.xml` files in the same directory)

## Configuration options

### `hbm2java`

| Parameter         | Default                     | Description                                |
|-------------------|-----------------------------|--------------------------------------------|
| `outputDirectory` | `target/generated-sources/` | Where to write `.java` files               |
| `ejb3`            | `true`                      | Generate JPA annotations (`@Entity`, etc.) |
| `jdk5`            | `true`                      | Use Java generics (`Set<Item>` vs raw `Set`)|
| `templatePath`    | —                           | Path to custom FreeMarker templates        |

### `generateHbm`

| Parameter         | Default                | Description                          |
|-------------------|------------------------|--------------------------------------|
| `outputDirectory` | `src/main/resources`   | Where to write `.hbm.xml` files      |
| `templatePath`    | —                      | Path to custom FreeMarker templates  |

### `generateMapping`

| Parameter         | Default                      | Description                             |
|-------------------|------------------------------|-----------------------------------------|
| `outputDirectory` | `target/generated-resources` | Where to write `.mapping.xml` files     |
| `templatePath`    | —                            | Path to custom FreeMarker templates     |

### `hbm2orm`

| Parameter     | Default              | Description                           |
|---------------|----------------------|---------------------------------------|
| `inputFolder` | `src/main/resources` | Directory containing `.hbm.xml` files |
| `format`      | `true`               | Pretty-print the output XML           |

### Common reverse-engineering parameters (hbm2java, generateHbm, generateMapping)

| Parameter                      | Default | Description                              |
|--------------------------------|---------|------------------------------------------|
| `packageName`                  | —       | Default package for generated classes    |
| `revengFile`                   | —       | Path to a `reveng.xml` customization file|
| `revengStrategy`               | —       | Custom `RevengStrategy` class name       |
| `detectManyToMany`             | `true`  | Detect pure link tables as many-to-many  |
| `detectOneToOne`               | `true`  | Detect one-to-one via foreign keys       |
| `detectOptimisticLock`         | `true`  | Map VERSION/TIMESTAMP columns            |
| `createCollectionForForeignKey`| `true`  | Generate collection for each FK          |
| `createManyToOneForForeignKey` | `true`  | Generate many-to-one for each FK         |
