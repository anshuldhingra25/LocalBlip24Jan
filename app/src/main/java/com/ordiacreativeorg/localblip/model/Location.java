package com.ordiacreativeorg.localblip.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dmytrobohachevskyy on 10/7/15.
 *
 * Represent location object
 */
public class Location implements Serializable{

    @SerializedName(value="locationid", alternate={"locid"})
    @Expose
    private int locationId = -1;

    @SerializedName("address1")
    @Expose
    private String address1;

    @SerializedName("address2")
    @Expose
    private String address2;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("addressstate")
    @Expose
    private String addressState;

    @SerializedName("zipcode")
    @Expose
    private String zipCode;

    @SerializedName("phone")
    @Expose
    private String phone;

    @SerializedName("description")
    @Expose
    private String description;


//    @SerializedName("description")
//    @Expose
//    private String description;

    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("online")
    @Expose
    private int nationwide;

    @SerializedName("locationalias")
    @Expose
    private String locationAlias;

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getAddressState() {
        return addressState;
    }

    public void setAddressState(String addressState) {
        this.addressState = addressState;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isNationwide() {
        return nationwide == 1;
    }

    public void setNationwide(boolean nationwide) {
        this.nationwide = nationwide ? 1 : 0;
    }

    public String getLocationAlias() {
        return locationAlias;
    }

    public void setLocationAlias(String locationAlias) {
        this.locationAlias = locationAlias;
    }

    /*public Location copyLocation(){
        Location location = new Location();
        location.setLocationId(locationId);
        location.setMarketAreaId(marketAreaId);
        location.setAddress1(address1);
        location.setAddress2(address2);
        location.setCity(city);
        location.setState(state);
        location.setZipCode(zipCode);
        location.setPhone(phone);
        location.setDescription(description);
        location.setWebsite(website);
        location.setNationwide(nationwide == 1);
        location.setLocationAlias(locationAlias);
        return location;
    }*/

}
 /*mMapView.getMapAsync(new OnMapReadyCallback() {
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


                        LatLng latLng = new LatLng(Flatitude,
                                FLongitude);
                        mPerth = googleMap.addMarker(new MarkerOptions().position(latLng).
                                title(mapBlips.get(count).getCouponTitle()).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_show_on_map)));
                        mPerth.setTag(count);
//                        mPerth.showInfoWindow();


                        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {


//                String[] latarr = ("" + marker.getPosition()).replace("lat/lng:", "").replace("(", "").replace(")", "").replaceAll(" ", "").split(",");
//                String _id = "" + rowItems.get(getIndex(latarr[0])).getRestaurant_id();
//                Toast.makeText(getActivity(), "" + rowItems.get(getIndex(latarr[0])).getRestaurant_id(), Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getActivity(), com.ordiacreative.localblip.activity.BlipDetails.class);
//                                Log.d("blip", "" + mapBlips.get(count));
//                                Log.d("count", "" + count);
                                intent.putExtra("blip", mapBlips.get((Integer) marker.getTag()));
                                startActivityForResult(intent, 1020);
                            }
                        });

                    }
                });
*/
