package com.prashant.noteit;

///**
// * Created by shishir on 3/5/16.
// */
//public class FragmentNotes {
//}


import android.app.Fragment;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;


public class FragmentNotes extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        inflate fragment layout
        return inflater.inflate(R.layout.fragment_notes,container,false);
    }

}