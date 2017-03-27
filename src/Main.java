import java.io.IOException;
public class Main {
    public static void main(String args[])throws IOException {
        //LexicalAnalyser lp = new LexicalAnalyser("gram.txt");
        //lp.dfa();
        //lp.grammar();
        Parser parser = new Parser("gram.txt");
        parser.genParseTable();
        //parser.genFirst();
        //parser.readProductions();
//        parser.removeLeftRecursion();
//        parser.removeLeftFactoring();
//        parser.generateFirstAndFollow();
//        parser.generateParseTable();
//        parser.checkLL1();
    }

}
