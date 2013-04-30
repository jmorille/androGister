package eu.ttbox.androgister.domain.provider;

import java.util.Map;

import android.app.backup.BackupManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import de.greenrobot.dao.AbstractDao;
import eu.ttbox.androgister.domain.DomainModel;

public abstract class AbstractGreenContentProvider<MODEL extends DomainModel> extends ContentProvider {

    public AbstractDao<MODEL, Long> entityDao;

    public static final int ENTITIES = 0;
    public static final int ENTITY = 1;

    private static final String TAG = "AbstractGreenContentProvider";

    @Override
    public boolean onCreate() {
        entityDao = getEntityDao();
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query for uri : " + uri);
        // Use the UriMatcher to see what kind of query we have and format the
        // db query accordingly
        switch (matchUriMatcher(uri)) {
        case ENTITIES:
            return queryEntities(projection, selection, selectionArgs, sortOrder);
        case ENTITY:
            String entityId = uri.getLastPathSegment();
            String mergedSelection  = mergeQuerySelectionClause(selection, getSelectClauseByEntityId());
            String[] mergedArgs = mergeQuerySelectionArgsClause(selectionArgs, new String[] { entityId });
            Log.d(TAG, "mergerSelection : " + mergedSelection);
            Log.d(TAG, "mergedArgs : " + mergedArgs.length); 
            return queryEntities(projection, mergedSelection, mergedArgs, null);
        default:
            throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }
    
    public String mergeQuerySelectionClause(String selection, String other) {
        String selectionMerged = other;
        if (!TextUtils.isEmpty(selection)) {
            selectionMerged = String.format("%s and (%s)",  other, selection);
        }
        return selectionMerged;
    }
    
    public String[] mergeQuerySelectionArgsClause(String[] selectionArgs, String[] otherArgs) {
        String[] args  = otherArgs;
        if (selectionArgs !=null && selectionArgs.length>0) {
            int pSelectionArgSize = selectionArgs.length;
            int otherArgSize = otherArgs.length;
            args  = new String[pSelectionArgSize +otherArgSize];
            System.arraycopy(otherArgs, 0, args, 0, otherArgSize);
            System.arraycopy(selectionArgs, 0, args, otherArgSize, pSelectionArgSize);
        }
        return args;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (matchUriMatcher(uri)) {
        case ENTITIES:
            long entityId = insertEntity(values);
            Uri entityUri = null;
            if (entityId > -1) {
                entityUri = getEntityUri(entityId);
                getContext().getContentResolver().notifyChange(uri, null);
                // Backup
                BackupManager.dataChanged(getContext().getPackageName());
            }
            return entityUri;
        default:
            throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        switch (matchUriMatcher(uri)) {
        case ENTITY:
            String entityId = uri.getLastPathSegment();
            String[] args = new String[] { entityId };
            count = updateEntity(values, getSelectClauseByEntityId(), args);
            break;
        case ENTITIES:
            count = updateEntity(values, selection, selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            // Backup
            BackupManager.dataChanged(getContext().getPackageName());
        }
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (matchUriMatcher(uri)) {
        case ENTITY:
            String entityId = uri.getLastPathSegment();
            String[] args = new String[] { entityId };
            count = deleteEntity(getSelectClauseByEntityId(), args);
            break;
        case ENTITIES:
            count = deleteEntity(selection, selectionArgs);
            break;
        default:
            throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
            // Backup
            BackupManager.dataChanged(getContext().getPackageName());
        }
        return count;
    }

    public long insertEntity(ContentValues values) throws SQLException {
        long result = -1;
        SQLiteDatabase db = entityDao.getDatabase();
        try {
            // normalizedContentValues(values);
            db.beginTransaction();
            try {
                result = db.insertOrThrow(entityDao.getTablename(), null, values);
                // commit
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
        return result;
    }

    public int updateEntity(ContentValues values, String selection, String[] selectionArgs) {
        int result = -1;
        // normalizedContentValues(values);
        SQLiteDatabase db = entityDao.getDatabase();
        try {
            db.beginTransaction();
            try {
                result = db.update(entityDao.getTablename(), values, selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
        return result;
    }

    public int deleteEntity(String selection, String[] selectionArgs) {
        int result = -1;
        SQLiteDatabase db = entityDao.getDatabase();
        try {
            db.beginTransaction();
            try {
                result = db.delete(entityDao.getTablename(), selection, selectionArgs);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } finally {
            db.close();
        }
        return result;
    }

    public Cursor queryEntities(String[] _projection, String selection, String[] selectionArgs, String order) {
        // Params
        String[] projection = _projection == null ? entityDao.getAllColumns() : _projection;
        // Query
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(entityDao.getTablename());
        builder.setProjectionMap(getEntityColumnMap());
        Cursor cursor = builder.query(entityDao.getDatabase(), projection, selection, selectionArgs, null, null, order);
        return cursor;
    }

    public abstract AbstractDao<MODEL, Long> getEntityDao();
    
    public abstract Map<String, String> getEntityColumnMap();
 
    public abstract String getSelectClauseByEntityId();

    public abstract int matchUriMatcher(Uri uri);
    
    public abstract Uri getEntityUri(long entityId) ;
}
