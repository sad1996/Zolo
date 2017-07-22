package xyz.sadaksapp.zolo;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileFragment extends Fragment {

    @BindViews({R.id.email,R.id.userName, R.id.phoneNumber})
    List<TextView> textViews;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mFirebaseDatabaseReference;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);
        //get firebase mAuth instance

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if (mUser == null) {
                    changeFragment(new LoginFragment());
                }
            }
        };

        if (mUser != null) {
            mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(mUser.getUid());
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String mPhone = String.valueOf(dataSnapshot.child("phone").getValue());
                    String mName = String.valueOf(dataSnapshot.child("name").getValue());
                    String mEmail = String.valueOf(dataSnapshot.child("email").getValue());
                    if (!TextUtils.isEmpty(mPhone) && !TextUtils.isEmpty(mName) && !TextUtils.isEmpty(mEmail)) {
                        textViews.get(0).setText(mEmail);
                        textViews.get(1).setText(mName);
                        textViews.get(2).setText(mPhone);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mFirebaseDatabaseReference.addListenerForSingleValueEvent(eventListener);
        }

        return view;
    }

    @OnClick(R.id.btnUpdate)
    void buttonUpdate() {
        changeFragment(new UpdateFragment());
    }

    @OnClick(R.id.btnLogout)
    void buttonLogout() {
        signOut();
        changeFragment(new LoginFragment());
    }

    //sign out method
    public void signOut() {
        mAuth.signOut();
    }

    private void changeFragment(Fragment fm) {
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fm);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().finish();
    }
}
