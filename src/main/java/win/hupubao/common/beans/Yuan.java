
/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package win.hupubao.common.beans;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 *
 * @author Moses.wei
 * @date 2018-06-29 22:17:57
 * 金额：元
 */
public class Yuan implements Serializable {

    private static final long serialVersionUID = -7495825595798176907L;

    private final BigDecimal value;

    public Yuan() {
        this.value =  new BigDecimal(0);
    }

    public Yuan(BigDecimal value) {
        if (value == null) {
            value = new BigDecimal(0);
        }
        this.value = new BigDecimal(value.setScale(2, RoundingMode.HALF_UP)
                .toPlainString());
    }

    public Yuan(String val) {
        this(new BigDecimal(val));
    }

    public Yuan(double val) {
        this(new BigDecimal(val));
    }

    public Yuan(char[] in, int offset, int len) {
        this(new BigDecimal(in, offset, len));
    }

    public Yuan(char[] in, int offset, int len, MathContext mc) {
        this(new BigDecimal(in, offset, len, mc));
    }

    public Yuan(char[] in) {
        this(new BigDecimal(in));
    }

    public Yuan(char[] in, MathContext mc) {
        this(new BigDecimal(in, mc));
    }

    public Yuan(String val, MathContext mc) {
        this(new BigDecimal(val, mc));
    }

    public Yuan(double val, MathContext mc) {
        this(new BigDecimal(val, mc));
    }

    public Yuan(BigInteger val) {
        this(new BigDecimal(val));
    }

    public Yuan(BigInteger val, MathContext mc) {
        this(new BigDecimal(val, mc));
    }

    public Yuan(BigInteger unscaledVal, int scale) {
        this(new BigDecimal(unscaledVal, scale));
    }

    public Yuan(BigInteger unscaledVal, int scale, MathContext mc) {
        this(new BigDecimal(unscaledVal, scale, mc));
    }

    public Yuan(int val) {
        this(new BigDecimal(val));
    }

    public Yuan(int val, MathContext mc) {
        this(new BigDecimal(val, mc));
    }

    public Yuan(long val) {
        this(new BigDecimal(val));
    }

    public Yuan(long val, MathContext mc) {
        this(new BigDecimal(val, mc));
    }

    public Yuan add(Yuan augend) {
       return new Yuan(this.value.add(augend.value));
    }

    public Yuan add(Yuan augend, MathContext mc) {
       return new Yuan(this.value.add(augend.value, mc));
    }

    
    public Yuan subtract(Yuan subtrahend) {
       return new Yuan(this.value.subtract(subtrahend.value));
    }

    
    public Yuan subtract(Yuan subtrahend, MathContext mc) {
       return new Yuan(this.value.subtract(subtrahend.value, mc));
    }

    
    public Yuan multiply(Yuan multiplicand) {
       return new Yuan(this.value.multiply(multiplicand.value));
    }

    
    public Yuan multiply(Yuan multiplicand, MathContext mc) {
        return new Yuan(this.value.multiply(multiplicand.value, mc));
    }

    
    public Yuan divide(Yuan divisor, int scale, int roundingMode) {
        return new Yuan(this.value.divide(divisor.value, scale, roundingMode));
    }

    
    public Yuan divide(Yuan divisor, int scale, RoundingMode roundingMode) {
        return new Yuan(this.value.divide(divisor.value, scale, roundingMode));
    }

    
    public Yuan divide(Yuan divisor, int roundingMode) {
        return new Yuan(this.value.divide(divisor.value, roundingMode));
    }

    
    public Yuan divide(Yuan divisor, RoundingMode roundingMode) {
        return new Yuan(this.value.divide(divisor.value, roundingMode));
    }

    
    public Yuan divide(Yuan divisor) {
        return new Yuan(this.value.divide(divisor.value));
    }

    
    public Yuan divide(Yuan divisor, MathContext mc) {
        return new Yuan(this.value.divide(divisor.value, mc));
    }

    
    public Yuan divideToIntegralValue(Yuan divisor) {
        return new Yuan(this.value.divideToIntegralValue(divisor.value));
    }

    
    public Yuan divideToIntegralValue(Yuan divisor, MathContext mc) {
        return new Yuan(this.value.divideToIntegralValue(divisor.value, mc));
    }

    
    public Yuan remainder(Yuan divisor) {
        return new Yuan(this.value.remainder(divisor.value));
    }

    
    public Yuan remainder(Yuan divisor, MathContext mc) {
        return new Yuan(this.value.remainder(divisor.value, mc));
    }

    
    public Yuan[] divideAndRemainder(Yuan divisor) {
        Yuan[] result = new Yuan[2];

        result[0] = this.divideToIntegralValue(divisor);
        result[1] = this.subtract(result[0].multiply(divisor));
        return result;
    }


    public Yuan[] divideAndRemainder(Yuan divisor, MathContext mc) {
        if (mc.getPrecision() == 0)
            return divideAndRemainder(divisor);

        Yuan[] result = new Yuan[2];
        Yuan lhs = this;

        result[0] = lhs.divideToIntegralValue(divisor, mc);
        result[1] = lhs.subtract(result[0].multiply(divisor));
        return result;
    }

    
    public Yuan pow(int n) {
        return new Yuan(this.value.pow(n));
    }

    
    public Yuan pow(int n, MathContext mc) {
        return new Yuan(this.value.pow(n, mc));
    }

    
    public Yuan abs(MathContext mc) {
        return new Yuan(this.value.abs(mc));
    }

    
    public Yuan negate() {
        return new Yuan(this.value.negate());
    }

    
    public Yuan negate(MathContext mc) {
        return new Yuan(this.value.negate(mc));
    }

    
    public Yuan plus() {
        return new Yuan(this.value.plus());
    }

    
    public Yuan plus(MathContext mc) {
        return new Yuan(this.value.plus(mc));
    }

    
    public int signum() {
        return this.value.signum();
    }

    
    public int scale() {
        return this.value.scale();
    }

    
    public int precision() {
        return this.value.precision();
    }

    
    public BigInteger unscaledValue() {
        return this.value.unscaledValue();
    }

    
    public Yuan round(MathContext mc) {
        return new Yuan(this.value.round(mc));
    }

    
    public Yuan setScale(int newScale, RoundingMode roundingMode) {
        return new Yuan(this.value.setScale(newScale, roundingMode));
    }

    
    public Yuan setScale(int newScale, int roundingMode) {
        return new Yuan(this.value.setScale(newScale, roundingMode));
    }

    
    public Yuan setScale(int newScale) {
        return new Yuan(this.value.setScale(newScale));
    }

    
    public Yuan movePointLeft(int n) {
        return new Yuan(this.value.movePointLeft(n));
    }

    
    public Yuan movePointRight(int n) {
        return new Yuan(this.value.movePointRight(n));
    }

    
    public Yuan scaleByPowerOfTen(int n) {
        return new Yuan(this.value.scaleByPowerOfTen(n));
    }

    
    public Yuan stripTrailingZeros() {
        return new Yuan(this.value.stripTrailingZeros());
    }

    
    public int compareTo(Yuan val) {
        return this.value.compareTo(val.value);
    }

    
    public boolean equals(Object x) {
        return this.value.equals(x);
    }

    
    public Yuan min(Yuan val) {
        return new Yuan(this.value.min(val.value));
    }

    
    public Yuan max(Yuan val) {
        return new Yuan(this.value.max(val.value));
    }

    
    public int hashCode() {
        return this.value.hashCode();
    }

    
    public String toString() {
        return this.value.toString();
    }

    
    public String toEngineeringString() {
        return this.value.toEngineeringString();
    }

    
    public String toPlainString() {
        return this.value.toPlainString();
    }

    
    public BigInteger toBigInteger() {
        return this.value.toBigInteger();
    }

    
    public BigInteger toBigIntegerExact() {
        return this.value.toBigIntegerExact();
    }

    
    public long longValue() {
        return this.value.longValue();
    }

    
    public long longValueExact() {
        return this.value.longValueExact();
    }

    
    public int intValue() {
        return this.value.intValue();
    }

    
    public int intValueExact() {
        return this.value.intValueExact();
    }

    
    public short shortValueExact() {
        return this.value.shortValueExact();
    }

    
    public byte byteValueExact() {
        return this.value.byteValueExact();
    }

    
    public float floatValue() {
        return this.value.floatValue();
    }

    
    public double doubleValue() {
        return this.value.doubleValue();
    }

    
    public Yuan ulp() {
        return new Yuan(this.value.ulp());
    }

    
    public Yuan abs() {
        return new Yuan(this.value.abs());
    }



    //////////custom methods
    public BigDecimal getValue() {
        return value;
    }
}
