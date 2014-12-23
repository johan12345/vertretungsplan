package com.johan.vertretungsplan_2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginDialogFragment extends DialogFragment {

    public static final String ARG_FIXED_LOGIN = "fixedLogin";
    private LoginDialogListener mListener;
    private View view;
    private String fixedLogin;

    public LoginDialogFragment() {
    }

    public static LoginDialogFragment getInstance(String fixedLogin) {
        LoginDialogFragment frag = new LoginDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FIXED_LOGIN, fixedLogin);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null && getArguments().containsKey(ARG_FIXED_LOGIN))
            fixedLogin = getArguments().getString(ARG_FIXED_LOGIN);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.dialog_signin, null);
        if (fixedLogin != null) {
            EditText login = (EditText) view.findViewById(R.id.username);
            login.setText(fixedLogin);
            login.setEnabled(false);
        }

        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.signin,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                LoginDialogFragment.this.getDialog().cancel();
                                mListener.onCancel();
                            }
                        });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the
            // host
            mListener = (LoginDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement LoginDialogListener");
        }
    }

    @Override
    public void onStart() {
        super.onStart(); // super.onStart() is where dialog.show() is actually
        // called on the underlying dialog, so we have to do
        // it after this point
        AlertDialog d = (AlertDialog) getDialog();
        if (d != null) {
            Button positiveButton = (Button) d
                    .getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String login = ((EditText) view.findViewById(R.id.username))
                            .getText().toString();
                    String password = ((EditText) view
                            .findViewById(R.id.password)).getText().toString();
                    mListener.onLogin(login, password);
                }
            });
        }
    }

    public void progress(boolean progress) {
        view.findViewById(R.id.progress).setVisibility(
                progress ? View.VISIBLE : View.GONE);
        view.findViewById(R.id.login).setVisibility(
                progress ? View.GONE : View.VISIBLE);
    }

    public interface LoginDialogListener {
        public void onLogin(String login, String password);

        public void onCancel();
    }

}
