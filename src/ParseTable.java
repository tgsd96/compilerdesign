import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

public class ParseTable {

    private Map<String,Tuple<String,String>> entries;
    private Stack<String> stack;

    public ParseTable(){
        this.entries = new HashMap<>();
        this.stack = new Stack<>();
    }

    public boolean put(String nonTerminal, String terminal, String LHS, String RHS){
        Tuple<String,String> newEntry = new Tuple(LHS,RHS);
        if(this.entries.get(nonTerminal+terminal)==null) {
            this.entries.put(nonTerminal + terminal, newEntry);
            return true;
        }
        else{
            return false;
        }


    }
    public Tuple<String,String> get(String nonTerminal, String terminal){
        return this.entries.get(nonTerminal+terminal);
    }

    public void print(){
        Iterator iterator = this.entries.entrySet().iterator();
        System.out.println("--------------Parsing Table--------------------------");
        while (iterator.hasNext()){
            Map.Entry<String, Tuple<String,String >> pair = (Map.Entry) iterator.next();
            System.out.println(pair.getKey()+" : "+ pair.getValue().val1+"->"+pair.getValue().val2);
        }
    }

    public void parse(String start, String input){

        /**
         * Prepare the input
         */
        input = input + "$";
        stack.push("$");
        stack.push(start);
        boolean parsing = true;
        int ptr = 0;
        System.out.println("\t\t\t\t\t-------------------------Parsing Operation----------------------------------");
        System.out.format("%32s%32s%32s\n","Parse Stack","Remaining Input","Parser Action");
        while(parsing){
            if(ptr>input.length()) break;
            String X = stack.peek().substring(0,1);
            String a = input.substring(ptr,ptr+1);
            System.out.format("%32s%32s",stack.toString(),input.substring(ptr));
            if((!Character.isUpperCase(X.toCharArray()[0])||X.equals("$")&&!X.equals("ε"))){
                if(X.equals(a)){
                    stack.pop();
                    ptr++;
                }
                else{
                    System.out.println("An error occurred");
                    parsing = false;
                }
            }else {
                if(entries.get(X+a)!=null) {
                    Tuple<String, String> production = entries.get(X + a);
                    stack.pop();
                    String RHS = production.val2;
                    if (RHS.equals("ε")) {

                    } else {
                        for (int i = RHS.length(); i > 0; i--) {
                            stack.push(RHS.substring(i - 1, i));
                        }
                    }
                    System.out.format("%32s",production.val1 + "->" + RHS);
                }
                else{
                    System.out.println("An error occurred");
                    parsing = false;
                }
            }
            System.out.print("\n");
            if(stack.peek().equals(input.substring(ptr,ptr+1))&&stack.peek().equals("$")){
                System.out.println("String accepted");
                parsing =false;
            }
            if(stack.peek().equals("$")){
                parsing=false;
            }

        }

    }
}
