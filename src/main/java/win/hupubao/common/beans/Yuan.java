package win.hupubao.common.beans;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

public class Yuan extends BigDecimal {

    public Yuan(char[] in, int offset, int len) {
        super(in, offset, len);
    }

    public Yuan(char[] in, int offset, int len, MathContext mc) {
        super(in, offset, len, mc);
    }

    public Yuan(char[] in) {
        super(in);
    }

    public Yuan(char[] in, MathContext mc) {
        super(in, mc);
    }

    public Yuan(String val) {
        super(val);
    }

    public Yuan(String val, MathContext mc) {
        super(val, mc);
    }

    public Yuan(double val) {
        super(val);
        setYuanScale();

    }

    public Yuan(double val, MathContext mc) {
        super(val, mc);
    }

    public Yuan(BigInteger val) {
        super(val);
    }

    public Yuan(BigInteger val, MathContext mc) {
        super(val, mc);
    }

    public Yuan(BigInteger unscaledVal, int scale) {
        super(unscaledVal, scale);
    }

    public Yuan(BigInteger unscaledVal, int scale, MathContext mc) {
        super(unscaledVal, scale, mc);
        setYuanScale();

    }

    public Yuan(int val) {
        super(val);
        setYuanScale();

    }

    public Yuan(int val, MathContext mc) {
        super(val, mc);
        setYuanScale();

    }

    public Yuan(long val) {
        super(val);
        setYuanScale();

    }

    public Yuan(long val, MathContext mc) {
        super(val, mc);
        setYuanScale();
    }

    private void setYuanScale() {
        this.setScale(2, RoundingMode.HALF_UP);
    }

    public static void main(String[] args) {
        Yuan yuan = new Yuan(52.25455);
        yuan.setScale(2, RoundingMode.HALF_UP);
        System.out.println(yuan);
    }
}
