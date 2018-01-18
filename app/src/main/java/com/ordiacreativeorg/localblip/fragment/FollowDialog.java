package com.ordiacreativeorg.localblip.fragment;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.adapter.FollowAdapter;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.SocialItem;
import com.ordiacreativeorg.localblip.model.SocialMedia;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/26/2015
 *
 */
public class FollowDialog extends DialogFragment {

    public static FollowDialog newInstance(Blip blip) {
        FollowDialog fragment = new FollowDialog();
        Bundle args = new Bundle();
        args.putSerializable("blip", blip);
        fragment.setArguments(args);
        return fragment;
    }

    public FollowDialog() {
        // Required empty public constructor
    }

    private RecyclerView mRecyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppTheme_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.follow_dialog, container, false);
        if (getArguments() != null){
            Blip blip = (Blip) getArguments().getSerializable("blip");
            if (blip != null) {
                // Inflate the layout for this fragment
                rootView.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDialog().cancel();
                    }
                });
                RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_categories);
                recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
                List<SocialItem> socialItems = new ArrayList<>();
                for (SocialMedia socialMedia : blip.getSocial()) {
                    if (!socialMedia.getFacebook().trim().isEmpty()){
                        SocialItem socialItem = new SocialItem();
                        socialItem.setAppPackage("com.facebook.katana");
                        socialItem.setAppUrl("fb://facewebmodal/f?href=https://www.facebook.com/");
                        socialItem.setUrl("https://www.facebook.com/");
                        socialItem.setId("G0retZ");
                        socialItem.setImageId(R.mipmap.facebook_icon_b);
                        socialItem.setName("Facebook");
                        socialItems.add(socialItem);
                    }
                    if (!socialMedia.getGoogle().trim().isEmpty()){
                        SocialItem socialItem = new SocialItem();
                        socialItem.setAppPackage("com.google.android.apps.plus");
                        socialItem.setAppUrl("https://plus.google.com/");
                        socialItem.setUrl("https://plus.google.com/");
                        socialItem.setId(socialMedia.getGoogle());
                        socialItem.setImageId(R.mipmap.g_plus_b);
                        socialItem.setName("+Google");
                        socialItems.add(socialItem);
                    }
                    if (!socialMedia.getInstagram().trim().isEmpty()){
                        SocialItem socialItem = new SocialItem();
                        socialItem.setAppPackage("com.instagram.android");
                        socialItem.setAppUrl("http://instagram.com/_u/");
                        socialItem.setUrl("http://instagram.com/_u/");
                        socialItem.setId(socialMedia.getInstagram());
                        socialItem.setImageId(R.mipmap.instagram_icon_b);
                        socialItem.setName("Instagram");
                        socialItems.add(socialItem);
                    }
                    if (!socialMedia.getTwitter().trim().isEmpty()){
                        SocialItem socialItem = new SocialItem();
                        socialItem.setAppPackage("com.twitter.android");
                        socialItem.setAppUrl("twitter://intent/follow?screen_name=");
                        socialItem.setUrl("https://twitter.com/intent/follow?screen_name=");
                        socialItem.setId(socialMedia.getTwitter());
                        socialItem.setImageId(R.mipmap.twitter_icon_b);
                        socialItem.setName("Twitter");
                        socialItems.add(socialItem);
                    }
                    if (!socialMedia.getPinterest().trim().isEmpty()){
                        SocialItem socialItem = new SocialItem();
                        socialItem.setAppPackage("com.pinterest");
                        socialItem.setAppUrl("https://www.pinterest.com/");
                        socialItem.setUrl("https://www.pinterest.com/");
                        socialItem.setId(socialMedia.getPinterest());
                        socialItem.setImageId(R.mipmap.pinterest_icon_b);
                        socialItem.setName("Pinterest");
                        socialItems.add(socialItem);
                    }
                    if (!socialMedia.getTumblr().trim().isEmpty()){
                        Log.e("tummm",""+socialMedia.getTumblr().trim().isEmpty());
                        SocialItem socialItem = new SocialItem();
                        socialItem.setAppPackage("com.tumblr");
                        socialItem.setAppUrl("http://www.tumblr.com/follow/");
                        socialItem.setUrl("http://www.tumblr.com/follow/");
                        socialItem.setId(socialMedia.getTumblr());
                        socialItem.setImageId(R.mipmap.t_icon_b);
                        socialItem.setName("Tumblr");
                        socialItems.add(socialItem);
                    }
                    if (!socialMedia.getSnapchat().trim().equals("")){
                        Log.e("snaapp",""+socialMedia.getSnapchat().trim().isEmpty());
                        SocialItem socialItem = new SocialItem();
                        socialItem.setAppPackage("com.snapchat");
                        socialItem.setAppUrl("https://www.snapchat.com/add/");
                        socialItem.setUrl("https://www.snapchat.com/add/");
                        socialItem.setId(socialMedia.getSnapchat());
                        socialItem.setImageId(R.mipmap.snapchat);
                        socialItem.setName("Snapchat");
                        socialItems.add(socialItem);
                    }
                }
                recyclerView.setAdapter(new FollowAdapter(getActivity(), socialItems));
            }
        }
        return rootView;
    }

}
