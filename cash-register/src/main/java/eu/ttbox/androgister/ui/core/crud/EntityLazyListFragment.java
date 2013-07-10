package eu.ttbox.androgister.ui.core.crud;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.Toast;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import eu.ttbox.androgister.AndroGisterApplication;
import eu.ttbox.androgister.domain.DaoSession;
import eu.ttbox.androgister.domain.DomainModel;
import eu.ttbox.androgister.domain.core.LazyListAdapter;

public abstract class EntityLazyListFragment<T extends DomainModel, DAO extends AbstractDao<T, Long>> extends Fragment {

    private static final String TAG = "EntityLazyListFragment";

    public static final int ENTITY_EDIT_REQUEST_CODE = 192;

    // Service
    public DAO entityDao;

    // Binding
    public AdapterView<ListAdapter> entitiesList;

    public LazyListAdapter<T, ? extends Object> listAdapter;

    // ===========================================================
    // Constructors
    // ===========================================================

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Service
        entityDao = getEntityDao();
        entitiesList = getAdapterContainer();
        listAdapter = createListAdapter(null);
        entitiesList.setAdapter(listAdapter);
        // List
        loadData(null);
        // Listener
        entitiesList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                T item = (T) parent.getItemAtPosition(position);
                if (item != null) {
                    Long entityId = item != null ? item.getId() : null;
                    onEntityClick(entityId);
                }
            }

        });
    }

    @Override
    public void onDestroyView() {
        if (listAdapter != null) {
            // Close LazyListAdpater for closing LazyList
            listAdapter.close();
            listAdapter = null;
        }
        super.onDestroyView();
    }

    // ===========================================================
    // Event
    // ===========================================================

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActivityResult :  requestCode = " + requestCode + "  ==> resultCode = " + resultCode);
        if (requestCode == ENTITY_EDIT_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Toast.makeText(getActivity(), "Success Edit", Toast.LENGTH_LONG).show();
            reloadData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // ===========================================================
    // Load Data
    // ===========================================================

    public void reloadData() {
        Log.d(TAG, "Begin reloadData");
        QueryBuilder<T> query = createSearchQuery(entityDao);
        listAdapter.changeCursor(query.listLazy());
    }

    public void loadData(Bundle bundle) {
        // entityDao.queryBuilder()
        QueryBuilder<T> query = createSearchQuery(entityDao);
        LazyList<T> entities = query.listLazy();
        listAdapter.changeCursor(entities);

    }

    public abstract QueryBuilder<T> createSearchQuery(DAO entityDao);

    public abstract LazyListAdapter<T, ? extends Object> createListAdapter(LazyList<T> lazyList);

    public abstract AdapterView<ListAdapter> getAdapterContainer();

    // ===========================================================
    // Services
    // ===========================================================

    public DaoSession getDaoSession() {
        AndroGisterApplication app = (AndroGisterApplication) getActivity().getApplication();
        return app.getDaoSession();
    }

    public abstract DAO getEntityDao();

    public abstract void onEntityClick(Long id);

}
