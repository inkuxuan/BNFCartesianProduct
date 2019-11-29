package team.mai.inku.cartesian.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptionItem extends Item {
    private List<Item> options;

    public OptionItem() {
        options = new ArrayList<>();
    }

    public OptionItem(String... strings){
        options = new ArrayList<>();
        for(String s: strings){
            options.add(new SimpleItem(s));
        }
    }

    public OptionItem(Item... items){
        options = Arrays.asList(items);
    }

    public OptionItem(List<Item> list){
        options = list;
    }

    public List<Item> getOptions() {
        return options;
    }

    public void setOptions(List<Item> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < options.size(); i++) {
            stringBuilder.append(options.get(i).toString());
            if (i < options.size() - 1) {
                stringBuilder.append("|");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    @Override
    public String toPlainText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Item option : options) {
            stringBuilder.append(option.toPlainText());
            stringBuilder.append('\n');
        }
        return stringBuilder.toString();
    }

}
