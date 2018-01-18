package com.ordiacreativeorg.localblip.fragment;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ordiacreativeorg.localblip.R;
import com.ordiacreativeorg.localblip.activity.BarcodeScanner;
import com.ordiacreativeorg.localblip.activity.MainActivity;
import com.ordiacreativeorg.localblip.api.Api;
import com.ordiacreativeorg.localblip.model.MemberDetail;
import com.ordiacreativeorg.localblip.model.Redeemed;
import com.ordiacreativeorg.localblip.util.TemporaryStorageSingleton;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Sergey Mitrofanov (goretz.m@gmail.com) on 11/15/2015
 */
public class Redeem extends Fragment {

    public Redeem() {
        // Required empty public constructor
    }

    private View mRootView;
    private ImageView mQRCodeImageView;
    private View mQRCodeHintTextView;
    private Button mSelectAnotherButton;
    private Button mRedeemButton;
    private ProgressDialog mProgressDialog;
    private Bitmap mPhotoBitmap = null;
    private File photo_file = null;


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), BarcodeScanner.class);
            intent.putExtra("imageUri", photo_file);
            startActivityForResult(intent, 1923);
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo_file));
//            startActivityForResult(intent, 1923);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            photo_file = File.createTempFile("QRCode", "jpg", getActivity().getCacheDir());
            photo_file.setWritable(true, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.redeem_layout, container, false);
        mRootView.setFocusableInTouchMode(true);
        mRootView.requestFocus();
        mRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (i == KeyEvent.KEYCODE_BACK) {
                        Log.e("lkkj",""+1);
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
//                        getFragmentManager().beginTransaction().replace(R.id.fl_fragment_container, new ShopBlips()).addToBackStack(null).commit();
//                        ((MainActivity) getActivity()).changeFragment();
//                        acc.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;

            }
        });
        mQRCodeImageView = (ImageView) mRootView.findViewById(R.id.iv_qr_image);
        mSelectAnotherButton = (Button) mRootView.findViewById(R.id.btn_capture);
        mRedeemButton = (Button) mRootView.findViewById(R.id.btn_redeem);
        mQRCodeHintTextView = mRootView.findViewById(R.id.tv_qr_hint);
        mProgressDialog = new ProgressDialog();

        mQRCodeImageView.setOnClickListener(onClickListener);
        mSelectAnotherButton.setOnClickListener(onClickListener);

        mRedeemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MemberDetail memberDetail = TemporaryStorageSingleton.getInstance().getMemberDetails();
                mProgressDialog.show(getActivity().getSupportFragmentManager(), "progress");
                ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
                mPhotoBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayBitmapStream);
                byte[] bytes = byteArrayBitmapStream.toByteArray();
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), bytes);
                Api.getInstance().getMethods().redeemBlip(
                        memberDetail.getEmail(),
                        memberDetail.getApiKey(),
                        requestBody
                ).enqueue(new Callback<Redeemed>() {
                    @Override
                    public void onResponse(Response<Redeemed> response, Retrofit retrofit) {
                        mProgressDialog.dismiss();
                        Redeemed redeemed = response.body();
                        if (redeemed != null) {
                            if (redeemed.getCouponTitle() != null && !redeemed.getCouponTitle().isEmpty()) {
                                mRedeemButton.setVisibility(View.GONE);
                                mSelectAnotherButton.setVisibility(View.GONE);
                                mQRCodeHintTextView.setVisibility(View.VISIBLE);
                                mQRCodeImageView.setOnClickListener(onClickListener);
                                mQRCodeImageView.setImageResource(R.drawable.no_qr_code);
                            }
//                            photo_file = null;
//                            mPhotoBitmap = null;
                        } else {
                            redeemed = new Redeemed();
                            redeemed.setResponse(getResources().getString(R.string.operation_failed) + " No response from server (null)");
                        }
                        RedeemDialog.newInstance(redeemed).show(getActivity().getSupportFragmentManager(), "result");
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Redeemed redeemed = new Redeemed();
                        redeemed.setResponse(getResources().getString(R.string.operation_failed) + " " + t.getMessage());
                        RedeemDialog.newInstance(redeemed).show(getActivity().getSupportFragmentManager(), "error");
                        mProgressDialog.dismiss();
                    }
                });
            }
        });
        return mRootView;
    }

    private void setQRCodeImage(final Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float ratio = width / height;
        if (ratio > 1) {
            if (width > 800) {
                float rat = width / 800;
                width = 800;
                height = Math.round((float) height / rat);
            }
        } else {
            if (height > 800) {
                float rat = height / 800;
                height = 800;
                width = Math.round((float) width / rat);

            }
        }
        mPhotoBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        mQRCodeImageView.setOnClickListener(null);
        mQRCodeHintTextView.setVisibility(View.GONE);
        mSelectAnotherButton.setVisibility(View.VISIBLE);
        mRedeemButton.setVisibility(View.VISIBLE);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mQRCodeImageView, "alpha", 1f, 0f);
        objectAnimator.setDuration(150);
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mQRCodeImageView.setImageBitmap(mPhotoBitmap);
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mQRCodeImageView, "alpha", 0f, 1f);
                objectAnimator.setDuration(300);
                objectAnimator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (photo_file != null) {
            outState.putString("photo", photo_file.getAbsolutePath());
        } else {
            outState.remove("photo");
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.containsKey("photo")) {
            photo_file = new File(savedInstanceState.getString("photo"));
            Bitmap bitmap = BitmapFactory.decodeFile(photo_file.getAbsolutePath());
            if (bitmap == null) {
                Snackbar.make(mRootView, "Failed to retrieve a picture", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                }).show();
                return;
            }
            setQRCodeImage(bitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1923:
                if (resultCode == Activity.RESULT_OK) {
                     Bitmap bitmap1 = BitmapFactory.decodeFile(photo_file.getAbsolutePath());
                   /* Bitmap bitmap = null;
                    byte[] byteArray = data.getByteArrayExtra("BitmapImage");
                    bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(photo_file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                        fos.close();
                        bitmap = BitmapFactory.decodeFile(photo_file.getAbsolutePath());
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    if (bitmap1 == null) {
                        Snackbar.make(mRootView, "Failed to retrieve a picture", Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        }).show();
                        return;
                    }
                    setQRCodeImage(bitmap1);
                }
                break;
        }
    }

    // Temporary
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity()).setBarTitle(R.string.nd_redeem, false);
    }
}
