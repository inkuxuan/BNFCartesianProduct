package team.mai.inku.cartesian.io;

import team.mai.inku.cartesian.model.OptionItem;

import java.io.IOException;

public interface BNFReader {
    public OptionItem read() throws IOException;

    void close() throws IOException;
}
