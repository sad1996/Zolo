package xyz.sadaksapp.zolo;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


public class UpdateFragment extends Fragment {

    private static final String TAG = "UpdateFragment";
    @BindViews({R.id.userName, R.id.phoneNumber, R.id.email})
    List<EditText> editTexts;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mFirebaseDatabaseReference;

    public UpdateFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_update, container, false);
        ButterKnife.bind(this, view);
        //get firebase mAuth instance


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
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
                        editTexts.get(2).setText(mEmail);
                        editTexts.get(0).setText(mName);
                        editTexts.get(1).setText(mPhone);
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

    @OnClick(R.id.btnLogout)
    void buttonLogout() {
        signOut();
        changeFragment(new LoginFragment());
    }

    @OnClick(R.id.btnUpdate)
    void buttonUpdate() {
        String mEmail = editTexts.get(2).getText().toString().trim();
        String mName = editTexts.get(0).getText().toString().trim();
        String mPhone = editTexts.get(1).getText().toString().trim();
        if (mUser != null) {
            if (TextUtils.isEmpty(mEmail) && TextUtils.isEmpty(mName) && TextUtils.isEmpty(mPhone)) {
                Toast.makeText(getContext(), "Fill all the details", Toast.LENGTH_SHORT).show();
            } else {
                updateEmail();
                String userId = mUser.getUid();
                writeNewUser(userId, mName, mPhone, mEmail);
                changeFragment(new ProfileFragment());
            }
        }
    }

    private void updateEmail() {
            String email = editTexts.get(2).getText().toString().trim();
            mUser.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getContext(), "Details updated.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
    }

    public static void writeNewUser( String userId, String name, String phone, String email) {
        User user = new User(name, phone, email);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").child(userId).setValue(user);
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
}
