package com.example.cyberproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.WriterException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.content.ContentValues.TAG;

import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import android.util.Base64;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LL_private_message_buttons = findViewById(R.id.LL_private_message_buttons);
        TV_userUID = findViewById(R.id.TV_userUID);
        LL_chatView = findViewById(R.id.LL_chatView);
        LL_chatView.setVisibility(View.GONE);
        TV_MyToken = findViewById(R.id.TV_MyToken);
        TV_ourSecretKey = findViewById(R.id.TV_ourSecretKey);
        ET_sendMsgText = findViewById(R.id.ET_sendMsgText);
        ET_EncryptionKey = findViewById(R.id.ET_EncryptionKey);
        TV_My_EncryptionKey = findViewById(R.id.TV_My_EncryptionKey);
        LV_massages_chat = findViewById(R.id.LV_massages_chat);
        LV_private_massages_chat = findViewById(R.id.LV_private_massages_chat);
        BT_sendMsg = findViewById(R.id.BT_sendMsg);
        BT_sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeNewMsg(mAuth.getUid(), ET_sendMsgText.getText().toString(), isPravateMsgState);
                ET_sendMsgText.setText("");
            }
        });
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getUid()!=null) {
            TV_userUID.setText(mAuth.getUid());
            initSendAndReadMsg();
            LL_chatView.setVisibility(View.VISIBLE);
        }
        scanQrCode = findViewById(R.id.scanQrCode);
        scanQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQrCode();
                Toast.makeText(MainActivity.this, ET_EncryptionKey.getText(),Toast.LENGTH_SHORT).show();
            }
        });
        showQrCode = findViewById(R.id.showQrCode);
        showQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showQrCode(TV_My_EncryptionKey.getText().toString());
            }
        });
    }

    //----------------------------------------------------------------------------------------------
    // MAIN MENU -----------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.sign_dialog, null);
        final EditText ET_sign_user_name = dialogView.findViewById(R.id.ET_sign_user_name);
        final EditText ET_sign_phone = dialogView.findViewById(R.id.ET_sign_phone);
        final EditText ET_sign_OPT_msg = dialogView.findViewById(R.id.ET_sign_OPT_msg);
        final Button BT_sign_send_OPT = dialogView.findViewById(R.id.BT_sign_send_OPT);
        ET_sign_OPT_msg.setVisibility(View.GONE);
        BT_sign_send_OPT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send OPT msg --------------------------------------------------------------------

                ET_sign_OPT_msg.setVisibility(View.VISIBLE);
                // firebase send OPT msg:
                firebase_send_OPT_msg(ET_sign_phone.getText().toString());
            }
        });
        if (item.getItemId()==R.id.item_sign_up){

            // do register -------------------------------------------------------------------------

            builder.setView(dialogView).setPositiveButton("Register", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // firebase register function:
                    firebase_get_OPT_msg(ET_sign_user_name.getText().toString(),
                            ET_sign_phone.getText().toString(),
                            ET_sign_OPT_msg.getText().toString());
                }
            }).show();
        }
        else if (item.getItemId()==R.id.item_sign_in){

            // do sign in --------------------------------------------------------------------------

            builder.setView(dialogView).setPositiveButton("sign in", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // firebase sign in function:
                    firebase_get_OPT_msg(ET_sign_user_name.getText().toString(),
                            ET_sign_phone.getText().toString(),
                            ET_sign_OPT_msg.getText().toString());
                }
            }).show();
        }
        else if (item.getItemId()==R.id.item_sign_out){

            // do sign out -------------------------------------------------------------------------

            mAuth.signOut();
            TV_userUID.setText("not connected");
            LL_chatView.setVisibility(View.GONE);
        }
        else if (item.getItemId()==R.id.item_private_msg){

            // go to private msg -------------------------------------------------------------------

            changMsgStates_isPrivate(true);
        }
        else if (item.getItemId()==R.id.item_msg){

            // go to msg ---------------------------------------------------------------------------

            changMsgStates_isPrivate(false);
        }
        return super.onOptionsItemSelected(item);
    }

    //----------------------------------------------------------------------------------------------
    // FIREBASE SIGN -------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private TextView TV_userUID;

    void firebase_send_OPT_msg(String sign_phone){

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                Log.d(TAG, "onVerificationCompleted:" + credential);
                Toast.makeText(MainActivity.this, "The verification is Completed!",Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Toast.makeText(MainActivity.this, "The verification code entered was invalid!",Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(MainActivity.this, e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                }
                // Show a message and update the UI
                Toast.makeText(MainActivity.this, "There is a problem!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent:" + verificationId);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
                Toast.makeText(MainActivity.this,"OTP send successfully", Toast.LENGTH_SHORT).show();
            }
        };
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+972"+sign_phone)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    void firebase_get_OPT_msg(String sign_user_name, String sign_phone, String sign_OPT_msg){
        LL_chatView.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, sign_OPT_msg);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Update UI
                            Toast.makeText(MainActivity.this, "Sign in success!",Toast.LENGTH_SHORT).show();
                            TV_userUID.setText(mAuth.getUid().toString());
                            // send and read msg:
                            initSendAndReadMsg();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(MainActivity.this, "The verification code entered was invalid!",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    // SEND AND READ MSG - Realtime Database -------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private TextView TV_MyToken;
    private String myToken;
    private Button BT_sendMsg;
    private EditText ET_sendMsgText;
    private List<Map<String,Object>> MessagesToView;
    private List<Map<String,Object>> PrivateMessagesToView;
    private LinearLayout LL_chatView;
    private ListView LV_massages_chat;
    private ListView LV_private_massages_chat;

    private SimpleAdapter simpleAdapter;
    private SimpleAdapter privateSimpleAdapter;
    private HashMap<String, Object> hMsg;
    private boolean isPravateMsgState;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference tempTable = firebaseDatabase.getReference("temp");

    private void initSendAndReadMsg(){
        isPravateMsgState=false;
        LL_chatView.setVisibility(View.VISIBLE);
        MessagesToView = new ArrayList<Map<String,Object>>();
        PrivateMessagesToView = new ArrayList<Map<String,Object>>();
        String[] from = {"createDate","createBy", "textMsg",  "comments"};
        int[] toIds= {R.id.TV_date_message_box,R.id.TV_createBy_message_box, R.id.TV_text_message_box, R.id.LV_comments_message_box};
        int[] pToIds= {R.id.TV_p_date_message_box,R.id.TV_p_createBy_message_box, R.id.TV_p_text_message_box, R.id.LV_p_comments_message_box};
        simpleAdapter = new SimpleAdapter(this, MessagesToView, R.layout.message_box, from, toIds);
        privateSimpleAdapter = new SimpleAdapter(this, PrivateMessagesToView, R.layout.private_message_box, from, pToIds);


        LV_massages_chat.setAdapter(simpleAdapter);
        LV_private_massages_chat.setAdapter(privateSimpleAdapter);


        writeMsgToChatListView();
    }

    private void writeMsgToChatListView(){
        tempTable.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    MessagesToView.clear();
                    PrivateMessagesToView.clear();
                    boolean ifSuccessDecryption;
                    for(DataSnapshot snapshotLine: snapshot.getChildren()){
                        Massage msg1 = snapshotLine.getValue(Massage.class);
                        if (!msg1.getIsEncrypted()) {
                            HashMap<String, Object> hMsg = new HashMap<>();
                            hMsg.put("createBy", msg1.getCreateBy());
                            hMsg.put("textMsg", msg1.getTextMsg());
                            hMsg.put("createDate", msg1.getCreateDate());
                            hMsg.put("comments", msg1.getComments());
                            MessagesToView.add(hMsg);
                        } else {
                            HashMap<String, Object> hMsg = new HashMap<>();
//                            hMsg.put("createBy", msg1.getCreateBy());
//                            hMsg.put("textMsg", msg1.getTextMsg());
//                            hMsg.put("createDate", msg1.getCreateDate());
//                            hMsg.put("comments", msg1.getComments());
//                            PrivateMessagesToView.add(hMsg);
                            encryption.setInputText(msg1.getTextMsg());
                            ifSuccessDecryption=encryption.DecipheredText();
                            if (ifSuccessDecryption) {
                                hMsg.put("createBy", msg1.getCreateBy());
                                hMsg.put("textMsg", encryption.getOutputText());
                                hMsg.put("createDate", msg1.getCreateDate());
                                hMsg.put("comments", msg1.getComments());
                                PrivateMessagesToView.add(hMsg);
                            }
                        }
                    }
                    simpleAdapter.notifyDataSetChanged();
                    privateSimpleAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        initEncryption();
    }

    private void writeNewMsg(String createBy, String textMsg, boolean isEncrypted){
        SimpleDateFormat formatD = new SimpleDateFormat("yy-MM-dd hh:mm:ss:SSS");
        Date date=new java.util.Date();

        Massage massage = new Massage();
        massage.setCreateBy(createBy);
        if (isPravateMsgState)
            massage.setTextMsg(encryptText(textMsg));
        else
            massage.setTextMsg(textMsg);
        massage.setIsEncrypted(isEncrypted);
        massage.setCreateDate(formatD.format(date));
        massage.setPlaceCreate(new Point(1,1));
        //massage.getComments().add(new Comment("cmdBy_1","comment_1"));

        tempTable.child(mAuth.getUid()+" "+massage.getCreateDate()).setValue(massage);
    }

    private void changMsgStates_isPrivate(boolean isPravateMsgState){
        this.isPravateMsgState = isPravateMsgState;
        if(isPravateMsgState){
            LV_massages_chat.setVisibility(View.GONE);
            LV_private_massages_chat.setVisibility(View.VISIBLE);
            LL_private_message_buttons.setVisibility(View.VISIBLE);
        } else {
            LV_massages_chat.setVisibility(View.VISIBLE);
            LV_private_massages_chat.setVisibility(View.GONE);
            LL_private_message_buttons.setVisibility(View.GONE);
        }
    }

//    //----------------------------------------------------------------------------------------------
//    // QR code -------------------------------------------------------------------------------------
//    //----------------------------------------------------------------------------------------------

    private LinearLayout LL_private_message_buttons;
    private Button scanQrCode;
    private Button showQrCode;
    private String intentData;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;

    private void scanQrCode(){
        BarcodeDetector barcodeDetector;
        intentData = "";
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.scan_dialog, null);
        final SurfaceView surfaceView = dialogView.findViewById(R.id.surfaceView);
        final TextView txtBarcodeValue = dialogView.findViewById(R.id.txtBarcodeValue);
        builder.setView(dialogView).setPositiveButton("Continu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save the QR text if exist:
                ET_EncryptionKey.setText(intentData);
                createOurSecretKey();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // to nothing:
                intentData="";
            }
        }).show();

        // scan code: ------------------------------------------------------------------------------

        Toast.makeText(getApplicationContext() , "Barcode scanner started" , Toast.LENGTH_SHORT).show();
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        cameraSource = new CameraSource.Builder(this , barcodeDetector)
                .setRequestedPreviewSize(1920 , 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this , Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this , new
                                String[]{Manifest.permission.CAMERA} , REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder , int format , int width , int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext() , "To prevent memory leaks barcode scanner has been stopped" , Toast.LENGTH_SHORT).show();
            }
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {
                    txtBarcodeValue.post(new Runnable() {
                        @Override
                        public void run() {
                            //btnAction.setText("LAUNCH URL");
                            intentData = barcodes.valueAt(0).displayValue;
                            txtBarcodeValue.setText(intentData);
                        }
                    });
                }
            }
        });
    }
    private void showQrCode(String key){
        Bitmap bitmap;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.show_qr_dialog, null);
        final ImageView qrImage = dialogView.findViewById(R.id.qrImage);
        builder.setView(dialogView).setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // to nothing:
            }
        }).show();
        int smallerDimension=700;
        QRGEncoder qrgEncoder = new QRGEncoder(key, null, QRGContents.Type.TEXT, smallerDimension);
        qrgEncoder.setColorBlack(Color.RED);
        qrgEncoder.setColorWhite(Color.BLUE);
        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (Exception e) {
            Log.v(TAG, e.toString());
        }
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        cameraSource.release();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        //initialiseDetectorsAndSources();
//    }

//    //----------------------------------------------------------------------------------------------
//    // Encryption ----------------------------------------------------------------------------------
//    //----------------------------------------------------------------------------------------------

    private EditText ET_EncryptionKey;
    private TextView TV_My_EncryptionKey;
    private TextView TV_ourSecretKey;
    private NewEncryption encryption;
    private HashMap<String, Object> temp_hMsg;

    private void initEncryption (){
        createMyEncryptionKey();
    }
    private void createMyEncryptionKey() {
        //create private key:
        encryption = new NewEncryption("");
        TV_My_EncryptionKey.setText(encryption.getKey1());
    }
    private void createOurSecretKey(){
        encryption.setKey2(ET_EncryptionKey.getText().toString());
        TV_ourSecretKey.setText(encryption.getSecretKey().toString());
        crackingEncryption();
    }
    private String encryptText(String inputText){
        encryption.setInputText(inputText);
        encryption.EncryptionText();
        return encryption.getOutputText();
    }
    private void crackingEncryption(){
        String myKey = TV_My_EncryptionKey.getText().toString();
        String secondKey = ET_EncryptionKey.getText().toString();
        List<Map<String,Object>> temp_MessagesToView = new ArrayList<Map<String,Object>>();
        int size = PrivateMessagesToView.size();
        boolean ifSuccessDecryption;
        for(int i=0; i<size; i++) {
            encryption.setInputText(""+PrivateMessagesToView.get(i).get("textMsg"));
            ifSuccessDecryption=encryption.DecipheredText();
            if (ifSuccessDecryption) {
                temp_hMsg = new HashMap<>();
                temp_hMsg.put("createBy", PrivateMessagesToView.get(i).get("createBy"));
                temp_hMsg.put("textMsg", encryption.getOutputText());
                temp_hMsg.put("createDate", PrivateMessagesToView.get(i).get("createDate"));
                temp_hMsg.put("comments", PrivateMessagesToView.get(i).get("comments"));
                temp_MessagesToView.add(temp_hMsg);
            }
        }
        PrivateMessagesToView.clear();
        for(int i=0; i<temp_MessagesToView.size(); i++)
            PrivateMessagesToView.add(temp_MessagesToView.get(i));
        privateSimpleAdapter.notifyDataSetChanged();
    }
}