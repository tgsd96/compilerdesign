import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser {
    private String filename;
    private Map<String, ArrayList<String>> productions;

    public Parser(String filename) throws IOException {
        this.filename = filename;
        this.productions = new LinkedHashMap<>();
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
            iterator.remove();
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

    public void calcFirst(String LHS, Map<String, Set<String>> first) {
        if ((this.productions.get(LHS)) != null) {
            ArrayList<String> RHS = this.productions.get(LHS);
            first.putIfAbsent(LHS, new HashSet<>());
            for (String x : RHS) {
                if (x.equals("\u03B5")) {
                    Set<String> set = first.get(LHS);
                    set.add("\u03B5");
                    first.replace(LHS, set);
                } else if (Character.isLowerCase(x.toCharArray()[0])) {
                    Set<String> set = first.get(LHS);
                    set.add(x.substring(0, 1));
                    first.replace(LHS, set);
                    /*
                    Set<String> prevset = first.get(Prev);
                    prevset.add(x.substring())
                    */
                } else {
                    int i = -1;
                    do{
                        i++;
                        if(i<x.length()) {

                            calcFirst(x.substring(i, i + 1), first);
                            Set<String> set = first.get(LHS);
                            Set<String> duoSet = first.get(x.substring(i, i + 1));
                            if (duoSet != null) {
                                for (String d : duoSet) {
                                    if (!d.equals("\u03B5")) {
                                        set.add(d);
                                    }
                                }
//                        first.replace()
                                first.replace(LHS, set);
                            }
                        }else{
                            break;
                        }

                    }while (true);

                }
            }
        }
    }

    public void generateFirstAndFollow() throws IOException {
        //printProductions(this.productions);
        //ArrayList<Tuple<String,ArrayList<String>>> productions = getProductions();
        Map<String, Set<String>> first = new LinkedHashMap<>();
        //Set<String> first = new HashSet<>();
        //HashMap<String, Set<String>> follow = new HashMap<>();
//        Map.Entry<String, Set<String>> pair = (Map.Entry)this.productions.entrySet().iterator().next();
        Iterator iterator = this.productions.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry<String, ArrayList<Integer>> pair = (Map.Entry) iterator.next();
                //System.out.println(pair.getKey() + "->" + pair.getValue());
                if(pair!=null)
                {calcFirst(pair.getKey(), first);}
                iterator.remove();
            }


        printProductions(first);
//        printProductions(this.productions);

    }

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
            //System.out.println("here:");
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
                        if (beta.toCharArray()[0] != AiLhs.toCharArray()[0] && beta.toCharArray()[beta.length() - 1] != '\'') {
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
}
