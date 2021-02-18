package com.carusel.app.constants;

import java.util.Arrays;
import java.util.List;

public class AppConstants{
    public static final List<String> IMAGE_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg");
    public static final List<String> VIDEO_EXTENSIONS = Arrays.asList("mp4", "mkv", "avi", "mov", "3gp");
    public static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList("pdf", "rar", "zip", "word", "exe");

    // General
    public static final String APP_ID = "f1f05396-4505-44a8-a125-4841c267f111";
    public static final String APP_ENV_LOCAL = "LOCALAPPDATA";
    public static final String APP_ENV_TEMP = "TEMP";
    public static final String APP_NAME = "Carusel";

    // Resource
    public static final String IMAGE_ELEMENT_TYPE_TEXT = "element-type-text.png";
    public static final String IMAGE_ELEMENT_TYPE_IMAGE = "element-type-image.png";
    public static final String IMAGE_ELEMENT_TYPE_VIDEO = "element-type-video.png";
    public static final String IMAGE_ELEMENT_TYPE_DOCUMENT = "element-type-document.png";
    public static final String IMAGE_LOCK = "lock.png";
    public static final String IMAGE_ACCEPT = "accept.png";
    public static final String IMAGE_SETTING = "setting.png";
    public static final String IMAGE_PIN = "pin-white.png";
    public static final String IMAGE_PIN_BLACK = "pin-black.png";
    public static final String IMAGE_TRASH_BLACK = "trash-black.png";
    public static final String IMAGE_BOX = "box.png";
    public static final String FILE_KEYS = "keys.txt";

    // Crypto
//    public static final String ALGORITHM = "AES";
//    public static final String ENCRYPTION_KEY = "1234567890123456";
//    public static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
//    public static final SecretKeySpec ENCRYPTION_KEY_SPEC = new SecretKeySpec(AppUtils.getUTF8Bytes(ENCRYPTION_KEY), ALGORITHM);
//    public static final IvParameterSpec ENCRYPTION_IV_SPEC = new IvParameterSpec(AppUtils.getUTF8Bytes(ENCRYPTION_KEY));

    // Database
    public static final int DATABASE_VERSION = 1;
}
