package space.minxie.bytebuddy.trace;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class TraceInterceptor {
    private static final ThreadLocal<TraceNodeTree> traceNodeTree = new ThreadLocal<>();

    @RuntimeType
    public static Object intercept(@Origin Method method, @SuperCall Callable<?> callable) throws Exception {
        start(method);
        try {
            return callable.call();
        } catch (Exception e) {
            throw e;
        } finally {
            end(method);
        }
    }

    /**
     * 方法开始，记录开始时间，方法调用次数
     * 怎么判断是不是同一个方法 判断方法签名
     *
     * @param method
     */
    private static void start(Method method) {
        Thread.currentThread().getStackTrace()[2].getLineNumber();
        StringBuilder sb = new StringBuilder();
        sb.append(method.getDeclaringClass().getName());
        sb.append("#");
        sb.append(method.getName());
        sb.append("(");
        for (int paramIndex = 0; paramIndex < method.getGenericParameterTypes().length; paramIndex++) {
            if (paramIndex == 0) {
                sb.append(method.getGenericParameterTypes()[paramIndex].getTypeName());
            } else {
                sb.append(",");
                sb.append(method.getGenericParameterTypes()[paramIndex].getTypeName());
            }
        }
        sb.append(")");
        String methodSignName = sb.toString();

        TraceNodeTree nodeTree = TraceInterceptor.traceNodeTree.get();
        if (nodeTree == null) {
            nodeTree = new TraceNodeTree(new TraceNode(methodSignName));
            TraceInterceptor.traceNodeTree.set(nodeTree);
            return;
        }
        TraceNode node = nodeTree.findNode(methodSignName);
        if (node == null) {
            node = new TraceNode(methodSignName);
            nodeTree.getCurrent().addChild(node);
        } else {
            node.addCount();
        }
        nodeTree.pushCurrent(node);
    }

    /**
     * 一个方法的结束
     * 记录结束时间
     * <p>
     * 如果是根方法，打印耗时
     *
     * @param method
     */
    private static void end(Method method) {
        TraceNodeTree nodeTree = TraceInterceptor.traceNodeTree.get();
        nodeTree.removeCurrent().addEndCount();
        if (nodeTree.getRoot().isEnd()) {
            StringBuilder sb = new StringBuilder();
            nodeTree.buildStr(sb);
            System.out.println(sb.toString());
            TraceInterceptor.traceNodeTree.remove();
        }
    }

}