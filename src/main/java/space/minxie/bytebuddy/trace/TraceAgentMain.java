package space.minxie.bytebuddy.trace;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.matcher.ElementMatchers.*;

public class TraceAgentMain {
    public static void premain(String agentArgs, Instrumentation instrumentation) {
        System.out.println("TraceAgentMain premain-start:" + agentArgs);
        ElementMatcher.Junction<NamedElement> matcher = nameContainsIgnoreCase("service").and(not(nameContainsIgnoreCase("api")));
        new AgentBuilder.Default().type(matcher)
                .transform((builder, typeDescription, classLoader, module, protectionDomain) ->
                        builder.method(any()).intercept(MethodDelegation.to(TraceInterceptor.class))).installOn(instrumentation);
        System.out.println("TraceAgentMain premain-end:" + agentArgs);
    }
}
