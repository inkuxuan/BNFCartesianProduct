package team.mai.inku.cartesian.io;

import team.mai.inku.cartesian.model.Item;
import team.mai.inku.cartesian.model.OptionItem;

import java.io.*;
import java.nio.Buffer;

public class PlaintTextBufferedWriter implements BNFResultWriter, AutoCloseable{

    private BufferedWriter writer;

    public PlaintTextBufferedWriter(BufferedWriter writer){
        this.writer = writer;
    }

    public PlaintTextBufferedWriter(String outpath, boolean append) throws FileNotFoundException {
        this.writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outpath, append)));
    }


    @Override
    public void write(OptionItem simpleOptionItem) throws IOException {
        for(Item item: simpleOptionItem.getOptions()){
            writer.write(item.toPlainText());
            writer.newLine();
        }
    }

    @Override
    public void close() throws Exception{
        writer.flush();
        writer.close();
    }
}
