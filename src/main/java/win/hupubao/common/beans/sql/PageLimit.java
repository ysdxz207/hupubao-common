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

package win.hupubao.common.beans.sql;

/**
 * 分页工具
 */
public class PageLimit {
    private int pageSize = 10;
    private int pageCurrent = 1;

    public PageLimit() {
    }

    public PageLimit(int pageSize, int pageCurrent) {
        this.pageSize = pageSize;
        this.pageCurrent = pageCurrent;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageCurrent() {
        return pageCurrent;
    }

    public void setPageCurrent(int pageCurrent) {
        this.pageCurrent = pageCurrent;
    }

    // custom methods
    public int calculateOffet() {
        return this.pageSize * (this.pageCurrent - 1);
    }

    public int calculateLimit() {
        return this.pageSize;
    }

    public RowBounds getRowBounds() {
        return new RowBounds(calculateOffet(), calculateLimit());
    }
}
