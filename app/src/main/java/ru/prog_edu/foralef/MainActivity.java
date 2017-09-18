package ru.prog_edu.foralef;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static String MainURL = "http://devcandidates.alef.im/list.php";
    private int numberOfColumns;

    private RecyclerView imageRacyclerView;
    private ArrayList<String> imagesList = new ArrayList<>();
    private TextView tv;
    RecyclerView.Adapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //здесь костыль для проверки какая разметка используется для планшета или для смартфона. И определяется количество  колонок в зависимости от разметки
        tv = (TextView) findViewById(R.id.btn1);
        if(tv != null){
            numberOfColumns = 3;
        }else{
            numberOfColumns = 2;
        }
        //инициализируем рециклер и сетим ему GridLayoutManager
        imageRacyclerView = (RecyclerView) findViewById(R.id.images_recycler_view);
        imageRacyclerView.setHasFixedSize(true);
        imageRacyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        imageAdapter = new ImageAdapter(imagesList);
        imageRacyclerView.setAdapter(imageAdapter);

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, MainURL, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray jsonArray = new JSONArray(Arrays.asList(response));//создаем массив для получения ответа с сервера, так как он приходит в виде массива, а не объекта
                            JSONArray jsonArray1 = jsonArray.getJSONArray(0);
                            // String[] linksForImages = new String[jsonArray.length()];//создаем массив, для помещения в него ссылок
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                imagesList.add(jsonArray1.getString(i));
                                System.out.println(Arrays.asList(jsonArray1.getString(i)));//здесь помещаем все из одного массива в другой
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        imageAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Еще не получилось", Toast.LENGTH_LONG).show();
                error.printStackTrace();
                System.out.println(TAG);
            }
        });
        MySingleton.getmInstance(MainActivity.this).addToRequestque(jsonArrayRequest);//реализовали синглтон
    }

    private class ImageHolder extends RecyclerView.ViewHolder{
        private ImageView itemImageView;

        public ImageHolder(View itemView) {
            super(itemView);
            itemImageView = (ImageView)itemView.findViewById(R.id.item_image_view);
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHolder>{
        private ArrayList<String> mImagesItems;// поле, где будет храниться лист полученных элементов

        //конструктор, при помощи которого элеенты идущие на вход присваиваются в поле класса
        public ImageAdapter(ArrayList<String> items) {
            mImagesItems = items;
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            View v = inflater.inflate(R.layout.image_item, parent, false);//создаем свою вью и надуваем по нашему шаблону с указанием род элемента

            return new ImageHolder(v);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position) {//наполняет вьюхолдер данными
            String imageItem = mImagesItems.get(position);

            Picasso.with(MainActivity.this)
                    .load(imageItem)
                    .into(holder.itemImageView);//инициализировали библиотеку, загрузили снимок по юрл, и засетили в наше вью
        }
        @Override
        public int getItemCount() {
            return mImagesItems.size();//просто возвращает кол-во элементов в списке
        }
    }
}
