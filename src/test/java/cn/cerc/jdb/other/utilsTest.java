package cn.cerc.jdb.other;

import static cn.cerc.jdb.other.utils.assigned;
import static cn.cerc.jdb.other.utils.ceil;
import static cn.cerc.jdb.other.utils.copy;
import static cn.cerc.jdb.other.utils.floatToStr;
import static cn.cerc.jdb.other.utils.isNumeric;
import static cn.cerc.jdb.other.utils.roundTo;
import static cn.cerc.jdb.other.utils.strToFloatDef;
import static cn.cerc.jdb.other.utils.trunc;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.cerc.jdb.core.Utils;

public class utilsTest {
    @Test
    public void test_roundTo() {
        assertThat("舍入测试", roundTo(1.234, -2), is(1.23));
        assertThat("进一测试", roundTo(1.235, -2), is(1.24));
        assertThat("进一测试", roundTo(1.245, -2), is(1.25));
        assertThat("进一测试", roundTo(11.5, 0), is(12.0));
        assertThat("银行家算法测试", roundTo(10.5, 0), is(10.0));
        assertThat("银行家算法测试", roundTo(10.45, -1), is(10.4));
        assertThat("银行家算法测试", roundTo(10.55, -1), is(10.6));
        assertThat("负数测试", roundTo(-12.3, 0), is(-12.0));
    }

    @Test
    public void test_Trunc() {
        assertThat(trunc(-123.55), is(-123.00));
    }

    @Test
    public void test_ceil() {
        assertThat(ceil(-123.55), is(-123));
        assertThat(ceil(123.15), is(124));
        assertThat(ceil(123), is(123));
        assertThat(ceil(-123.1), is(-123));
    }

    @Test
    public void test_safeString() {
        String str = "' and '='1";
        String result = "'' and ''=''1";
        assertThat(Utils.safeString(str), is(result));
    }

    @Test
    public void test_assigned() {
        String obj = null;
        assertTrue(!assigned(obj));
        obj = "";
        assertTrue(assigned(obj));
    }

    @Test
    public void test_isNumeric() {
        assertTrue(isNumeric("111.333"));
        assertTrue(isNumeric("1113232333"));
        assertTrue(!isNumeric("a111.333"));
    }

    @Test
    public void test_copy() {
        assertEquals("a", copy("abcd", 1, 1));
        assertEquals("abcd", copy("abcd", 1, 5));
        assertEquals("bcd", copy("abcd", 2, 5));
        assertEquals("", copy(null, 2, 5));
    }

    @Test
    public void test_floatToStr() {
        assertEquals("1.3", floatToStr(1.30));
        assertEquals("0.0", floatToStr(0.00));
        assertEquals("-2.02", floatToStr(-2.02));
    }

    @Test
    public void test_strToFloatDef() {
        assertEquals(1.03, strToFloatDef("1.03", 0), 0);
        assertEquals(0, strToFloatDef("1.03a", 0), 0);
        assertEquals(-1.03, strToFloatDef("-1.03", 0), 0);
    }
}
