package cards;

import java.util.List;
import java.util.Set;

public class DameJosephine extends Knight {

    @Override
    public String name() {
        return "Dame Josephine";
    }

    @Override
    public Set<Type> types() {
        return types(Type.ACTION, Type.ATTACK, Type.VICTORY);
    }

    @Override
    public String htmlType() {
        return "Action-Attack-Knight-Victory";
    }

    @Override
    public String htmlHighlightType() {
        return "action-victory";
    }

    @Override
    protected void addUniqueDescription(List<String> description) {
        description.add("2_VP");
    }

    @Override
    public int victoryValue() {
        return 2;
    }

}
