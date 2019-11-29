package eg.edu.alexu.csd.oop.db.cs2.Factories;

public class Factory {
    static Factory instance;
    private Factory(){}
    public synchronized static Factory getInstance(){
        if(instance == null){
            instance = new Factory();
        }
        return instance;
    }
    public Object getObject( String value){
        String type = getType(value);
        if(type.equals("int")){
            return Integer.parseInt(value);
        }
        if(type.equals("varchar")){
            return value;
        }
        return null;
    }
    public String getType(String value){
        if(value.matches("[0-9]+")){
            return "int";
        }
        if(value.matches("\\'\\w+\\'")){
            return "varchar";
        }
        throw new ClassCastException("Unsupported data-type");
    }
    public int compareObject(Object obj1, Object obj2 ){
        if(obj1 instanceof Integer && obj2 instanceof Integer){
            Integer a = (Integer) obj1;
            Integer b = (Integer) obj2;
            return a.compareTo(b);
        }else if(obj1 instanceof String && obj2 instanceof String){
            String a = (String) obj1;
            String b = (String) obj2;
            return a.toLowerCase().compareTo(b.toLowerCase());
        }else{
            throw new ClassCastException("Unsupported data-type");
        }
    }
    public boolean checkInstance(String type, Object obj){
        if(type.equals("int")) {
            return obj instanceof Integer;
        }
        if(type.equals("varchar")){
            return obj instanceof String;
        }
        return false;
    }
}
