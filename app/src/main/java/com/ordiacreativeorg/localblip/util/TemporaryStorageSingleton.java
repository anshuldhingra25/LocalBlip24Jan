package com.ordiacreativeorg.localblip.util;

import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.MarketArea;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.Vendor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by MarksUser on 3/26/2015.
 *
 * @author nine3_marks
 */
public class TemporaryStorageSingleton {

    private static volatile TemporaryStorageSingleton instance;

    public static TemporaryStorageSingleton getInstance() {
        TemporaryStorageSingleton localInstance = instance;
        if (localInstance == null) {
            synchronized (TemporaryStorageSingleton.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new TemporaryStorageSingleton();
                }
            }
        }
        return localInstance;
    }

    // local data

    private MemberDetail memberDetails = null;
    private ArrayList<Vendor> vendors;
    private ArrayList<Category> categories;
    private ArrayList<MarketArea> marketAreas;
    private String searchKeyWord = "";
    private boolean showLocalVendors = true;
    private int miles = 1;
    private int zipCode = -1;
    private int categoryPosition = 0;
    private ArrayList<String> mStatesNames;
    private ArrayList<String> mStatesIds;
    private HashMap<String, List<MarketArea>> marketAreasByStates;
    private int valueZip = 1;


    public int getValueZip() {
        return valueZip;
    }

    public void setValueZip(int valueZip) {
        this.valueZip = valueZip;
    }

    public void setMemberDetails(MemberDetail memberDetails) {
        this.memberDetails = memberDetails;
    }

    public ArrayList<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(ArrayList<Vendor> vendors) {
        this.vendors = vendors;
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList<Category> categories) {
        this.categories = categories;
    }

    public List<MarketArea> getMarketAreasByStateName(String stateName) {
        return getMarketAreasByStateId(mStatesIds.get(mStatesNames.indexOf(stateName)));
    }

    public List<MarketArea> getMarketAreasByStateId(String stateId) {
        return marketAreasByStates.get(stateId);
    }

    public MarketArea getMarketAreaById(int id) {
        for (MarketArea marketArea : marketAreas) {
            if (marketArea.getId() == id) {
                return marketArea;
            }
        }
        return null;
    }

    public void setMarketAreas(ArrayList<MarketArea> marketAreas) {
        this.marketAreas = marketAreas;
    }

    public void parseMarketAreas(List<String> states) {
        mStatesNames = new ArrayList<>();
        mStatesIds = new ArrayList<>();
        marketAreasByStates = new HashMap<>();
        for (String state : states) {
            String[] tmp = state.split(":");
            mStatesNames.add(tmp[0]);
            mStatesIds.add(tmp[1]);
            ArrayList<MarketArea> marktAreas = new ArrayList<>();
            for (MarketArea marketArea : marketAreas) {
                if (marketArea.getState().equals(tmp[1])) {
                    marktAreas.add(marketArea);
                }
            }
            marketAreasByStates.put(tmp[1], marktAreas);
        }
    }

    public List<String> getStatesNames() {
        return mStatesNames;
    }

    public List<String> getStatesIds() {
        return mStatesIds;
    }

    public String getStateNameByID(String id) {
        int index = mStatesIds.indexOf(id);
        if (index < 0) return null;
        return mStatesNames.get(index);
    }

    public MemberDetail getMemberDetails() {
        return memberDetails;
    }

    public String getSearchKeyWord() {
        return searchKeyWord;
    }

    public void setSearchKeyWord(String searchKeyWord) {
        this.searchKeyWord = searchKeyWord;
    }

    public boolean isShowLocalVendors() {
        return showLocalVendors;
    }

    public void setShowLocalVendors(boolean showLocalVendors) {
        this.showLocalVendors = showLocalVendors;
    }

    public int getMiles() {
        return miles;
    }

    public void setMiles(int miles) {
        this.miles = miles;
    }

    public int getZipCode() {
        return zipCode;
    }

    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }

    public int getCategoryPosition() {
        return categoryPosition;
    }

    public void setCategoryPosition(int categoryPosition) {
        this.categoryPosition = categoryPosition;
    }

    public int getSelectedCategoryId() {
        return categories.get(categoryPosition).getCategoryId();
    }

    public Category getCategoryById(int id) {
        for (Category category : categories) {
            if (category.getCategoryId() == id) return category;
        }
        return null;
    }

}
