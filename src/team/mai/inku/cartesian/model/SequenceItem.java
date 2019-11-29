package team.mai.inku.cartesian.model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SequenceItem extends Item {
    private List<Item> items;

    public SequenceItem(){
        items = new ArrayList<>();
    }

    public SequenceItem(String... strings){
        items = new ArrayList<>();
        for(String s: strings){
            items.add(new SimpleItem(s));
        }
    }

    public SequenceItem(Item... items){
        this.items = Arrays.asList(items);
    }

    public SequenceItem(List<Item> items){
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<");
        for (int i = 0; i < items.size(); i++) {
            stringBuilder.append(items.get(i).toString());
            if(i<items.size()-1){
                stringBuilder.append("+");
            }
        }
        stringBuilder.append(">");
        return stringBuilder.toString();
    }

    @Override
    public String toPlainText() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            stringBuilder.append(items.get(i).toString());
        }
        return stringBuilder.toString();
    }
}
