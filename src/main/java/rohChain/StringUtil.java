package rohChain;

import java.security.MessageDigest;
import java.security.*;
import java.util.Base64;
import java.util.ArrayList;

//basically a carbon copy of http://www.baeldung.com/sha-256-hashing-java, just to has using SHA256
public class StringUtil {

    public static String applySha256(String input){
         try {
             MessageDigest digest = MessageDigest.getInstance("SHA-256");
             //apply sha256
             byte[] hash = digest.digest(input.getBytes("UTF-8"));
             StringBuffer hexString = new StringBuffer(); //hexdec form of the hash
             for (int i = 0; i < hash.length; i++){
                 String hex = Integer.toHexString(0xff & hash[i]);
                 if(hex.length() == 1) hexString.append('0');
                 hexString.append(hex);
             }

             return hexString.toString();

         }

         catch(Exception e){
             throw new RuntimeException(e);
         }
    }

    //returns EDCDSA sig
    public static byte[] applyECDSASig(PrivateKey privateKey, String input){
        Signature dsa;
        byte[] output = new byte[0];
        try{
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;

        }catch(Exception e){
            throw new RuntimeException(e);
        }
        return output;
    }

    //verify String sig
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature){
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        } catch(Exception e){
            throw new RuntimeException(e);
        }
    }

    //encode key toString
    public static String getStringFromKey(Key key){
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getMerkleRoot(ArrayList<txn> txns){
        int count = txns.size();
        ArrayList<String> prevTreeLayer = new ArrayList<String>();
        for(txn Txn : txns){
            prevTreeLayer.add(Txn.txnId);
        }
        ArrayList<String> treeLayer = prevTreeLayer;
        while(count > 1){
            treeLayer = new ArrayList<String>();
            for(int i = 1; i < prevTreeLayer.size(); i++){
                treeLayer.add(applySha256(prevTreeLayer.get(i-1) + prevTreeLayer.get(i)));
            }
            count = treeLayer.size();
            prevTreeLayer = treeLayer;
        }
        String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkleRoot;
    }

}
