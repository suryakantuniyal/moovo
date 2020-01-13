package in.innobins.customer;

/**
 * Created by sasuke on 11/9/15.
 */
public class OrderItem {
    String mdatetime, morderid, mpickup;
    public OrderItem(String datetime, String orderid, String pickup){
        mdatetime = datetime;
        morderid = orderid;
        mpickup=pickup;
    }
}
