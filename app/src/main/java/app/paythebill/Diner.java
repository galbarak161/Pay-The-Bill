package app.paythebill;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

public class Diner implements Serializable {
    public int id;
    public String Name;
    public double TotalToPay;
    public List<Order> DinerOrders;

    // C'tor
    public Diner(int id, String dinerName) {
        this.id = id;
        Name = dinerName;
        TotalToPay = 0;
        DinerOrders = new LinkedList<Order>();
    }

    public double UpdatePaymentAfterTip() {
        // Calculate the new payment after adding the tip
        return this.TotalToPay * (Order.tip_for_order + 100) / 100;
    }

    public String PrintDinerDetails(){
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("###.##"); // Take only to numbers after decimal point
        double paymentAfterTip = UpdatePaymentAfterTip();
        sb.append(Name + ": " + df.format(TotalToPay) + " \u20AA ");

        if(paymentAfterTip > this.TotalToPay)
            sb.append("לאחר טיפ - " + df.format(paymentAfterTip) + " \u20AA ");

        sb.append("\n" + "הזמינ/ה - ");

        for (Order o: DinerOrders) {
            sb.append(" " + o.Name + " ,");
        }
        if(sb.lastIndexOf(",")==sb.length()-1)
            sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }

    @NonNull
    @Override
    public String toString() {
        return Name;
    }

}
