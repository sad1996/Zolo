package xyz.sadaksapp.zolo;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    @BindViews({R.id.email, R.id.password, R.id.userName, R.id.phoneNumber})
    List<EditText> editTexts;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private DatabaseReference mFirebaseDatabaseReference;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(this, view);
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        return view;
    }

    @OnClick(R.id.btnRegister)
    void buttonRegister() {
        final String mEmail = editTexts.get(0).getText().toString().trim();
        final String mPassword = editTexts.get(1).getText().toString().trim();
        final String mName = editTexts.get(2).getText().toString().trim();
        final String mPhone = editTexts.get(3).getText().toString().trim();

        if (TextUtils.isEmpty(mEmail) && TextUtils.isEmpty(mPassword) && TextUtils.isEmpty(mName) && TextUtils.isEmpty(mPhone)) {
            Toast.makeText(getContext(), "Enter email address and password", Toast.LENGTH_SHORT).show();
        } else if (mPassword.length() < 6) {
            Toast.makeText(getContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
        } else if (mPhone.length() < 10 || mPhone.length() > 10) {
            editTexts.get(3).setError(getString(R.string.minimum_phone));
        } else {

            auth.createUserWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Toast.makeText(getContext(), "User account created", Toast.LENGTH_SHORT).show();

                            if (!task.isSuccessful()) {
                                Toast.makeText(getContext(), "Authentication failed." + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                onAuthSuccess(task.getResult().getUser());
                            }
                        }
                    });
        }
    }

    @OnClick(R.id.btnLogin)
    void buttonLogin() {
        changeFragment(new LoginFragment());
    }

    private void onAuthSuccess(FirebaseUser user) {
        final String mEmail = editTexts.get(0).getText().toString().trim();
        final String mName = editTexts.get(2).getText().toString().trim();
        final String mPhone = editTexts.get(3).getText().toString().trim();
        String mUserId = user.getUid();
        writeNewUser(mFirebaseDatabaseReference, mUserId, mEmail, mName, mPhone);
        changeFragment(new ProfileFragment());
    }

    public static void writeNewUser(DatabaseReference databaseReference, String userId, String name, String email, String phone) {
        User user = new User(email, name, phone);
        databaseReference.child("users").child(userId).setValue(user);
    }

    private void changeFragment(Fragment fm) {
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fm);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }

}
