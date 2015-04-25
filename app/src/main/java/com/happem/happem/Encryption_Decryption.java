package com.happem.happem;


import java.io.UnsupportedEncodingException;
import java.lang.Object;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Encryption_Decryption {
	  byte[] encodedBytes = null;
	
	 public String encrypt(String toEncrypt, SecretKey pbeKey, PBEParameterSpec pbeParamSpec){
        
              
        try {
        	
    		// Creo PBE Cipher
    		Cipher c = Cipher.getInstance("PBEWithMD5AndDES");
			// inizializzo PBE cipher con chiavi e parametri
    		c.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
            // Encrypt 
    		encodedBytes = c.doFinal(toEncrypt.getBytes());
        } catch (Exception e) {
            Log.e("PasswordBasedEncryptionActivity", "PBE encryption error 1");
        }
         
        return Base64.encodeToString(encodedBytes, Base64.DEFAULT);

       
        
	}
	public String decrypt(String toDecrypt,  SecretKey pbeKey, PBEParameterSpec pbeParamSpec){
        // Decodifico  
        byte[] decodedBytes = null;
        try {
    		Cipher c2 = Cipher.getInstance("PBEWithMD5AndDES");
    		c2.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
   
            decodedBytes = c2.doFinal(Base64.decode(toDecrypt, Base64.DEFAULT));
            
    		
        } catch (Exception e) {
            Log.e("PasswordBasedEncryptionActivity", "PBE decryption error 2 :   "+e.toString());
        }
        
        String s=null;
		try {
			s = new String(decodedBytes, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}	
		
        return s;
	}

	
	
	
}
