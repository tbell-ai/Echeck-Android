package kr.co.tbell.echeck.views.fragment.account;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kr.co.tbell.echeck.R;
import kr.co.tbell.echeck.views.Manager.EcheckDatabaseManager;

public class Account1Fragment extends Fragment {

    public Account1Fragment() {}

    public static Account1Fragment newInstance(String param1, String param2) {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_account1, container, false);


        return rootView;
    }
}