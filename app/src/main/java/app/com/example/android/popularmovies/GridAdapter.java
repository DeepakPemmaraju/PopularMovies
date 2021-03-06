package app.com.example.android.popularmovies;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public final class GridAdapter extends BaseAdapter {

    // Assume it's known
    private static final int ROW_ITEMS = 3;
    final ArrayList<String> mItems;
    final int mCount;

    /**
     * Default constructor
     * @param items to fill data to
     */
    public GridAdapter(final ArrayList<String> items) {

        mCount = items.size() * ROW_ITEMS;
        mItems = new ArrayList<String>(mCount);

        // for small size of items it's ok to do it here, sync way
        for (String item : items) {
            // get separate string parts, divided by ,
            final String[] parts = item.split(",");

            // remove spaces from parts
            for (String part : parts) {
                part.replace(" ", "");
                mItems.add(part);
            }
        }
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public Object getItem(final int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(final int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View view = convertView;

        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        final TextView text = (TextView) view.findViewById(android.R.id.text1);

        text.setText(mItems.get(position));

        return view;
    }
}
