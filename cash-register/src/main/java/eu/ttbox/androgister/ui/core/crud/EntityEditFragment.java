package eu.ttbox.androgister.ui.core.crud;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import de.greenrobot.dao.AbstractDao;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.DaoSession;
import eu.ttbox.androgister.domain.DomainModel;
import eu.ttbox.androgister.ui.core.validator.Form;

public abstract class EntityEditFragment<T extends DomainModel> extends Fragment {

    private static final String TAG = "EntityEditFragment";

    // Service
    private AbstractDao<T, Long> entityDao;
    private Form formValidator;

    // Instance
    public T entity;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onActivityCreated BEGIN");
        super.onActivityCreated(savedInstanceState);

        // Service
        entityDao = getEntityDao();
        // Form
        formValidator = createValidator(getActivity());

        // Load Data
        // Bundle args = getArguments();
        // loadEntity(args);
    }

    // ===========================================================
    // Menu
    // ===========================================================

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.admin_edit_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_admin_edit_save:
            onSaveClick();
            return true;
        case R.id.menu_admin_edit_cancel:
            onCancelClick();
            return true;
        case R.id.menu_admin_edit_delete:
            onDeleteClick();
            return true;
        }
        return false;
    }

    // ===========================================================
    // Services
    // ===========================================================

    public DaoSession getDaoSession() {
        AndroGisterApplication app = (AndroGisterApplication) getActivity().getApplication();
        return app.getDaoSession();
    }

    public abstract AbstractDao<T, Long> getEntityDao();

    public abstract Form createValidator(Context context);

    // ===========================================================
    // Bindings
    // ===========================================================

    public abstract void bindView(T entity);

    public abstract T bindValue(T entity);

    // ===========================================================
    // Load
    // ===========================================================

    public void handleIntent(Intent intent) {
        loadEntity(intent.getExtras());
    }

    private void loadEntity(Bundle args) {
        if (args != null && args.containsKey(Intent.EXTRA_UID)) {
            Long entityId = args.getLong(Intent.EXTRA_UID);
            Log.d(TAG, "Edit Entity Id : " + entityId);
            entity = entityDao.load(entityId);
            bindView(entity);
        } else {
            Log.d(TAG, "Prepare new Entity");
            // prepare for insert
            entity = prepareInsert(args);
        }
    }

    public abstract T prepareInsert(Bundle args);

    // ===========================================================
    // Action
    // ===========================================================

    public void onCancelClick() {
        getActivity().setResult(Activity.RESULT_CANCELED);
        getActivity().finish();
    }

    public void onDeleteClick() {
        if (entity != null && entity.getId() != null) {
            entityDao.delete(entity);
            getActivity().setResult(Activity.RESULT_OK);
        } else {
            getActivity().setResult(Activity.RESULT_CANCELED);
        }
        getActivity().finish();
    }

    public void onSaveClick() {
        if (formValidator.validate()) {
            bindValue(entity);
            // save
            if (entity.getId() != null) {
                entityDao.update(entity);
                Log.d(TAG, "Entity Updated Id :" + entity.getId());
            } else {
                long productId = entityDao.insert(entity);
                Log.d(TAG, "Entity Inserted Id : " + productId + " ===> propagate to Entity Id : " + entity.getId());
            }

            // productDao.insertOrReplace(entity);
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        } else {
            Toast.makeText(getActivity(), "Invalid Form", Toast.LENGTH_LONG).show();
        }
    }

}
