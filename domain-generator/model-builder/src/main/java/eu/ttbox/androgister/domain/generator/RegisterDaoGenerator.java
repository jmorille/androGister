package eu.ttbox.androgister.domain.generator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
 
 

public class RegisterDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(Integer.parseInt(args[0]), args[1]);

        addProduct(schema);
        addOffer(schema);

        new DaoGenerator().generateAll(schema, args[2]);
    }

     

    private static void addProduct(Schema schema) {
        Entity product = schema.addEntity("Product");
        product.implementsInterface("eu.ttbox.androgister.domain.DomainModel");
        
        product.addIdProperty();
        product.addStringProperty("name").notNull();
        product.addStringProperty("description");
        product.addLongProperty("priceHT");
        product.addStringProperty("tag"); 
    }
    
    private static void addOffer(Schema schema) {
        Entity offer = schema.addEntity("Offer");
        offer.addIdProperty();
        offer.addStringProperty("name").notNull();
        offer.addStringProperty("description");
        offer.addIntProperty("priceHT");
        offer.addStringProperty("tag"); 
    }
    
    private static void addTag(Schema schema) {
        Entity note = schema.addEntity("Tag");
        note.addIdProperty();
        note.addStringProperty("name").notNull();
        note.addStringProperty("comment");
        note.addDateProperty("date");
    }

}
