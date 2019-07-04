package cn.xylink.mting.speech;

import java.util.ArrayList;
import java.util.List;

public class SpeechHelper {

    public List<String> prepare(String textBody)
    {
        List<String> fragments = new ArrayList<>();

        String[] strArr = textBody.split("[\n。，！？；.,!?]");

        for(int i = 0,j = strArr.length; i < j; i++)
        {
            if(strArr[i].trim() == "")
                continue;

            fragments.add(strArr[i]);
        }

        return fragments;
    }

    public int seekFragmentIndex(float seekPercentage, List<String> fragments)
    {
        if(fragments == null || fragments.size() == 0)
        {
            return -1;
        }

        if(seekPercentage >= 1.0f)
        {
            seekPercentage = 1.0f;
        }

        if(seekPercentage < 0)
        {
            seekPercentage = 0;
        }

        return (int)Math.ceil((double)(seekPercentage * (fragments.size() - 1)));

    }
}
