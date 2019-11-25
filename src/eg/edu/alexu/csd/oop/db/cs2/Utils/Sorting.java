package eg.edu.alexu.csd.oop.db.cs2.Utils;

import eg.edu.alexu.csd.oop.db.cs2.structures.Factory;

import java.util.Arrays;
import java.util.List;

public class Sorting {
    public Object[][] sortTable(Object[][] table, List<Integer> idx){
        Arrays.sort(table, (Object[] a, Object[] b) -> {
            int i = 0;
            int cmp = 0;
            while(i < idx.size()){
                cmp = Factory.getInstance().compareObject(a[i], b[i]);
                if(cmp != 0){
                    return cmp;
                }
                i++;
            }
            return cmp;
        });
        return table;
    }
}
