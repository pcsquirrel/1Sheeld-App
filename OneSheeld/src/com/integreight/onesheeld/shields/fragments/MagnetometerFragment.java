package com.integreight.onesheeld.shields.fragments;

import com.integreight.onesheeld.R;
import com.integreight.onesheeld.utils.ShieldFragmentParent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MagnetometerFragment extends
		ShieldFragmentParent<MagnetometerFragment> {

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.no_shield, container, false);
	}
}
