package space.minxie.bytebuddy.trace;

import java.util.ArrayList;
import java.util.List;

public class TraceNode {
    private int deep = 0;
    private List<TraceNode> children = new ArrayList<TraceNode>();
    private final String name;
    private int startCount;
    private int endCount = 0;
    private final Long startTime;
    private Long endTime = null;


    public TraceNode(String methodSignName) {
        this.startCount = 1;
        this.name = methodSignName;
        this.startTime = System.currentTimeMillis();
    }

    public void addCount() {
        this.startCount++;
    }

    public void addEndCount() {
        this.endCount++;
        this.endTime = System.currentTimeMillis();
    }

    public boolean isEnd() {
        return endCount == startCount;
    }

    public void addChild(TraceNode node) {
        node.setDeep(this.deep + 1);
        this.children.add(node);
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

    public String getName() {
        return name;
    }


    public void buildStr(StringBuilder sb) {
        for (int i = 0; i < deep; i++) {
            sb.append("  ");
        }
        if (deep > 0) {
            sb.append("+");
        }
        sb.append(name).append(" : count = ").append(endCount)
                .append(", allCost = ").append(endTime - startTime).append(" ms")
                .append(", avgCost = ").append((endTime - startTime) / endCount).append(" ms")
                .append('\n');
        for (TraceNode child : this.children) {
            child.buildStr(sb);
        }
    }
}
