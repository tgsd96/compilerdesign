import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    private String filename;
    public Parser(String filename){
        this.filename = filename;
    }

    private void removeImmediate(ArrayList<Tuple<String,ArrayList<String>>> productions,ArrayList<Tuple<String,ArrayList<String>>> newproductions ){

        for (int k=0;k<productions.size();k++){
            String AiLhs = productions.get(k).val1;
            ArrayList<String> AiRhs = productions.get(k).val2;
            for(int l=0;l<AiRhs.size();l++){
                if(AiLhs.toCharArray()[0]==AiRhs.get(l).toCharArray()[0]){
                    //System.out.println(AiRhs.get(l));
                    ArrayList<Tuple<Integer,String>> temp = new ArrayList<>();
                    int size = AiRhs.size();
                    //int[] index = new int[10];
                    for (int z=0;z<size;z++){
                        String beta = AiRhs.get(z);
                        if(beta.toCharArray()[0]!=AiLhs.toCharArray()[0]&&beta.toCharArray()[beta.length()-1]!='\''){
                            //System.out.println(beta);
                            temp.add(new Tuple<>(z,beta+AiLhs.toCharArray()[0]+"\'"));
                            //productions.get(k).val2.add(z,beta+AiLhs.toCharArray()[0]+"\'");

                        }
                    }
                    for (int z=0;z<temp.size();z++){
                        Tuple<Integer,String> q = temp.get(z);
                        productions.get(k).val2.set(q.val1,q.val2);
                    }
                    ArrayList<String> newRhs = new ArrayList<>();
                    newRhs.add("\u03B5");
                    if(AiRhs.get(l).substring(1).toCharArray()[AiRhs.get(l).length()-2]=='\''){
                        newRhs.add(AiRhs.get(l).substring(1));
                    }
                    else
                        newRhs.add(AiRhs.get(l).substring(1)+AiLhs.toCharArray()[0]+"\'");
                    newproductions.add(new Tuple<>(AiLhs.toCharArray()[0]+"\'",newRhs));
                    //alphabetaCount++;
                    productions.get(k).val2.remove(l);
                }
            }
        }
    }

    public  void removeLeftRecursion() throws IOException {
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
                    productions.add(new Tuple<String,ArrayList<String>>(lines[0],rhs));
                }
            }
            ArrayList<Tuple<String,ArrayList<String>>> newproductions = new ArrayList<>();
            //array that stores variable for removing immediate left recursion
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
                            //
                            for (String prod:AiRhs) {
                                //System.out.println("Here:");
                                //if (Character.isLowerCase(AjRhs.get(l).toCharArray()[0]))
                                {
                                    productions.get(j).val2.add(prod+Ajy.substring(1));
                                    //System.out.println(AjRhs.get(l) + Ajy.substring(1));
                                }
                            }
                            //productions.get(i).val2.addAll(temp);
                            productions.get(j).val2.remove(k);
                        }
                    }
                }
                removeImmediate(productions,newproductions);

            }
            System.out.println("The productions after removing left recursion are:");
            for(Tuple<String,ArrayList<String>> x:productions){
                System.out.print(x.val1 + "->");
                for (int i=0;i<x.val2.size();i++) {
                    System.out.print(x.val2.get(i)+"|");
                }
                System.out.print("\n");
            }
            for(Tuple<String,ArrayList<String>> y:newproductions){
                System.out.print(y.val1 + "->");
                for (int i=0;i<y.val2.size();i++) {
                    System.out.print(y.val2.get(i)+"|");
                }
                System.out.print("\n");
            }
        }finally {
            in.close();
        }
    }
}
