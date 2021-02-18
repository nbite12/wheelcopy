package com.carusel.app.manager;

import com.rockaport.alice.Alice;
import com.rockaport.alice.AliceContext;
import com.rockaport.alice.AliceContextBuilder;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class CryptoManager{
    // Singleton
    private static CryptoManager instance;
    public static CryptoManager getInstance(){
        if(instance == null){
            synchronized(CryptoManager.class){
                if(instance == null){
                    instance = new CryptoManager();
                }
            }
        }
        return instance;
    }

    // Fields
    private final Alice alice;

    // Constants
    private static final char[] PASSWORD = {'A', 'G', '3', 'F', '9', '2', 'B', 'Z', 'G', '0'};

    // Constructor
    private CryptoManager(){
        this.alice  = new Alice(new AliceContextBuilder().build());

        init();
    }

    // Initialize
    private void init(){

    }

    public byte[] encrypt(String data){
        try{
            byte[] encrypted = alice.encrypt(data.getBytes(), PASSWORD);
            return encrypted;

        }catch(GeneralSecurityException | IOException e){
            throw new RuntimeException(e.getMessage());
        }
    }

    public String decrypt(byte[] encrypted){
        try{
            byte[] decrypted = alice.decrypt(encrypted, PASSWORD);
            String string = new String(decrypted);
            return string;

        }catch(GeneralSecurityException e){
            throw new RuntimeException(e.getMessage());
        }
    }
}
