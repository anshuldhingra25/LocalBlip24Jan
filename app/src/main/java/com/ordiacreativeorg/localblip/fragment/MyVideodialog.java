package com.ordiacreativeorg.localblip.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;


public class MyVideodialog extends DialogFragment {

    private RelativeLayout choosevideo_tab_layout, browse_layout, edit_layout;
    private Button btn_continue, btn_browse;
    private LinearLayout choosevideo_list_main_layout, choosevideo_upload, choosevideo_youtube, choosevideo_embedded;
    private TextView txt_videos, error_txt, txt_uploadvideo, txt_youtubevideo, txt_embeded, upload_txt, link_txt;
    private EditText et_link;
    private String videotype = "1";
    private String mediaPath = "";
    private String videoUrl = "";

    public MyVideodialog() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.myvideo_dialog, container, false);


        choosevideo_tab_layout = (RelativeLayout) rootView.findViewById(R.id.choosevideo_tab_layout);
        browse_layout = (RelativeLayout) rootView.findViewById(R.id.browse_layout);
        edit_layout = (RelativeLayout) rootView.findViewById(R.id.edit_layout);
        btn_continue = (Button) rootView.findViewById(R.id.btn_continue);
        btn_browse = (Button) rootView.findViewById(R.id.btn_browse);
        choosevideo_list_main_layout = (LinearLayout) rootView.findViewById(R.id.choosevideo_list_main_layout);
        choosevideo_upload = (LinearLayout) rootView.findViewById(R.id.choosevideo_upload);
        choosevideo_youtube = (LinearLayout) rootView.findViewById(R.id.choosevideo_youtube);
        choosevideo_embedded = (LinearLayout) rootView.findViewById(R.id.choosevideo_embedded);
        txt_uploadvideo = (TextView) rootView.findViewById(R.id.txt_uploadvideo);
        txt_youtubevideo = (TextView) rootView.findViewById(R.id.txt_youtubevideo);
        txt_embeded = (TextView) rootView.findViewById(R.id.txt_embeded);
        upload_txt = (TextView) rootView.findViewById(R.id.upload_txt);
        link_txt = (TextView) rootView.findViewById(R.id.link_txt);
        et_link = (EditText) rootView.findViewById(R.id.et_link);

        error_txt = (TextView) rootView.findViewById(R.id.error_txt);
        rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent();
                switch (videotype) {
                    case "1":
                        if (mediaPath.isEmpty()) {
                            error_txt.setVisibility(View.VISIBLE);
                            error_txt.setText("Select video first*");
                        } else {
                            fireintent(in);
                        }
                        break;
                    case "2":
                        videoUrl = et_link.getText().toString().trim();
                        if (videoUrl.isEmpty()) {
                            error_txt.setVisibility(View.VISIBLE);
                            error_txt.setText("Enter youtube url*");
                        } else {
                            fireintent(in);
                        }

                        break;
                    case "3":
                        videoUrl = et_link.getText().toString().trim();
                        if (videoUrl.isEmpty()) {
                            error_txt.setVisibility(View.VISIBLE);
                            error_txt.setText("Enter embedded code*");
                        } else {
                            fireintent(in);
                        }
                        break;
                }


            }
        });
        btn_browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1212);
            }
        });

        choosevideo_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videotype = "1";
                choosevideo_upload.setBackgroundColor(Color.parseColor("#3A737A"));
                choosevideo_youtube.setBackgroundColor(Color.parseColor("#FFFFFF"));
                choosevideo_embedded.setBackgroundColor(Color.parseColor("#FFFFFF"));
                txt_uploadvideo.setTextColor(Color.parseColor("#FFFFFF"));
                txt_youtubevideo.setTextColor(Color.parseColor("#3A737A"));
                txt_embeded.setTextColor(Color.parseColor("#3A737A"));
                error_txt.setVisibility(View.GONE);
                browse_layout.setVisibility(View.VISIBLE);
                edit_layout.setVisibility(View.GONE);
            }
        });

        choosevideo_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videotype = "2";
                choosevideo_upload.setBackgroundColor(Color.parseColor("#FFFFFF"));
                choosevideo_youtube.setBackgroundColor(Color.parseColor("#3A737A"));
                choosevideo_embedded.setBackgroundColor(Color.parseColor("#FFFFFF"));
                txt_uploadvideo.setTextColor(Color.parseColor("#3A737A"));
                txt_youtubevideo.setTextColor(Color.parseColor("#FFFFFF"));
                txt_embeded.setTextColor(Color.parseColor("#3A737A"));
                browse_layout.setVisibility(View.GONE);
                error_txt.setVisibility(View.GONE);
                edit_layout.setVisibility(View.VISIBLE);
                link_txt.setText("Youtube Video URL *");
//                et_link.setText("https://www.youtube.com/watch?v=P-N8To7zCwo");

            }
        });

        choosevideo_embedded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videotype = "3";
                choosevideo_upload.setBackgroundColor(Color.parseColor("#FFFFFF"));
                choosevideo_youtube.setBackgroundColor(Color.parseColor("#FFFFFF"));
                choosevideo_embedded.setBackgroundColor(Color.parseColor("#3A737A"));
                txt_uploadvideo.setTextColor(0xFF3A737A);
                txt_youtubevideo.setTextColor(Color.parseColor("#3A737A"));
                txt_embeded.setTextColor(Color.parseColor("#FFFFFF"));
                browse_layout.setVisibility(View.GONE);
                edit_layout.setVisibility(View.VISIBLE);
                error_txt.setVisibility(View.GONE);
                link_txt.setText("Video Embed Code *");
            }
        });
        return rootView;
    }

    private void fireintent(Intent in) {
        in.putExtra("videotype", videotype);
        Log.e("videotype", "" + videotype);
        in.putExtra("mediaPath", mediaPath);
        Log.e("mediaPath", "" + mediaPath);
        in.putExtra("videoUrl", videoUrl);
        Log.e("videoUrl", "" + videoUrl);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, in);
        dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {


            case 1212:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    Uri selectedVideo = data.getData();
                    String[] filePathColumn = {MediaStore.Video.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedVideo, filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String videoPath = cursor.getString(columnIndex);
                        mediaPath = cursor.getString(columnIndex);
                        cursor.close();


                    }
                }
                break;


        }
    }

}
