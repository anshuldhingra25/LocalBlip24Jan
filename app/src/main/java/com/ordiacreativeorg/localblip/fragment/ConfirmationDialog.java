package com.ordiacreativeorg.localblip.fragment;


import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConfirmationDialog extends DialogFragment {

    public ConfirmationDialog() {
        // Required empty public constructor
    }

    public static ConfirmationDialog newInstance(String title, String message, String okText, String cancelText, OnActionConfirmationListener onActionConfirmationListener) {

        Bundle args = new Bundle();
        if (title != null) args.putString("title", title);
        if (message != null) args.putString("message", message);
        if (okText != null) args.putString("oktext", okText);
        if (cancelText != null) args.putString("canceltext", cancelText);

        ConfirmationDialog fragment = new ConfirmationDialog();
        fragment.setArguments(args);
        fragment.onActionConfirmationListener = onActionConfirmationListener;
        return fragment;
    }

    public static ConfirmationDialog newInstance(String title, String message, String okText, String cancelText, OnActionConfirmationListener onActionConfirmationListener, boolean hideCancel) {

        Bundle args = new Bundle();
        if (title != null) args.putString("title", title);
        if (message != null) args.putString("message", message);
        if (okText != null) args.putString("oktext", okText);
        if (cancelText != null) args.putString("canceltext", cancelText);

        args.putBoolean( "hideCancel", hideCancel );

        ConfirmationDialog fragment = new ConfirmationDialog();
        fragment.setArguments(args);
        fragment.onActionConfirmationListener = onActionConfirmationListener;
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
            if (args.containsKey("title")){
                view.findViewById(R.id.tv_title).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tv_title)).setText(args.getString("title"));
            }
            if (args.containsKey("message")){
                ((TextView) view.findViewById(R.id.tv_message)).setText(args.getString("message"));
            }
            if (args.containsKey("oktext")){
                ((Button) view.findViewById(R.id.btn_ok)).setText(args.getString("oktext"));
            }
            if (args.containsKey("canceltext")){
                ((Button) view.findViewById(R.id.btn_cancel)).setText(args.getString("canceltext"));
            }

            if ( args.getBoolean( "hideCancel", false ) ) {
                view.findViewById( R.id.btn_cancel ).setVisibility( View.GONE );
            } else {
                view.findViewById( R.id.btn_cancel ).setVisibility( View.VISIBLE );
            }

            view.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onActionConfirmationListener != null){
                        onActionConfirmationListener.onConfirmed();
                    }
                    dismiss();
                }
            });
            view.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDialog().cancel();
                }
            });
        }
        return view;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (onActionConfirmationListener != null){
            onActionConfirmationListener.onCanceled();
        }
        super.onCancel(dialog);
    }

    private OnActionConfirmationListener onActionConfirmationListener;

    public interface OnActionConfirmationListener {
        void onConfirmed();
        void onCanceled();
    }
}
