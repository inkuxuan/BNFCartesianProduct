package team.mai.inku.cartesian;

import jdk.internal.util.xml.impl.Input;
import org.mozilla.universalchardet.UniversalDetector;
import team.mai.inku.cartesian.io.BNFBufferedReader;
import team.mai.inku.cartesian.io.BNFReader;
import team.mai.inku.cartesian.io.BNFResultWriter;
import team.mai.inku.cartesian.io.PlaintTextBufferedWriter;
import team.mai.inku.cartesian.model.Item;
import team.mai.inku.cartesian.model.OptionItem;
import team.mai.inku.cartesian.model.SequenceItem;
import team.mai.inku.cartesian.model.SimpleItem;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static final String VERSION = "1.1.0";
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
        String encoding;
        try {
            UniversalDetector detector = new UniversalDetector(null);
            FileInputStream fis = new FileInputStream(files.get(0));
            int nread;
            byte[] buf = new byte[64];
            while((nread = fis.read(buf)) > 0 && !detector.isDone()){
                detector.handleData(buf, 0, nread);
            }
            detector.dataEnd();
            encoding = detector.getDetectedCharset();
            if(encoding == null)
                encoding = "UTF-8";
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try (PlaintTextBufferedWriter writer = new PlaintTextBufferedWriter(outpath, append, encoding)) {

            for (String file : files) {
                try {
                    BNFReader reader = new BNFBufferedReader(new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding)));
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
