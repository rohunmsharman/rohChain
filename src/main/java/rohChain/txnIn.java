package rohChain;

public class txnIn {
    public String txnOutId; //txnOut --> txnId
    public txnOut UTXO;
    public int prevTxnIndex;




    public txnIn(String txnOutId){
        this.txnOutId = txnOutId;
    }
 // add UTXO databse

}
