package eu.ttbox.androgister.database;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;
 
 

public class GreenDaoInitializer {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(Integer.parseInt(args[0]), args[1]);

        addProduct(schema);
        addOffer(schema);

        new DaoGenerator().generateAll(schema, args[2]);
    }

    
    public static void init() {
        Schema schema = new Schema(1, "eu.ttbox.androgister");
        addProduct(schema);
        addOffer(schema);
        String outDir = "";
        new DaoGenerator().generateAll(schema, outDir);
    }

    private static void addProduct(Schema schema) {
        Entity product = schema.addEntity("Product");
        product.addIdProperty();
        product.addStringProperty("name").notNull();
        product.addStringProperty("description");
        product.addIntProperty("priceHT");
        product.addStringProperty("tag"); 
    }
    
    private static void addOffer(Schema schema) {
        Entity offer = schema.addEntity("Product");
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
