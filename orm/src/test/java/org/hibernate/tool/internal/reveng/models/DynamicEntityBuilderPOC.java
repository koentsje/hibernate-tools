/*
 * Copyright 2010 - 2025 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hibernate.tool.internal.reveng.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.hibernate.boot.models.JpaAnnotations;
import org.hibernate.boot.models.annotations.internal.ColumnJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.EntityJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.GeneratedValueJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.IdJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.JoinColumnJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.ManyToOneJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.OneToManyJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.OneToOneJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.AttributeOverrideJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.AttributeOverridesJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.BasicJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.DiscriminatorColumnJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.DiscriminatorValueJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.EmbeddableJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.EmbeddedIdJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.EmbeddedJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.InheritanceJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.JoinTableJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.LobJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.ManyToManyJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.PrimaryKeyJoinColumnJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.TableJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.TemporalJpaAnnotation;
import org.hibernate.boot.models.annotations.internal.VersionJpaAnnotation;
import org.hibernate.models.internal.BasicModelsContextImpl;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.MutableClassDetailsRegistry;
import org.hibernate.models.internal.ParameterizedTypeDetailsImpl;
import org.hibernate.models.internal.SimpleClassLoading;
import org.hibernate.models.internal.dynamic.DynamicClassDetails;
import org.hibernate.models.internal.dynamic.DynamicFieldDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableAnnotationTarget;
import org.hibernate.models.spi.TypeDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.TemporalType;

/**
 * Proof-of-concept for building dynamic entity ClassDetails with JPA annotations
 * from database metadata using Hibernate Models API.
 *
 * This demonstrates how to replace the current org.hibernate.mapping.* approach
 * with a modern annotation-based metadata model.
 *
 * @author Koen Aers
 */
public class DynamicEntityBuilderPOC {

	private final ModelsContext modelsContext;

	public DynamicEntityBuilderPOC() {
		// Initialize the models context
		ClassLoading classLoading = SimpleClassLoading.SIMPLE_CLASS_LOADING;
		this.modelsContext = new BasicModelsContextImpl(classLoading, false, null);
	}

	/**
	 * Creates a ClassDetails representing an entity from database table metadata.
	 *
	 * @param tableMetadata The database table metadata
	 * @return ClassDetails with JPA annotations attached
	 */
	public ClassDetails createEntityFromTable(TableMetadata tableMetadata) {
		String className = tableMetadata.getEntityPackage() + "." + tableMetadata.getEntityClassName();

		// Resolve parent class if this is a subclass
		ClassDetails superClass = null;
		if (tableMetadata.getParentEntityClassName() != null) {
			String parentClassName = tableMetadata.getParentEntityPackage() + "." + tableMetadata.getParentEntityClassName();
			superClass = resolveOrCreateClassDetails(
				tableMetadata.getParentEntityClassName(),
				parentClassName
			);
		}

		// Create the dynamic class with both simple name and fully qualified class name
		DynamicClassDetails entityClass = new DynamicClassDetails(
			tableMetadata.getEntityClassName(),
			className,
			false,
			superClass,
			null,
			modelsContext
		);

		// Add @Entity annotation
		addEntityAnnotation(entityClass, tableMetadata.getEntityClassName());

		// Add @Table annotation
		addTableAnnotation(entityClass, tableMetadata);

		// Add inheritance annotations
		addInheritanceAnnotations(entityClass, tableMetadata);

		// Add fields for each column (skip columns that are part of a foreign key)
		for (ColumnMetadata columnMetadata : tableMetadata.getColumns()) {
			if (!tableMetadata.isForeignKeyColumn(columnMetadata.getColumnName())) {
				addField(entityClass, columnMetadata);
			}
		}

		// Add ManyToOne relationship fields
		for (ForeignKeyMetadata fkMetadata : tableMetadata.getForeignKeys()) {
			addManyToOneField(entityClass, fkMetadata);
		}

		// Add OneToMany relationship fields
		for (OneToManyMetadata o2mMetadata : tableMetadata.getOneToManys()) {
			addOneToManyField(entityClass, o2mMetadata);
		}

		// Add OneToOne relationship fields
		for (OneToOneMetadata o2oMetadata : tableMetadata.getOneToOnes()) {
			addOneToOneField(entityClass, o2oMetadata);
		}

		// Add ManyToMany relationship fields
		for (ManyToManyMetadata m2mMetadata : tableMetadata.getManyToManys()) {
			addManyToManyField(entityClass, m2mMetadata);
		}

		// Add Embedded fields
		for (EmbeddedFieldMetadata embeddedMetadata : tableMetadata.getEmbeddedFields()) {
			addEmbeddedField(entityClass, embeddedMetadata);
		}

		// Add @EmbeddedId composite key field
		if (tableMetadata.getCompositeId() != null) {
			addCompositeIdField(entityClass, tableMetadata.getCompositeId());
		}

		// Register in the context
		registerClassDetails(entityClass);

		return entityClass;
	}

	/**
	 * Creates a ClassDetails representing an embeddable class from column metadata.
	 *
	 * @param embeddableMetadata The embeddable class metadata
	 * @return ClassDetails with @Embeddable annotation attached
	 */
	public ClassDetails createEmbeddable(EmbeddableMetadata embeddableMetadata) {
		String className = embeddableMetadata.getPackageName() + "." + embeddableMetadata.getClassName();

		DynamicClassDetails embeddableClass = new DynamicClassDetails(
			embeddableMetadata.getClassName(),
			className,
			false,
			null,
			null,
			modelsContext
		);

		// Add @Embeddable annotation
		MutableAnnotationTarget mutableClass = (MutableAnnotationTarget) embeddableClass;
		EmbeddableJpaAnnotation embeddableAnnotation = JpaAnnotations.EMBEDDABLE.createUsage(modelsContext);
		mutableClass.addAnnotationUsage(embeddableAnnotation);

		// Add fields for each column
		for (ColumnMetadata columnMetadata : embeddableMetadata.getColumns()) {
			addField(embeddableClass, columnMetadata);
		}

		// Register in the context
		registerClassDetails(embeddableClass);

		return embeddableClass;
	}

	private void addEntityAnnotation(DynamicClassDetails entityClass, String entityName) {
		MutableAnnotationTarget mutableClass = (MutableAnnotationTarget) entityClass;
		EntityJpaAnnotation entityAnnotation = JpaAnnotations.ENTITY.createUsage(modelsContext);
		entityAnnotation.name(entityName);
		mutableClass.addAnnotationUsage(entityAnnotation);
	}

	private void addTableAnnotation(DynamicClassDetails entityClass, TableMetadata tableMetadata) {
		MutableAnnotationTarget mutableClass = (MutableAnnotationTarget) entityClass;
		TableJpaAnnotation tableAnnotation = JpaAnnotations.TABLE.createUsage(modelsContext);
		tableAnnotation.name(tableMetadata.getTableName());

		if (tableMetadata.getSchema() != null) {
			tableAnnotation.schema(tableMetadata.getSchema());
		}
		if (tableMetadata.getCatalog() != null) {
			tableAnnotation.catalog(tableMetadata.getCatalog());
		}

		mutableClass.addAnnotationUsage(tableAnnotation);
	}

	private void addInheritanceAnnotations(DynamicClassDetails entityClass, TableMetadata tableMetadata) {
		MutableAnnotationTarget mutableClass = (MutableAnnotationTarget) entityClass;

		// Add @Inheritance on root entities
		InheritanceMetadata inheritance = tableMetadata.getInheritance();
		if (inheritance != null) {
			InheritanceJpaAnnotation inheritanceAnnotation = JpaAnnotations.INHERITANCE.createUsage(modelsContext);
			inheritanceAnnotation.strategy(inheritance.getStrategy());
			mutableClass.addAnnotationUsage(inheritanceAnnotation);

			// Add @DiscriminatorColumn on root entities with discriminator-based strategies
			if (inheritance.getDiscriminatorColumnName() != null) {
				DiscriminatorColumnJpaAnnotation discColAnnotation =
					JpaAnnotations.DISCRIMINATOR_COLUMN.createUsage(modelsContext);
				discColAnnotation.name(inheritance.getDiscriminatorColumnName());
				if (inheritance.getDiscriminatorType() != null) {
					discColAnnotation.discriminatorType(inheritance.getDiscriminatorType());
				}
				if (inheritance.getDiscriminatorColumnLength() > 0) {
					discColAnnotation.length(inheritance.getDiscriminatorColumnLength());
				}
				mutableClass.addAnnotationUsage(discColAnnotation);
			}
		}

		// Add @DiscriminatorValue on entities in discriminator-based hierarchies
		if (tableMetadata.getDiscriminatorValue() != null) {
			DiscriminatorValueJpaAnnotation discValAnnotation =
				JpaAnnotations.DISCRIMINATOR_VALUE.createUsage(modelsContext);
			discValAnnotation.value(tableMetadata.getDiscriminatorValue());
			mutableClass.addAnnotationUsage(discValAnnotation);
		}

		// Add @PrimaryKeyJoinColumn on subclasses in JOINED strategy
		if (tableMetadata.getPrimaryKeyJoinColumnName() != null) {
			PrimaryKeyJoinColumnJpaAnnotation pkJoinColAnnotation =
				JpaAnnotations.PRIMARY_KEY_JOIN_COLUMN.createUsage(modelsContext);
			pkJoinColAnnotation.name(tableMetadata.getPrimaryKeyJoinColumnName());
			mutableClass.addAnnotationUsage(pkJoinColAnnotation);
		}
	}

	private void addField(DynamicClassDetails entityClass, ColumnMetadata columnMetadata) {
		// Resolve the field type
		ClassDetails fieldTypeClass = modelsContext.getClassDetailsRegistry()
			.resolveClassDetails(columnMetadata.getJavaType().getName());

		TypeDetails fieldType = new ClassTypeDetailsImpl(
			fieldTypeClass,
			TypeDetails.Kind.CLASS
		);

		// Create the field
		DynamicFieldDetails field = entityClass.applyAttribute(
			columnMetadata.getFieldName(),
			fieldType,
			false,  // isArray
			false,  // isPlural
			modelsContext
		);

		MutableAnnotationTarget mutableField = (MutableAnnotationTarget) field;

		// Add @Id annotation if primary key
		if (columnMetadata.isPrimaryKey()) {
			IdJpaAnnotation idAnnotation = JpaAnnotations.ID.createUsage(modelsContext);
			mutableField.addAnnotationUsage(idAnnotation);

			// Add @GeneratedValue if auto-increment
			if (columnMetadata.isAutoIncrement()) {
				GeneratedValueJpaAnnotation generatedAnnotation =
					JpaAnnotations.GENERATED_VALUE.createUsage(modelsContext);
				generatedAnnotation.strategy(GenerationType.IDENTITY);
				mutableField.addAnnotationUsage(generatedAnnotation);
			}
		}

		// Add @Version annotation for optimistic locking
		if (columnMetadata.isVersion()) {
			VersionJpaAnnotation versionAnnotation = JpaAnnotations.VERSION.createUsage(modelsContext);
			mutableField.addAnnotationUsage(versionAnnotation);
		}

		// Add @Basic annotation if fetch type or optional is explicitly configured
		if (columnMetadata.getBasicFetchType() != null || columnMetadata.isBasicOptionalSet()) {
			BasicJpaAnnotation basicAnnotation = JpaAnnotations.BASIC.createUsage(modelsContext);
			if (columnMetadata.getBasicFetchType() != null) {
				basicAnnotation.fetch(columnMetadata.getBasicFetchType());
			}
			if (columnMetadata.isBasicOptionalSet()) {
				basicAnnotation.optional(columnMetadata.isBasicOptional());
			}
			mutableField.addAnnotationUsage(basicAnnotation);
		}

		// Add @Temporal annotation for date/time types
		if (columnMetadata.getTemporalType() != null) {
			TemporalJpaAnnotation temporalAnnotation = JpaAnnotations.TEMPORAL.createUsage(modelsContext);
			temporalAnnotation.value(columnMetadata.getTemporalType());
			mutableField.addAnnotationUsage(temporalAnnotation);
		}

		// Add @Lob annotation for large objects
		if (columnMetadata.isLob()) {
			LobJpaAnnotation lobAnnotation = JpaAnnotations.LOB.createUsage(modelsContext);
			mutableField.addAnnotationUsage(lobAnnotation);
		}

		// Add @Column annotation
		ColumnJpaAnnotation columnAnnotation = JpaAnnotations.COLUMN.createUsage(modelsContext);
		columnAnnotation.name(columnMetadata.getColumnName());
		columnAnnotation.nullable(columnMetadata.isNullable());

		if (columnMetadata.getLength() > 0) {
			columnAnnotation.length(columnMetadata.getLength());
		}
		if (columnMetadata.getPrecision() > 0) {
			columnAnnotation.precision(columnMetadata.getPrecision());
		}
		if (columnMetadata.getScale() > 0) {
			columnAnnotation.scale(columnMetadata.getScale());
		}

		mutableField.addAnnotationUsage(columnAnnotation);
	}

	private void addManyToOneField(DynamicClassDetails entityClass, ForeignKeyMetadata fkMetadata) {
		// Resolve the target entity ClassDetails from the registry
		String targetClassName = fkMetadata.getTargetEntityPackage() + "." + fkMetadata.getTargetEntityClassName();
		ClassDetails targetClassDetails = modelsContext.getClassDetailsRegistry()
			.resolveClassDetails(targetClassName);

		TypeDetails fieldType = new ClassTypeDetailsImpl(
			targetClassDetails,
			TypeDetails.Kind.CLASS
		);

		// Create the field
		DynamicFieldDetails field = entityClass.applyAttribute(
			fkMetadata.getFieldName(),
			fieldType,
			false,
			false,
			modelsContext
		);

		MutableAnnotationTarget mutableField = (MutableAnnotationTarget) field;

		// Add @ManyToOne annotation
		ManyToOneJpaAnnotation manyToOneAnnotation = JpaAnnotations.MANY_TO_ONE.createUsage(modelsContext);
		if (fkMetadata.getFetchType() != null) {
			manyToOneAnnotation.fetch(fkMetadata.getFetchType());
		}
		manyToOneAnnotation.optional(fkMetadata.isOptional());
		mutableField.addAnnotationUsage(manyToOneAnnotation);

		// Add @JoinColumn annotation
		JoinColumnJpaAnnotation joinColumnAnnotation = JpaAnnotations.JOIN_COLUMN.createUsage(modelsContext);
		joinColumnAnnotation.name(fkMetadata.getForeignKeyColumnName());
		if (fkMetadata.getReferencedColumnName() != null) {
			joinColumnAnnotation.referencedColumnName(fkMetadata.getReferencedColumnName());
		}
		joinColumnAnnotation.nullable(fkMetadata.isOptional());
		mutableField.addAnnotationUsage(joinColumnAnnotation);
	}

	private void addOneToManyField(DynamicClassDetails entityClass, OneToManyMetadata o2mMetadata) {
		// Resolve the element entity ClassDetails, creating a placeholder if not yet registered
		String elementClassName = o2mMetadata.getElementEntityPackage() + "." + o2mMetadata.getElementEntityClassName();
		ClassDetails elementClassDetails = resolveOrCreateClassDetails(
			o2mMetadata.getElementEntityClassName(),
			elementClassName
		);

		// Build a Set<ElementEntity> parameterized type
		ClassDetails setClassDetails = modelsContext.getClassDetailsRegistry()
			.resolveClassDetails(Set.class.getName());

		TypeDetails elementType = new ClassTypeDetailsImpl(
			elementClassDetails,
			TypeDetails.Kind.CLASS
		);

		TypeDetails fieldType = new ParameterizedTypeDetailsImpl(
			setClassDetails,
			Collections.singletonList(elementType),
			null
		);

		// Create the field as a plural (collection) field
		DynamicFieldDetails field = entityClass.applyAttribute(
			o2mMetadata.getFieldName(),
			fieldType,
			false,  // isArray
			true,   // isPlural
			modelsContext
		);

		MutableAnnotationTarget mutableField = (MutableAnnotationTarget) field;

		// Add @OneToMany annotation
		OneToManyJpaAnnotation oneToManyAnnotation = JpaAnnotations.ONE_TO_MANY.createUsage(modelsContext);
		oneToManyAnnotation.mappedBy(o2mMetadata.getMappedBy());
		if (o2mMetadata.getFetchType() != null) {
			oneToManyAnnotation.fetch(o2mMetadata.getFetchType());
		}
		if (o2mMetadata.getCascadeTypes() != null) {
			oneToManyAnnotation.cascade(o2mMetadata.getCascadeTypes());
		}
		oneToManyAnnotation.orphanRemoval(o2mMetadata.isOrphanRemoval());
		mutableField.addAnnotationUsage(oneToManyAnnotation);
	}

	private void addOneToOneField(DynamicClassDetails entityClass, OneToOneMetadata o2oMetadata) {
		// Resolve the target entity ClassDetails, creating a placeholder if not yet registered
		String targetClassName = o2oMetadata.getTargetEntityPackage() + "." + o2oMetadata.getTargetEntityClassName();
		ClassDetails targetClassDetails = resolveOrCreateClassDetails(
			o2oMetadata.getTargetEntityClassName(),
			targetClassName
		);

		TypeDetails fieldType = new ClassTypeDetailsImpl(
			targetClassDetails,
			TypeDetails.Kind.CLASS
		);

		// Create the field
		DynamicFieldDetails field = entityClass.applyAttribute(
			o2oMetadata.getFieldName(),
			fieldType,
			false,
			false,
			modelsContext
		);

		MutableAnnotationTarget mutableField = (MutableAnnotationTarget) field;

		// Add @OneToOne annotation
		OneToOneJpaAnnotation oneToOneAnnotation = JpaAnnotations.ONE_TO_ONE.createUsage(modelsContext);
		if (o2oMetadata.getMappedBy() != null) {
			oneToOneAnnotation.mappedBy(o2oMetadata.getMappedBy());
		}
		if (o2oMetadata.getFetchType() != null) {
			oneToOneAnnotation.fetch(o2oMetadata.getFetchType());
		}
		if (o2oMetadata.getCascadeTypes() != null) {
			oneToOneAnnotation.cascade(o2oMetadata.getCascadeTypes());
		}
		oneToOneAnnotation.optional(o2oMetadata.isOptional());
		oneToOneAnnotation.orphanRemoval(o2oMetadata.isOrphanRemoval());
		mutableField.addAnnotationUsage(oneToOneAnnotation);

		// Add @JoinColumn only on the owning side (no mappedBy)
		if (o2oMetadata.getMappedBy() == null && o2oMetadata.getForeignKeyColumnName() != null) {
			JoinColumnJpaAnnotation joinColumnAnnotation = JpaAnnotations.JOIN_COLUMN.createUsage(modelsContext);
			joinColumnAnnotation.name(o2oMetadata.getForeignKeyColumnName());
			if (o2oMetadata.getReferencedColumnName() != null) {
				joinColumnAnnotation.referencedColumnName(o2oMetadata.getReferencedColumnName());
			}
			joinColumnAnnotation.unique(true);
			joinColumnAnnotation.nullable(o2oMetadata.isOptional());
			mutableField.addAnnotationUsage(joinColumnAnnotation);
		}
	}

	private void addManyToManyField(DynamicClassDetails entityClass, ManyToManyMetadata m2mMetadata) {
		// Resolve the target entity ClassDetails, creating a placeholder if not yet registered
		String targetClassName = m2mMetadata.getTargetEntityPackage() + "." + m2mMetadata.getTargetEntityClassName();
		ClassDetails targetClassDetails = resolveOrCreateClassDetails(
			m2mMetadata.getTargetEntityClassName(),
			targetClassName
		);

		// Build a Set<TargetEntity> parameterized type
		ClassDetails setClassDetails = modelsContext.getClassDetailsRegistry()
			.resolveClassDetails(Set.class.getName());

		TypeDetails elementType = new ClassTypeDetailsImpl(
			targetClassDetails,
			TypeDetails.Kind.CLASS
		);

		TypeDetails fieldType = new ParameterizedTypeDetailsImpl(
			setClassDetails,
			Collections.singletonList(elementType),
			null
		);

		// Create the field as a plural (collection) field
		DynamicFieldDetails field = entityClass.applyAttribute(
			m2mMetadata.getFieldName(),
			fieldType,
			false,  // isArray
			true,   // isPlural
			modelsContext
		);

		MutableAnnotationTarget mutableField = (MutableAnnotationTarget) field;

		// Add @ManyToMany annotation
		ManyToManyJpaAnnotation manyToManyAnnotation = JpaAnnotations.MANY_TO_MANY.createUsage(modelsContext);
		if (m2mMetadata.getMappedBy() != null) {
			manyToManyAnnotation.mappedBy(m2mMetadata.getMappedBy());
		}
		if (m2mMetadata.getFetchType() != null) {
			manyToManyAnnotation.fetch(m2mMetadata.getFetchType());
		}
		if (m2mMetadata.getCascadeTypes() != null) {
			manyToManyAnnotation.cascade(m2mMetadata.getCascadeTypes());
		}
		mutableField.addAnnotationUsage(manyToManyAnnotation);

		// Add @JoinTable only on the owning side (no mappedBy)
		if (m2mMetadata.getMappedBy() == null && m2mMetadata.getJoinTableName() != null) {
			JoinTableJpaAnnotation joinTableAnnotation = JpaAnnotations.JOIN_TABLE.createUsage(modelsContext);
			joinTableAnnotation.name(m2mMetadata.getJoinTableName());

			if (m2mMetadata.getJoinColumnName() != null) {
				JoinColumnJpaAnnotation joinColumn = JpaAnnotations.JOIN_COLUMN.createUsage(modelsContext);
				joinColumn.name(m2mMetadata.getJoinColumnName());
				joinTableAnnotation.joinColumns(new jakarta.persistence.JoinColumn[]{ joinColumn });
			}

			if (m2mMetadata.getInverseJoinColumnName() != null) {
				JoinColumnJpaAnnotation inverseJoinColumn = JpaAnnotations.JOIN_COLUMN.createUsage(modelsContext);
				inverseJoinColumn.name(m2mMetadata.getInverseJoinColumnName());
				joinTableAnnotation.inverseJoinColumns(new jakarta.persistence.JoinColumn[]{ inverseJoinColumn });
			}

			mutableField.addAnnotationUsage(joinTableAnnotation);
		}
	}

	private void addEmbeddedField(DynamicClassDetails entityClass, EmbeddedFieldMetadata embeddedMetadata) {
		// Resolve the embeddable ClassDetails from the registry
		String embeddableClassName = embeddedMetadata.getEmbeddablePackage() + "." + embeddedMetadata.getEmbeddableClassName();
		ClassDetails embeddableClassDetails = resolveOrCreateClassDetails(
			embeddedMetadata.getEmbeddableClassName(),
			embeddableClassName
		);

		TypeDetails fieldType = new ClassTypeDetailsImpl(
			embeddableClassDetails,
			TypeDetails.Kind.CLASS
		);

		// Create the field
		DynamicFieldDetails field = entityClass.applyAttribute(
			embeddedMetadata.getFieldName(),
			fieldType,
			false,
			false,
			modelsContext
		);

		MutableAnnotationTarget mutableField = (MutableAnnotationTarget) field;

		// Add @Embedded annotation
		EmbeddedJpaAnnotation embeddedAnnotation = JpaAnnotations.EMBEDDED.createUsage(modelsContext);
		mutableField.addAnnotationUsage(embeddedAnnotation);

		// Add @AttributeOverrides container if there are overrides
		List<AttributeOverrideMetadata> overrides = embeddedMetadata.getAttributeOverrides();
		if (!overrides.isEmpty()) {
			jakarta.persistence.AttributeOverride[] overrideArray =
				new jakarta.persistence.AttributeOverride[overrides.size()];

			for (int i = 0; i < overrides.size(); i++) {
				AttributeOverrideJpaAnnotation overrideAnnotation =
					JpaAnnotations.ATTRIBUTE_OVERRIDE.createUsage(modelsContext);
				overrideAnnotation.name(overrides.get(i).getFieldName());

				ColumnJpaAnnotation columnAnnotation = JpaAnnotations.COLUMN.createUsage(modelsContext);
				columnAnnotation.name(overrides.get(i).getColumnName());
				overrideAnnotation.column(columnAnnotation);

				overrideArray[i] = overrideAnnotation;
			}

			AttributeOverridesJpaAnnotation overridesContainer =
				JpaAnnotations.ATTRIBUTE_OVERRIDES.createUsage(modelsContext);
			overridesContainer.value(overrideArray);
			mutableField.addAnnotationUsage(overridesContainer);
		}
	}

	private void addCompositeIdField(DynamicClassDetails entityClass, CompositeIdMetadata compositeId) {
		// Resolve the embeddable ID class from the registry
		String idClassName = compositeId.getIdClassPackage() + "." + compositeId.getIdClassName();
		ClassDetails idClassDetails = resolveOrCreateClassDetails(
			compositeId.getIdClassName(),
			idClassName
		);

		TypeDetails fieldType = new ClassTypeDetailsImpl(
			idClassDetails,
			TypeDetails.Kind.CLASS
		);

		// Create the field
		DynamicFieldDetails field = entityClass.applyAttribute(
			compositeId.getFieldName(),
			fieldType,
			false,
			false,
			modelsContext
		);

		MutableAnnotationTarget mutableField = (MutableAnnotationTarget) field;

		// Add @EmbeddedId annotation
		EmbeddedIdJpaAnnotation embeddedIdAnnotation = JpaAnnotations.EMBEDDED_ID.createUsage(modelsContext);
		mutableField.addAnnotationUsage(embeddedIdAnnotation);

		// Add @AttributeOverrides container if there are overrides
		List<AttributeOverrideMetadata> overrides = compositeId.getAttributeOverrides();
		if (!overrides.isEmpty()) {
			jakarta.persistence.AttributeOverride[] overrideArray =
				new jakarta.persistence.AttributeOverride[overrides.size()];

			for (int i = 0; i < overrides.size(); i++) {
				AttributeOverrideJpaAnnotation overrideAnnotation =
					JpaAnnotations.ATTRIBUTE_OVERRIDE.createUsage(modelsContext);
				overrideAnnotation.name(overrides.get(i).getFieldName());

				ColumnJpaAnnotation columnAnnotation = JpaAnnotations.COLUMN.createUsage(modelsContext);
				columnAnnotation.name(overrides.get(i).getColumnName());
				overrideAnnotation.column(columnAnnotation);

				overrideArray[i] = overrideAnnotation;
			}

			AttributeOverridesJpaAnnotation overridesContainer =
				JpaAnnotations.ATTRIBUTE_OVERRIDES.createUsage(modelsContext);
			overridesContainer.value(overrideArray);
			mutableField.addAnnotationUsage(overridesContainer);
		}
	}

	/**
	 * Resolves a ClassDetails from the registry, or creates a dynamic placeholder
	 * if the class hasn't been built yet. This handles forward references where
	 * a relationship references an entity that will be built later.
	 */
	private ClassDetails resolveOrCreateClassDetails(String simpleName, String className) {
		MutableClassDetailsRegistry registry =
			(MutableClassDetailsRegistry) modelsContext.getClassDetailsRegistry();
		return registry.resolveClassDetails(
			className,
			name -> new DynamicClassDetails(simpleName, name, false, null, null, modelsContext)
		);
	}

	private void registerClassDetails(ClassDetails classDetails) {
		MutableClassDetailsRegistry registry =
			(MutableClassDetailsRegistry) modelsContext.getClassDetailsRegistry();
		registry.addClassDetails(classDetails);
	}

	public ModelsContext getModelsContext() {
		return modelsContext;
	}

	/**
	 * Represents metadata for a database table.
	 */
	public static class TableMetadata {
		private String tableName;
		private String schema;
		private String catalog;
		private String entityClassName;
		private String entityPackage;
		private List<ColumnMetadata> columns = new ArrayList<>();
		private List<ForeignKeyMetadata> foreignKeys = new ArrayList<>();
		private List<OneToManyMetadata> oneToManys = new ArrayList<>();
		private List<OneToOneMetadata> oneToOnes = new ArrayList<>();
		private List<ManyToManyMetadata> manyToManys = new ArrayList<>();
		private List<EmbeddedFieldMetadata> embeddedFields = new ArrayList<>();
		private InheritanceMetadata inheritance;
		private String discriminatorValue;
		private String parentEntityClassName;
		private String parentEntityPackage;
		private String primaryKeyJoinColumnName;
		private CompositeIdMetadata compositeId;

		public TableMetadata(String tableName, String entityClassName, String entityPackage) {
			this.tableName = tableName;
			this.entityClassName = entityClassName;
			this.entityPackage = entityPackage;
		}

		public TableMetadata addColumn(ColumnMetadata column) {
			this.columns.add(column);
			return this;
		}

		public TableMetadata addForeignKey(ForeignKeyMetadata foreignKey) {
			this.foreignKeys.add(foreignKey);
			return this;
		}

		public TableMetadata addOneToMany(OneToManyMetadata oneToMany) {
			this.oneToManys.add(oneToMany);
			return this;
		}

		public TableMetadata addOneToOne(OneToOneMetadata oneToOne) {
			this.oneToOnes.add(oneToOne);
			return this;
		}

		public TableMetadata addManyToMany(ManyToManyMetadata manyToMany) {
			this.manyToManys.add(manyToMany);
			return this;
		}

		public TableMetadata addEmbeddedField(EmbeddedFieldMetadata embeddedField) {
			this.embeddedFields.add(embeddedField);
			return this;
		}

		public boolean isForeignKeyColumn(String columnName) {
			return foreignKeys.stream()
				.anyMatch(fk -> fk.getForeignKeyColumnName().equals(columnName))
				|| oneToOnes.stream()
				.filter(o2o -> o2o.getForeignKeyColumnName() != null)
				.anyMatch(o2o -> o2o.getForeignKeyColumnName().equals(columnName));
		}

		// Getters and setters
		public String getTableName() { return tableName; }
		public void setTableName(String tableName) { this.tableName = tableName; }

		public String getSchema() { return schema; }
		public void setSchema(String schema) { this.schema = schema; }

		public String getCatalog() { return catalog; }
		public void setCatalog(String catalog) { this.catalog = catalog; }

		public String getEntityClassName() { return entityClassName; }
		public void setEntityClassName(String entityClassName) { this.entityClassName = entityClassName; }

		public String getEntityPackage() { return entityPackage; }
		public void setEntityPackage(String entityPackage) { this.entityPackage = entityPackage; }

		public List<ColumnMetadata> getColumns() { return columns; }
		public void setColumns(List<ColumnMetadata> columns) { this.columns = columns; }

		public List<ForeignKeyMetadata> getForeignKeys() { return foreignKeys; }
		public void setForeignKeys(List<ForeignKeyMetadata> foreignKeys) { this.foreignKeys = foreignKeys; }

		public List<OneToManyMetadata> getOneToManys() { return oneToManys; }
		public void setOneToManys(List<OneToManyMetadata> oneToManys) { this.oneToManys = oneToManys; }

		public List<OneToOneMetadata> getOneToOnes() { return oneToOnes; }
		public void setOneToOnes(List<OneToOneMetadata> oneToOnes) { this.oneToOnes = oneToOnes; }

		public List<ManyToManyMetadata> getManyToManys() { return manyToManys; }
		public void setManyToManys(List<ManyToManyMetadata> manyToManys) { this.manyToManys = manyToManys; }

		public List<EmbeddedFieldMetadata> getEmbeddedFields() { return embeddedFields; }
		public void setEmbeddedFields(List<EmbeddedFieldMetadata> embeddedFields) { this.embeddedFields = embeddedFields; }

		public InheritanceMetadata getInheritance() { return inheritance; }
		public TableMetadata inheritance(InheritanceMetadata inheritance) {
			this.inheritance = inheritance;
			return this;
		}

		public String getDiscriminatorValue() { return discriminatorValue; }
		public TableMetadata discriminatorValue(String discriminatorValue) {
			this.discriminatorValue = discriminatorValue;
			return this;
		}

		public String getParentEntityClassName() { return parentEntityClassName; }
		public String getParentEntityPackage() { return parentEntityPackage; }
		public TableMetadata parent(String parentEntityClassName, String parentEntityPackage) {
			this.parentEntityClassName = parentEntityClassName;
			this.parentEntityPackage = parentEntityPackage;
			return this;
		}

		public String getPrimaryKeyJoinColumnName() { return primaryKeyJoinColumnName; }
		public TableMetadata primaryKeyJoinColumn(String primaryKeyJoinColumnName) {
			this.primaryKeyJoinColumnName = primaryKeyJoinColumnName;
			return this;
		}

		public CompositeIdMetadata getCompositeId() { return compositeId; }
		public TableMetadata compositeId(CompositeIdMetadata compositeId) {
			this.compositeId = compositeId;
			return this;
		}
	}

	/**
	 * Represents metadata for a database column.
	 */
	public static class ColumnMetadata {
		private String columnName;
		private String fieldName;
		private Class<?> javaType;
		private boolean nullable;
		private int length;
		private int precision;
		private int scale;
		private boolean primaryKey;
		private boolean autoIncrement;
		private boolean version;
		private FetchType basicFetchType;
		private Boolean basicOptional;
		private TemporalType temporalType;
		private boolean lob;

		public ColumnMetadata(String columnName, String fieldName, Class<?> javaType) {
			this.columnName = columnName;
			this.fieldName = fieldName;
			this.javaType = javaType;
			this.nullable = true;
		}

		public ColumnMetadata primaryKey(boolean primaryKey) {
			this.primaryKey = primaryKey;
			this.nullable = false;
			return this;
		}

		public ColumnMetadata autoIncrement(boolean autoIncrement) {
			this.autoIncrement = autoIncrement;
			return this;
		}

		public ColumnMetadata nullable(boolean nullable) {
			this.nullable = nullable;
			return this;
		}

		public ColumnMetadata length(int length) {
			this.length = length;
			return this;
		}

		public ColumnMetadata precision(int precision) {
			this.precision = precision;
			return this;
		}

		public ColumnMetadata scale(int scale) {
			this.scale = scale;
			return this;
		}

		public ColumnMetadata version(boolean version) {
			this.version = version;
			return this;
		}

		public ColumnMetadata basicFetch(FetchType fetchType) {
			this.basicFetchType = fetchType;
			return this;
		}

		public ColumnMetadata basicOptional(boolean optional) {
			this.basicOptional = optional;
			return this;
		}

		public ColumnMetadata temporal(TemporalType temporalType) {
			this.temporalType = temporalType;
			return this;
		}

		public ColumnMetadata lob(boolean lob) {
			this.lob = lob;
			return this;
		}

		// Getters
		public String getColumnName() { return columnName; }
		public String getFieldName() { return fieldName; }
		public Class<?> getJavaType() { return javaType; }
		public boolean isNullable() { return nullable; }
		public int getLength() { return length; }
		public int getPrecision() { return precision; }
		public int getScale() { return scale; }
		public boolean isPrimaryKey() { return primaryKey; }
		public boolean isAutoIncrement() { return autoIncrement; }
		public boolean isVersion() { return version; }
		public FetchType getBasicFetchType() { return basicFetchType; }
		public boolean isBasicOptionalSet() { return basicOptional != null; }
		public boolean isBasicOptional() { return basicOptional != null ? basicOptional : true; }
		public TemporalType getTemporalType() { return temporalType; }
		public boolean isLob() { return lob; }
	}

	/**
	 * Represents metadata for a database foreign key relationship.
	 */
	public static class ForeignKeyMetadata {
		private String fieldName;
		private String foreignKeyColumnName;
		private String referencedColumnName;
		private String targetEntityClassName;
		private String targetEntityPackage;
		private FetchType fetchType;
		private boolean optional;

		public ForeignKeyMetadata(
				String fieldName,
				String foreignKeyColumnName,
				String targetEntityClassName,
				String targetEntityPackage) {
			this.fieldName = fieldName;
			this.foreignKeyColumnName = foreignKeyColumnName;
			this.targetEntityClassName = targetEntityClassName;
			this.targetEntityPackage = targetEntityPackage;
			this.optional = true;
		}

		public ForeignKeyMetadata referencedColumnName(String referencedColumnName) {
			this.referencedColumnName = referencedColumnName;
			return this;
		}

		public ForeignKeyMetadata fetchType(FetchType fetchType) {
			this.fetchType = fetchType;
			return this;
		}

		public ForeignKeyMetadata optional(boolean optional) {
			this.optional = optional;
			return this;
		}

		// Getters
		public String getFieldName() { return fieldName; }
		public String getForeignKeyColumnName() { return foreignKeyColumnName; }
		public String getReferencedColumnName() { return referencedColumnName; }
		public String getTargetEntityClassName() { return targetEntityClassName; }
		public String getTargetEntityPackage() { return targetEntityPackage; }
		public FetchType getFetchType() { return fetchType; }
		public boolean isOptional() { return optional; }
	}

	/**
	 * Represents metadata for a OneToMany (inverse) relationship.
	 */
	public static class OneToManyMetadata {
		private String fieldName;
		private String mappedBy;
		private String elementEntityClassName;
		private String elementEntityPackage;
		private FetchType fetchType;
		private CascadeType[] cascadeTypes;
		private boolean orphanRemoval;

		public OneToManyMetadata(
				String fieldName,
				String mappedBy,
				String elementEntityClassName,
				String elementEntityPackage) {
			this.fieldName = fieldName;
			this.mappedBy = mappedBy;
			this.elementEntityClassName = elementEntityClassName;
			this.elementEntityPackage = elementEntityPackage;
		}

		public OneToManyMetadata fetchType(FetchType fetchType) {
			this.fetchType = fetchType;
			return this;
		}

		public OneToManyMetadata cascade(CascadeType... cascadeTypes) {
			this.cascadeTypes = cascadeTypes;
			return this;
		}

		public OneToManyMetadata orphanRemoval(boolean orphanRemoval) {
			this.orphanRemoval = orphanRemoval;
			return this;
		}

		// Getters
		public String getFieldName() { return fieldName; }
		public String getMappedBy() { return mappedBy; }
		public String getElementEntityClassName() { return elementEntityClassName; }
		public String getElementEntityPackage() { return elementEntityPackage; }
		public FetchType getFetchType() { return fetchType; }
		public CascadeType[] getCascadeTypes() { return cascadeTypes; }
		public boolean isOrphanRemoval() { return orphanRemoval; }
	}

	/**
	 * Represents metadata for a OneToOne relationship.
	 * Supports both the owning side (with foreignKeyColumnName) and
	 * the inverse side (with mappedBy).
	 */
	public static class OneToOneMetadata {
		private String fieldName;
		private String targetEntityClassName;
		private String targetEntityPackage;
		private String foreignKeyColumnName;
		private String referencedColumnName;
		private String mappedBy;
		private FetchType fetchType;
		private CascadeType[] cascadeTypes;
		private boolean optional;
		private boolean orphanRemoval;

		public OneToOneMetadata(
				String fieldName,
				String targetEntityClassName,
				String targetEntityPackage) {
			this.fieldName = fieldName;
			this.targetEntityClassName = targetEntityClassName;
			this.targetEntityPackage = targetEntityPackage;
			this.optional = true;
		}

		public OneToOneMetadata foreignKeyColumnName(String foreignKeyColumnName) {
			this.foreignKeyColumnName = foreignKeyColumnName;
			return this;
		}

		public OneToOneMetadata referencedColumnName(String referencedColumnName) {
			this.referencedColumnName = referencedColumnName;
			return this;
		}

		public OneToOneMetadata mappedBy(String mappedBy) {
			this.mappedBy = mappedBy;
			return this;
		}

		public OneToOneMetadata fetchType(FetchType fetchType) {
			this.fetchType = fetchType;
			return this;
		}

		public OneToOneMetadata cascade(CascadeType... cascadeTypes) {
			this.cascadeTypes = cascadeTypes;
			return this;
		}

		public OneToOneMetadata optional(boolean optional) {
			this.optional = optional;
			return this;
		}

		public OneToOneMetadata orphanRemoval(boolean orphanRemoval) {
			this.orphanRemoval = orphanRemoval;
			return this;
		}

		// Getters
		public String getFieldName() { return fieldName; }
		public String getTargetEntityClassName() { return targetEntityClassName; }
		public String getTargetEntityPackage() { return targetEntityPackage; }
		public String getForeignKeyColumnName() { return foreignKeyColumnName; }
		public String getReferencedColumnName() { return referencedColumnName; }
		public String getMappedBy() { return mappedBy; }
		public FetchType getFetchType() { return fetchType; }
		public CascadeType[] getCascadeTypes() { return cascadeTypes; }
		public boolean isOptional() { return optional; }
		public boolean isOrphanRemoval() { return orphanRemoval; }
	}

	/**
	 * Represents metadata for a ManyToMany relationship.
	 * Supports both the owning side (with joinTable) and
	 * the inverse side (with mappedBy).
	 */
	public static class ManyToManyMetadata {
		private String fieldName;
		private String targetEntityClassName;
		private String targetEntityPackage;
		private String mappedBy;
		private String joinTableName;
		private String joinColumnName;
		private String inverseJoinColumnName;
		private FetchType fetchType;
		private CascadeType[] cascadeTypes;

		public ManyToManyMetadata(
				String fieldName,
				String targetEntityClassName,
				String targetEntityPackage) {
			this.fieldName = fieldName;
			this.targetEntityClassName = targetEntityClassName;
			this.targetEntityPackage = targetEntityPackage;
		}

		public ManyToManyMetadata mappedBy(String mappedBy) {
			this.mappedBy = mappedBy;
			return this;
		}

		public ManyToManyMetadata joinTable(String joinTableName, String joinColumnName, String inverseJoinColumnName) {
			this.joinTableName = joinTableName;
			this.joinColumnName = joinColumnName;
			this.inverseJoinColumnName = inverseJoinColumnName;
			return this;
		}

		public ManyToManyMetadata fetchType(FetchType fetchType) {
			this.fetchType = fetchType;
			return this;
		}

		public ManyToManyMetadata cascade(CascadeType... cascadeTypes) {
			this.cascadeTypes = cascadeTypes;
			return this;
		}

		// Getters
		public String getFieldName() { return fieldName; }
		public String getTargetEntityClassName() { return targetEntityClassName; }
		public String getTargetEntityPackage() { return targetEntityPackage; }
		public String getMappedBy() { return mappedBy; }
		public String getJoinTableName() { return joinTableName; }
		public String getJoinColumnName() { return joinColumnName; }
		public String getInverseJoinColumnName() { return inverseJoinColumnName; }
		public FetchType getFetchType() { return fetchType; }
		public CascadeType[] getCascadeTypes() { return cascadeTypes; }
	}

	/**
	 * Represents metadata for an embeddable class.
	 */
	public static class EmbeddableMetadata {
		private String className;
		private String packageName;
		private List<ColumnMetadata> columns = new ArrayList<>();

		public EmbeddableMetadata(String className, String packageName) {
			this.className = className;
			this.packageName = packageName;
		}

		public EmbeddableMetadata addColumn(ColumnMetadata column) {
			this.columns.add(column);
			return this;
		}

		// Getters
		public String getClassName() { return className; }
		public String getPackageName() { return packageName; }
		public List<ColumnMetadata> getColumns() { return columns; }
	}

	/**
	 * Represents metadata for an embedded field on an entity.
	 */
	public static class EmbeddedFieldMetadata {
		private String fieldName;
		private String embeddableClassName;
		private String embeddablePackage;
		private List<AttributeOverrideMetadata> attributeOverrides = new ArrayList<>();

		public EmbeddedFieldMetadata(
				String fieldName,
				String embeddableClassName,
				String embeddablePackage) {
			this.fieldName = fieldName;
			this.embeddableClassName = embeddableClassName;
			this.embeddablePackage = embeddablePackage;
		}

		public EmbeddedFieldMetadata addAttributeOverride(String fieldName, String columnName) {
			this.attributeOverrides.add(new AttributeOverrideMetadata(fieldName, columnName));
			return this;
		}

		// Getters
		public String getFieldName() { return fieldName; }
		public String getEmbeddableClassName() { return embeddableClassName; }
		public String getEmbeddablePackage() { return embeddablePackage; }
		public List<AttributeOverrideMetadata> getAttributeOverrides() { return attributeOverrides; }
	}

	/**
	 * Represents metadata for an @AttributeOverride mapping.
	 */
	public static class AttributeOverrideMetadata {
		private String fieldName;
		private String columnName;

		public AttributeOverrideMetadata(String fieldName, String columnName) {
			this.fieldName = fieldName;
			this.columnName = columnName;
		}

		// Getters
		public String getFieldName() { return fieldName; }
		public String getColumnName() { return columnName; }
	}

	/**
	 * Represents metadata for an @EmbeddedId composite primary key.
	 * The ID class should be created separately via createEmbeddable().
	 */
	public static class CompositeIdMetadata {
		private String fieldName;
		private String idClassName;
		private String idClassPackage;
		private List<AttributeOverrideMetadata> attributeOverrides = new ArrayList<>();

		public CompositeIdMetadata(
				String fieldName,
				String idClassName,
				String idClassPackage) {
			this.fieldName = fieldName;
			this.idClassName = idClassName;
			this.idClassPackage = idClassPackage;
		}

		public CompositeIdMetadata addAttributeOverride(String fieldName, String columnName) {
			this.attributeOverrides.add(new AttributeOverrideMetadata(fieldName, columnName));
			return this;
		}

		// Getters
		public String getFieldName() { return fieldName; }
		public String getIdClassName() { return idClassName; }
		public String getIdClassPackage() { return idClassPackage; }
		public List<AttributeOverrideMetadata> getAttributeOverrides() { return attributeOverrides; }
	}

	/**
	 * Represents metadata for JPA inheritance configuration on a root entity.
	 */
	public static class InheritanceMetadata {
		private InheritanceType strategy;
		private String discriminatorColumnName;
		private DiscriminatorType discriminatorType;
		private int discriminatorColumnLength;

		public InheritanceMetadata(InheritanceType strategy) {
			this.strategy = strategy;
		}

		public InheritanceMetadata discriminatorColumn(String discriminatorColumnName) {
			this.discriminatorColumnName = discriminatorColumnName;
			return this;
		}

		public InheritanceMetadata discriminatorType(DiscriminatorType discriminatorType) {
			this.discriminatorType = discriminatorType;
			return this;
		}

		public InheritanceMetadata discriminatorColumnLength(int discriminatorColumnLength) {
			this.discriminatorColumnLength = discriminatorColumnLength;
			return this;
		}

		// Getters
		public InheritanceType getStrategy() { return strategy; }
		public String getDiscriminatorColumnName() { return discriminatorColumnName; }
		public DiscriminatorType getDiscriminatorType() { return discriminatorType; }
		public int getDiscriminatorColumnLength() { return discriminatorColumnLength; }
	}
}
