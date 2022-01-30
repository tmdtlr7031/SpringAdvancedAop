package hello.aop.pointcut;

import hello.aop.member.MemberServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import java.lang.reflect.Method;

@Slf4j
public class ArgsTest {

    Method helloMethod;

    @BeforeEach
    public void init() throws NoSuchMethodException {
        helloMethod = MemberServiceImpl.class.getMethod("hello", String.class); // hello 메서드의 String 파라미터 같는 것
    }

    private AspectJExpressionPointcut pointcut(String expression) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(expression);
        return pointcut;
    }

    @Test
    void args() {
        // hello(String)과 매칭된다고 보면 된다.
        Assertions.assertThat(pointcut("args(String)").matches(helloMethod, MemberServiceImpl.class)).isTrue();

        // 실제 메서드에서 String 이지만 Object는 최상위 객체기 때문에 가능
        Assertions.assertThat(pointcut("args(Object)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        Assertions.assertThat(pointcut("args()").matches(helloMethod, MemberServiceImpl.class)).isFalse();
        Assertions.assertThat(pointcut("args(..)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        Assertions.assertThat(pointcut("args(*)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        Assertions.assertThat(pointcut("args(String, ..)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
    }

    /**
     *  execution(* *(java.io.Serializable)): 메서드와 시그니처로 판단 (정적)
     *  arg(java.io.Serializable) : 런타임에 전달된 인수로 판단 (동적)
     */
    @Test
    void argsVsExecution() {
        // Args
        Assertions.assertThat(pointcut("args(String)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        Assertions.assertThat(pointcut("args(java.io.Serializable)").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        Assertions.assertThat(pointcut("args(Object)").matches(helloMethod, MemberServiceImpl.class)).isTrue();

        // Execution
        Assertions.assertThat(pointcut("execution(* *(String))").matches(helloMethod, MemberServiceImpl.class)).isTrue();
        Assertions.assertThat(pointcut("execution(* *(java.io.Serializable))").matches(helloMethod, MemberServiceImpl.class)).isFalse();
        Assertions.assertThat(pointcut("execution(* *(Object))").matches(helloMethod, MemberServiceImpl.class)).isFalse();
    }
}
