package team.mai.inku.cartesian.model;


public class SimpleItem extends Item {
    private String string;

    public SimpleItem(String string) {
        this.string = string;
    }

    public SimpleItem() {
    }

    @Override
    public String toString(){
        return string;
    }

    @Override
    public String toPlainText() {
        return toString();
    }
}
