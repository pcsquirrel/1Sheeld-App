package com.integreight.onesheeld.shields.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.integreight.onesheeld.Log;
import com.integreight.onesheeld.R;
import com.integreight.onesheeld.shields.controller.EmailShield;
import com.integreight.onesheeld.shields.controller.EmailShield.EmailEventHandler;
import com.integreight.onesheeld.shields.controller.utils.GmailSinginPopup;
import com.integreight.onesheeld.utils.ConnectionDetector;
import com.integreight.onesheeld.utils.SecurePreferences;
import com.integreight.onesheeld.utils.ShieldFragmentParent;

public class EmailFragment extends ShieldFragmentParent<EmailFragment> {

	TextView sendTo, subject, userName;
	Button login_bt, logout_bt;
	private static SharedPreferences mSharedPreferences;
	private static final String PREF_EMAIL_SHIELD_USER_LOGIN = "user_login_status";
	private static final String PREF_EMAIL_SHIELD_GMAIL_ACCOUNT = "gmail_account";
	private static final String PREF_EMAIL_SHIELD_GMAIL_PASSWORD = "gmail_password";
	private String userEmail;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View v = inflater.inflate(R.layout.email_shield_fragment_layout,
				container, false);
		return v;
	}

	@Override
	public void onStart() {
		super.onStart();

	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		Log.d("Email Sheeld::OnActivityCreated()", "");
		mSharedPreferences = getActivity().getApplicationContext()
				.getSharedPreferences("com.integreight.onesheeld",
						Context.MODE_PRIVATE);

		sendTo = (TextView) getView().findViewById(
				R.id.gmail_shield_sendto_textview);
		userName = (TextView) getView().findViewById(
				R.id.gmail_shield_username_textview);
		subject = (TextView) getView().findViewById(
				R.id.gmail_shield_subject_textview);
		login_bt = (Button) getView().findViewById(R.id.login_gmail_bt);
		logout_bt = (Button) getView().findViewById(R.id.logout_gmail_bt);
		login_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ConnectionDetector.isConnectingToInternet(getActivity()))
					// show dialog of registration then call add account method
					new GmailSinginPopup(getActivity(), emailEventHandler)
							.show();
				else
					Toast.makeText(
							getApplication().getApplicationContext(),
							"Please check your Internet connection and try again.",
							Toast.LENGTH_SHORT).show();
			}
		});

		// if user logged in run controller else ask for login
		if (isGmailLoggedInAlready()) {
			userEmail = mSharedPreferences.getString(
					PREF_EMAIL_SHIELD_GMAIL_ACCOUNT, "");

			((EmailShield) getApplication().getRunningShields().get(
					getControllerTag()))
					.setEmailEventHandler(emailEventHandler);
			login_bt.setVisibility(View.INVISIBLE);
			logout_bt.setVisibility(View.VISIBLE);
			userName.setVisibility(View.VISIBLE);
			userName.setText(userEmail);
		} else {
			login_bt.setVisibility(View.VISIBLE);
		}

		logout_bt.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutGmailAccount();
			}
		});

	}

	private boolean isGmailLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(PREF_EMAIL_SHIELD_USER_LOGIN,
				false);
	}

	private EmailEventHandler emailEventHandler = new EmailEventHandler() {

		@Override
		public void onSendingAuthError(String error) {
			if (canChangeUI())
				Toast.makeText(getApplication(), error, Toast.LENGTH_LONG)
						.show();

		}

		@Override
		public void onEmailsent(String email_send_to, String subject_text) {
			if (canChangeUI()) {
				sendTo.setText(email_send_to);
				subject.setText(subject_text);
			} else {

			}

		}

		@Override
		public void onSuccess() {
			if (canChangeUI())
				Toast.makeText(getApplication(), "Email sent Successful",
						Toast.LENGTH_LONG).show();
		}

		@Override
		public void onLoginSuccess(String userName, String password) {
			addAccount(userName, password);
		}

		@Override
		public void onEmailnotSent(String message_not_sent) {

			if (canChangeUI()) {
				Toast.makeText(getApplication(), message_not_sent,
						Toast.LENGTH_LONG).show();
			}
		}
	};

	private void initializeFirmata() {
		if (getApplication().getRunningShields().get(getControllerTag()) == null) {
			getApplication().getRunningShields().put(getControllerTag(),
					new EmailShield(getActivity(), getControllerTag()));
			((EmailShield) getApplication().getRunningShields().get(
					getControllerTag()))
					.setEmailEventHandler(emailEventHandler);
		}
	}

	@Override
	public void doOnServiceConnected() {
		initializeFirmata();
	}

	private void addAccount(String accountName, String password) {
		Log.d("account name ", accountName);
		// save in share perefrences
		SharedPreferences.Editor editor = mSharedPreferences.edit();
		editor.putString(PREF_EMAIL_SHIELD_GMAIL_ACCOUNT, accountName);
		// encrypt password before saved in share perefrence...
		byte[] b = SecurePreferences.convertStirngToByteArray(password);
		// encrypt
		try {
			byte[] key = SecurePreferences.generateKey();
			byte[] encryptedPassword = SecurePreferences.encrypt(key, b);
			// String encryptedPassword_str =
			// SecurePreferences.convertByteArrayToString(encryptedPassword);
			String encryptedPassword_str = Base64.encodeToString(
					encryptedPassword, Base64.DEFAULT);
			editor.putString(PREF_EMAIL_SHIELD_GMAIL_PASSWORD,
					encryptedPassword_str);
			// String key_str = SecurePreferences.convertByteArrayToString(key);
			// String key_str = Base64.encodeToString(key, Base64.DEFAULT);

			// editor.putString("M_KEY",key_str );
			// editor.putString(PREF_EMAIL_SHIELD_GMAIL_PASSWORD, password);
			editor.putBoolean(PREF_EMAIL_SHIELD_USER_LOGIN, true);
			// Commit the edits!
			editor.commit();
			((EmailShield) getApplication().getRunningShields().get(
					getControllerTag()))
					.setEmailEventHandler(emailEventHandler);
			login_bt.setVisibility(View.INVISIBLE);
			logout_bt.setVisibility(View.VISIBLE);
			userName.setVisibility(View.VISIBLE);
			userName.setText(accountName);

		} catch (Exception e) {
			Log.d("Email", "EmaiFragment:: filed in password encryption");
		}

	}

	private void logoutGmailAccount() {
		Editor e = mSharedPreferences.edit();
		e.remove(PREF_EMAIL_SHIELD_GMAIL_ACCOUNT);
		e.remove(PREF_EMAIL_SHIELD_GMAIL_PASSWORD);
		e.remove(PREF_EMAIL_SHIELD_USER_LOGIN);
		e.commit();
		login_bt.setVisibility(View.VISIBLE);
		logout_bt.setVisibility(View.INVISIBLE);
		userName.setVisibility(View.INVISIBLE);
		subject.setText("");
		sendTo.setText("");

	}

}
