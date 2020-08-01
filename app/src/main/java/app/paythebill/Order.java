package app.paythebill;

import androidx.annotation.NonNull;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Order implements Serializable {
    public String Name;
    public double Price;
    public List<String> Diners;
    public boolean is_removed;
    public static int tip_for_order;

    // C'tor
    public Order() {
        Diners = new LinkedList<>();
        is_removed = false;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Name + ": " + Price + " \u20AA" + "\n");
        sb.append(" הוזמן על ידי - ");
        for (String diner: Diners) {
            sb.append(" " + diner + " ,");
        }
        if(sb.lastIndexOf(",")==sb.length()-1)
            sb.deleteCharAt(sb.length()-1);

        return sb.toString();
    }
};