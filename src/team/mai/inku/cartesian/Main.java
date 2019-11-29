package team.mai.inku.cartesian;

import team.mai.inku.cartesian.io.BNFBufferedReader;
import team.mai.inku.cartesian.io.BNFReader;
import team.mai.inku.cartesian.io.BNFResultWriter;
import team.mai.inku.cartesian.io.PlaintTextBufferedWriter;
import team.mai.inku.cartesian.model.Item;
import team.mai.inku.cartesian.model.OptionItem;
import team.mai.inku.cartesian.model.SequenceItem;
import team.mai.inku.cartesian.model.SimpleItem;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static final String VERSION = "1.0.0";
    public static final String HELP_STR = "BNF Product " + VERSION + " Help" +
            "parameters: [-o <output-file>] [-append] <input-file {input-file}>";

    public static void main(String[] args) {
        List<String> files = new ArrayList<>();
        String outpath = null;
        boolean append = false;

        for (int i = 0; i < args.length; i++) {
            final String arg = args[i];

            if (arg.startsWith("-")) {
                if (arg.equals("-o")) {
                    outpath = args[++i];
                    continue;
                }
                if (arg.equals("-append")) {
                    append = true;
                    continue;
                }
                if (arg.equals("-help")) {
                    System.out.println(HELP_STR);
                    return;
                }
            }

            files.add(arg);
        }
        if (outpath == null) {
            outpath = "./output.txt";
        }

        if (files.size() < 1) {
            System.out.println(HELP_STR);
        }

        try (PlaintTextBufferedWriter writer = new PlaintTextBufferedWriter(outpath, append)) {

            for (String file : files) {
                try {
                    BNFReader reader = new BNFBufferedReader(file);
                    OptionItem data = reader.read();

                    KtCartesianItemProcessor processor = new KtCartesianItemProcessor();
                    OptionItem optionItem = processor.extractToOptionItem(data);

                    writer.write(optionItem);

                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
