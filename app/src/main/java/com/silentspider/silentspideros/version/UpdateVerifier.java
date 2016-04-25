package com.silentspider.silentspideros.version;
import com.silentspider.silentspideros.R;

import android.content.Context;
import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class UpdateVerifier {

    private final static String SD_CARD_PATH = "/storage/external_storage/sdcard1";

    public static boolean verifyUpdate(Context ctx) {

        try {
            PublicKey pu1 = getPublicKey(ctx, R.raw.public_key1);

            File file = new File(SD_CARD_PATH, "update.zip");
            byte[] data = readFile(file);

            file = new File(SD_CARD_PATH, "signature");
            byte[] digitalSignature = readFile(file);

            return verifySig(data, pu1, digitalSignature);
        }
        catch(Exception e) {
            Log.e("UpdateVerifier", "Failed to verify signature due to " + e.getMessage());
            return false;
        }
    }

    private static byte[] readFile(File f) throws IOException {
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] bytes = new byte[(int)f.length()];
        dis.readFully(bytes);
        dis.close();
        return bytes;
    }

    private static boolean verifySig(byte[] data, PublicKey key, byte[] sig) throws Exception {
        Signature signer = Signature.getInstance("SHA512withRSA");
        signer.initVerify(key);
        signer.update(data);
        return (signer.verify(sig));

    }

    private static PublicKey getPublicKey(Context ctx, int res)
            throws Exception {

        InputStream is = ctx.getResources().openRawResource(res);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        X509EncodedKeySpec spec =
                new X509EncodedKeySpec(buffer.toByteArray());
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

}
