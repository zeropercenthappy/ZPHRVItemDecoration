package com.zeropercenthappy.decorationlibrary;

/**
 * @author ybq
 * @date 2018/1/5
 */

public class DecorationUtils {
    public static boolean isLastRow(int position, int spanCount, int total) {
        int lastRowFirstColumn =
                total - (total % spanCount == 0 ? spanCount : total % spanCount) + 1;
        return position >= lastRowFirstColumn;
    }

    public static boolean isLastColumn(int position, int spanCount, int total) {
        if (total < spanCount) {
            spanCount = total;
        }
        return position % spanCount == 0;
    }
}
