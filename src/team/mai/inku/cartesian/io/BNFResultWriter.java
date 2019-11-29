package team.mai.inku.cartesian.io;

import team.mai.inku.cartesian.model.OptionItem;

import java.io.IOException;

public interface BNFResultWriter {
    public void write(OptionItem simpleOptionItem) throws IOException;
}
