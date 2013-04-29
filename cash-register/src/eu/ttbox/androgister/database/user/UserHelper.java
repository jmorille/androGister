package eu.ttbox.androgister.database.user;

import android.content.ContentValues;
import android.database.Cursor;
import android.widget.TextView;
import eu.ttbox.androgister.database.user.UserDatabase.UserColumns;
import eu.ttbox.androgister.model.user.User;

@Deprecated
public class UserHelper {

    boolean isNotInit = true;
    public int idIdx = -1;
    public int lastnameIdx = -1;
    public int firstnameIdx = -1;
    public int matriculeIdx = -1;
    public int tagIdx = -1;
    public int passwordIdx = -1;

    public UserHelper initWrapper(Cursor cursor) {
        idIdx = cursor.getColumnIndex(UserColumns.KEY_ID);
        lastnameIdx = cursor.getColumnIndex(UserColumns.KEY_LASTNAME);
        firstnameIdx = cursor.getColumnIndex(UserColumns.KEY_FIRSTNAME);
        matriculeIdx = cursor.getColumnIndex(UserColumns.KEY_MATRICULE);
        
        tagIdx = cursor.getColumnIndex(UserColumns.KEY_TAG);
        passwordIdx = cursor.getColumnIndex(UserColumns.KEY_PASSWORD);
        isNotInit = false;
        return this;
    }

    public User getEntity(Cursor cursor) {
        if (isNotInit) {
            initWrapper(cursor);
        }
        User user = new User();
        user.setId(idIdx > -1 ? cursor.getLong(idIdx) : -1);
        user.setLastname(lastnameIdx > -1 ? cursor.getString(lastnameIdx) : null);
        // Description
        user.setFirstname(firstnameIdx > -1 ? cursor.getString(firstnameIdx) : null);
        // Ean
        user.setLogin(matriculeIdx > -1 ? cursor.getString(matriculeIdx) : null);
        // Tag
        user.setTag(tagIdx > -1 ? cursor.getString(tagIdx) : null);
        // Price 
        user.setPassword(passwordIdx > -1 ? cursor.getString(passwordIdx) : null);
        return user;
    }
    
    private UserHelper setTextWithIdx(TextView view, Cursor cursor, int idx) {
        view.setText(cursor.getString(idx));
        return this;
    }

    public UserHelper setTextUserId(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, idIdx);
    }

    public String getUserIdAsString( Cursor cursor) {
        return cursor.getString(idIdx);
    }
    public long getUserId( Cursor cursor) {
        return cursor.getLong(idIdx);
    }
    
    public UserHelper setTextUserLastname(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, lastnameIdx);
    }

    public UserHelper setTextUserFirstname(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, firstnameIdx);
    }
    
    public UserHelper setTextUserMatricule(TextView view, Cursor cursor) {
        return setTextWithIdx(view, cursor, matriculeIdx);
    }
    
    public UserHelper setTextUserPassword(TextView view, Cursor cursor) {
        String passwordSum = cursor.getString(passwordIdx);
        // TODO Hide it
        String passwordText = passwordSum;
        view.setText(passwordText);
        return this;
    }

    public static ContentValues getContentValues(User user) {
        ContentValues initialValues = new ContentValues();
        if (user.id > -1) {
            initialValues.put(UserColumns.KEY_ID, Long.valueOf(user.id));
        }
        initialValues.put(UserColumns.KEY_LASTNAME, user.lastname);
        initialValues.put(UserColumns.KEY_FIRSTNAME, user.firstname);
        initialValues.put(UserColumns.KEY_MATRICULE, user.login);
        initialValues.put(UserColumns.KEY_TAG, user.tag);
        initialValues.put(UserColumns.KEY_PASSWORD, user.password); 
        return initialValues;
    }

}
