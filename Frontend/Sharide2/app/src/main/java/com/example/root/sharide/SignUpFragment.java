package com.example.root.sharide;

/**
 * Created by root on 22/5/15.
 */

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

public class SignUpFragment extends Fragment implements  View.OnClickListener{

    private static EditText name;
    private static EditText emailId;
    private static EditText password;
    private Button registerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);
        name = (EditText) view.findViewById(R.id.name);
        emailId = (EditText) view.findViewById(R.id.emailAddress);
        password = (EditText) view.findViewById(R.id.newPassword);

        this.registerButton = (Button)view.findViewById(R.id.signUpButton);
        this.registerButton.setOnClickListener(this);

        return view;
    }

    public static String getEditTextValue(EditText editText) {
        return editText.getText().toString().trim();
    }

    @Override
    public void onClick(final View view) {
        if (view == this.registerButton) {
            if (getEditTextValue(name).length() > 1 && getEditTextValue(password).length() > 1 && getEditTextValue(emailId).length() > 1) {
                JsonObject dataObject = new JsonObject();
                dataObject.addProperty("x-auth-name", getEditTextValue(name));
                dataObject.addProperty("x-auth-password", getEditTextValue(password));
                dataObject.addProperty("x-auth-email", getEditTextValue(emailId));
                AppClient.registerUser(dataObject, new AppClient.INetworkResponse<JsonObject>() {
                    @Override
                    public void onSuccess(JsonObject data) {
                        if (data.get("res").getAsBoolean()) {
                            Snackbar.make(getView(), (String) data.get("response").getAsString(), Snackbar.LENGTH_LONG).show();
                            final Dialog dialog = new Dialog(getActivity());
                            dialog.setContentView(R.layout.otp_popup);
                            dialog.setTitle("ENTER OTP");
//                                dialog.setContentView(view);
                            final EditText mOTP = (EditText) dialog.findViewById(R.id.otp);
                            Button send = (Button) dialog.findViewById(R.id.OTPsend);
                            dialog.show();
                            dialog.setCanceledOnTouchOutside(false);
                            dialog.setCancelable(false);
                            send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    JsonObject dataObject = new JsonObject();
                                    dataObject.addProperty("OTP", getEditTextValue(mOTP));
                                    dataObject.addProperty("x-auth-name", getEditTextValue(name));
                                    dataObject.addProperty("x-auth-password", getEditTextValue(password));
                                    dataObject.addProperty("x-auth-email", getEditTextValue(emailId));
                                    AppClient.checkOTP(dataObject, new AppClient.INetworkResponse<JsonObject>() {
                                        @Override
                                        public void onSuccess(JsonObject data) {
                                            if (data.get("res").getAsBoolean()) {

                                                SharedPreferencesManager.get(getActivity()).setString("token", data.get("token").getAsString());
                                                SharedPreferencesManager.get(getActivity()).setString("name", getEditTextValue(name));
                                                SharedPreferencesManager.get(getActivity()).setString("email", getEditTextValue(emailId));

                                                startActivity(new Intent(getActivity(), RidesActivity.class));
                                                getActivity().finish();
                                            }
                                            else {
                                                Snackbar.make(getView(), data.get("response").getAsString(), Snackbar.LENGTH_LONG).show();
                                            }
                                        }
                                        @Override
                                        public void onError(Exception e) {
                                            Snackbar.make(getView(), "Check Your Internet Connection", Snackbar.LENGTH_LONG).show();
                                            return;
                                        }
                                    });
                                }
                            });
                            return;
                        } else {
                            Snackbar.make(getView(), data.get("response").getAsString(), Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    }
                    @Override
                    public void onError(Exception e) {
                        Snackbar.make(getView(), "Check Your Internet Connection", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                });
            }
            else {
                Snackbar.make(getView(), "Check You have entered all feilds", Snackbar.LENGTH_LONG).show();
            }
        }
    }
}