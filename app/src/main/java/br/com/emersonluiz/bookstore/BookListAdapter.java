package br.com.emersonluiz.bookstore;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.Map;

/**
 * Created by emersonc on 11/19/16.
 */

public class BookListAdapter extends BaseAdapter {

    private final List<Map<String, Object>> books;
    private final Activity activity;

    BookListAdapter(List<Map<String, Object>> books, Activity activity) {
        this.books = books;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View v = activity.getLayoutInflater().inflate(R.layout.listview_item_row, viewGroup, false);

        Map<String, Object> item = books.get(position);

        ImageView icon = (ImageView) v.findViewById(R.id.thumbnail);
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView publisher = (TextView) v.findViewById(R.id.publisher);
        TextView pageCount = (TextView) v.findViewById(R.id.pageCount);

        icon.setImageBitmap((Bitmap) item.get("thumbnail"));
        title.setText((String) item.get("title"));
        publisher.setText((String) item.get("publisher"));
        pageCount.setText((String) item.get("pageCount"));

        final String link = (String) item.get("link");

        if (link != "") {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri uri = Uri.parse(link);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    activity.startActivity(intent);
                }
            });
        }

        return v;
    }
}
