package com.ordiacreativeorg.localblip.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.model.Redeemed;

/**
 * A simple {@link Fragment} subclass.
 */
public class RedeemDialog extends DialogFragment {

    public RedeemDialog() {
        // Required empty public constructor
    }

    public static RedeemDialog newInstance(Redeemed redeemed) {

        Bundle args = new Bundle();
        args.putSerializable("redeemed", redeemed);
        RedeemDialog fragment = new RedeemDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.confirmation_dialog, container, false);
        if (getArguments() != null){
            final Bundle args = getArguments();
            Redeemed redeemed = (Redeemed) args.getSerializable("redeemed");
            view.findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
            view.findViewById(R.id.btn_cancel).setVisibility(View.GONE);
            if (redeemed == null || redeemed.getCouponTitle() == null || redeemed.getCouponTitle().isEmpty()){
                ((TextView) view.findViewById(R.id.tv_title)).setText("Redemption failed");
                if (redeemed != null){
                    ((TextView) view.findViewById(R.id.tv_message)).setText(redeemed.getResponse());
                }
            }else{
                if (redeemed.isRedeemed()){
                    ((TextView) view.findViewById(R.id.tv_title)).setText("Redemption complete");
                    String message = "This coupon was redeemed\n\n"
                            + "Vendor: " + redeemed.getVendorName() + "\n"
                            + "Coupon: " + redeemed.getCouponTitle() + "\n"
                            + "Expires at: " + redeemed.getExpireDate() + "\n"
                            + (redeemed.getValueType() == 1 ? "$" + redeemed.getValue() + " off" : redeemed.getValue() + "% off");
                    ((TextView) view.findViewById(R.id.tv_message)).setText(message);
                }else{
                    ((TextView) view.findViewById(R.id.tv_title)).setText("Redemption incomplete");
                    ((TextView) view.findViewById(R.id.tv_message)).setText("Somehow this coupon redemption incomplete. Try again please!");
                }
            }
            view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
        }
        return view;
    }
}
