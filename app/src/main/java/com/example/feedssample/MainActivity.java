package com.example.feedssample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;


import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore;
    private FirestoreRecyclerAdapter adapter;

    LinearLayoutManager linearLayoutManager;
    ProgressBar progressBar;

    RecyclerView recyclerView;
    ArrayList<ModelFeed> modelFeedArrayList = new ArrayList<>();
    AdapterFeed adapterFeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        firebaseFirestore = FirebaseFirestore.getInstance();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        getData();

        adapterFeed = new AdapterFeed(this, modelFeedArrayList);
        recyclerView.setAdapter(adapterFeed);


        FloatingActionButton fab = findViewById(R.id.btnFloating);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TambahStatusActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getData(){

        //Untuk Menambah Colection didalam firebase
        Query query = firebaseFirestore.collection("Feeds");

        //untuk mengambil data yang ada didalam model
        FirestoreRecyclerOptions<ModelFeed> response = new FirestoreRecyclerOptions.Builder<ModelFeed>()
                .setQuery(query, ModelFeed.class).build();

        //LOGIC DARI SETIAP VIEW ITEM MENU
        adapter = new FirestoreRecyclerAdapter<ModelFeed, FeedsHolder>(response){
            @NonNull
            @Override
            public FeedsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
                return new FeedsHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull MainActivity.FeedsHolder holder, int position, @NonNull ModelFeed model) {
                progressBar.setVisibility(View.GONE);
                if (model.getProPic() != null){
                    Picasso.get().load(model.getProPic()).fit().into(holder.fotoUser);
                }else{
                    Picasso.get().load(R.drawable.icon_contact).fit().into(holder.fotoUser);
                }
                holder.userName.setText(model.getName());
                holder.status.setText(model.getStatus());

                if (model.getPostPic() != null){
                    Picasso.get().load(model.getPostPic()).fit().into(holder.postGmbr);
                }else{
                    setVisible(false);
                }

                holder.btnShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                holder.btnComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                holder.btnLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @Override
            public void onError(@NonNull FirebaseFirestoreException e) {
                Log.e("Ditemukan error: ", e.getMessage());
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    public class FeedsHolder extends RecyclerView.ViewHolder{

    RelativeLayout relativeLayout;
    RelativeLayout layoutUser;
    CircleImageView fotoUser;
    TextView userName;
    TextView status;
    ImageView postGmbr;
    LinearLayoutCompat layoutButton;
    Button btnShare;
    Button btnComment;
    Button btnLocation;

    public FeedsHolder(@NonNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.feedsLayout);

            layoutUser = itemView.findViewById(R.id.userLayout);
            fotoUser = itemView.findViewById(R.id.imgView_proPic);
            userName = itemView.findViewById(R.id.tv_name);

            status = itemView.findViewById(R.id.tv_status);
            postGmbr = itemView.findViewById(R.id.imgView_postPic);

            layoutButton = itemView.findViewById(R.id.viewBtnPlace);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnLocation = itemView.findViewById(R.id.btnLocation);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


}