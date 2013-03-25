package eu.ttbox.androgister.ui.admin.tag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import de.greenrobot.dao.query.LazyList;
import de.greenrobot.dao.query.QueryBuilder;
import eu.ttbox.androgister.R;
import eu.ttbox.androgister.domain.Tag;
import eu.ttbox.androgister.domain.TagDao;
import eu.ttbox.androgister.domain.TagDao.Properties;
import eu.ttbox.androgister.ui.core.crud.EntityLazyListFragment;

public class TagListFragment  extends EntityLazyListFragment<Tag, TagDao> {

    private static final String TAG = "TagListFragment";

    public static final int PRODUCT_EDIT_REQUEST_CODE = 111;

    // Binding
    ListView listView;

 // Listener

    private OnSelectTagListener onSelectTagListener;

    // ===========================================================
    // Listener
    // ===========================================================

    public void setOnSelectTagListener(OnSelectTagListener onSelectTagListener) {
        this.onSelectTagListener = onSelectTagListener;
    }

    public interface OnSelectTagListener {

        void onSelectTagId(Long tagId);

    }

    
    // ===========================================================
    // Constructor
    // ===========================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.admin_calatog_list, container, false);
        // Binding
        listView = (ListView) v.findViewById(R.id.calalog_list);

        return v;
    }

    
    public AdapterView<ListAdapter> getAdapterContainer() {
        return listView;
    }
    // ===========================================================
    // Service
    // ===========================================================

    @Override
    public TagDao getEntityDao() {
        return getDaoSession().getTagDao();
    }

    @Override
    public QueryBuilder<Tag> createSearchQuery(TagDao entityDao) {
        QueryBuilder<Tag> query = entityDao.queryBuilder() //
                .orderAsc(Properties.Name); //
        return query;
    }

    @Override
    public TagListAdapter createListAdapter(LazyList<Tag> lazyList) {
        return new TagListAdapter(getActivity(), lazyList);
    }

    // ===========================================================
    // Action
    // ===========================================================

    @Override
    public void onEntityClick(Long tagId) {
        if (onSelectTagListener != null) {
            onSelectTagListener.onSelectTagId(  tagId);
        } 

    }
}
