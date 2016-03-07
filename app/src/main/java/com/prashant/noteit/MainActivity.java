package com.prashant.noteit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.Button;
import android.support.design.widget.FloatingActionButton;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.ResultCallback;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener{

    //Signin button
    private SignInButton signInButton;
    public int i=0;
    public NotesModel noteData1;
    private Button signOutButton;

    private RecyclerView recyclerView;
    private List<NotesModel> noteList = new ArrayList<>();
    public NotesAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    //Signing Options
    private GoogleSignInOptions gso;
    //google api client
    private GoogleApiClient mGoogleApiClient;
    //Signin constant to check the activity result
    private int RC_SIGN_IN = 100;

    //TextViews
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewNameIs;
    private TextView textViewEmailIs;


    private NetworkImageView profilePhoto;

    //Image Loader
    private ImageLoader imageLoader;

    SharedPreferences shareP;
    Editor editor;
    Button btn_one;
    Button btn_two;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareP = getSharedPreferences("MyPref", 0); // 0 - for private mode

        editor = shareP.edit();
        setContentView(R.layout.fragment_notes);
        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);

        //Log.e("", ((String) R.id.recycler_view));
        mAdapter = new NotesAdapter(noteList);

        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NotesModel note = noteList.get(position);
                Toast.makeText(getApplicationContext(), note.getNoteTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



//        if(shareP.getBoolean("firstRun",true)){
//            Toast.makeText(getApplicationContext(),"hola",Toast.LENGTH_LONG).show();
//            shareP.edit().putBoolean("firstRun",false).commit();
//
//        }
        //Log.e("sharedPref Val",shareP.getString("",));

        setContentView(R.layout.activity_main);
        btn_one = (Button) findViewById(R.id.btn_one);

        btn_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   replace fragment_one
                Fragment fragment_one = new FragmentNotes();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_replace, fragment_one);
                fragmentTransaction.commit();
                prepareNoteData();
                noteList.get(0);


            }
        });

        btn_two = (Button) findViewById(R.id.btn_two);

        btn_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //   replace fragment_two
                Fragment fragment_two = new FragmentCreateModify();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_replace, fragment_two);
                fragmentTransaction.commit();

            }
        });

        btn_one.setVisibility(View.GONE);
        btn_two.setVisibility(View.GONE);

            //Initializing Views
            textViewName = (TextView) findViewById(R.id.textViewName);
            textViewEmail = (TextView) findViewById(R.id.textViewEmail);
            profilePhoto = (NetworkImageView) findViewById(R.id.profileImage);

        if(shareP.getBoolean("firstRun",true))
        {
            shareP.edit().putBoolean("firstRun",false).commit();
            Toast.makeText(getApplicationContext(),"hola",Toast.LENGTH_LONG).show();
            //Initializing google signin option
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();


            //Initializing signinbutton
            signInButton = (SignInButton) findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_WIDE);
            signInButton.setScopes(gso.getScopeArray());

            //Initializing google api client
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            //Setting onclick listener to signing button
            signInButton.setOnClickListener(this);

        }

        else {
            Toast.makeText(getApplicationContext(),shareP.getString("Email","naahi"),Toast.LENGTH_LONG).show();
            signInButton = (SignInButton) findViewById(R.id.sign_in_button);

            signInButton.setVisibility(View.GONE);
            btn_one.setVisibility(View.VISIBLE);
            btn_two.setVisibility(View.VISIBLE);
        }

    }

    //This function will option signing intent
    private void signIn() {
        //Creating an intent
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        btn_one.setVisibility(View.VISIBLE);
        btn_two.setVisibility(View.VISIBLE);
        //Starting intent for result
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        }
    }


    //After the signing we are calling this function
    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            GoogleSignInAccount acct = result.getSignInAccount();
            textViewNameIs = (TextView)findViewById(R.id.textViewNameIs);
            textViewEmailIs = (TextView)findViewById(R.id.textViewEmailIs);
            //Displaying name and email
            textViewEmailIs.setVisibility(View.VISIBLE);
            textViewNameIs.setVisibility(View.VISIBLE);
            textViewName.setText(acct.getDisplayName());
            textViewEmail.setText(acct.getEmail());

            //Initializing image loader
            imageLoader = CustomVolleyRequest.getInstance(this.getApplicationContext())
                    .getImageLoader();

            imageLoader.get(acct.getPhotoUrl().toString(),
                    ImageLoader.getImageListener(profilePhoto,
                            R.mipmap.ic_launcher,
                            R.mipmap.ic_launcher));

            //Loading image
            profilePhoto.setImageUrl(acct.getPhotoUrl().toString(), imageLoader);

            //hide the sign in button
            com.google.android.gms.common.SignInButton sign_in = (SignInButton) findViewById(R.id.sign_in_button);
            ViewGroup layout = (ViewGroup) sign_in.getParent();
            layout.removeView(sign_in);

            shareP.edit().putString("Email", acct.getEmail().toString()).commit();
            shareP.edit().putString("Name", acct.getDisplayName()).commit();

            String gum= shareP.getString("Name", acct.getDisplayName()) + shareP.getString("Email", acct.getEmail().toString());


            Toast.makeText(getApplicationContext(), gum, Toast.LENGTH_LONG).show();

//            editor.putString("Email", acct.getEmail().toString());
//            editor.putString("Name", acct.getDisplayName());


            signOutButton = (Button) findViewById(R.id.sign_out_button);
            signOutButton.setVisibility(View.VISIBLE);

        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == signInButton) {
            //Calling signin
            signIn();
        }
    }

    public void signOut(View v) {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public void buttonPress(View v){

        NotesModel noteData = new NotesModel();
        EditText noteT = (EditText)findViewById(R.id.titleNote);
        EditText noteD = (EditText)findViewById(R.id.contentNote);


        noteData.setNoteTitle(noteT.getText().toString());
        noteData.setNoteDescription(noteD.getText().toString());
        noteData.setUserId(shareP.getString("Email", "naahi"));


        NoteDBHandler db = new NoteDBHandler(getApplicationContext());
        boolean bn = db.addNote(noteData);

        if(bn) {
            Toast.makeText(this, "Note Saved", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Beta na ho payega tumse", Toast.LENGTH_LONG).show();

        }
    }

//    public void showData (View v){
//
//        String userName = textViewEmail.getText().toString();
//        NoteDBHandler db = new NoteDBHandler(getApplicationContext());
//
//        ArrayList<NotesModel> NoteList = new ArrayList<NotesModel>();
//
//        NoteList = db.findNotesByUser(shareP.getString("Email","naahi"));
//
//        int max=NoteList.size();
//        TextView title = (TextView)findViewById(R.id.textView3);
//        TextView content = (TextView)findViewById(R.id.textView4);
//
//        NotesModel nm = new NotesModel();
//
//        if(i<max) {
//            nm = NoteList.get(i);
//            title.setText(nm.getNoteTitle());
//            content.setText(nm.getNoteDescription());
//            i++;
//        }
//        else{
//            title.setText("End of file");
//            content.setText("End of file");
//        }
//    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private void prepareNoteData() {

        String userName = textViewEmail.getText().toString();
        NoteDBHandler db = new NoteDBHandler(getApplicationContext());

        noteList = db.findNotesByUser(shareP.getString("Email","naahi"));

//        int max=noteList.size();
//        TextView title = (TextView)findViewById(R.id.textView3);
//        TextView content = (TextView)findViewById(R.id.textView4);
//
//        NotesModel nm = new NotesModel();
//
//        for(i=0;i<max;i++) {
//            nm = noteList.get(i);
//            title.setText(nm.getNoteTitle());
//            content.setText(nm.getNoteDescription());
//            i++;
//        }
        mAdapter.notifyDataSetChanged();
    }

}
