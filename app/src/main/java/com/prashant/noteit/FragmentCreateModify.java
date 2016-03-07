package com.prashant.noteit;

///**
// * Created by shishir on 3/5/16.
// */
//public class FragmentCreateModify {
//}
//

import android.annotation.TargetApi;
import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Console;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class FragmentCreateModify extends Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //        inflate fragment layout
        View rootView = inflater.inflate(R.layout.fragment_create_modify, container, false);

        return rootView;//inflater.inflate(R.layout.fragment_create_modify,container,false);


    }

    public void saveNote (View v) {
        NotesModel insertNote = new NotesModel();

        //hardcoded user name
        String userName = "kumarprashant27@gmail.com";

        String noteTitle;
        String noteDescription;
        Timestamp timeOfCreate;

        EditText noteT = (EditText) getView().findViewById(R.id.titleNote);
        EditText noteD = (EditText) getView().findViewById(R.id.contentNote);

        noteTitle = noteT.getText().toString();
        noteDescription = noteD.getText().toString();

        insertNote.setNoteTitle(noteTitle);
        insertNote.setNoteDescription(noteDescription);

       // NoteDBHandler noteDB = new NoteDBHandler();
        //noteDB.

    }

    public void buttonPress(View v){
        Toast.makeText(getContext(), "hello", Toast.LENGTH_LONG).show();
    }


}