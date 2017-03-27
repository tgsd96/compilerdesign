import com.sun.corba.se.impl.orb.ParserTable;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser {
    private String filename;
    private Map<String, ArrayList<String>> productions;
    private Map<String, Set<String>> first;
    private Map<String, Set<String>> follow;
    private String[] newGeneratedSymbol = {"Q","Y","O","P","K","Z","B","V"};
    private int index =0;
    private Map<String,Map<String,Tuple<String,String>>> parseTable;
    private ParseTable parserTable;

    public Parser(String filename) throws IOException {
        this.filename = filename;
        this.productions = new LinkedHashMap<>();
        this.first = new LinkedHashMap<>();
        this.follow = new LinkedHashMap<>();
        this.parserTable = new ParseTable();
        try {
            readProductions();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readProductions() throws IOException {
        FileReader in = null;
        try {
            in = new FileReader(this.filename);
            BufferedReader reader = new BufferedReader(in);
            String line;
            //ArrayList<Tuple<String, ArrayList<String>>> productions = new ArrayList<>();
            Character[] var = new Character[10];
            //int front = 0;
            int rear = 0;
            //System.out.println("The initial productions are: ");
            while ((line = reader.readLine()) != null) {
                String[] lines = line.split("->");
                //System.out.print(lines[0] + "->" + lines[1] + "\n");
                if (!lines[0].matches("[A-Z]")) {
                    System.out.println("Wrong Format.");
                } else {
                    char[] tochar;
                    tochar = lines[0].toCharArray();
                    if (rear == 0) {
                        var[rear] = tochar[0];
                        rear++;
                    } else {
                        int i = 0;
                        for (; i < rear; i++) {
                            if (var[i] == tochar[0]) {
                                break;
                            }
                        }
                        if (i == rear) {
                            var[rear] = tochar[0];
                            rear++;
                        }

                    }
                    ArrayList<String> rhs = new ArrayList<>();
                    String[] prod = lines[1].split("\\|");
                    for (String x : prod) {
                        rhs.add(x);
                    }
                    this.productions.put(lines[0], rhs);
                }
            }
        } finally {
            in.close();
        }
    }

    public void printProductions(Map x) {
        Iterator iterator = x.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<Integer>> pair = (Map.Entry) iterator.next();
            System.out.println(pair.getKey() + "->" + pair.getValue());
            //iterator.remove();
        }

    }

    private String greatestCommonPrefix(String a, String b) {
        int minlength = Math.min(a.length(), b.length());
        for (int p = 0; p < minlength; p++) {
            if (a.charAt(p) != b.charAt(p) && p != 0) {
                //System.out.println(a.substring(0,p));
                return a.substring(0, p);
            }
            if (a.charAt(p) != b.charAt(p) && p == 0) {
                //System.out.println(a.substring(0,p));
                return null;
            }
        }
        if (a.length() > b.length()) {
            return b;
        } else {
            return a;
        }
    }

    private ArrayList<Tuple<String, ArrayList<String>>> getProductions() throws IOException {
        FileReader in = null;
        try {
            in = new FileReader(this.filename);
            BufferedReader reader = new BufferedReader(in);
            String line;
            ArrayList<Tuple<String, ArrayList<String>>> productions = new ArrayList<>();
            Character[] var = new Character[10];
            //int front = 0;
            int rear = 0;
            System.out.println("The initial productions are: ");
            while ((line = reader.readLine()) != null) {
                String[] lines = line.split("->");
                System.out.print(lines[0] + "->" + lines[1] + "\n");
                if (!lines[0].matches("[A-Z]")) {
                    System.out.println("Wrong Format.");
                } else {
                    char[] tochar;
                    tochar = lines[0].toCharArray();
                    if (rear == 0) {
                        var[rear] = tochar[0];
                        rear++;
                    } else {
                        int i = 0;
                        for (; i < rear; i++) {
                            if (var[i] == tochar[0]) {
                                break;
                            }
                        }
                        if (i == rear) {
                            var[rear] = tochar[0];
                            rear++;
                        }

                    }
                    ArrayList<String> rhs = new ArrayList<>();
                    String[] prod = lines[1].split("\\|");
                    for (String x : prod) {
                        rhs.add(x);
                    }
                    productions.add(new Tuple<>(lines[0], rhs));

                }
            }
            return productions;
        } finally {
            in.close();
        }
        //return null;
    }
    private void findFirst(String LHS){
        if(this.productions.get(LHS)!=null){
            ArrayList<String> RHS = this.productions.get(LHS);
            if(first.get(LHS)==null){
                first.put(LHS,new HashSet<>());
                for(String valofRhs : RHS){
                    if(valofRhs.equals("\u03B5")) {
                        first.get(LHS).add("\u03B5");
                    }else if (!Character.isUpperCase(valofRhs.toCharArray()[0])) {
                        first.get(LHS).add(valofRhs.substring(0,1));
                    }else{
                        int i=-1;
                        boolean hasepsilon = true;
                        while(hasepsilon){
                            i++;
                            if(i>valofRhs.length()) break;
                            findFirst(valofRhs.substring(i,i+1));
                            first.get(LHS).addAll(first.get(valofRhs.substring(i,i+1)));
                            if(first.get(valofRhs.substring(i,i+1)).contains("\u03B5")) continue;
                            else {
                                hasepsilon = false;
                                break;
                            }
                        }
                        if(hasepsilon==false){
                            first.get(LHS).remove("\u03B5");
                        }
                    }

                }
            }
        }
    }

    public void genFirst() throws IOException{
        Iterator iterator = this.productions.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> pair = (Map.Entry) iterator.next();
            if(pair!=null) {
                findFirst(pair.getKey());
            }
        }
        printProductions(first);
//        genFollow();
    }

    public void genFollow() throws IOException{
        Iterator iterator = this.productions.entrySet().iterator();
        Map.Entry<String, ArrayList<String>> pair = (Map.Entry) iterator.next();
        Set<String> tempSet = new HashSet();
        tempSet.add("$");
        this.follow.put(pair.getKey(),tempSet );
        iterator = this.productions.entrySet().iterator();
        while (iterator.hasNext()){
            pair = (Map.Entry) iterator.next();
            for(String x : pair.getValue()){
                for(int i = 0; i< x.length()-1;i++) {
                    for(int  j=i+1;j<x.length();j++) {
                        Character alpha = x.substring(i, i+1).toCharArray()[0];
                        Character beta = x.substring(j, j + 1).toCharArray()[0];
                        if (Character.isUpperCase(alpha) && !Character.isUpperCase(beta)) {
                            if (this.follow.get(x.substring(i, j)) == null) {
                                Set<String> temp = new HashSet();
                                temp.add(x.substring(j, j+1));
                                this.follow.put(x.substring(i, i+1), temp);
                            } else {
                                this.follow.get(x.substring(i, i+1)).add(x.substring(j, j+1));
                            }
                            break;
                        }

                        if (Character.isUpperCase(alpha) && Character.isUpperCase(beta)) {
                            if (this.first.get(x.substring(j, j+1)) != null) {
                                if (this.follow.get(x.substring(i, i+1)) == null)
                                    follow.put(x.substring(i, i + 1), first.get(x.substring(j, j+1)));
                                else
                                    this.follow.get(x.substring(i, i + 1)).addAll(first.get(x.substring(j, j+1)));
                            }
                            if(first.get(x.substring(j,j+1)).contains("ε")){
                                continue;
                            }else
                            {
                                break;
                            }
//                        this.follow.get(x.substring(i, i + 1)).remove("ε");
                        }
                    }
                }
            }
        }
        iterator = this.productions.entrySet().iterator();
        while (iterator.hasNext()){
            pair = (Map.Entry) iterator.next();
            for(String x : pair.getValue()){
                for(int i=x.length();i>0;i--){
                    if(follow.get(pair.getKey())!=null){
                        if(Character.isUpperCase(x.substring(i-1,i).toCharArray()[0])) {
                            if (follow.get(x.substring(i - 1, i)) == null) {
                                follow.put(x.substring(i - 1, i), follow.get(pair.getKey()));
                            } else {
                                follow.get(x.substring(i - 1, i)).addAll(follow.get(pair.getKey()));
                            }
                                if(first.get(x.substring(i-1,i)).contains("ε")){
                                    continue;
                                }else {
                                    break;
                                }
                        }else{
                            break;
                        }
                    }
                }
            }
        }
        Iterator iterator1 = this.follow.entrySet().iterator();
        while(iterator1.hasNext()){
            Map.Entry<String, HashSet<String>> fllw = (Map.Entry) iterator1.next();
            if(fllw.getValue().contains("ε")){
                fllw.getValue().remove("ε");
            }
        }
        System.out.println("Follow:");
        printProductions(follow);
    }


    public void genParseTable() throws IOException{
        genFirst();
        genFollow();
        Iterator iterator = this.productions.entrySet().iterator();
        boolean flag = true;
        while (iterator.hasNext()) {
            Map.Entry<String, ArrayList<String>> pair = (Map.Entry) iterator.next();
            for (String Rhs : pair.getValue()) {
                if(!Character.isUpperCase(Rhs.substring(0,1).toCharArray()[0]))
                {
                    if(!Rhs.substring(0,1).equals("ε")) {
                        flag = parserTable.put(pair.getKey(), Rhs.substring(0, 1), pair.getKey(), Rhs);
                        if(!flag) break;
                    }
                    else
                    {
                        for (String fllw : follow.get(pair.getKey())){
                           flag = parserTable.put(pair.getKey(), fllw, pair.getKey(), Rhs);
                           if(!flag) break;
                        }
                    }

                }else {
                    int i=0;
                    for(;i<Rhs.length();i++) {
                        for (String terminal : first.get(Rhs.substring(i, i+1))) {
                            if (!terminal.equals("ε")) {
                                flag = parserTable.put(pair.getKey(), terminal, pair.getKey(), Rhs);
                                if(!flag) break;
                            }
                        }
                        if (first.get(Rhs.substring(i,i+1)).contains("ε")){
                            continue;
                        }else break;
                    }
                    if(i==Rhs.length()){
                        for (String fllw : follow.get(pair.getKey())){
                            flag = parserTable.put(pair.getKey(), fllw, pair.getKey(), "ε");
                            if(!flag){
                                break;
                            }

                        }
                    }
                }
            }
        }
        if(flag) {
            parserTable.print();
            parserTable.parse("E", "i+i*i");
        }else{
            System.out.println("Multiple entries are found! Exiting process!");
        }
    }

/**
    Remove Left factoring and other string transformations below
 */
    public void removeLeftFactoring() throws IOException {

        ArrayList<Tuple<String, ArrayList<String>>> productions = getProductions();
        ArrayList<Tuple<String, ArrayList<String>>> newproductions = new ArrayList<>();
        for (int i = 0; i < productions.size(); i++) {
            ArrayList<String> AiRhs = productions.get(i).val2;
            HashMap<String, ArrayList<Integer>> substrings = new HashMap<>();
            for (int j = 0; j < AiRhs.size(); j++) {
                String prod1 = AiRhs.get(j);
                for (int k = j + 1; k < AiRhs.size(); k++) {
                    String prod2 = AiRhs.get(k);
                    String substr = greatestCommonPrefix(prod1, prod2);
                    //System.out.println(substr);
                    if (substr != null) {
                        if (substrings.get(substr) == null) {
                            ArrayList<Integer> temp = new ArrayList<>();
                            temp.add(j);
                            temp.add(k);
                            substrings.put(substr, temp);
                        } else {
                            ArrayList<Integer> temp = substrings.get(substr);
                            temp.add(k);
                            substrings.replace(substr, temp);
                        }
                    }
                }
            }
            Iterator iterator = substrings.entrySet().iterator();
            while (iterator.hasNext()) {
                HashMap.Entry<String, ArrayList<Integer>> pair = (HashMap.Entry) iterator.next();
                String prod = productions.get(i).val1;
                ArrayList<String> newProd = new ArrayList<>();
                System.out.println(pair.getKey() + "->" + pair.getValue());
                int len = pair.getKey().length();
                for (int ind : pair.getValue()) {
                    if (productions.get(i).val2.get(ind).substring(len).length() == 0) {
                        newProd.add("\u03B5");
                    } else
                        newProd.add(productions.get(i).val2.get(ind).substring(len));
                }
                for (int z = pair.getValue().size() - 1; z >= 0; z--) {
                    productions.get(i).val2.remove((int) pair.getValue().get(z));
                }
                productions.get(i).val2.add(pair.getKey() + prod + "\"");
                newproductions.add(new Tuple<>(prod + "\"", newProd));
                iterator.remove();
            }
        }
        System.out.println("The productions after removing left recursion are:");
        for (Tuple<String, ArrayList<String>> x : productions) {
            System.out.print(x.val1 + "->");
            for (int i = 0; i < x.val2.size(); i++) {
                System.out.print(x.val2.get(i) + "|");
            }
            System.out.print("\n");
        }
        for (Tuple<String, ArrayList<String>> y : newproductions) {
            System.out.print(y.val1 + "->");
            for (int i = 0; i < y.val2.size(); i++) {
                System.out.print(y.val2.get(i) + "|");
            }
            System.out.print("\n");
        }

    }

    private void removeImmediate(ArrayList<Tuple<String, ArrayList<String>>> productions, ArrayList<Tuple<String, ArrayList<String>>> newproductions) {

        for (int k = 0; k < productions.size(); k++) {
            String AiLhs = productions.get(k).val1;
            ArrayList<String> AiRhs = productions.get(k).val2;
            for (int l = 0; l < AiRhs.size(); l++) {
                if (AiLhs.toCharArray()[0] == AiRhs.get(l).toCharArray()[0]) {
                    //System.out.println(AiRhs.get(l));
                    ArrayList<Tuple<Integer, String>> temp = new ArrayList<>();
                    int size = AiRhs.size();
                    //int[] index = new int[10];
                    for (int z = 0; z < size; z++) {
                        String beta = AiRhs.get(z);
                        if (beta.toCharArray()[0] != AiLhs.toCharArray()[0]) {
                            //System.out.println(beta);
                            temp.add(new Tuple<>(z, beta + AiLhs.toCharArray()[0] + "\'"));
                            //productions.get(k).val2.add(z,beta+AiLhs.toCharArray()[0]+"\'");

                        }
                    }
                    for (int z = 0; z < temp.size(); z++) {
                        Tuple<Integer, String> q = temp.get(z);
                        productions.get(k).val2.set(q.val1, q.val2);
                    }
                    ArrayList<String> newRhs = new ArrayList<>();
                    newRhs.add("\u03B5");
                    if (AiRhs.get(l).substring(1).toCharArray()[AiRhs.get(l).length() - 2] == '\'') {
                        newRhs.add(AiRhs.get(l).substring(1));
                    } else
                        newRhs.add(AiRhs.get(l).substring(1) + AiLhs.toCharArray()[0] + "\'");
                    newproductions.add(new Tuple<>(AiLhs.toCharArray()[0] + "\'", newRhs));
                    //alphabetaCount++;
                    productions.get(k).val2.remove(l);
//                    productions.add();
                }
            }
        }
    }

    public void removeLeftRecursion() throws IOException {
        ArrayList<Tuple<String, ArrayList<String>>> productions = getProductions();
        ArrayList<Tuple<String, ArrayList<String>>> newproductions = new ArrayList<>();
        //array that stores variable for removing immediate left recursion
        int n = productions.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                //System.out.println(i);
                Tuple<String, ArrayList<String>> Ai = productions.get(i);
                ArrayList<String> AiRhs = Ai.val2;
                //System.out.print("\nRhs of: ");
                String AiLhs = productions.get(i).val1;
                //String AjLhs = productions.get(j).val1;
                ArrayList<String> AjRhs = productions.get(j).val2;
                for (int k = 0; k < AjRhs.size(); k++) {
                    String Ajy = AjRhs.get(k);
                    if (AiLhs.toCharArray()[0] == Ajy.toCharArray()[0]) {
                        //System.out.println(productions.get(j).val2.get(k));
                        ArrayList<String> temp = new ArrayList<>();
                        //
                        for (String prod : AiRhs) {
                            //System.out.println("Here:");
                            //if (Character.isLowerCase(AjRhs.get(l).toCharArray()[0]))
                            {
                                productions.get(j).val2.add(prod + Ajy.substring(1));
                                //System.out.println(AjRhs.get(l) + Ajy.substring(1));
                            }
                        }
                        //productions.get(i).val2.addAll(temp);
                        productions.get(j).val2.remove(k);
                    }
                }
            }
            removeImmediate(productions, newproductions);

        }
        System.out.println("The productions after removing left recursion are:");
        for (Tuple<String, ArrayList<String>> x : productions) {
            System.out.print(x.val1 + "->");
            ArrayList<String> rhs = new ArrayList<>();
            for (int i = 0; i < x.val2.size(); i++) {
                System.out.print(x.val2.get(i) + "|");
                rhs.add(x.val2.get(i));
            }
            this.productions.put(x.val1, rhs);
            System.out.print("\n");
        }
        for (Tuple<String, ArrayList<String>> y : newproductions) {
            System.out.print(y.val1 + "->");
            ArrayList<String> rhs = new ArrayList<>();
            for (int i = 0; i < y.val2.size(); i++) {
                System.out.print(y.val2.get(i) + "|");
                rhs.add(y.val2.get(i));
            }
            this.productions.put(y.val1, rhs);
            System.out.print("\n");
        }
        printProductions(this.productions);
    }
}
