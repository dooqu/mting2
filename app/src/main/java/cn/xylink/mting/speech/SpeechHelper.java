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


    public static List<String> prepareTextFragments(String textBody, int singleFragmentMaxSize, boolean tryAloneFragment) {
        List<String> fragments = prepareTextFragments(textBody, tryAloneFragment);
        if(tryAloneFragment) {
            return fragments;
        }
        List<String> fragmentsGenerated = new ArrayList<>();
        int newFragmentSizeTotal = 0;
        //使用stringbuilder进行缓冲
        StringBuilder newFragmentText = new StringBuilder();
        for(int index = 0, size = fragments.size(); index < size; ++index) {
            int currFragSize = fragments.get(index).length();
            //如果当前缓冲区的剩余空间不可以放下当前的segment
            if(newFragmentSizeTotal + currFragSize >= singleFragmentMaxSize && newFragmentSizeTotal != 0) {
                //把目前缓冲区内的送进list
                fragmentsGenerated.add(newFragmentText.toString());
                //清空缓冲区
                newFragmentText.delete(0, newFragmentText.length());
                newFragmentSizeTotal = 0;
            }
            newFragmentSizeTotal += currFragSize;
            newFragmentText.append(fragments.get(index));

            if(fragments.get(index).endsWith("\n")) {
                fragmentsGenerated.add(newFragmentText.toString());
                newFragmentText.delete(0, newFragmentText.length());
                newFragmentSizeTotal = 0;
                continue;
            }

            if(index == (size - 1)) {
                fragmentsGenerated.add(newFragmentText.toString());
            }
        }
        return fragmentsGenerated;
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
