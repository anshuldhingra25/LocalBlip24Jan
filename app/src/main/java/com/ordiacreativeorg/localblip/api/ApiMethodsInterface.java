package com.ordiacreativeorg.localblip.api;

import com.ordiacreativeorg.localblip.model.Blip;
import com.ordiacreativeorg.localblip.model.BlipAlert;
import com.ordiacreativeorg.localblip.model.BlipAnalytics;
import com.ordiacreativeorg.localblip.model.BlipBoost;
import com.ordiacreativeorg.localblip.model.Category;
import com.ordiacreativeorg.localblip.model.CategoryWithImages;
import com.ordiacreativeorg.localblip.model.ChatMessage;
import com.ordiacreativeorg.localblip.model.Conversation;
import com.ordiacreativeorg.localblip.model.Location;
import com.ordiacreativeorg.localblip.model.MarketArea;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.NewBlipCount;
import com.ordiacreativeorg.localblip.model.Redeemed;
import com.ordiacreativeorg.localblip.model.SentMessage;
import com.ordiacreativeorg.localblip.model.SimpleResponse;
import com.ordiacreativeorg.localblip.model.SocialMedia;
import com.ordiacreativeorg.localblip.model.UnreadMessagesCount;
import com.ordiacreativeorg.localblip.model.Vendor;
import com.squareup.okhttp.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created Sergey Mitrofanov (goretz.m@gmail.com) on 11/5/2015
 * <p>
 * All API methods are here
 */
public interface ApiMethodsInterface {


    /*
     *  CREATE BLIP ALERT JASYC
     */
    @POST("/admin/apiincreasemyvisibilitycustom.php")
    Call<BlipAlert> blipAlert(
            @Query("blipid") String coupanid,
            @Query("zipcode") String zipcode,               // Member's email
            @Query("distance") String distance,             // Member's API Key
            @Query("age") String age,               // New blip title
            @Query("gender") String gender,                    // New blip discount type (1 =$ / 2 =%)
            @Query("category") String category,          // New blip value
            @Query("content") String content,         // New blip categories as IDs (Array) or (INT)
            @Query("date") String date,   // New blip subcategories as IDs (Array) or (INT)
            @Query("hour") String hour,
            @Query("min") String minute,
            @Query("productid") String productid, // New blip locations as IDs (Array) or (INT)
            @Query("senttype") String sentType,
            @Query("email") String email,
            @Query("apikey") String apiKey
//    @Query("aid") String userid

            // All optional fields should be here
//    @QueryMap Map<String, String> otherOptions
    );

    /*
    *  CREATE BLIP MAIL JASYC
    */

    @POST("/admin/apiincreasemyvisibilitycustom.php")
    Call<BlipAlert> blipMail(
            @Query("blipid") String coupanid,
            @Query("zipcode") String zipcode,               // Member's email
            @Query("distance") String distance,             // Member's API Key
            @Query("age") String age,               // New blip title
            @Query("gender") String gender,                    // New blip discount type (1 =$ / 2 =%)
            @Query("category") String category,// New blip value
            @Query("subject") String subject,
            @Query("content") String content,         // New blip categories as IDs (Array) or (INT)
            @Query("date") String date,   // New blip subcategories as IDs (Array) or (INT)
            @Query("hour") String hour,
            @Query("min") String minute,
            @Query("productid") String productid, // New blip locations as IDs (Array) or (INT)
            @Query("senttype") String sentType,
            @Query("email") String email,
            @Query("apikey") String apiKey,
            @QueryMap Map<String, String> otherOptions
//            @Part("img\"; filename=\"photo.jpg") RequestBody file
//    @Query("aid") String userid

            // All optional fields should be here
//    @QueryMap Map<String, String> otherOptions
    );

    @Multipart
    @Headers({
            "cache-control: no-cache"
    })
    @POST("/admin/apiincreasemyvisibilitycustom.php")
    Call<BlipAlert> blipMail(
            @Query("blipid") String coupanid,
            @Query("zipcode") String zipcode,               // Member's email
            @Query("distance") String distance,             // Member's API Key
            @Query("age") String age,               // New blip title
            @Query("gender") String gender,                    // New blip discount type (1 =$ / 2 =%)
            @Query("category") String category,// New blip value
            @Query("subject") String subject,
            @Query("content") String content,         // New blip categories as IDs (Array) or (INT)
            @Query("date") String date,   // New blip subcategories as IDs (Array) or (INT)
            @Query("hour") String hour,
            @Query("min") String minute,
            @Query("productid") String productid, // New blip locations as IDs (Array) or (INT)
            @Query("senttype") String sentType,
            @Query("email") String email,
            @Query("apikey") String apiKey,
            @Part("img\"; filename=\"photo.jpg") RequestBody file
//    @Query("aid") String userid

            // All optional fields should be here
//    @QueryMap Map<String, String> otherOptions
    );

    /*
    *  CREATE BLIP PICK JASYC
    */
    @GET("/admin/apiincreasemyvisibilitycustom.php")
    Call<BlipAlert> blipPick(

            @Query("coupons") String zipcode,               // Member's email
            @Query("zipcode") String distance,             // Member's API Key
            @Query("category") String age,               // New blip title
            @Query("weekstart") String gender,                    // New blip discount type (1 =$ / 2 =%)
            @Query("productid") String category,// New blip value
            @Query("email") String email,
            @Query("apikey") String apiKey
    );

    /*
     *  CREATE BLIP
     */

    @Multipart
    @Headers({
            "cache-control: no-cache"
    })
    /*for link image*/
    @POST("/admin/createblipapicustom.php")
    Call<Blip> createBlip(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey,             // Member's API Key
            @Query("title") String title,               // New blip title
            @Query("type") int type,                    // New blip discount type (1 =$ / 2 =%)
            @Query("blipvalue") int blipvalue,          // New blip value
            @Query("category") String category,         // New blip categories as IDs (Array) or (INT)
            @Query("subcategory") String subcategory,   // New blip subcategories as IDs (Array) or (INT)
            @Query("location") String location,            // New blip locations as IDs (Array) or (INT)
            @Query("franchise") String fra,

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Query("videotype") int videotype,
            @Query("contenttype") int contenttype,
            @Part("videofile\"; filename=\"Video.mp4") RequestBody video
    );

    /*ni image no video*/
    @POST("/admin/createblipapicustom.php")
    Call<Blip> createBlip(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey,             // Member's API Key
            @Query("title") String title,               // New blip title
            @Query("type") int type,                    // New blip discount type (1 =$ / 2 =%)
            @Query("blipvalue") int blipvalue,          // New blip value
            @Query("category") String category,         // New blip categories as IDs (Array) or (INT)
            @Query("subcategory") String subcategory,   // New blip subcategories as IDs (Array) or (INT)
            @Query("location") String location,            // New blip locations as IDs (Array) or (INT)
            @Query("franchise") String fra,

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Query("videotype") int videotype,
            @Query("contenttype") int contenttype


    );


    @POST("/admin/createblipapicustom.php")
    Call<Blip> createBlip(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey,             // Member's API Key
            @Query("title") String title,               // New blip title
            @Query("type") int type,                    // New blip discount type (1 =$ / 2 =%)
            @Query("blipvalue") int blipvalue,          // New blip value
            @Query("category") String category,         // New blip categories as IDs (Array) or (INT)
            @Query("subcategory") String subcategory,   // New blip subcategories as IDs (Array) or (INT)
            @Query("location") String location,            // New blip locations as IDs (Array) or (INT)
            @Query("franchise") String fra,

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Query("videotype") int videotype,
            @Query("contenttype") int contenttype,
            @Query("video") String videourl

            // These fields are optional:
            //originalprice -	New blip original price
            //description	-	New blip description
            //fineprint	    -	New blip fine print
            //expiremonth	-	New blip expire month (if not set it will NEVER expire)
            //expireday     -	New blip expire day (if not set it will NEVER expire)
            //expireyear	-   New blip expire year (if not set it will NEVER expire)
            //quantity      -   new blip quantity (if not set it will be UNLIMITED)
            //status    	-   New blip status(0 for inactive | 1 for active)
            //published    	-   New blip published status(0 for don't publish | 1 for published)
            //promote    	-   New blip promoted status(0 | 1)
            //sticky    	-   New blip sticky status(0 | 1)
            //imgurl    	-   coupon image
            //img    	    -   TYPE=FILE (allow upload images to server using file type field)

            /*@Query("originalprice") int originalprice,
            @Query("description") String description,
            @Query("fineprint") String fineprint,
            @Query("expiremonth") String expiremonth,

            @Query("expireday") String expireday,
            @Query("expireyear") String expireyear,
            @Query("quantity") String quantity,
            @Query("status") int status,

            @Part("img") RequestBody img*/
    );


    /*
     *  CREATE BLIP WITH IMAGE UPLOAD
     */
    @Multipart
    @Headers({
            "cache-control: no-cache"
    })
    @POST("/admin/createblipapicustom.php")
    Call<Blip> createBlip(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey,             // Member's API Key
            @Query("title") String title,               // New blip title
            @Query("type") int type,                    // New blip discount type (1 =$ / 2 =%)
            @Query("blipvalue") int blipvalue,          // New blip value
            @Query("category") String category,         // New blip categories as IDs (Array) or (INT)
            @Query("subcategory") String subcategory,   // New blip subcategories as IDs (Array) or (INT)
            @Query("location") String location,            // New blip locations as IDs (Array) or (INT)
            @Query("videotype") int videotype,   // New blip subcategories as IDs (Array) or (INT)
            @Query("contenttype") int contenttype,
            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Part("img\"; filename=\"photo.jpg") RequestBody file,

            @Part("videofile\"; filename=\"Video.mp4") RequestBody video
            // These fields are optional:
            //originalprice -	New blip original price
            //description	-	New blip description
            //fineprint	    -	New blip fine print
            //expiremonth	-	New blip expire month (if not set it will NEVER expire)
            //expireday     -	New blip expire day (if not set it will NEVER expire)
            //expireyear	-   New blip expire year (if not set it will NEVER expire)
            //quantity      -   new blip quantity (if not set it will be UNLIMITED)
            //status    	-   New blip status(0 for inactive | 1 for active)
            //published    	-   New blip published status(0 for don't publish | 1 for published)
            //promote    	-   New blip promoted status(0 | 1)
            //sticky    	-   New blip sticky status(0 | 1)
            //imgurl    	-   coupon image
            //img    	    -   TYPE=FILE (allow upload images to server using file type field)

            /*@Query("originalprice") int originalprice,
            @Query("description") String description,
            @Query("fineprint") String fineprint,
            @Query("expiremonth") String expiremonth,

            @Query("expireday") String expireday,
            @Query("expireyear") String expireyear,
            @Query("quantity") String quantity,
            @Query("status") int status,

            @Part("img") RequestBody img*/
    );


    @Multipart
    @Headers({
            "cache-control: no-cache"
    })
    @POST("/admin/createblipapicustom.php")
    Call<Blip> createBlip(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey,             // Member's API Key
            @Query("title") String title,               // New blip title
            @Query("type") int type,                    // New blip discount type (1 =$ / 2 =%)
            @Query("blipvalue") int blipvalue,          // New blip value
            @Query("category") String category,         // New blip categories as IDs (Array) or (INT)
            @Query("subcategory") String subcategory,   // New blip subcategories as IDs (Array) or (INT)
            @Query("location") String location,            // New blip locations as IDs (Array) or (INT)
            @Query("videotype") int videotype,   // New blip subcategories as IDs (Array) or (INT)
            @Query("contenttype") int contenttype,
            @Query("video") String videourl,
            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Part("img\"; filename=\"photo.jpg") RequestBody file

    );


    /*
     *  UPDATE BLIP
     */
    @Multipart
    @Headers({
            "cache-control: no-cache"
    })
    @POST("/admin/updatecouponapicustom.php")
    Call<Blip> updateBlip(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") int id,            // Coupon id
            @Query("videotype") int videotype,   // New blip subcategories as IDs (Array) or (INT)
            @Query("contenttype") int contenttype,
            @Query("video") String videourl,
            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Part("img\"; filename=\"photo.jpg") RequestBody file
    );

    /*
     *  UPDATE BLIP WITH IMAGE UPLOAD
     */
    @Multipart
    @Headers({
            "cache-control: no-cache"
    })
    @POST("/admin/updatecouponapicustom.php")
    Call<Blip> updateBlip(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") int id,            // Coupon id
            @Query("videotype") int videotype,   // New blip subcategories as IDs (Array) or (INT)
            @Query("contenttype") int contenttype,

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Part("img\"; filename=\"photo.jpg") RequestBody file,
            @Part("videofile\"; filename=\"Video.mp4") RequestBody video

    );



    /*for link image*/
    @POST("/admin/updatecouponapicustom.php")
    Call<Blip> updateBlip(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") int id,            // Coupon id

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Query("videotype") int videotype,
            @Query("contenttype") int contenttype

    );


    @Multipart
    @Headers({
            "cache-control: no-cache"
    })
    /*for link image*/
    @POST("/admin/updatecouponapicustom.php")
    Call<Blip> updateBlip(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") int id,            // Coupon id

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Query("videotype") int videotype,
            @Query("contenttype") int contenttype,
            @Part("videofile\"; filename=\"Video.mp4") RequestBody video
    );


    @POST("/admin/updatecouponapicustom.php")
    Call<Blip> updateBlip(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") int id,            // Coupon id

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions,
            @Query("videotype") int videotype,
            @Query("contenttype") int contenttype,
            @Query("video") String videourl

    );

    /*
     *  DELETE BLIP
     */
    @GET("/admin/deletecouponapicustom.php")
    //Call<SimpleResponse> deleteBlip(
    Call<String> deleteBlip(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") int id             // blip id
    );


    /*
     *  CREATE LOCATION
     */
    @GET("admin/locationapicustom.php")
    Call<Location> createLocation(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //state         -   New location state (=online for online blips)
            //city          -   New location city
            //phone	        -	New location phone
            //address1	    -	New location address line1
            //address2	    -	New location address line2
            //zipcode	    -	New location zipcode
            //description	-	New location description
            //status	    -   location status (0 | 1)
            //website       -   location website url
            //addressstate  -   New location address state
            //marketarea	-   location marketarea id
    );


    /*
     *  UPDATE LOCATION
     */
    @GET("/admin/updatelocationapicustom.php")
    Call<Location> updateLocation(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") String id,         // location id

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //state         -   New location state (=online for online blips)
            //city          -   New location city
            //phone	        -	New location phone
            //address1	    -	New location address line1
            //address2	    -	New location address line2
            //zipcode	    -	New location zipcode
            //description	-	New location description
            //status	    -   location status (0 | 1)
            //addressstate  -   New location address state
            //website       -   location website url
            //marketarea	-   location marketarea id
    );


    /*
     *  DELETE LOCATION
     */
    @GET("/admin/deletelocationapicustom.php")
    Call<SimpleResponse> deleteLocation(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") int id          // location id
    );


    /*
     *  CREATE BLIP ALERT
     */
    @GET("/admin/blipalertapicustom.php")
    Call<SocialMedia> createBlipAlert(
            @Query("email") String email,       // Member's email
            @Query("apikey") String apiKey,     // Member's API Key
            @Query("state") String state,       // New blip alert state
            @Query("city") String city,         // New blip alert city
            @Query("subject") String subject,   // New blip alert subject
            @Query("content") String content,   // New blip alert content
            @Query("senttype") String senttype, // 0 text | 1 email

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //maxrecipients     -   Blip Alert number of max receipients (default 1000)
            //gender            -   0 = All | 1 = Maile | 2 = Female
            //age               -	0 = All Ages | 1 = 10 - 17 | 2 = 18 - 24 | 3 = 25 . 34 | 4 = 35 . 44 | 5 = 45 . 54 | 6 = 55 . 64 | 7 = 65+
    );


    /*
     *  DELETE BLIP ALERT
     */
    @GET("/admin/deleteblipalertapicustom.php")
    Call<SimpleResponse> deleteBlipAlert(
            @Query("email") String email,       // Member's email
            @Query("apikey") String apiKey,     // Member's API Key
            @Query("alertid") String alertid    // New blip alert id
    );


    /*
     *  ADD / REMOVE BLIPSBOOK
     */
    @POST("/admin/addblipsbookapicustom.php")
    Call<SimpleResponse> addRemoveBlipsBook(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") int id          // Blip ID
    );


    /*
     *  RATE BLIP (LIKE - DISLIKE - FLAG etc)
     */
    @GET("/admin/bliprateapicustom.php")
    Call<SimpleResponse> rateBlip(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") int id,            // Blip ID
            @Query("type") int type         // 1 = Flag as inappropriate | 2= hide vendor | 3 = like | 4=dislike
    );


    /*
     *  Send Message (Message center)
     */
    @GET("/admin/sendmessagesapicustom.php")
    Call<SentMessage> sendMessage(
            @Query("email") String email,       // Member's email
            @Query("apikey") String apiKey,     // Member's API Key
            @Query("message") String message,   // Message to be sent

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //type      -   DEFAULT 0 send to admin
            //recemail  -   receiver email (type should be set to 1 to be able to send to other members -Not admin-)
            //reply     -   1 for reply
            //orig      -   ID of original message for reply
    );


    /*
     *  CREATE MEMBER
     */
    @GET("admin/createapicustom.php")
    Call<MemberDetail> createMember(
            @Query("email") String email,       // Member's email
            @Query("password") String password, // Member's Password
            @Query("package") int userPackage,  // Member's package (4 = Vendor & 3 = Consumer)
            @Query("fname") String fname,       // Member's first name for Consumer & Business name for vendor

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //zipcode   -   Home Location zipcode
            //phone     -   Member's phone number
    );


    /*
     *  UPDATE MEMBER
     */
    @POST("/admin/updatememberapicustom.php?action=1")
    Call<MemberDetail> updateMemberDetail(
            @Query("apikey") String apiKey,     // Member's API KEY
            @Query("email") String email,    // The email address of the member being updated
            @Query("newemail") String newemail, // The new email address of the member
            @Query("fname") String fname,
            @Query("description") String dsc,// The new first name of the member

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //age           -   Member's age (1 = 10 - 17 || 2 = 18 - 24 || 3 = 25 - 34 || 4 = 35 - 44 || 5 = 45 - 54 || 6 = 55 - 64 || 7 = 65+) DEFAULT=2
            //gender        -   0 for male | 1  for female DEFAULT=0
            //zipcode       -   Home Location zipcode
            //ziphome       -   Set zipcode as home for member (0 or 1) DEFAULT=1
            //phone         -   Member's phone number
            //newpassword   -   The new password which should be assigned to the member.
    );


    @POST("/admin/updatememberapicustom.php?action=1")
    Call<MemberDetail> updateMemberDetail(
            @Query("apikey") String apiKey,     // Member's API KEY
            @Query("email") String email,    // The email address of the member being updated
            @Query("newemail") String newemail, // The new email address of the member
            @Query("fname") String fname,

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //age           -   Member's age (1 = 10 - 17 || 2 = 18 - 24 || 3 = 25 - 34 || 4 = 35 - 44 || 5 = 45 - 54 || 6 = 55 - 64 || 7 = 65+) DEFAULT=2
            //gender        -   0 for male | 1  for female DEFAULT=0
            //zipcode       -   Home Location zipcode
            //ziphome       -   Set zipcode as home for member (0 or 1) DEFAULT=1
            //phone         -   Member's phone number
            //newpassword   -   The new password which should be assigned to the member.
    );


    /*
     *  DELETE MEMBER
     */
    @GET("/admin/deletememberapicustom.php")
    Call<SocialMedia> deleteMember(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey  // Member's API Key
    );


    /*
     *  RESET MEMBER'S PASSWORD
     */
    @GET("/admin/resetapicustom.php")
    Call<SimpleResponse> resetMemberPassword(
            @Query("email") String email   // Member's email
    );


    /*
     *  GET MEMBER's DETAILS
     */
    @GET("/admin/getmemberapicustom.php")
    Call<MemberDetail> getMembersDetails(
            @Query("email") String email,       // Member's email
            @Query("apikey") String apiKey,
            @QueryMap Map<String, String> otherOptions  // Member's API key (Either password is required or API Key)
    );


    /*
     *  SIGN IN MEMBER
     */
    @GET("/admin/getmemberapicustom.php")
    Call<MemberDetail> signIn(
            @Query("email") String email,       // Member's email
            @Query("password") String password,
            @QueryMap Map<String, String> otherOptions // Member's password   (Either password is required or API Key)
    );


    /*
     *  GET CATEGORIES
     */
    @GET("/admin/categoryapicustom.php")
    Call<ArrayList<Category>> getCategories();


    /*
     *  GET BLIPBOOK ITEMS
     */
    @GET("/admin/blipbookapicustom.php")
    Call<ArrayList<Blip>> getBlipBookItems(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey  // Member's API Key
    );


    /*
     *  GET BLIP DETAILS
     */
    @GET("/admin/getblipapicustom.php")
    Call<Blip> getBlipDetails(
            @Query("blipid") int blipid,  // id of the blip which you want to get details for
            @Query("email") String email,   // Vendor email
            @Query("apikey") String apiKey
    );


    /*
     *  GET VENDOR'S BLIPS
     */
    @GET("/admin/getvendorblipapicustom.php")
    Call<ArrayList<Blip>> getVendorsBlips(
            @Query("email") String email,   // Vendor email
            @Query("apikey") String apiKey  // Vendor api key
    );


    /*
     *  GET BLIPSPICKS
     */
    @GET("/admin/blipspicksapicustom.php")
    Call<SocialMedia> getBlipsPicks(
            @Query("zipcode") String zipcode   // blips zipcode
    );


    /*
     *  SEARCH BLIPS
     */
    @GET("admin/searchblipsapicustom.php")
    Call<ArrayList<Blip>> searchBlips(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //zipcode   -   search by zipcode
            //distance	-   location distance
            //keyword	-	search keyword
            //catid	    -	category id (0 = all categories) default 0
            //online	-	Search online vendor (1 for online - 0 for local) default = 0
            //first	    -	Search starts with number # (default 0)
            //count	    -   Number of results displayed
    );


    /*
     *  BLIP FEED
     */
    @GET("/admin/blipfeedapicustom.php")
    Call<SocialMedia> getBlipFeed();


    /*
     *  SET / GET NOTIFICATIONS
     */
    @POST("/admin/setnotificationapicustom.php")
    Call<ArrayList<Integer>> setCategory(
            @Query("email") String email,
            @Query("apikey") String apiKey,
            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //category   -   either comma separated list of category ids or an array of category ids "if empty it returns current notification list"
            //distance   -   distance in miles from zip code
    );

    @GET("/admin/setnotificationapicustom.php")
    Call<ArrayList<Integer>> getCategory(
            @Query("email") String email,
            @Query("apikey") String apiKey
    );


    /*
     *  SET / GET SOCIAL MEDIA
     */
    @GET("/admin/setsocialapicustom.php")
    Call<SocialMedia> setSocialMedia(
            @Query("email") String email,   // Member email
            @Query("apikey") String apiKey, // Member API KEY

            // If no variable passed other than email / API it will get current social media values
            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //urls      -   Either an array of urls having social media name as key or a set of variables passed with each social media name as follow
            //facebook  -   Facebook
            //google    -   Google
            //instagram -   Instagram
            //pinterest -   Pinterest
            //twitter   -   Twitter
            //tumblr    -   Tumblr
    );

    @GET("/admin/setsocialapicustom.php")
    Call<SocialMedia> getSocialMedia(
            @Query("email") String email,   // Member email
            @Query("apikey") String apiKey
    );


    /*
     *  BLIP ANALYTICS
     */
    @GET("/admin/blipanalyticsapicustom.php")
    Call<ArrayList<Blip>> getBlipAnalytics(
            @Query("email") String email,   // Member email
            @Query("apikey") String apiKey  // Member API KEY
    );


    /*
     *  GET MESSAGE BY ID
     */
    @GET("/admin/mymessageapicustom.php")
    Call<ArrayList<Conversation>> getMessageById(
            @Query("email") String email,   // Member email
            @Query("apikey") String apiKey, // Member API KEY
            @Query("id") String id          // Message id
    );


    /*
     *  GET MESSAGES
     */
    @GET("/admin/mymessagesapicustom.php")
    Call<ArrayList<Conversation>> getMessages(
            @Query("email") String email,   // Member email
            @Query("apikey") String apiKey // Member API KEY
    );


    /*
     *  REMOVE IMAGE
     */
    @GET("/admin/deleteimageapicustom.php")
    Call<SimpleResponse> deleteBlipImage(
            @Query("email") String email,   // Member email
            @Query("apikey") String apiKey, // Member API KEY
            @Query("id") int id             // Coupon ID that you want to remove its image
    );


    /*
     *  GET LIBRARY IMAGES
     */
    @GET("/admin/getlibraryapicustom.php")
    Call<List<CategoryWithImages>> getLibraryImages(
            @Query("email") String email,   // Member email
            @Query("apikey") String apiKey  // Member API KEY
    );


    /*
     *  GET VENDORS LIST
     */
    @GET("/admin/getvendorsapicustom.php")
    Call<ArrayList<Vendor>> getVendorsList(
            @Query("email") String email,   // Member email
            @Query("apikey") String apiKey  // Member API KEY
    );


    /*
     *  GET MESSAGE HISTORY
     */
    @GET("/admin/pairmessageapicustom.php")
    Call<ArrayList<ChatMessage>> getMessageHistory(
            @Query("email") String email,   // Member email(first member email)
            @Query("apikey") String apiKey, // Member API KEY
            @Query("pairemail") String sender     // sender email (second member email)
    );


    /*
     *  DELETE MESSAGE
     */
    @GET("/admin/deletemessageapicustom.php")
    Call<SimpleResponse> deleteMessage(
            @Query("email") String email,   // Member email
            @Query("apikey") String apiKey, // Member API KEY
            @Query("id") String id          // Message id
    );


    /*
     *  GET BLIP RATING
     */
    @POST("/admin/getbliprateapicustom.php")
    Call<SimpleResponse> getBlipRating(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") String id          // Blip ID
    );


    /*
     *  GET MARKET AREAS
     */
    @GET("/admin/marketareaapicustom.php")
    Call<ArrayList<MarketArea>> getMarketAreas(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //id    -   marketarea id (if id not assigned then it will return all marketareas)
    );


    /*
     *  GET SUB CATEGORIES
     */
    @GET("/admin/subcategoryapicustom.php")
    Call<ArrayList<Category>> getSubCategories(
            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //id    -   Category id
            //title -   Category title
    );


    /*
     *  GET INCREASE VISIBILITY CONTENT
     */
    @GET("/admin/increasevisibilityapicustom.php")
    Call<ArrayList<BlipBoost>> getBlipBoosts(
            // All optional fields should be here
            //@QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //id    -   Category id
            //title -   Category title
    );


    /*
     *  SEND PRODUCT EMAIL
     */
    @POST("/admin/visibilitymessageapicustom.php")
    Call<BlipBoost> sendBoostInfoEmail(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key
            @Query("id") String id          // product id
    );


    /*
     *  GET NEW BLIP LIST
     */
    @GET("admin/newblipsapicustom.php")
    Call<ArrayList<Blip>> getNewBlips(
            @Query("email") String email,   // Member's email
            @Query("apikey") String apiKey, // Member's API Key

            // All optional fields should be here
            @QueryMap Map<String, String> otherOptions

            // These fields are optional:
            //first	    -	Search starts with number # (default 0)
            //count	    -   Number of results per page # (default 20)
    );

    /*
     *  CREATE BLIP WITH IMAGE UPLOAD
     */
    @Multipart
    @Headers({
            "cache-control: no-cache"
    })
    @POST("/qrblipapi.php")
    Call<Redeemed> redeemBlip(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey,             // Member's API Key
            @Part("img\"; filename=\"photo.jpg") RequestBody file
    );

    /*
     *  BLIP EXPIRE API
     */
    @GET("/admin/blipexpireapicustom.php")
    Call<ArrayList<Blip>> getBlipsExpiration(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey              // Member's API Key
    );

    /*
     *  BLIP ANALYTICS DETAILS
     */
    @GET("/admin/blipanalyticsstatsapicustom.php")
    Call<BlipAnalytics> getBlipAnalyticsDetails(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey,             // Member's API Key
            @Query("blipid") int blipID               // Blip ID
    );

    /*
     * unread messages count
     */
    @GET("/admin/messagescountapicustom.php")
    Call<UnreadMessagesCount> getUnredMessageCount(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey              // Member's API Key
    );

    /*
     * new blip count
     */
    @GET("/admin/newblipscountapicustom.php")
    Call<NewBlipCount> getNewBlipCount(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey              // Member's API Key
    );

    /*
     * reset new blip count
     */
    @GET("/admin/newblipsresetapicustom.php")
    Call<SimpleResponse> resetNewBlipCount(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey              // Member's API Key
    );

    /*
     * delete thread
     */
    @GET("/admin/deletethreadmessageapicustom.php")
    Call<SimpleResponse> deleteThread(
            @Query("email") String email,               // Member's email
            @Query("apikey") String apiKey,             // Member's API Key
            @Query("threadid") int threadId             // Member's API Key
    );


    /*http://dev2.localblip.com/admin/createapicustom.php?
    email=ans@gmail.com&password=1234&package=3
    &fname=anshul&gender=male&zipcode=201301&age=25-34*/
    @GET("admin/createapicustom.php")
    Call<MemberDetail> createMemberNew(
            @Query("email") String email,       // Member's email
            @Query("password") String password, // Member's Password
            @Query("package") String userPackage,  // Member's package (4 = Vendor & 3 = Consumer)
            @Query("age") String age,
            @Query("gender") String gender,
            @Query("zipcode") String zipCode,
            @Query("fname") String fname // Member's first name for Consumer & Business name for vendor

            // All optional fields should be here

            // These fields are optional:
            //zipcode   -   Home Location zipcode
            //phone     -   Member's phone number
    );

}
