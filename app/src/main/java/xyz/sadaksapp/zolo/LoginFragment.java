package xyz.sadaksapp.zolo;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    @BindView(R.id.email) EditText inputEmail;
    @BindView(R.id.password) EditText inputPassword;
    @BindView(R.id.logoText) TextView logoText;
    @BindView(R.id.logo) ImageView mLogo;

    private String mEmail, mPassword;
    private FirebaseAuth mAuth;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, view);

        Animation fadeOutAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);
        mLogo.setAnimation(fadeOutAnimation);
        logoText.setAnimation(fadeOutAnimation);

        //Get Firebase mAuth instance
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            changeFragment(new ProfileFragment());
        }

        return view;
    }

    @OnClick(R.id.btnSignup)
    void buttonRegister() {
        changeFragment(new RegisterFragment());
    }

    @OnClick(R.id.btnReset)
    void buttonForgot() {
        changeFragment(new ForgotFragment());
    }

    @OnClick(R.id.btnLogin)
    void buttonLogin() {
        mEmail = inputEmail.getText().toString();
        mPassword = inputPassword.getText().toString();
        if (TextUtils.isEmpty(mEmail) && TextUtils.isEmpty(mPassword)) {
            Toast.makeText(getContext(), "Enter mEmail address and mPassword!", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the mAuth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (mPassword.length() < 8) {
                                    inputPassword.setError(getString(R.string.minimum_password));
                                } else {
                                    Toast.makeText(getContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                changeFragment(new ProfileFragment());
                            }
                        }
                    });
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().finish();
    }

    private void changeFragment(Fragment fm) {
        android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content, fm);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
        ft.commit();
    }


}
