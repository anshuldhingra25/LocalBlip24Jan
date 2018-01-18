package com.ordiacreativeorg.localblip.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.fragment.ProgressDialog;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.BlipAnalytics;
import com.ordiacreativeorg.localblip.model.BlipStatDetailsByAge;
import com.ordiacreativeorg.localblip.model.BlipStatDetailsByGender;
import com.ordiacreativeorg.localblip.model.BlipStatDetailsByLocation;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;

import java.util.List;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/12/2015
 *
 */
public class BlipAnalyticsDetails extends BaseActivity {

    private TableLayout locationTableLayout;
    private ImageButton locationExpandButton;
    private boolean locationTableLayoutExpanded = false;
    private TableLayout ageTableLayout;
    private ImageButton ageExpandButton;
    private boolean ageTableLayoutExpanded = false;
    private TableLayout genderTableLayout;
    private ImageButton genderExpandButton;
    private boolean genderTableLayoutExpanded = false;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Blip blip = (Blip) getIntent().getSerializableExtra("blip");
        mProgressDialog = new ProgressDialog();
        setContentView(R.layout.blip_analytics_details);
        locationTableLayout = (TableLayout) findViewById(R.id.tl_location);
        locationExpandButton = (ImageButton) findViewById(R.id.ib_expand_loc);
        ageTableLayout = (TableLayout) findViewById(R.id.tl_age);
        ageExpandButton = (ImageButton) findViewById(R.id.ib_expand_age);
        genderTableLayout = (TableLayout) findViewById(R.id.tl_gend);
        genderExpandButton = (ImageButton) findViewById(R.id.ib_expand_gend);
        if (blip == null) finish();
        loadAnalyticsDetails(blip);
        ImageView back = (ImageView)findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadAnalyticsDetails(Blip blip){
        mProgressDialog.show(getSupportFragmentManager(), "progress");
        MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
        Api.getInstance().getMethods().getBlipAnalyticsDetails(
                memberDetail.getEmail(),
                memberDetail.getApiKey(),
                blip.getCouponId()
        ).enqueue(new Callback<BlipAnalytics>() {
            @Override
            public void onResponse(Response<BlipAnalytics> response, Retrofit retrofit) {
                final BlipAnalytics blipAnalytics = response.body();
                if (blipAnalytics != null) {
                    fillByLocationTable(blipAnalytics.getByLocations());
                    fillByAgeTable(blipAnalytics.getByAges());
                    fillByGenderTable(blipAnalytics.getByGenders());
                } else {
                    Snackbar.make(locationTableLayout, getResources().getString(R.string.failed_to_get_data), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
                }
                mProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Throwable t) {
                mProgressDialog.dismiss();
                Snackbar.make(locationTableLayout, getResources().getString(R.string.failed_to_get_data) + t.getMessage(), Snackbar.LENGTH_INDEFINITE).setAction( R.string.ok, new View.OnClickListener() {  @Override  public void onClick( View v ) {} } ).show();
            }
        });
    }

    private void fillByLocationTable(List<BlipStatDetailsByLocation> byLocations){
        if (byLocations != null && byLocations.size() > 6){
            locationExpandButton.setVisibility(View.VISIBLE);
            locationTableLayout.getLayoutParams().height = Math.round(240 * getResources().getDisplayMetrics().density);
            locationExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup.LayoutParams layoutParams = locationTableLayout.getLayoutParams();
                    if (locationTableLayoutExpanded){
                        layoutParams.height = Math.round(240 * getResources().getDisplayMetrics().density);
                        ((ImageButton) v).setImageResource(R.drawable.ic_action_expand);
                    }else{
                        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        ((ImageButton) v).setImageResource(R.drawable.ic_action_collapse);
                    }
                    locationTableLayoutExpanded = !locationTableLayoutExpanded;
                    locationTableLayout.setLayoutParams(layoutParams);
                }
            });
        }

        for (BlipStatDetailsByLocation byLocation : byLocations){
            addTableRow(locationTableLayout, byLocation.getZipcode(), byLocation.getViews(), byLocation.getBlipBook(), byLocation.getRedeemed());
        }
    }

    private void fillByAgeTable(List<BlipStatDetailsByAge> byAges){
        if (byAges.size() > 6){
            ageExpandButton.setVisibility(View.VISIBLE);
            ageTableLayout.getLayoutParams().height = Math.round(240 * getResources().getDisplayMetrics().density);
            ageExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup.LayoutParams layoutParams = ageTableLayout.getLayoutParams();
                    if (ageTableLayoutExpanded){
                        layoutParams.height = Math.round(240 * getResources().getDisplayMetrics().density);
                        ((ImageButton) v).setImageResource(R.drawable.ic_action_expand);
                    }else{
                        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        ((ImageButton) v).setImageResource(R.drawable.ic_action_collapse);
                    }
                    ageTableLayoutExpanded = !ageTableLayoutExpanded;
                    ageTableLayout.setLayoutParams(layoutParams);
                }
            });
        }
        for (BlipStatDetailsByAge byAge : byAges){
            addTableRow(ageTableLayout, byAge.getAge(), byAge.getViews(), byAge.getBlipBook(), byAge.getRedeemed());
        }
    }

    private void fillByGenderTable(List<BlipStatDetailsByGender> byGenders){
        if (byGenders.size() > 6){
            genderExpandButton.setVisibility(View.VISIBLE);
            genderTableLayout.getLayoutParams().height = Math.round(240 * getResources().getDisplayMetrics().density);
            genderExpandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewGroup.LayoutParams layoutParams = genderTableLayout.getLayoutParams();
                    if (genderTableLayoutExpanded){
                        layoutParams.height = Math.round(240 * getResources().getDisplayMetrics().density);
                        ((ImageButton) v).setImageResource(R.drawable.ic_action_expand);
                    }else{
                        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                        ((ImageButton) v).setImageResource(R.drawable.ic_action_collapse);
                    }
                    genderTableLayoutExpanded = !genderTableLayoutExpanded;
                    genderTableLayout.setLayoutParams(layoutParams);
                }
            });
        }
        for (BlipStatDetailsByGender byGender : byGenders){
            addTableRow(genderTableLayout, byGender.getGender(), byGender.getViews(), byGender.getBlipBook(), byGender.getRedeemed());
        }
    }

    private void addTableRow(TableLayout tableLayout, String name, int views, int blipbooks, int redeems){
        TableRow tableRow = (TableRow) getLayoutInflater().inflate(R.layout.blip_analytics_details_row, tableLayout, false);
        ((TextView) tableRow.findViewById(R.id.tv_name)).setText(name);
        ((TextView) tableRow.findViewById(R.id.tv_views)).setText(String.valueOf(views));
        ((TextView) tableRow.findViewById(R.id.tv_blipbooks)).setText(String.valueOf(blipbooks));
        ((TextView) tableRow.findViewById(R.id.tv_redeems)).setText(String.valueOf(redeems));
        tableLayout.addView(tableRow);
    }
}