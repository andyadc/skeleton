package com.andyadc.skeleton.shield.test.jmh;

import com.alibaba.fastjson2.JSON;
import com.andyadc.skeleton.shield.test.model.TestABClass;
import com.andyadc.skeleton.shield.test.model.TestAClass;
import com.andyadc.skeleton.shield.test.model.TestBClass;
import com.andyadc.skeleton.shield.utils.ReflectUtils;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

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

    @Param(value = {"100", "10000", "1000000"})
    private int length;

    public static void main(String[] args) throws RunnerException {
        Options opts = new OptionsBuilder().include(ShieldJMHTest.class.getSimpleName()).resultFormat(ResultFormatType.JSON).build();
        new Runner(opts).run();
    }

    @Benchmark
    public void shieldUtilsTest() {
        for (int i = 0; i < length; i++) {
            ReflectUtils.reflectToLogStringByFields(aClass);
        }
    }

    @Benchmark
    public void toStringTest() {
        for (int i = 0; i < length; i++) {
            aClass.toString();
        }
    }

    @Benchmark
    public void jsonTest() {
        for (int i = 0; i < length; i++) {
            JSON.toJSONString(aClass);
        }
    }

    @Benchmark
    public void noShieldUtilsTest() {
        for (int i = 0; i < length; i++) {
            ReflectUtils.reflectToLogStringByFields(bClass);
        }
    }

}
