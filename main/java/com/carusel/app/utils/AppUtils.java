package com.carusel.app.utils;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

public class AppUtils{
    private static FileLock lock;
    private static FileChannel channel;
    
    public static boolean isAnotherAppIsRunning(){
        try{
            // Create a lock file in temp directory
            File lockFile = new File(System.getProperty("java.io.tmpdir"), "wheelcopy.lock");
            channel = new RandomAccessFile(lockFile, "rw").getChannel();
            lock = channel.tryLock();
            
            if(lock == null){
                channel.close();
                return true;
            }
            
            // Add shutdown hook to release lock
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try{
                    if(lock != null) lock.release();
                    if(channel != null) channel.close();
                    lockFile.delete();
                }catch(Exception e){
                    // Ignore
                }
            }));
            
            return false;
        }catch(Exception e){
            return true;
        }
    }
}