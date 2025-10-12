package com.andyadc.skeleton.shield.test.jmh;

import com.andyadc.skeleton.shield.test.model.TestABClass;
import com.andyadc.skeleton.shield.test.model.TestAClass;
import com.andyadc.skeleton.shield.test.model.TestBClass;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.All)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 1, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@Threads(8)
public class ShieldJMHTest {

    private static final TestAClass aClass;
    private static final TestBClass bClass;

    static {
        aClass = new TestAClass();
        aClass.setA("AAAAAAAAAAAAAAAAAAAAAAAA");
        aClass.setB("BBBBBBBBBBBBBBBBBBBBBBBB");
        aClass.setBint(1234567890);
        aClass.setBlong(1234567890L);
        aClass.setC("CCCCCCCCCCCCCCCCCCCCCCCC");

        TestABClass abClass = new TestABClass();
        abClass.setA("AAAAAAAAAAAAAAAAAAAAAAAAA");
        aClass.setTestABClass(abClass);

        bClass = new TestBClass();
        bClass.setA("AAAAAAAAAAAAAAAAAAAAAAAA");
        bClass.setB("BBBBBBBBBBBBBBBBBBBBBBBB");
        bClass.setBint(1234567890);
        bClass.setBlong(1234567890L);
        bClass.setC("CCCCCCCCCCCCCCCCCCCCCCCC");

    }

}
