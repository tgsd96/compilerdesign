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

}

