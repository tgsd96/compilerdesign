import java.io.IOException;
public class Main {
    public static void main(String args[])throws IOException {
        //LexicalAnalyser lp = new LexicalAnalyser("gram.txt");
        //lp.dfa();
        //lp.grammar();
        Parser parser = new Parser("gram.txt");
        //parser.readProductions();
        //parser.removeLeftFactoring();
        parser.generateFirstAndFollow();
    }

}
