package cn.cerc.jdb.other;

import static cn.cerc.jdb.other.utils.roundTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class utilsTest2 {
    // 以下代码请勿删除，此是用于大量数据批次测试的范本
    @Parameters
    public static Collection<Object[]> init() {
        Object[][] objects = { { 1.234, 1.23 }, { 1.235, 1.24 }, { 1.245, 1.25 } };
        return Arrays.asList(objects);
    }

    private double value;
    private double expecked;

    public utilsTest2(double value, double expecked) {
        this.value = value;
        this.expecked = expecked;
    }

    @Test
    public void testRoundTo() {
        double val = roundTo(value, -2);
        assertThat(val, is(expecked));
    }
}
