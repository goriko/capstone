package com.example.dar.systemdesign;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePasswordActivity extends Fragment {

    private Button buttonProceed;
    private EditText editTextOld;
    private EditText editTextNew;
    private EditText editTextConfirm;
    private FirebaseAuth firebaseAuth;
    private Fragment fragment = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_change_password, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = firebaseAuth.getCurrentUser();

        editTextOld = (EditText) rootView.findViewById(R.id.editTextOld);
        editTextNew = (EditText) rootView.findViewById(R.id.editTextNew);
        editTextConfirm = (EditText) rootView.findViewById(R.id.editTextConfirm);
        buttonProceed = (Button) rootView.findViewById(R.id.buttonProceed);

        buttonProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = editTextOld.getText().toString();
                String newPass = editTextNew.getText().toString();
                String confirmPass = editTextConfirm.getText().toString();
                final String email = user.getEmail();
                AuthCredential credential = EmailAuthProvider.getCredential(email,oldPass);

                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){

                            if(newPass.equals(confirmPass)){
                                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getActivity(), "Successfully changed password", Toast.LENGTH_SHORT).show();
                                            fragment = new ProfileActivity();
                                            replaceFragment(fragment);
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(getActivity(), "Please Enter the same new password", Toast.LENGTH_SHORT).show();
                                editTextOld.setText("");
                                editTextNew.setText("");
                                editTextConfirm.setText("");
                            }

                        }else{
                            Toast.makeText(getActivity(), "Please Enter the correct Password", Toast.LENGTH_SHORT).show();
                            editTextOld.setText("");
                            editTextNew.setText("");
                            editTextConfirm.setText("");
                        }
                    }
                });

            }
        });

        return rootView;
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.main_frame, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
