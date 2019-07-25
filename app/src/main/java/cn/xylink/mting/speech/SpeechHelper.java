package cn.xylink.mting.speech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class SpeechHelper {

    static HashSet<Character> SplitSymbols = new HashSet<>();

    static {
        SplitSymbols.add('，');
        SplitSymbols.add('。');
        SplitSymbols.add('？');
        SplitSymbols.add('！');
        SplitSymbols.add('；');
        SplitSymbols.add('\n');
        SplitSymbols.add(',');
        SplitSymbols.add(';');
        SplitSymbols.add('!');
        SplitSymbols.add('?');
        SplitSymbols.add('.');
    }

    public static boolean isSymbol(char c) {
        return String.valueOf(c).matches("\\p{P}");
    }


    public static List<String> prepareTextFragments(String textBody, boolean tryAloneFragment) {
        List<String> textFragments = new ArrayList<>();
        if (textBody == null || textBody.trim().equals("")) {
            return textFragments;
        }
        if (tryAloneFragment) {
            try {
                if (textBody.getBytes("utf-8").length <= 99) {
                    textFragments.add(textBody);
                }
            }
            catch (IOException ex) {

            }
            return textFragments;
        }
        else {

            for (int index = 0, length = textBody.length(), fragIndex = 0, fragLength = 0; index < length; ++index, ++fragLength) {
                if (fragLength >= 100) {
                    textFragments.add(textBody.substring(fragIndex, index));
                    fragIndex = index;
                    fragLength = 0;
                    continue;
                }
                char c = textBody.charAt(index);
                if (SplitSymbols.contains(c)) {
                    if (c == '.'
                            && index - 1 > 0 && index + 1 <= length
                            && Character.isDigit(textBody.charAt(index - 1))
                            && Character.isDigit(textBody.charAt(index + 1))) {
                        continue;
                    }

                    boolean enQuoStart = false;
                    for (int i = index + 1; i < length; i++) {
                        if (SplitSymbols.contains(textBody.charAt(i)) || isSymbol(textBody.charAt(i))) {
                            index = i;
                            continue;
                        }
                        break;
                    }

                    textFragments.add(textBody.substring(fragIndex, index + 1));
                    fragIndex = index + 1;
                    fragLength = 0;
                    continue;
                }

                if (index == length - 1) {
                    textFragments.add(textBody.substring(fragIndex, index + 1));
                }
            }
            return textFragments;
        }
    }

    public int seekFragmentIndex(float seekPercentage, List<String> fragments) {
        if (fragments == null || fragments.size() == 0) {
            return -1;
        }
        if (seekPercentage >= 1.0f) {
            seekPercentage = 1.0f;
        }
        if (seekPercentage < 0) {
            seekPercentage = 0;
        }
        return (int) Math.ceil((double) (seekPercentage * (fragments.size() - 1)));
    }
}
