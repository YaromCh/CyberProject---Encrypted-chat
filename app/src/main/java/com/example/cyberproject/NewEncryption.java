package com.example.cyberproject;

import android.util.Log;

import java.io.IOException;

public class NewEncryption {

    final String SUCCESS_MSG="YH";
    final int SIZE_KEY=8;               // size of chars
    final int SIZE_MIN_PADDING= 10;
    final int SIZE_MAX_PADDING= 70;
    //final int MAX_INTEGER_TO_CHAR_VALUE=65535;
    final int MAX_INTEGER_TO_CHAR_VALUE=126;//122;
    final int MIN_INTEGER_TO_CHAR_VALUE=33;//65;

    private String key1;
    private String key2;
    private String secretKey;
    private int padding;
    private String inputText;
    private String outputText;

    // constructors: -------------------------------------------------------------------------------

    public NewEncryption(){
        this.padding=SIZE_KEY;
        this.key1=createKey();
        this.key2="--------";
        this.secretKey="--------";
    }
    public NewEncryption(String inputText){
        this.padding=SIZE_KEY;
        this.inputText=inputText;
        this.key1=createKey();
        this.key2="--------";
        this.secretKey="--------";
    }
    public NewEncryption(String key2, String inputText, boolean doPadding){
        if (doPadding)
            this.padding=((int)(Math.random()*(SIZE_MAX_PADDING-SIZE_MIN_PADDING+1))+SIZE_MIN_PADDING)+SIZE_KEY;
        else
            this.padding=SIZE_KEY;
        this.inputText=inputText;
        this.key2=key2;
        this.key1=createKey();
        createSecretKey();
    }
    public NewEncryption(String key1, String key2, String inputText, boolean doPadding){
        if (doPadding)
            this.padding=((int)(Math.random()*(SIZE_MAX_PADDING-SIZE_MIN_PADDING+1))+SIZE_MIN_PADDING)+SIZE_KEY;
        else
            this.padding=SIZE_KEY;
        this.inputText=inputText;
        this.key2=key2;
        this.key1=key1;
        createSecretKey();
    }
    public NewEncryption(String key2, String inputText, int padding){
        this.padding=padding;
        this.inputText=inputText;
        this.key2=key2;
        this.key1=createKey();
        createSecretKey();
    }

    // gets & sets: --------------------------------------------------------------------------------

    public String getKey1() { return key1; }
    public String getKey2() { return key2; }
    public String getSecretKey() { return secretKey; }
    public int getPadding() { return padding; }
    public String getInputText () { return inputText; }
    public String getOutputText() { return outputText; }

    public void setKey1(String key1) { this.key1 = key1; }
    public void setKey2(String key2) { this.key2 = key2; createSecretKey(); }
    public void setPadding(int padding) { this.padding = padding; }
    public void setInputText(String inputText) { this.inputText = inputText; }

    // helping functions: ----------------------------------------------------------------------------------

    private String xorTwoStrings(String a, String b){
        byte[] aBytes = a.getBytes();
        byte[] bBytes = b.getBytes();
        int one, two, xor;
        String stringXor="";
        for(int i=0; (i<aBytes.length)&&(i<bBytes.length); i++){
            one = aBytes[i];
            two = bBytes[i];
            xor = one ^ two;
            stringXor+= (char)xor;
            //Log.d("Encryption","add----> "+stringXor);
        }
        Log.d("Encryption", "[XOR function] "+new String(aBytes)+" XOR "+new String(bBytes)+" => "+stringXor);
        return stringXor;
    }

    private String randomChar(){
        //return (char)((int)(Math.random()*(MAX_INTEGER_TO_CHAR_VALUE+1)))+"";
        return (char)(((int)(Math.random()*(MAX_INTEGER_TO_CHAR_VALUE - MIN_INTEGER_TO_CHAR_VALUE+1)))+MIN_INTEGER_TO_CHAR_VALUE)+"";
        //return "a";
    }

    private String splitString(String text, int StartIndex){
        String split = "";
        byte[] byteSplit = text.getBytes();
        for (int i=StartIndex; (i<StartIndex+SIZE_KEY)&&(i<text.length()); i++) {
            //Log.d("Encryption", ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> index: "+i+" char is: " + (char) byteSplit[i]+" from text: "+text);
            split += (char) byteSplit[i];
        }
        return split;
    }

    // functions: ----------------------------------------------------------------------------------

    private void setPadding(){
        outputText="";
        for(int i=0; i<SIZE_KEY; i++)
            outputText += randomChar();
        outputText += inputText;
        padding+=8;
        int addToPadding = SIZE_KEY-((outputText.length()+padding) % SIZE_KEY);
        if (addToPadding==8)
            addToPadding=0;
        Log.d("Encryption","[set padding function] padding "+padding+" change to "+(padding+addToPadding));
        padding += addToPadding;
        for(int i=0; i<this.padding; i++)
            outputText += randomChar();
        outputText += SUCCESS_MSG+padding;
        Log.d("Encryption","[set padding function] text: "+inputText+" change to: "+outputText);
    }

    public String createKey(){
        String newKey="";
        for (int i=0; i<SIZE_KEY; i++)
            newKey+=randomChar();
        return newKey;
    }

    private String recursionEncryptionText(String xorKey, int startIndex){
        if(startIndex<outputText.length()) {
            String newKey=xorTwoStrings(xorKey, splitString(outputText, startIndex));
            return newKey + recursionEncryptionText(newKey,startIndex+SIZE_KEY);
        }
        return "";
    }
    public void EncryptionText(){
        Log.d("Encryption","----------- start padding -----------");
        setPadding();
        Log.d("Encryption","----------- start encryption -----------");
        Log.d("Encryption","original text: "+outputText);
        String EncryptionText=recursionEncryptionText(secretKey,0);
        outputText=EncryptionText;
        Log.d("Encryption","encryption text: "+outputText);
    }

    private boolean removePadding(){
        byte[] copyText = outputText.getBytes();
        if(!(((char)copyText[outputText.length()-4]=='Y')&&(char)copyText[outputText.length()-3]=='H'))
            return false;
        String stringPadding = (char)copyText[outputText.length()-2]+""+(char)copyText[outputText.length()-1];
        this.padding=Integer.valueOf(stringPadding);
        String newText = "";
        for(int i=8; i<8+(outputText.length()-(padding+8+4)); i++){
            newText+=(char)copyText[i];
        }
        outputText=newText;
        return true;
    }
    private String recursionDecipheredText(String xorKey, int startIndex){
        if(startIndex<outputText.length()) {
            String secondKey=splitString(outputText, startIndex);
            String resoltKey=xorTwoStrings(xorKey, secondKey);
            return resoltKey + recursionDecipheredText(secondKey,startIndex+SIZE_KEY);
        }
        return "";
    }
    public boolean DecipheredText(){
        outputText=inputText;
        Log.d("Encryption","----------- start decryption -----------");
        Log.d("Encryption","encryption text: "+outputText);
        String decryptionText=recursionDecipheredText(secretKey,0);
        outputText=decryptionText;
        Log.d("Encryption","decryption text: "+outputText);
        Log.d("Encryption","----------- remove padding -----------");
        boolean ret = removePadding();
        Log.d("Encryption","decryption text: "+outputText);
        return ret;
    }

    private void createSecretKey(){
        secretKey=xorTwoStrings(this.key1,this.key2);
        Log.d("Encryption","----------- create SecretKey -----------");
        Log.d("Encryption","secretKey: "+secretKey);
    }
}