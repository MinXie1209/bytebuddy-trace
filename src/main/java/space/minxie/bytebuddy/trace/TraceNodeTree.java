package space.minxie.bytebuddy.trace;

import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;

public class TraceNodeTree {
    private TraceNode root;
    private Stack<TraceNode> current = new Stack<>();
    private Map<String, TraceNode> nodeMap = new ConcurrentHashMap<>();

    public TraceNodeTree(TraceNode node) {
        this.root = node;
        this.current.addElement(node);
        nodeMap.put(node.getName(), node);
    }

    public TraceNode findNode(String methodSignName) {
        return nodeMap.get(methodSignName);
    }

    public TraceNode getCurrent() {
        return current.peek();
    }

    public TraceNode removeCurrent() {
        return current.pop();
    }

    public void pushCurrent(TraceNode current) {
        this.current.addElement(current);
        if (!nodeMap.containsKey(current.getName())) {
            nodeMap.put(current.getName(), current);
        }
    }

    /**
     * 打印耗时
     */
    public void buildStr(StringBuilder sb) {
        this.root.buildStr(sb);
    }

    public TraceNode getRoot() {
        return root;
    }
}
