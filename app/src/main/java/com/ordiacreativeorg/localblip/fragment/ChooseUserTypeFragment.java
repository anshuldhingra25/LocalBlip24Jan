package com.ordiacreativeorg.localblip.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.LogInActivity;
import com.ordiacreativeorg.localblip.constants.Constants;

/**
 * Created by dmytrobohachevskyy on 9/25/15.
 * <p>
 * Allow user to choose sign up type:
 * Vendor or consumer
 */
public class ChooseUserTypeFragment extends Fragment implements View.OnClickListener {
    // static info
    private View view;

    public static ChooseUserTypeFragment newInstance() {
        return new ChooseUserTypeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.chooise_user_type_fragment, container, false);
        return this.view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ((LogInActivity) getActivity()).setBarTitle(R.string.sign_up, true);

        // Initialize view elements and class members
        initViewAndClassMembers();
    }

    /**
     * Init views from current fragment
     */
    private void initViewAndClassMembers() {
        View vi = this.view;

        vi.findViewById(R.id.vendorButton).setOnClickListener(this);
        vi.findViewById(R.id.consumerButton).setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Constants.UserType userType = Constants.UserType.CONSUMER;

        switch (v.getId()) {
            case R.id.consumerButton:
                userType = Constants.UserType.CONSUMER;
                break;

            case R.id.vendorButton:
                userType = Constants.UserType.VENDOR;
                break;
        }
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragment_container, SignUpFragment.newInstance(userType))
                .addToBackStack("chooseUserType")
                .commit();
    }
}
