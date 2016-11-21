package br.com.emersonluiz.bookstore;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.widget.AbsListView.OnScrollListener;

public class BookListActivity extends Activity {

    private ListView listView1;
    private EditText search;
    private List<Map<String, Object>> books;
    private BookListAdapter adapter;
    private boolean isLoading = false;
    private BookListTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        listView1 = (ListView) findViewById(R.id.listView1);
        listView1.setOnScrollListener(onScrollListener());

        ProgressBar footer = new ProgressBar(BookListActivity.this);
        listView1.addFooterView(footer);
        listView1.setFooterDividersEnabled(false);

        search = (EditText) findViewById(R.id.search);

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                createList();
                createTask(search.getText().toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        createList();
        createTask("");
    }

    private OnScrollListener onScrollListener() {
        return new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == 0) {
                    isLoading = false;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if(lastInScreen == totalItemCount && totalItemCount != 0 && books.size() == view.getLastVisiblePosition() && books.size() > 14 && !isLoading){
                    createTask(search.getText().toString());
                    isLoading = true;
                }
            }
        };
    }

    private void createTask(String search) {
        if (task != null) {
            task.cancel(true);
        }
        task = new BookListTask(listView1, books, adapter, this);
        if (search == null || search.equals("")) {
            search="android";
        }
        task.execute(search);
    }

    private void createList() {
        //String[] from = {"thumbnail", "title", "publisher", "pageCount"};
        //int[] to = {R.id.thumbnail, R.id.title, R.id.publisher, R.id.pageCount};
        books = new ArrayList<>();
        adapter = new BookListAdapter(books, this); //new SimpleAdapter(this, books, R.layout.listview_item_row, from, to);
        listView1.setAdapter(adapter);
    }

    public void refresh(View view) {
        createList();
        createTask(search.getText().toString());
    }
}
