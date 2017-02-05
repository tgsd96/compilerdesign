import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class LexicalAnalyser {

    private String filename;
    private final static String[] keyWords = {"int","double","float","char","break","while","for",
            "else","if","long","switch","enum","case","register",
            "typedef","union","return","exturn","const","short","unsigned","continue",
            "signed","void","default","goto","sizeof","volatile","static","do"};
    private final static String[] punc = {
            "!","%","^","&","[","]","{","}","+",",","+","-","/","*","|","~","#",".",">","<","=","?","\\","(",")",
            ";",":","\'","\"","@","$"};
    private final static char[] seper = {
            '{','}','(',')','[',']',';','#',':','@'
    };
    private final static char[] operators ={
            '+','-','*','%','/','<','>','='
    };
    private HashMap<String,ArrayList<Integer>> keyWordTable;

    public class Tuple<X,Y>{
        private final X val1;
        private final Y val2;
        Tuple(X x, Y y){
            this.val1 = x;
            this.val2 = y;
        }
    }
    public LexicalAnalyser(String filename)
    {
        this.filename = filename;
        this.keyWordTable = new HashMap<>();
        for(String x : keyWords)
        {
            this.keyWordTable.put(x,new ArrayList<>());
        }
    }

    public  void analyseFinal() throws IOException{
        FileReader in = null;
        try{
            in = new FileReader(this.filename);
            BufferedReader reader = new BufferedReader(in);
            String line;
            while ((line = reader.readLine())!=null){

                //split the line based on space
                String[] split = line.split("\\s+");

                //read each splited component
                ArrayList<String> tokens = new ArrayList<>();
                //find punctuators and split the splitted.
                for (String l:split){
                    String[] splitOfSplit = l.split("((?<=\\W)|(?=\\W))");
                    for(String z:splitOfSplit) {
                        tokens.add(z);
                        //System.out.println(z);
                    }
                }

                //tokenize the tokens

                ArrayList<Tuple<String,String>> tokenTable = new ArrayList<>();
                for(String l : tokens)
                {
                    //check for numbers
                    if(l.matches("[0-9]+")){
                        Tuple<String,String> tup = new Tuple<>(l,"number");
                        tokenTable.add(tup);
                    }
                    else if(keyWordTable.get(l)!=null)
                    {
                        Tuple<String,String> tup = new Tuple<>(l,"keyword");
                        tokenTable.add(tup);
                    }
                    else if(l.matches("[a-z|A-Z|_]\\w*"))
                    {
                        Tuple<String,String> tup = new Tuple<>(l,"identifier");
                        tokenTable.add(tup);
                    }
                    else if(l.matches("[0-9]+\\w+")){
                        Tuple<String,String> tup = new Tuple<>(l,"invalid token ");
                        tokenTable.add(tup);
                    }
                    else
                    {
                        for(String c:punc)
                        {
                            if(l.equals(c))
                            {
                                Tuple<String,String> tup = new Tuple<>(l,"punctuators");
                                tokenTable.add(tup);
                            }
                        }
                    }


                }
                for(Tuple<String,String> l : tokenTable){
                    System.out.println(l.val1+" : "+l.val2);
                }

            }
        }finally {
            in.close();
        }
    }

    public void dfa() throws IOException{
        FileReader in = null;
        try{
            in = new FileReader(this.filename);
            BufferedReader reader = new BufferedReader(in);
            PushbackReader pr = new PushbackReader(in);
            ArrayList<Tuple<String,String>> tokens = new ArrayList<>();
            int c;
            int state = 0;
            String buffer = "";
            while(true){
                c= pr.read();
                switch (state){

                    //case 0
                    case 0: if (Character.isDigit(c)){
                        state = 1;
                        pr.unread(c);
                        break;
                    }
                        if(Character.isWhitespace(c))
                        {
                            state = 0;
                            break;
                        }
                        if(!Character.isAlphabetic(c)){
                            //System.out.println("lol");
                            state =2;

                            //System.out.println(buffer);
                            if(c=='_') {
                                buffer = buffer + (char) c;
                                state = 6;
                            }
                            else
                            {
                                pr.unread(c);
                            }
                            break;
                        }
                        if(Character.isAlphabetic(c))
                        {
                            state = 6;
                            buffer = buffer + (char)c;
                            //pr.unread(c);
                            break;
                        }
                        if(c==-1){
                            break;
                        }
                        break;

                    //case 1
                    case 1: if(Character.isDigit(c)){
                        state = 1;
                        buffer = buffer+(char)c;
                        break;
                    }
                        if(c=='.')
                        {
                            buffer = buffer+(char)c;
                            break;
                        }
                        if(Character.isWhitespace(c)||c==-1 || c==(int)'\n'){
                            tokens.add(new Tuple<>(buffer,"Digit"));
                            buffer = "";
                            pr.unread(c);
                            state = 0;
                            break;
                        }
                        if(!Character.isAlphabetic(c))
                        {
                            //System.out.println("here");
                            tokens.add(new Tuple<>(buffer,"Constants"));
                            buffer = "";
                            state = 0;
                            pr.unread(c);
                            break;
                        }
                        if(Character.isAlphabetic(c))
                        {
                            buffer = buffer+(char)c;
                            tokens.add(new Tuple<>(buffer,"Invalid"));
                            buffer="";
                            state=0;
                            break;
                        }
                        break;

                    //case 2
                    case 2 : if(!Character.isAlphabetic(c)&&!Character.isDigit(c)) {
                        buffer = buffer + (char) c;
                        for (char x:seper){
                            if(x==c){
                                tokens.add(new Tuple<>(buffer, "Punctuators"));
                            }
                        }
                        for (char x:operators){
                            if(x==c){
                                tokens.add(new Tuple<>(buffer, "Operators"));
                            }
                        }


                        buffer = "";
                        state = 0;
                        if(c=='\''){
                            //System.out.println("Nonono");
                            state =4;
                            break;
                        }
                        if (c == '\"') {
                            state = 3;
                            break;
                        }

                        //pr.unread(c);
                        break;
                    }
                        //case 3
                    case 3: if(c!='\"')
                    {
                        buffer=buffer + (char)c;
                    }else{
                        tokens.add(new Tuple<>(buffer, "Literals"));
                        buffer = "";
                        state=0;
                        //pr.unread(c);
                        tokens.add(new Tuple<>("\"","Punctuators"));
                        break;
                    }
                        break;

                    //case 4
                    case 4: //System.out.println("Herewa");
                        buffer=buffer+(char)c;
                        state =5 ;
                        break;

                    //case 5
                    case 5 : if(c=='\''){
                        tokens.add(new Tuple<>(buffer, "Character"));
                    }
                    else
                    {
                        tokens.add(new Tuple<>(buffer, "Invalid"));
                    }
                        buffer = "";
                        state=0;
                        pr.unread(c);
                        break;

                    //case 6
                    case 6: if(Character.isAlphabetic(c)||Character.isDigit(c)||c=='_'){
                        buffer=buffer+(char)c;
                        break;
                    }
                    else
                    {
                        if(keyWordTable.get(buffer)!=null)
                        {
                            tokens.add(new Tuple<>(buffer, "Keyword"));
                            buffer = "";
                            state=0;
                            pr.unread(c);
                            break;
                        }
                        else
                        {
                            tokens.add(new Tuple<>(buffer, "Identifier"));
                            buffer = "";
                            state=0;
                            pr.unread(c);
                            break;
                        }
                    }

                }
                if(c==-1)
                {

                    break;
                }
            }
            for(Tuple<String,String> l : tokens){
                System.out.println(l.val1+" : "+l.val2);
            }
        }finally {
            in.close();
        }

    }

    private  void grammar() throws IOException{
        FileReader in = null;
        try {
            in = new FileReader(this.filename);
            BufferedReader reader = new BufferedReader(in);
            String line;
            ArrayList<Tuple<String,ArrayList<String>>> productions = new ArrayList<>();
            Character[] var = new Character[10];
            //int front = 0;
            int rear = 0;
            System.out.println("The initial productions are: ");
            while ((line=reader.readLine())!=null){
                String[] lines= line.split("->");
                System.out.print(lines[0]+"->"+lines[1]+"\n");
                if(!lines[0].matches("[A-Z]")) {
                    System.out.println("Wrong Format.");
                }

                else{
                    char[] tochar;
                    tochar = lines[0].toCharArray();
                    if(rear==0){
                        var[rear] = tochar[0];
                        rear++;
                    }
                    else{
                        int i =0;
                        for(; i<rear;i++)
                        {
                            if(var[i]==tochar[0])
                            {
                                break;
                            }
                        }
                        if(i==rear)
                        {
                            var[rear]=tochar[0];
                            rear++;
                        }

                    }
                    ArrayList<String> rhs = new ArrayList<>();
                    String[] sads = lines[1].split("\\|");
                    for(String x:sads) {
                        rhs.add(x);
                    }
                    productions.add(new Tuple<>(lines[0],rhs));
                }
            }

            //array that stores variable for removing immediate left recursion
            String[] alphabeta = {"!","@","#","$","%","^","&","*","(",")","-","+","/",";","="};
            int alphabetaCount = 0;
            int n = productions.size();
            for(int i=0;i<n;i++){
                for (int j=0;j<i;j++){
                    //System.out.println(i);
                    Tuple<String,ArrayList<String>> Ai = productions.get(i);
                    ArrayList<String> AiRhs = Ai.val2;
                    //System.out.print("\nRhs of: ");
                    String AiLhs = productions.get(i).val1;
                    //String AjLhs = productions.get(j).val1;
                    ArrayList<String> AjRhs = productions.get(j).val2;
                    for(int k=0;k<AjRhs.size();k++){
                        String Ajy = AjRhs.get(k);
                        if(AiLhs.toCharArray()[0]==Ajy.toCharArray()[0]) {
                            //System.out.println(productions.get(j).val2.get(k));
                            ArrayList<String> temp = new ArrayList<>();
                            for (String zedd:AiRhs) {
                                //System.out.println("Here:");
                                //if (Character.isLowerCase(AjRhs.get(l).toCharArray()[0]))
                                {
                                    productions.get(j).val2.add(zedd+Ajy.substring(1));
                                    //System.out.println(AjRhs.get(l) + Ajy.substring(1));
                                }
                            }
                           // productions.get(i).val2.addAll(temp);
                            productions.get(j).val2.remove(k);
                        }
                    }

                }
                for (int k=0;k<productions.size();k++){
                    String AiLhs = productions.get(k).val1;
                    ArrayList<String> AiRhs = productions.get(k).val2;
                    for(int l=0;l<AiRhs.size();l++){
                        if(AiLhs.toCharArray()[0]==AiRhs.get(l).toCharArray()[0]){
                            //System.out.println(AiRhs.get(l));
                            ArrayList<String> temp = new ArrayList<>();
                            for (int z=0;z<AiRhs.size();z++){
                                String beta = AiRhs.get(z);
                                if(beta.toCharArray()[0]!=AiLhs.toCharArray()[0]){
                                    //System.out.println(beta);
                                    temp.add(beta+alphabeta[alphabetaCount]);
                                }
                            }
                            productions.get(k).val2.addAll(temp);
                            ArrayList<String> newRhs = new ArrayList<>();
                            newRhs.add("$");
                            newRhs.add(AiRhs.get(l).substring(1)+alphabeta[alphabetaCount]);
                            productions.add(new Tuple<>(alphabeta[alphabetaCount],newRhs));
                            alphabetaCount++;
                            productions.get(k).val2.remove(l);
                        }
                    }

                }
            }
            System.out.println("The productions after removing left recursion are:");
            for(Tuple<String,ArrayList<String>>x:productions){
                System.out.print(x.val1 + "->");
                for (int i=0;i<x.val2.size();i++) {
                     System.out.print(x.val2.get(i)+"|");
                }
                System.out.print("\n");
            }
        }finally {
            in.close();
        }
    }

    public static void main(String args[])throws IOException{
        LexicalAnalyser lp = new LexicalAnalyser("gram.txt");
        //lp.dfa();
        lp.grammar();
    }


}

