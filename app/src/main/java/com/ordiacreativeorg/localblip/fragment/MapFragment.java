package com.ordiacreativeorg.localblip.fragment;

import android.content.Intent;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.adapter.ShopBlipsAdapter;
import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.util.GPSTracker;
import com.ordiacreativeorg.localblip.util.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.ordiacreativeorg.localblip.fragment.ShopBlips.mapBlips;


public class MapFragment extends Fragment {
    ProgressBar progressmap;
    Double Flatitude = 0.0, FLongitude = 0.0;
    int count = -1;
    Marker mPerth;
    String formattedaddress;
    private static final String TAG = MapsInitializer.class.getName();
    MapView mMapView;
    private GoogleMap googleMap;
    ShopBlipsAdapter adapter;
    Marker source;
    String category = "";
    private String id = "";
    private ImageButton back;
    ArrayList<LatLng> locations;
    private List<Blip> ad;
    Geocoder geocoder;
    GPSTracker gpsTracker;
    private ProgressDialog mProgressDialog;
    private ArrayList<String> completeAddrress = new ArrayList<>();
    String apiAddress = "";
    private ArrayList<String> lattApi, longApi, titleApi;
    private int counter = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.map_fragment, container, false);
        ((MainActivity) getActivity()).back.setVisibility(View.VISIBLE);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                        Log.e("lkkj", "" + 1);
//                        ((MainActivity) getActivity()).back.setVisibility(View.GONE);
//                        getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, new ShopBlips()).addToBackStack(null).commit();
//                        ((MainActivity) getActivity()).changeFragment();
//                        acc.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;

            }
        });
        Log.d("MAPBLIPSCOUNT", String.valueOf(mapBlips.size()));
        Log.d("BLIPS", mapBlips.get(0).getCouponTitle());
        Log.d("MAPBLIPS", String.valueOf(mapBlips.get(0).getLocations().get(0).getZipCode()));
        for (int i = 0; i < mapBlips.size(); i++) {
            String comAddress = mapBlips.get(i).getLocations().get(0).getAddress1() + "," +
                    mapBlips.get(i).getLocations().get(0).getAddress2() + ","
                    + mapBlips.get(i).getLocations().get(0).getCity() + ","
                    + mapBlips.get(i).getLocations().get(0).getState() + ","
                    + String.valueOf(mapBlips.get(i).getLocations().get(0).getZipCode());
            System.out.println("Zipcode----" +
                    comAddress);
            completeAddrress.add(comAddress);

        }
        lattApi = new ArrayList<>();
        longApi = new ArrayList<>();
        titleApi = new ArrayList<>();
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        gpsTracker = new GPSTracker(getActivity());


        locations = new ArrayList();


        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        back = (ImageButton) rootView.findViewById(R.id.back);
        mProgressDialog = new ProgressDialog();
        mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
        checkCount(0);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                MainActivity mainActivity = new MainActivity();
//                mainActivity.changeFragment();

//                ((MainActivity)getActivity()).changeFragment();
                getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, new ShopBlips()).commit();
            }
        });
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
//        callThread();

        return rootView;
    }

    private void checkCount(int i) {


        if (i != mapBlips.size() || (mapBlips.size() == 1 && counter != 1)) {
            titleApi.add(mapBlips.get(i).getCouponTitle());
            apiAddress = completeAddrress.get(i);
            new DataLongOperationAsynchTask().execute();

        } else {
            mProgressDialog.dismiss();
            for (String member : lattApi) {
                Log.i("latitudenew: ", member);
            }
            for (String member : longApi) {
                Log.i("longitudenew: ", member);
            }
            for (String member : titleApi) {
                Log.i("titleApi: ", member);
            }


            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap mMap) {
                    googleMap = mMap;
                    // For showing a move to my location button
//                googleMap.setMyLocationEnabled(true);
                    // Changing map type


                    googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    // Showing / hiding your current location
                    googleMap.setMyLocationEnabled(false);
                    // Enable / Disable zooming controls
                    googleMap.getUiSettings().setZoomControlsEnabled(false);
                    // Enable / Disable my location button
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    // Enable / Disable Compass icon
                    googleMap.getUiSettings().setCompassEnabled(false);

                    // Enable / Disable Rotate gesture
                    googleMap.getUiSettings().setRotateGesturesEnabled(false);
                    // Enable / Disable zooming functionality
                    googleMap.getUiSettings().setZoomGesturesEnabled(true);


                    CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(gpsTracker.getLatitude(),
                            gpsTracker.getLongitude())).zoom(8).build();
                    googleMap.addMarker(new MarkerOptions().position(new LatLng(gpsTracker.getLatitude(),
                            gpsTracker.getLongitude())));

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

//                List<String> latt = convertZipToLatLng();
//                for (int i = 0; i < latt.size(); i++) {
//
//
//                    LatLng latLng = new LatLng(Double.parseDouble(latt.get(i).split("n")[0]),
//                            Double.parseDouble(latt.get(i).split("n")[1]));
//                    googleMap.addMarker(new MarkerOptions().position(latLng));
//
//                }

                    for (int i = 0; i < lattApi.size(); i++) {


                        LatLng sydney1 = new LatLng(Double.parseDouble(lattApi.get(i)), Double.parseDouble(longApi.get(i)));
                        mPerth = googleMap.addMarker(new MarkerOptions().position(sydney1).title(titleApi.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_show_on_map)));

                        mPerth.showInfoWindow();

                        String a = String.valueOf(mPerth.getPosition());
                        Log.e("Valuemarker", "" + a);

                    }
//                    for (Blip blip : mapBlips) {
//                        if (blip.isOnline()) {
//                            mPerth.setVisible(false);
//                            break;
//
//                        }
//                    }
//                    LatLng latLng = new LatLng(Double.parseDouble(lattApi.get(0)),
//                            Double.parseDouble(longApi.get(0)));
//                    mPerth = googleMap.addMarker(new MarkerOptions().position(latLng).
//                            title(mapBlips.get(counter).getCouponTitle()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_show_on_map)));

//                        mPerth.showInfoWindow();


                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            Log.e("MarkerTitle", marker.getTitle());
                            for (Blip blip : mapBlips) {
                                if (blip.getCouponTitle().equals(marker.getTitle())) {
                                    Intent intent = new Intent(getActivity(), com.ordiacreativeorg.localblip.activity.BlipDetails.class);
//                                Log.d("blip", "" + mapBlips.get(count));
                                    intent.putExtra("blip", blip);
                                    startActivityForResult(intent, 1020);
                                    break;

                                }
                            }


//                String[] latarr = ("" + marker.getPosition()).replace("lat/lng:", "").replace("(", "").replace(")", "").replaceAll(" ", "").split(",");
//                String _id = "" + rowItems.get(getIndex(latarr[0])).getRestaurant_id();
//                Toast.makeText(getActivity(), "" + rowItems.get(getIndex(latarr[0])).getRestaurant_id(), Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getActivity(), com.ordiacreative.localblip.activity.BlipDetails.class);
////                                Log.d("blip", "" + mapBlips.get(count));
//                            intent.putExtra("blip", mapBlips.get((Integer) marker.getTag()));
//                            startActivityForResult(intent, 1020);
                        }
                    });

                }
            });


        }
    }

    private void callThread() {
//        final int[] aa = {0};
        Log.d("size", "" + ShopBlips.mapBlips.size());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    // thread to sleep for 1000 milliseconds

                    while (count < ShopBlips.mapBlips.size()) {
                        Log.d("count", "" + count);
                        Thread.sleep(700);
                        new DataLongOperationAsynchTask().execute();
                        count = count + 1;
                        if (count == ShopBlips.mapBlips.size() - 1) {
                            mProgressDialog.dismiss();
                        }

                    }

                } catch (Exception e) {
                    System.out.println(e);
                }

            }
        };
        new Thread(runnable).start();


    }


    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private class DataLongOperationAsynchTask extends AsyncTask<String, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String[] doInBackground(String... params) {
            String response;
            try {
                Log.d("apiAddress", "" + apiAddress);
                JSONParser jsonParser = new JSONParser();

                response = jsonParser.makeServiceCall("https://maps.google.com/maps/api/geocode/json?address=" +
                        apiAddress.replaceAll(" ", "") + "&sensor=false");
                Log.d("ResponseLatLng", "" + response);
                return new String[]{response};
            } catch (Exception e) {

                return new String[]{"error"};

            }
        }

        @Override
        protected void onPostExecute(String... result) {
            try {
                JSONObject jsonObject = new JSONObject(result[0]);
                if (counter != mapBlips.size() || (mapBlips.size() == 1 && counter != 1)) {
                    double lng = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lng");
                    FLongitude = lng;


                    double lat = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                            .getJSONObject("geometry").getJSONObject("location")
                            .getDouble("lat");
                    Flatitude = lat;
                    Log.d("latitude", "" + lat);
                    Log.d("longitude", "" + lng);
                    lattApi.add(String.valueOf(Flatitude));
                    longApi.add(String.valueOf(FLongitude));
                }

            } catch (JSONException e) {
                Log.d("Responseerror", "" + "error");
                e.printStackTrace();
            }

            Log.e("counter", "" + counter);


            checkCount(counter);
            counter++;
        }
    }


}