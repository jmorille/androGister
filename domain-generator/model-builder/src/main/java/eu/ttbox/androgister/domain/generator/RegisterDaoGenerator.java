package eu.ttbox.androgister.domain.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

public class RegisterDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(Integer.parseInt(args[0]), args[1]);

        Entity taxe = addTaxe(schema);

        Entity tag = addTag(schema);
        Entity product = addProduct(schema, tag, taxe);
        Entity catalog = addCatalog(schema);

        addCatalogProduct(schema, catalog, product);

        addOffer(schema);
        
        
        new DaoGenerator().generateAll(schema, args[2]);
    }
 

    
    private static void implementsVersionning(Entity entity) {
        entity.implementsInterface("eu.ttbox.androgister.domain.VersioningModel");
        entity.addLongProperty("creationDate");
        entity.addLongProperty("versionDate");
    }
    
    private static void implementsSync(Entity entity) {
//        entity.implementsInterface("eu.ttbox.androgister.domain.VersioningModel");
        entity.addBooleanProperty("dirty").notNull();
        entity.addBooleanProperty("deleted").notNull();
        entity.addStringProperty("serverUuid");
        entity.addLongProperty("serverVersion");
    }
    

    private static Entity implementsDomainModel(Entity entity) {
        entity.implementsInterface("eu.ttbox.androgister.domain.DomainModel");
        entity.addIdProperty();
        return entity;
    }

    private static Entity addCatalogProduct(Schema schema, Entity catalog, Entity product) {
        Entity catalogProduct = schema.addEntity("CatalogProduct");
        implementsDomainModel(catalogProduct);
        // Add Property
        Property catalogFk = catalogProduct.addLongProperty("catalogId") //
                .getProperty();
        Property productFk = catalogProduct.addLongProperty("productId") //
                .getProperty();
        catalogProduct.addToOne(catalog, catalogFk);
        catalogProduct.addToOne(product, productFk);

        // Index
        Index indexPk = new Index().makeUnique();
        indexPk.addPropertyAsc(catalogFk);
        indexPk.addPropertyAsc(productFk);
        catalogProduct.addIndex(indexPk);

        return catalogProduct;
    }

    private static Entity addProduct(Schema schema, Entity tag, Entity taxe) {
        Entity product = schema.addEntity("Product");//
        implementsDomainModel(product);
        implementsVersionning(product);
        implementsSync(product);
        // Properties
        product.addStringProperty("name").notNull();
        product.addStringProperty("description");
        product.addLongProperty("priceHT").notNull();
        // product.addStringProperty("tag");

        // Tag
        Property tagFk = product.addLongProperty("tagId")//
                .index() //
                .notNull() //
                .getProperty();
        product.addToOne(tag, tagFk);

        // taxes
        Property taxeFk = product.addLongProperty("taxeId")//
                .index() //
                // TDOO .notNull() //
                .getProperty();
        product.addToOne(taxe, taxeFk);

        return product;
    }

    private static Entity addTag(Schema schema) {
        Entity tag = schema.addEntity("Tag");
        implementsDomainModel(tag);
        // Properties
        tag.addStringProperty("name").notNull();
        tag.addIntProperty("color").notNull();
        return tag;
    }

    private static Entity addCatalog(Schema schema) {
        Entity catalog = schema.addEntity("Catalog");
        implementsDomainModel(catalog);
        // Properties

        catalog.addStringProperty("name").notNull();
        catalog.addBooleanProperty("enabled").notNull();
        return catalog;
    }

    private static Entity addTaxe(Schema schema) {
        Entity taxe = schema.addEntity("Taxe");
        implementsDomainModel(taxe);
        // Properties
        // Primary
        taxe.addStringProperty("title").notNull();
        taxe.addStringProperty("taxeName").notNull();
        taxe.addFloatProperty("taxePercent").notNull();
        // Alternate
        taxe.addStringProperty("alternateName");
        taxe.addFloatProperty("alternateTaxePercent");
        return taxe;
    }

    private static Entity addOffer(Schema schema) {
        Entity offer = schema.addEntity("Offer");
        implementsDomainModel(offer);
        implementsVersionning(offer);
        // Properties
        offer.addStringProperty("name").notNull();
        offer.addStringProperty("description");
        offer.addIntProperty("priceHT");
        offer.addStringProperty("tag");
        return offer;
    }

}
