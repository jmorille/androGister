package eu.ttbox.androgister.domain.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Index;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class RegisterDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(Integer.parseInt(args[0]), args[1]);

        Entity taxe = addTaxe(schema);

        Entity tag = addTag(schema);
        Entity product = addProduct(schema, tag, taxe);
        Entity catalog = addCatalog(schema);

        addCatalogProduct(schema, catalog, product);
        addOffer(schema);

        
        // Person
        addPerson(schema);
        addUser(schema);
        
        // Order
        addOrder(schema);
        
        
        new DaoGenerator().generateAll(schema, args[2]);
    }

    private static void implementsVersionning(Entity entity) {
        entity.implementsInterface("eu.ttbox.androgister.domain.VersioningModel");
        entity.addLongProperty("creationDate");
        entity.addLongProperty("versionDate");
    }

    private static void implementsSync(Entity entity) {
        // entity.implementsInterface("eu.ttbox.androgister.domain.VersioningModel");
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
        product.addStringProperty("ean");
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
//                .index() //
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

    private static Entity addPerson(Schema schema) {
        Entity person = schema.addEntity("Person");
        implementsDomainModel(person);

        person.addStringProperty("lastname");
        person.addStringProperty("firstname");
        person.addStringProperty("matricule");
 
        person.addStringProperty("tag");
        person.addLongProperty("priceHT");
        
 
        return person;
    }
    
    private static Entity addUser(Schema schema) {
        Entity user = schema.addEntity("User");
        implementsDomainModel(user);

        user.addStringProperty("lastname");
        user.addStringProperty("firstname");
        user.addStringProperty("login"); 
        user.addStringProperty("password");  
        return user;
    }
    
    private static Entity addOrder(Schema schema) {
        Entity order = schema.addEntity("Order");
        order.setTableName("ORDERS"); // "ORDER" is a reserved keyword
        order.setHasKeepSections(Boolean.TRUE);
        order.implementsParceable();
        
        implementsDomainModel(order);
        
        // Properties
        order.addLongProperty("orderNumber").notNull(); // KEY_ORDER_NUMBER
        order.addLongProperty("orderDate").notNull(); // KEY_ORDER_DATE
        order.addStringProperty("orderUUID").notNull(); // KEY_ORDER_UUID
        order.addStringProperty("orderDeleteUUID"); // KEY_ORDER_DELETE_UUID

        order.addIntProperty("statusId").notNull(); // KEY_STATUS
        order.addIntProperty("paymentModeId"); // KEY_PAYMENT_MODE
        order.addLongProperty("priceSumHT").notNull(); // KEY_PRICE_SUM_HT
        
        
        order.addLongProperty("personId"); // KEY_PERS_ID
        order.addStringProperty("personMatricule"); // KEY_PERS_MATRICULE
        order.addStringProperty("personFirstname"); // KEY_PERS_FIRSTNAME
        order.addStringProperty("personLastname"); // KEY_PERS_LASTNAME

        // Items
        Entity orderItem  = addOrderItem(schema);
        Property orderFk =  orderItem.addLongProperty("orderId").notNull().getProperty();
        ToMany orderToOrderItems = order.addToMany(orderItem, orderFk);
        orderToOrderItems.setName("items");
//        orderToOrderItems.orderAsc(properties);
        
        return order;
    }

    private static Entity addOrderItem(Schema schema) {
        Entity orderItem = schema.addEntity("OrderItem");
        orderItem.implementsParceable();
        implementsDomainModel(orderItem);
        // Properties
        orderItem.addStringProperty("name"); // KEY_NAME 
        orderItem.addLongProperty("productId"); //"PRODUCT_ID";
        orderItem.addStringProperty("ean"); // KEY_EAN = "EAN";
        orderItem.addIntProperty("quantity").notNull(); // KEY_QUANTITY  
        orderItem.addLongProperty("priceUnitHT").notNull();// KEY_PRICE_UNIT_HT  
        orderItem.addLongProperty("priceSumHT").notNull(); // KEY_PRICE_SUM_HT  
        return orderItem;
    }

}
