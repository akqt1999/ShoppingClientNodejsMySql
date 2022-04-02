//package etn.app.danghoc.shoppingclient.chat;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.os.Bundle;
//import android.widget.ImageView;
//
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import android.content.Intent;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.squareup.picasso.Picasso;
//
//import etn.app.danghoc.shoppingclient.R;
//
//
//public class ChatActivity2 extends AppCompatActivity {
//
//
//    private FirebaseFirestore firebaseFirestore;
//    LinearLayoutManager linearLayoutManager;
//    private FirebaseAuth firebaseAuth;
//
//    ImageView mimageviewofuser;
//
//    FirestoreRecyclerAdapter<firebasemodel, chatFragment.NoteViewHolder> chatAdapter;
//
//    RecyclerView mrecyclerview;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_chat2);
//
//        firebaseAuth=FirebaseAuth.getInstance();
//        firebaseFirestore= FirebaseFirestore.getInstance();
//        mrecyclerview=findViewById(R.id.recyclerview);
//
//        // Query query=firebaseFirestore.collection("Users");
//        Query query=firebaseFirestore.collection("Users").whereNotEqualTo("uid",firebaseAuth.getUid());
//        FirestoreRecyclerOptions<firebasemodel> allusername=new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query,firebasemodel.class).build();
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//
//        switch (item.getItemId())
//        {
//            case R.id.profile:
//                Intent intent=new Intent(chatActivity.this,ProfileActivity.class);
//                startActivity(intent);
//                break;
//
//            case R.id.settings:
//                Toast.makeText(getApplicationContext(),"Settign is clicked",Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//
//
//        return  true;
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuInflater menuInflater=getMenuInflater();
//        menuInflater.inflate(R.menu.menu,menu);
//
//
//        return true;
//    }
//        chatAdapter=new FirestoreRecyclerAdapter<firebasemodel, chatFragment.NoteViewHolder>(allusername) {
//            @Override
//            protected void onBindViewHolder(@NonNull chatFragment.NoteViewHolder noteViewHolder, int i, @NonNull firebasemodel firebasemodel) {
//
//                noteViewHolder.particularusername.setText(firebasemodel.getName());
//                String uri=firebasemodel.getImage();
//
//                Picasso.get().load(uri).into(mimageviewofuser);
//                if(firebasemodel.getStatus().equals("Online"))
//                {
//                    noteViewHolder.statusofuser.setText(firebasemodel.getStatus());
//                    noteViewHolder.statusofuser.setTextColor(Color.GREEN);
//                }
//                else
//                {
//                    noteViewHolder.statusofuser.setText(firebasemodel.getStatus());
//                }
//
//                noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent intent=new Intent(getActivity(),specificchat.class);
//                        intent.putExtra("name",firebasemodel.getName());
//                        intent.putExtra("receiveruid",firebasemodel.getUid());
//                        intent.putExtra("imageuri",firebasemodel.getImage());
//                        startActivity(intent);
//                    }
//                });
//
//
//
//            }
//
//            @NonNull
//            @Override
//            public chatFragment.NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//
//                View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout,parent,false);
//                return new chatFragment.NoteViewHolder(view);
//            }
//        };
//
//
//        mrecyclerview.setHasFixedSize(true);
//        linearLayoutManager=new LinearLayoutManager(this);
//        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        mrecyclerview.setLayoutManager(linearLayoutManager);
//        mrecyclerview.setAdapter(chatAdapter);
//
//    }
//
//    public class NoteViewHolder extends RecyclerView.ViewHolder
//    {
//
//        private TextView particularusername;
//        private TextView statusofuser;
//
//        public NoteViewHolder(@NonNull View itemView) {
//            super(itemView);
//            particularusername=itemView.findViewById(R.id.nameofuser);
//            statusofuser=itemView.findViewById(R.id.statusofuser);
//            mimageviewofuser=itemView.findViewById(R.id.imageviewofuser);
//        }
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        chatAdapter.startListening();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if(chatAdapter!=null)
//        {
//            chatAdapter.stopListening();
//        }
//    }
//}