package eu.ttbox.androgister.ui.admin.tag;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
//        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Tag item = (Tag) parent.getItemAtPosition(position);
                Long entityId = item != null ? item.getId() : null;
                if (item != null && !UNSET_ID.equals(entityId) && !ADD_ID.equals(entityId)) {
                    onEntityEditClick(item.getId());
                    return true;
                } else {
                    return false;
                }
            }
        });

        // ListView Header
        View listViewHeader = inflater.inflate(R.layout.admin_calatog_list_item, container, false);
        Tag headerData = new Tag();
        headerData.setId(UNSET_ID);
        String allLabel = getString(R.string.all);
        headerData.setName(allLabel);
        ((TextView) listViewHeader).setText(allLabel);
        listView.addHeaderView(listViewHeader, headerData, true);

        // ListView Footer
        View listViewFooter = inflater.inflate(R.layout.admin_footer_list_item_add, container, false);
        Tag footerData = new Tag();
        footerData.setId(ADD_ID);
        String addLabel = getString(R.string.add);
        footerData.setName(addLabel);
        listView.addFooterView(listViewFooter, footerData, true);
//        listView.addFooterView(listViewFooter );
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

    public void onEntityEditClick(Long entityId) {
        if (!UNSET_ID.equals(entityId) && !ADD_ID.equals(entityId)) {
            Intent intent = new Intent(getActivity(), TagEditActivity.class);
            intent.setAction(Intent.ACTION_EDIT);
            intent.putExtra(Intent.EXTRA_UID, entityId);
            startActivityForResult(intent, ENTITY_EDIT_REQUEST_CODE);
        }
    }

    @Override
    public void onEntityClick(Long selectTagId) {
        Log.d(TAG, "onSelectTagId : " + selectTagId + " / with listener = " + (mOnSelectTagListener != null));
        if (ADD_ID.equals(selectTagId)) {
            // Add
            Intent intent = new Intent(getActivity(), TagEditActivity.class);
            intent.setAction(Intent.ACTION_EDIT);
            // intent.putExtra(Intent.EXTRA_UID, entityId);
            startActivityForResult(intent, ENTITY_EDIT_REQUEST_CODE);
        } else if (mOnSelectTagListener != null) {
            Long entityId = selectTagId;
            if (UNSET_ID.equals(entityId)) {
                entityId = null;
            }
            mOnSelectTagListener.onSelectTagId(entityId);
        }

    }

}
