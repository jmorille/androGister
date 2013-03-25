package eu.ttbox.androgister.ui.admin.tag;

import android.os.Bundle;
import android.util.Log;
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

public class TagListFragment extends EntityLazyListFragment<Tag, TagDao> {

    private static final String TAG = "TagListFragment";

    public static final int PRODUCT_EDIT_REQUEST_CODE = 111;
    
    private static final Long UNSET_ID = Long.MIN_VALUE;
    private static final Long ADD_ID = Long.MAX_VALUE;

    // Binding
    ListView listView;

    // Listener

    private OnSelectTagListener mOnSelectTagListener;

    // ===========================================================
    // Listener
    // ===========================================================

    public void setOnSelectTagListener(OnSelectTagListener onSelectTagListener) {
        this.mOnSelectTagListener = onSelectTagListener;
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
        // ListView Header
        View listViewHeader = inflater.inflate(R.layout.admin_calatog_list_item, container, false);
        Tag headerData = new Tag();
        headerData.setId(UNSET_ID);
        headerData.setName("All");
        listView.addHeaderView(listViewHeader, headerData, true);
        // ListView Footer
        View listViewFooter = inflater.inflate(R.layout.admin_footer_list_item_add, container, false);
        Tag footerData = new Tag();
        footerData.setId(ADD_ID);
        footerData.setName("Add");
        listView.addFooterView(listViewFooter, footerData, true);
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
    public void onEntityClick(Long selectTagId) {
        Log.d(TAG, "onSelectTagId : " + selectTagId + " / with listener = " + (mOnSelectTagListener != null));
        if (ADD_ID.equals(selectTagId)) {
//            TODO Add
        } else  if (mOnSelectTagListener != null) {
            Long tagId = selectTagId;
            if (UNSET_ID.equals(tagId)) {
                tagId = null;
            }  
            mOnSelectTagListener.onSelectTagId(tagId);
        }

    }
}
