package br.com.emersonluiz.bookstore;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by emersonc on 11/16/16.
 */

public class BookListTask extends AsyncTask<String, Void, List<Map<String, Object>>> {

    private ListView listView;
    private BookListActivity c;
    private List<Map<String, Object>> books;
    private BookListAdapter adapter;

    public BookListTask(ListView listView, List<Map<String, Object>> books, BookListAdapter adapter, BookListActivity c) {
        this.listView = listView;
        this.c = c;
        this.books = books;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(List<Map<String, Object>> result) {
        if (result != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected List<Map<String, Object>> doInBackground(String... params) {

         try {
            String filter = params[0];
            String urlGoogle = "https://www.googleapis.com/books/v1/volumes?q="+filter+"&maxResults=15&startIndex="+books.size();
            String url = Uri.parse(urlGoogle).toString();
            String content = HTTPUtils.acessar(url);

            if (content != null) {

                // get result
                JSONObject jsonObject = new JSONObject(content);
                JSONArray results = jsonObject.getJSONArray("items");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject book = results.getJSONObject(i);

                    JSONObject obj = book.getJSONObject("volumeInfo");

                    if (obj.has("title")) {

                        Bitmap image = null;

                        if(obj.has("imageLinks")) {
                            if(obj.getJSONObject("imageLinks").has("smallThumbnail")) {
                                image = downloadBitmap(obj.getJSONObject("imageLinks").getString("smallThumbnail"));
                            }
                        }

                        String title = obj.getString("title");
                        String publisher = (obj.has("publisher")) ? obj.getString("publisher") : "unknown";
                        String pageCount = (obj.has("pageCount")) ? obj.getString("pageCount") : "unknown";
                        String link = (obj.has("previewLink")) ? obj.getString("previewLink") : "";

                        Map<String, Object> item = new HashMap<>();
                        item.put("thumbnail", image);
                        item.put("title", title );
                        item.put("publisher", publisher);
                        item.put("pageCount", pageCount + " pages");
                        item.put("link", link);
                        books.add(item);
                    }
                }
            }

            return books;

          } catch (Exception e) {
            throw new RuntimeException(e);
          }
    }

    private Bitmap downloadBitmap(String url) {
        HttpURLConnection urlConnection = null;
        try {
            URL uri = new URL(url);
            urlConnection = (HttpURLConnection) uri.openConnection();
            int statusCode = urlConnection.getResponseCode();
           // if (statusCode != HttpStatus.SC_OK) {
                //return null;
           // }

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream != null) {
                return BitmapFactory.decodeStream(inputStream);
            }
        } catch (Exception e) {
            urlConnection.disconnect();
            Log.w("ImageDownloader", "Error downloading image from " + url);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }
}
