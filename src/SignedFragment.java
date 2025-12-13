import java.security.*;

public class SignedFragment {
    private final byte[] fragment;
    private final byte[] signature;

    public SignedFragment(byte[] fragment, PrivateKey privateKey) throws Exception {
        this.fragment = fragment;
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initSign(privateKey);
        sig.update(fragment);
        this.signature = sig.sign();
    }

    public boolean verify(PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withECDSA");
        sig.initVerify(publicKey);
        sig.update(fragment);
        return sig.verify(signature);
    }

    public byte[] getFragment() {
        return fragment;
    }
}