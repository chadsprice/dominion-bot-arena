package cards;

import java.util.List;

public class DameJosephine extends Knight {

    public DameJosephine() {
        super();
        isVictory = true;
    }

    @Override
    public int victoryValue() {
        return 2;
    }

    @Override
    public String htmlClass() {
        return "action-victory";
    }

    @Override
    public String htmlType() {
        return "Action-Attack-Knight-Victory";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add("2 VP");
    }

    @Override
    public String toString() {
        return "Dame Josephine";
    }

}
