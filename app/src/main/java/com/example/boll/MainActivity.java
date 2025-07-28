package com.example.boll;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postList;
    private RequestQueue requestQueue;
    private EditText editTextTitle;
    private EditText editTextBody;
    private Button buttonSubmit;
    private TextView textViewApiResult;

    private static final String API_URL1 = "https://jsonplaceholder.typicode.com/posts";
    private static final String API_URL = "https://jsonplaceholder.typicode.com/posts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextBody = findViewById(R.id.editTextBody);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        textViewApiResult = findViewById(R.id.textViewApiResult);

        requestQueue = Volley.newRequestQueue(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList);
        recyclerView.setAdapter(postAdapter);

        fetchPost();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString().trim();
                String body = editTextBody.getText().toString().trim();

                if (title.isEmpty() || body.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    createPost(title, body);
                }
            }

            private void createPost(String title, String body) {
                JSONObject postData = new JSONObject();
                try {
                    postData.put("title", title);
                    postData.put("body", body);
                    postData.put("userId", 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        API_URL1,
                        postData,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String id = response.getString("id");
                                    String responseTitle = response.getString("title");
                                    String responseBody = response.getString("body");

                                    String result = "Post creado exitosamente:\n" +
                                            "ID: " + id + "\n" +
                                            "Título: " + responseTitle + "\n" +
                                            "Cuerpo: " + responseBody;
                                    textViewApiResult.setText(result);
                                    Toast.makeText(MainActivity.this, "Post creado con ID: " + id, Toast.LENGTH_LONG).show();

                                    editTextTitle.setText("");
                                    editTextBody.setText("");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "Error al parsear la respuesta JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, "Error al crear post: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                textViewApiResult.setText("Error al conectar con la API: " + error.getMessage());
                                error.printStackTrace();
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        return headers;
                    }
                };

                requestQueue.add(jsonObjectRequest);
            }
        });
    }

    private void fetchPost() {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject postObject = response.getJSONObject(i);

                                int userId = postObject.getInt("userId");
                                int id = postObject.getInt("id");
                                String title = postObject.getString("title");
                                String body = postObject.getString("body");

                                Post post = new Post(userId, id, title, body);
                                postList.add(post);
                            }
                            postAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Error al parsear JSON: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "Error de red: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
