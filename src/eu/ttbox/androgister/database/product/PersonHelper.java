package eu.ttbox.androgister.database.product;

import android.content.ContentValues;
import android.database.Cursor;
import android.widget.TextView;
import eu.ttbox.androgister.database.product.PersonDatabase.PersonColumns;
import eu.ttbox.androgister.model.Person;
import eu.ttbox.androgister.model.PriceHelper;

public class PersonHelper {

    boolean isNotInit = true;
    public int idIdx = -1;
    public int lastnameIdx = -1;
    public int firstnameIdx = -1;
    public int matriculeIdx = -1;
    public int tagIdx = -1;
    public int priceIdx = -1;

    public PersonHelper initWrapper(Cursor cursor) {
        idIdx = cursor.getColumnIndex(PersonColumns.KEY_ID);
        lastnameIdx = cursor.getColumnIndex(PersonColumns.KEY_LASTNAME);
        firstnameIdx = cursor.getColumnIndex(PersonColumns.KEY_FIRSTNAME);
        matriculeIdx = cursor.getColumnIndex(PersonColumns.KEY_MATRICULE);
        
        tagIdx = cursor.getColumnIndex(PersonColumns.KEY_TAG);
        priceIdx = cursor.getColumnIndex(PersonColumns.KEY_PRICEHT);
        isNotInit = false;
        return this;
    }

    public Person getEntity(Cursor cursor) {
        if (isNotInit) {
            initWrapper(cursor);
        }
        Person product = new Person();
        product.setId(idIdx > -1 ? cursor.getLong(idIdx) : -1);
        product.setLastname(lastnameIdx > -1 ? cursor.getString(lastnameIdx) : null);
        // Description
        product.setFirstname(firstnameIdx > -1 ? cursor.getString(firstnameIdx) : null);
        // Ean
        product.setMatricule(matriculeIdx > -1 ? cursor.getString(matriculeIdx) : null);
        // Tag
        product.setTag(tagIdx > -1 ? cursor.getString(tagIdx) : null);
        // Price 
        product.setPriceHT(priceIdx > -1 ? cursor.getLong(priceIdx) : -1);
        return product;
    }
    
    private PersonHelper setTextWithIdx(TextView view, Cursor cursor, int idx) {
        view.setText(cursor.getString(idx));
        return this;
    }

    public PersonHelper setTextPersonId(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, idIdx);
    }

    public PersonHelper setTextPersonLastname(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, lastnameIdx);
    }

    public PersonHelper setTextPersonFirstname(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, firstnameIdx);
    }
    
    public PersonHelper setTextPersonMatricule(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, matriculeIdx);
    }
    
    public PersonHelper setTextPersonPrice(TextView view, Cursor cursor) {
        long priceSum = cursor.getLong(priceIdx);
        String priceText = PriceHelper.getToStringPrice(priceSum);
        view.setText(priceText);
        return this;
    }

    public static ContentValues getContentValues(Person person) {
        ContentValues initialValues = new ContentValues();
        if (person.getId() > -1) {
            initialValues.put(PersonColumns.KEY_ID, Long.valueOf(person.getId()));
        }
        initialValues.put(PersonColumns.KEY_LASTNAME, person.getLastname());
        initialValues.put(PersonColumns.KEY_FIRSTNAME, person.getFirstname());
        initialValues.put(PersonColumns.KEY_MATRICULE, person.getMatricule());
        initialValues.put(PersonColumns.KEY_TAG, person.getTag());
        initialValues.put(PersonColumns.KEY_PRICEHT, Long.valueOf(person.getPriceHT())); 
        return initialValues;
    }

}
