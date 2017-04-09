import java.util.HashMap;

public class OperatorParser {
    private HashMap<String,String> table;

    public OperatorParser(){
        this.table = new HashMap<>();
        this.table.put("i+",".>");
        this.table.put("i*",".>");
        this.table.put("i$", ".>");

        this.table.put("+i","<.");
        this.table.put("++",".>");
        this.table.put("+*","<.");
        this.table.put("+$",".>");
        this.table.put("*i","<.");
        this.table.put("*+",".>");
        this.table.put("**",".>");
        this.table.put("*$",".>");
        this.table.put("$i","<.");
        this.table.put("$+","<.");
        this.table.put("$*","<.");
        this.table.put("$$",".>");
    }
}
