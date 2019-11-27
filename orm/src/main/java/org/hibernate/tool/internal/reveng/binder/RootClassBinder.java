package org.hibernate.tool.internal.reveng.binder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.DuplicateMappingException;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Table;
import org.hibernate.tool.api.reveng.DatabaseCollector;
import org.hibernate.tool.api.reveng.ReverseEngineeringStrategy;
import org.hibernate.tool.api.reveng.TableIdentifier;
import org.hibernate.tool.internal.reveng.PrimaryKeyInfo;
import org.hibernate.tool.internal.reveng.RevEngUtils;

public class RootClassBinder {
	
	private static final Logger LOGGER = Logger.getLogger(RootClassBinder.class.getName());
	
	public static RootClassBinder create(
			MetadataBuildingContext metadataBuildingContext,
			InFlightMetadataCollector metadataCollector,
			ReverseEngineeringStrategy revengStrategy,
			String defaultCatalog,
			String defaultSchema,
			boolean preferBasicCompositeIds) {
		return new RootClassBinder(
				metadataBuildingContext, 
				metadataCollector, 
				revengStrategy, 
				defaultCatalog, 
				defaultSchema, 
				preferBasicCompositeIds);
	}
	
	private final MetadataBuildingContext metadataBuildingContext;
	private final InFlightMetadataCollector metadataCollector;
	private final ReverseEngineeringStrategy revengStrategy;
	private final String defaultCatalog;
	private final String defaultSchema;
	private final boolean preferBasicCompositeIds;
	
	private RootClassBinder(
			MetadataBuildingContext metadataBuildingContext,
			InFlightMetadataCollector metadataCollector,
			ReverseEngineeringStrategy revengStrategy,
			String defaultCatalog,
			String defaultSchema,
			boolean preferBasicCompositeIds) {
		this.metadataBuildingContext = metadataBuildingContext;
		this.metadataCollector = metadataCollector;
		this.revengStrategy = revengStrategy;
		this.defaultCatalog = defaultCatalog;
		this.defaultSchema = defaultSchema;
		this.preferBasicCompositeIds = preferBasicCompositeIds;
	}

	public void bind(Table table, DatabaseCollector collector, Mapping mapping) {
		if (table.getCatalog() != null && table.getCatalog().equals(defaultCatalog)) {
			table.setCatalog(null);
		}
		if (table.getSchema() != null && table.getSchema().equals(defaultSchema)) {
			table.setSchema(null);
		}   	
		RootClass rc = new RootClass(metadataBuildingContext);
		TableIdentifier tableIdentifier = TableIdentifier.create(table);
		String className = revengStrategy.tableToClassName( tableIdentifier );
		LOGGER.log(Level.INFO, "Building entity " + className + " based on " + tableIdentifier);
		rc.setEntityName( className );
		rc.setJpaEntityName( StringHelper.unqualify( className ) );
		rc.setClassName( className );
		rc.setProxyInterfaceName( rc.getEntityName() ); // TODO: configurable ?
		rc.setLazy(true);

		rc.setMetaAttributes(
				BinderUtils.safeMap(
						RevEngUtils.getTableToMetaAttributesInRevengStrategy(
								revengStrategy, 
								table, 
								defaultCatalog, 
								defaultSchema)));


		rc.setDiscriminatorValue( rc.getEntityName() );
		rc.setTable(table);
		try {
			metadataCollector.addEntityBinding(rc);
		} catch(DuplicateMappingException dme) {
			// TODO: detect this and generate a "permutation" of it ?
			PersistentClass class1 = metadataCollector.getEntityBinding(dme.getName());
			Table table2 = class1.getTable();
			throw new RuntimeException("Duplicate class name '" + rc.getEntityName() + "' generated for '" + table + "'. Same name where generated for '" + table2 + "'");
		}
		metadataCollector.addImport( rc.getEntityName(), rc.getEntityName() );

		Set<Column> processed = new HashSet<Column>();
		
		PrimaryKeyBinder primaryKeyBinder = PrimaryKeyBinder
				.create(
						metadataBuildingContext, 
						metadataCollector, 
						revengStrategy, 
						defaultCatalog, 
						defaultSchema, 
						preferBasicCompositeIds);
		PrimaryKeyInfo pki = primaryKeyBinder.bind(
						table, 
						rc, 
						processed, 
						mapping, 
						collector);
		
		VersionPropertyBinder
			.create(
					metadataBuildingContext, 
					metadataCollector, 
					revengStrategy, 
					defaultCatalog, 
					defaultSchema)
			.bind(
					table, 
					rc, 
					processed, 
					mapping);
		
		bindOutgoingForeignKeys(table, rc, processed);
		bindColumnsToProperties(table, rc, processed, mapping);
		bindIncomingForeignKeys(rc, processed, collector, mapping);
		primaryKeyBinder.updatePrimaryKey(rc, pki);
		
	}

	private void bindIncomingForeignKeys(PersistentClass rc, Set<Column> processed, DatabaseCollector collector, Mapping mapping) {
		List<ForeignKey> foreignKeys = collector.getOneToManyCandidates().get(rc.getEntityName());
		if(foreignKeys!=null) {
			ForeignKeyBinder foreignKeyBinder = ForeignKeyBinder.create(
					metadataBuildingContext, 
					metadataCollector, 
					revengStrategy, 
					defaultCatalog, 
					defaultSchema);
			for (Iterator<ForeignKey> iter = foreignKeys.iterator(); iter.hasNext();) {
				foreignKeyBinder.bindIncoming(iter.next(), rc, processed, mapping);
			}
		}
	}


	private void bindOutgoingForeignKeys(Table table, RootClass rc, Set<Column> processedColumns) {

		ForeignKeyBinder foreignKeyBinder = ForeignKeyBinder.create(
				metadataBuildingContext, 
				metadataCollector, 
				revengStrategy, 
				defaultCatalog, 
				defaultSchema);

		// Iterate the outgoing foreign keys and create many-to-one's
		for(Iterator<?> iterator = table.getForeignKeyIterator(); iterator.hasNext();) {
			ForeignKey foreignKey = (ForeignKey) iterator.next();

			boolean mutable = true;
            if ( contains( foreignKey.getColumnIterator(), processedColumns ) ) {
				if ( !preferBasicCompositeIds ) continue; //it's in the pk, so skip this one
				mutable = false;
            }
            
            foreignKeyBinder.bindOutgoing(foreignKey, table, rc, processedColumns, mutable);

		}
	}

	private void bindColumnsToProperties(Table table, RootClass rc, Set<Column> processedColumns, Mapping mapping) {
		for (Iterator<?> iterator = table.getColumnIterator(); iterator.hasNext();) {
			Column column = (Column) iterator.next();
			if ( !processedColumns.contains(column) ) {
				BinderUtils.checkColumnForMultipleBinding(column);
				String propertyName = 
						RevEngUtils.getColumnToPropertyNameInRevengStrategy(
								revengStrategy, 
								table, 
								defaultCatalog, 
								defaultSchema, 
								column.getName());				
				Property property = BasicPropertyBinder
						.create(
								metadataBuildingContext, 
								metadataCollector, 
								revengStrategy, 
								defaultCatalog, 
								defaultCatalog)
						.bind(BinderUtils.makeUnique(rc,propertyName), table, column, mapping);
				rc.addProperty(property);
			}
		}
	}

    private boolean contains(Iterator<Column> columnIterator, Set<Column> processedColumns) {
        while (columnIterator.hasNext() ) {
            Column element = (Column) columnIterator.next();
            if(processedColumns.contains(element) ) {
                return true;
            }
        }
        return false;
    }

}
