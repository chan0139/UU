package com.example.uu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;
interface whatKindOfLounge{
    int Crew=0;
    int Recruitment=1;
}
public class LoungeActivity extends AppCompatActivity {
    //Fierbase 인스턴스
    private FirebaseAuth mFirebaseAuth;
    //사용자 이름, 사진, 현재 시간
    private String username;
    private String userProfileUrl;
    private SimpleDateFormat format_sendedTime = new SimpleDateFormat("hh:mm:ss a");

    private FirebaseRecyclerAdapter<ChatMessage,MessageViewHolder> mFirebaseAdapter;
    private FirebaseRecyclerOptions<ChatMessage> options;

    private Context context;

    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView messageTextView;
        ImageView messageImageView;
        TextView messengerTextView;
        CircleImageView messengerImageView;
        TextView sentTimeTextView;
        public MessageViewHolder(@NonNull View v) {
            super(v);
            messageTextView=itemView.findViewById(R.id.messageTextView);
            messageImageView=itemView.findViewById(R.id.messageImageView);
            messengerTextView=itemView.findViewById(R.id.messengerTextView);
            messengerImageView=itemView.findViewById(R.id.messengerImageView);
            sentTimeTextView=itemView.findViewById(R.id.sentTimeTextView);
        }
    }
    private RecyclerView mMessageRecyclerView;
    private DatabaseReference mFirebaseDatabaseReference;
    private EditText mMessageEditText;
    private static final String MESSAGES_CHILD="messages";
    private ImageButton send_button;

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //firebase recycler adapter realtime query start
        mFirebaseAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //firebase recycler adapter realtime query stop
        mFirebaseAdapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lounge);

        Intent intent = getIntent();
        int Lounge=intent.getIntExtra("whatKindOfLounge",-1);
        String LoungeID = intent.getStringExtra("LoungeID");

        Log.d("testkang",LoungeID+Lounge);
        mMessageRecyclerView=findViewById(R.id.message_recycler_view);
        //Firebase 인증 초기화
        mFirebaseAuth=FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference("UU")
                .child("UserAccount").child(mFirebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userObject info = snapshot.getValue(userObject.class);
                username=info.getUserName();
                userProfileUrl=info.getUserProfileUrl();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TextView crewnameLounge=(TextView) findViewById(R.id.crewnameLuonge);
        RelativeLayout relativeLayout=(RelativeLayout)findViewById(R.id.summary);
        TextView summary=(TextView)findViewById(R.id.summaryTitle);
        //Firebase 실시간 데이터베이스 초기화
        if(Lounge==whatKindOfLounge.Crew){
            mFirebaseDatabaseReference= FirebaseDatabase.getInstance().getReference("Crew");
            crewnameLounge.setText(LoungeID);
            relativeLayout.setVisibility(View.GONE);
        }
        else if(Lounge==whatKindOfLounge.Recruitment){
            context=this;
            mFirebaseDatabaseReference= FirebaseDatabase.getInstance().getReference("Recruit");
            mFirebaseDatabaseReference.child(LoungeID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    recruit_object summaryInfo=snapshot.getValue(recruit_object.class);

                    Glide.with(context)
                            .load(summaryInfo.getMapUrl())
                            .into((ImageView)findViewById(R.id.summaryImg));

                    TextView context=(TextView)findViewById(R.id.firstContext);
                    context.setText(summaryInfo.getDate());
                    context=(TextView)findViewById(R.id.secondContext);
                    context.setText(summaryInfo.getDistance()+" km");
                    context=(TextView)findViewById(R.id.thirdContext);
                    context.setText(summaryInfo.getTime());
                    context=(TextView)findViewById(R.id.fourthContext);
                    context.setText(summaryInfo.getOrigin());
                    context=(TextView)findViewById(R.id.lastContext);
                    context.setText(summaryInfo.getDestination());

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            mFirebaseDatabaseReference.child(LoungeID).child("leader").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    crewnameLounge.setText(snapshot.getValue(String.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("testkang","wrong way to access");
                }
            });
        }
        mMessageEditText=findViewById(R.id.message_edit);

        //쿼리 수행
        Query query = mFirebaseDatabaseReference.child(LoungeID).child(MESSAGES_CHILD).limitToLast(50);
        //옵션
        options = new FirebaseRecyclerOptions.Builder<ChatMessage>().setQuery(query,ChatMessage.class).build();

        //어댑터
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull ChatMessage model) {
                holder.messageTextView.setText(model.getText());
                holder.messengerTextView.setText(model.getName());
                if(model.getPhotoUrl() == null){
                    holder.messengerImageView.setImageDrawable(ContextCompat.getDrawable(
                            LoungeActivity.this,R.drawable.ic_baseline_account_circle_24));
                }
                else{
                    Glide.with(LoungeActivity.this)
                            .load(model.getPhotoUrl())
                            .into(holder.messengerImageView);
                }
                holder.sentTimeTextView.setText(model.getSentTime());
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message,parent,false);
                return new MessageViewHolder(view);
            }
        };

        //리사이클러뷰에 레이아웃 메니저, 어뎁터 설정
        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecyclerView.setAdapter(mFirebaseAdapter);

        //전송버튼
        send_button=findViewById(R.id.send_button);
        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date now=new Date();
                ChatMessage chatMessage=new ChatMessage(messageFormatter(mMessageEditText.getText().toString()),
                        username,userProfileUrl,format_sendedTime.format(now),null);
                mFirebaseDatabaseReference.child(LoungeID).child(MESSAGES_CHILD)
                        .push().setValue(chatMessage);
                mMessageEditText.setText("");
            }
        });

        findViewById(R.id.outLounge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    public String messageFormatter(String message){
        String[] splitbyEnter = message.split("\n");
        message="";
        int count=0;
        for(int i=0;i<splitbyEnter.length;i++){
            if(splitbyEnter[i].length()>30){
                String[] splitbySpace = splitbyEnter[i].split(" ");
                splitbyEnter[i]="";
                for(int j=0;j<splitbySpace.length;j++){
                    if(count+splitbySpace[j].length()<=30){
                        if(j>0) {
                            splitbyEnter[i]+=" ";
                            count+=1;
                        }
                        splitbyEnter[i]+=splitbySpace[j];
                        count+=splitbySpace[j].length();
                    }
                    else{
                        if(count==30) {
                            splitbyEnter[i]+="\n";
                            count=0;
                            j-=1;
                        }
                        else {
                            int count_addString=30-count;
                            if(j!=0) {
                                splitbyEnter[i]+=" ";
                            }
                            splitbyEnter[i]+=splitbySpace[j].substring(0, 29-count)+"\n";
                            while(count_addString!=splitbySpace[j].length()) {
                                if(splitbySpace[j].length()-count_addString<=30) {
                                    splitbyEnter[i]+=splitbySpace[j].substring(count_addString);
                                    count_addString=splitbySpace[j].length();
                                    count=splitbySpace[j].length()-count_addString;
                                }
                                else {
                                    splitbyEnter[i]+=splitbySpace[j].substring(count_addString,count_addString+29);
                                    count_addString+=30;
                                }
                            }
                        }
                    }
                }
            }
            if(i!=0){
                message+="\n";
            }
            message+=splitbyEnter[i];
        }
        return message;
    }
}