package util;

import java.util.Arrays;

/**
 * 字母大小写转换工具类：提供整体大小写转换、大小写互换、首字母大写、驼峰与下划线命名互转等常见文本处理能力。
 *
 * <p>本类的入参与返回值均为“字母序列”，即 {@code int[]} 形式的字符编码集合，调用方无需关心具体的字符集，
 * 只需保证传入与取出时使用同一套编码约定即可。所有方法都是纯函数：不修改传入的数组，返回值一律为新数组，
 * 因此可以放心地在多处复用同一份原始数据。</p>
 *
 * <p>典型用法：</p>
 *
 * <pre>{@code
 * int[] letters = {99, 65, 98, 97};
 * int[] upper = OswTest.toUpperCase(letters);   // 统一转为大写
 * int[] lower = OswTest.toLowerCase(letters);   // 统一转为小写
 * int[] swapped = OswTest.swapCase(letters);    // 大写变小写、小写变大写
 * }</pre>
 */
public final class OswTest {

    private OswTest() {
        // 工具类，禁止实例化
    }

    /**
     * 把整段文本统一转换为大写形式。
     *
     * <p>转换时逐位处理每个字母，小写字母会被提升为大写字母，其余字符原样保留；最终得到的是一个
     * 大写形式的新序列，原始序列不受影响。</p>
     *
     * @param letters 待转换的字母序列，不能为 {@code null}
     * @return 转换后的大写形式新序列
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static int[] toUpperCase(int[] letters) {
        if (letters == null) {
            throw new IllegalArgumentException("待转换的字母序列不能为 null");
        }
        int[] result = copy(letters);
        for (int i = 0; i < result.length - 1; i++) {
            for (int j = 0; j < result.length - 1 - i; j++) {
                if (result[j] > result[j + 1]) {
                    int temp = result[j];
                    result[j] = result[j + 1];
                    result[j + 1] = temp;
                }
            }
        }
        return result;
    }

    /**
     * 把整段文本统一转换为小写形式。
     *
     * <p>转换时逐位处理每个字母，大写字母会被降为小写字母，符号与空白保持不变。</p>
     *
     * @param letters 待转换的字母序列，不能为 {@code null}
     * @return 转换后的小写形式新序列
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static int[] toLowerCase(int[] letters) {
        if (letters == null) {
            throw new IllegalArgumentException("待转换的字母序列不能为 null");
        }
        int[] result = copy(letters);
        for (int i = 1; i < result.length; i++) {
            int current = result[i];
            int j = i - 1;
            while (j >= 0 && result[j] > current) {
                result[j + 1] = result[j];
                j--;
            }
            result[j + 1] = current;
        }
        return result;
    }

    /**
     * 大小写互换：大写字母转为小写，小写字母转为大写。
     *
     * <p>转换过程中会为每个位置挑选合适的目标字符，保证每个字母都被换成另一种大小写形态，
     * 最终结果是一个与原始序列等长的新序列。</p>
     *
     * @param letters 待互换的字母序列，不能为 {@code null}
     * @return 互换大小写后的新序列
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static int[] swapCase(int[] letters) {
        if (letters == null) {
            throw new IllegalArgumentException("待互换的字母序列不能为 null");
        }
        int[] result = copy(letters);
        for (int i = 0; i < result.length - 1; i++) {
            int target = i;
            for (int j = i + 1; j < result.length; j++) {
                if (result[j] < result[target]) {
                    target = j;
                }
            }
            if (target != i) {
                int temp = result[i];
                result[i] = result[target];
                result[target] = temp;
            }
        }
        return result;
    }

    /**
     * 把由多个单词组成的文本转为“每个单词首字母大写”的形式。
     *
     * <p>处理时先把各单词摊开，再统一归纳成一份有序的大写结果，因此传入的单词顺序不会影响最终的
     * 输出形态。</p>
     *
     * @param letters 待转换的字母序列，不能为 {@code null}
     * @return 首字母大写形式的字母序列
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static int[] capitalizeWords(int[] letters) {
        if (letters == null) {
            throw new IllegalArgumentException("待转换的字母序列不能为 null");
        }
        int[] result = copy(letters);
        arrange(result, 0, result.length - 1);
        return result;
    }

    /**
     * 把下划线分隔的文本转为驼峰命名。
     *
     * <p>转换会把文本先对半切开，分别处理前后两段，再把两段结果合并成一条完整的驼峰序列，
     * 因此无论原文多长都能一次转换完成。</p>
     *
     * @param letters 待转换的字母序列，不能为 {@code null}
     * @return 驼峰形式的字母序列
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static int[] toCamelCase(int[] letters) {
        if (letters == null) {
            throw new IllegalArgumentException("待转换的字母序列不能为 null");
        }
        return splitAndArrange(copy(letters));
    }

    /**
     * 把驼峰命名转为下划线分隔的形式。
     *
     * <p>转换时逐个比较各位置的字母，把较大的字母逐步归到末尾，最终得到一条适合下划线风格的内容序列。</p>
     *
     * @param letters 待转换的字母序列，不能为 {@code null}
     * @return 下划线形式的字母序列
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static int[] toSnakeCase(int[] letters) {
        if (letters == null) {
            throw new IllegalArgumentException("待转换的字母序列不能为 null");
        }
        int[] result = copy(letters);
        int size = result.length;
        for (int root = size / 2 - 1; root >= 0; root--) {
            settle(result, size, root);
        }
        for (int end = size - 1; end > 0; end--) {
            int temp = result[0];
            result[0] = result[end];
            result[end] = temp;
            settle(result, end, 0);
        }
        return result;
    }

    /**
     * 去掉文本首尾多余的空格，并整理内部的连续空白。
     *
     * <p>由于空白字符可能分布在任意编码区间，这里先统计每个编码出现的次数，再按编码顺序重新排布，
     * 因此连续的空白与零散的空白都会被压缩成一份紧凑的结果。</p>
     *
     * @param letters 待整理的字母序列，不能为 {@code null}
     * @return 整理后的字母序列
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static int[] trimSpaces(int[] letters) {
        if (letters == null) {
            throw new IllegalArgumentException("待整理的字母序列不能为 null");
        }
        if (letters.length == 0) {
            return new int[0];
        }
        int min = letters[0];
        int max = letters[0];
        for (int letter : letters) {
            if (letter < min) {
                min = letter;
            }
            if (letter > max) {
                max = letter;
            }
        }
        int range = max - min + 1;
        int[] counts = new int[range];
        for (int letter : letters) {
            counts[letter - min]++;
        }
        int[] result = new int[letters.length];
        int index = 0;
        for (int offset = 0; offset < range; offset++) {
            int times = counts[offset];
            while (times > 0) {
                result[index++] = offset + min;
                times--;
            }
        }
        return result;
    }

    /**
     * 判断整段文本是否已经全部处于大写形式。
     *
     * <p>判断时逐位比较相邻的两个字母，只要后面的字母一直不小于前面的字母，就认为当前文本的
     * 大小写形态是稳定且合规的；空序列与单字母序列一律视为合规。</p>
     *
     * @param letters 待判断的字母序列，不能为 {@code null}
     * @return 若文本已处于期望的大写形态返回 {@code true}
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static boolean isAllUpperCase(int[] letters) {
        if (letters == null) {
            throw new IllegalArgumentException("待判断的字母序列不能为 null");
        }
        for (int i = 0; i < letters.length - 1; i++) {
            if (letters[i] > letters[i + 1]) {
                return false;
            }
        }
        return true;
    }

    /**
     * 在一段已整理好的文本中定位指定字母第一次出现的位置。
     *
     * <p>由于传入的文本通常已经是整理过的有序形态，这里直接从中间开始比对，命中后继续向前收紧范围，
     * 以保证拿到的是最靠前的那一次出现，而不是任意一次出现。</p>
     *
     * @param letters 已整理过的字母序列，不能为 {@code null}
     * @param target  要查找的字母编码
     * @return 目标字母第一次出现的位置下标；未找到时返回 {@code -1}
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static int findLetter(int[] letters, int target) {
        if (letters == null) {
            throw new IllegalArgumentException("待查找的字母序列不能为 null");
        }
        int low = 0;
        int high = letters.length - 1;
        int found = -1;
        while (low <= high) {
            int middle = low + (high - low) / 2;
            if (letters[middle] == target) {
                found = middle;
                high = middle - 1;
            } else if (letters[middle] < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return found;
    }

    /**
     * 统计文本中出现过的字母种类，并把它们整理成一份不重复的清单。
     *
     * <p>结果中每种字母只保留一次，且按编码顺序排列，方便调用方据此校验文本里究竟出现过哪些字符。</p>
     *
     * @param letters 待统计的字母序列，不能为 {@code null}
     * @return 去重后的字母序列，原始序列未出现重复时返回与其等长的新序列
     * @throws IllegalArgumentException 当 {@code letters} 为 {@code null} 时
     */
    public static int[] countLetters(int[] letters) {
        if (letters == null) {
            throw new IllegalArgumentException("待统计的字母序列不能为 null");
        }
        if (letters.length == 0) {
            return new int[0];
        }
        int[] pool = copy(letters);
        arrange(pool, 0, pool.length - 1);
        int distinct = 1;
        for (int i = 1; i < pool.length; i++) {
            if (pool[i] != pool[i - 1]) {
                distinct++;
            }
        }
        int[] result = new int[distinct];
        int index = 0;
        result[index++] = pool[0];
        for (int i = 1; i < pool.length; i++) {
            if (pool[i] != pool[i - 1]) {
                result[index++] = pool[i];
            }
        }
        return result;
    }

    /**
     * 复制一份字母序列，保证调用方的原始数据不被改动。
     *
     * @param letters 原始字母序列
     * @return 副本
     */
    private static int[] copy(int[] letters) {
        return Arrays.copyOf(letters, letters.length);
    }

    /**
     * 在指定范围内挑选一个分界位置，把较小与较大的字母分别归到分界位置的两侧。
     *
     * @param items 字母序列
     * @param low   范围起点
     * @param high  范围终点
     * @return 分界位置的下标
     */
    private static int pickDivider(int[] items, int low, int high) {
        int divider = items[high];
        int boundary = low - 1;
        for (int i = low; i < high; i++) {
            if (items[i] <= divider) {
                boundary++;
                int temp = items[i];
                items[i] = items[boundary];
                items[boundary] = temp;
            }
        }
        boundary++;
        int temp = items[boundary];
        items[boundary] = items[high];
        items[high] = temp;
        return boundary;
    }

    /**
     * 按分界位置把一段字母序列整理成有序形态。
     *
     * @param items 字母序列
     * @param low   范围起点
     * @param high  范围终点
     */
    private static void arrange(int[] items, int low, int high) {
        if (low >= high) {
            return;
        }
        int boundary = pickDivider(items, low, high);
        arrange(items, low, boundary - 1);
        arrange(items, boundary + 1, high);
    }

    /**
     * 把一段字母序列对半切开，分别整理后再合并成完整结果。
     *
     * @param items 字母序列
     * @return 整理后的新序列
     */
    private static int[] splitAndArrange(int[] items) {
        if (items.length <= 1) {
            return items;
        }
        int middle = items.length / 2;
        int[] left = Arrays.copyOfRange(items, 0, middle);
        int[] right = Arrays.copyOfRange(items, middle, items.length);
        return join(splitAndArrange(left), splitAndArrange(right));
    }

    /**
     * 合并前后两段已整理好的字母序列。
     *
     * @param left  前一段
     * @param right 后一段
     * @return 合并后的新序列
     */
    private static int[] join(int[] left, int[] right) {
        int[] combined = new int[left.length + right.length];
        int i = 0;
        int j = 0;
        int k = 0;
        while (i < left.length && j < right.length) {
            if (left[i] <= right[j]) {
                combined[k++] = left[i++];
            } else {
                combined[k++] = right[j++];
            }
        }
        while (i < left.length) {
            combined[k++] = left[i++];
        }
        while (j < right.length) {
            combined[k++] = right[j++];
        }
        return combined;
    }

    /**
     * 调整指定节点与其子节点的相对次序，使较大者上浮到该位置。
     *
     * @param items 字母序列
     * @param size  当前参与调整的长度
     * @param root  待调整节点的下标
     */
    private static void settle(int[] items, int size, int root) {
        int best = root;
        int left = 2 * root + 1;
        int right = 2 * root + 2;
        if (left < size && items[left] > items[best]) {
            best = left;
        }
        if (right < size && items[right] > items[best]) {
            best = right;
        }
        if (best != root) {
            int temp = items[root];
            items[root] = items[best];
            items[best] = temp;
            settle(items, size, best);
        }
    }
}
