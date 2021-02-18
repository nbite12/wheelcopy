package com.carusel.app.usecase;

import com.carusel.app.constants.ClipboardType;
import com.carusel.app.model.clipboard.ClipboardData;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.jcodec.api.FrameGrab;
import org.jcodec.api.JCodecException;
import org.jcodec.common.model.ColorSpace;
import org.jcodec.common.model.Picture;
import org.jcodec.scale.ColorUtil;
import org.jcodec.scale.RgbToBgr;
import org.jcodec.scale.Transform;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

public class ConvertVideoFileToClipboardDataUseCase{
    // Field
    private final File file;

    // Constructor
    public ConvertVideoFileToClipboardDataUseCase(File file){
        this.file = file;
    }

    // Execute
    public ClipboardData execute(){
        ClipboardData clipboardData = new ClipboardData();
        clipboardData.setContentFile(file);
        clipboardData.setClipboardType(ClipboardType.VIDEO);

        try{
            Picture src = FrameGrab.getFrameAtSec(file,0);

            if(src.getColor() != ColorSpace.RGB){
                Transform transform = ColorUtil.getTransform(src.getColor(), ColorSpace.RGB);
                if(transform == null){
                    throw new IllegalArgumentException("Unsupported input colorspace: " + src.getColor());
                }
                Picture out = Picture.create(src.getWidth(), src.getHeight(), ColorSpace.RGB);
                transform.transform(src, out);
                new RgbToBgr().transform(out, out);
                src = out;
            }

            Image image = createImage(src);

            clipboardData.setThumbnail(image);
            clipboardData.setThumbnailSupported(true);
            return clipboardData;

        }catch(IOException | JCodecException ignored){}

        clipboardData.setThumbnailSupported(false);
        return clipboardData;
    }

    private Image createImage(Picture src){
        BufferedImage dst = new BufferedImage(src.getCroppedWidth(), src.getCroppedHeight(), BufferedImage.TYPE_3BYTE_BGR);

        // Without crop
        if(src.getCrop() == null){
            byte[] data = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
            byte[] srcData = src.getPlaneData(0);
            for(int i = 0; i < data.length; i++){
                data[i] = (byte) (srcData[i] + 128);
            }
        }

        // With crop
        else{
            byte[] data = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
            byte[] srcData = src.getPlaneData(0);
            int dstStride = dst.getWidth() * 3;
            int srcStride = src.getWidth() * 3;
            for(int line = 0, srcOff = 0, dstOff = 0; line < dst.getHeight(); line++){
                for(int id = dstOff, is = srcOff; id < dstOff + dstStride; id += 3, is += 3){
                    data[id] = (byte) (srcData[is] + 128);
                    data[id + 1] = (byte) (srcData[is + 1] + 128);
                    data[id + 2] = (byte) (srcData[is + 2] + 128);
                }
                srcOff += srcStride;
                dstOff += dstStride;
            }
        }

        Image image = SwingFXUtils.toFXImage(dst, null);
        return image;
    }
}
