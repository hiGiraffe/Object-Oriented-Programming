package data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class OkTest {

    public OkTest() {
    }

    public int deleteColdEmojiOkTest(int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
                                     ArrayList<HashMap<Integer, Integer>> afterData, int result) {
        if (!checkCeOk1(limit, beforeData, afterData)) {
            return 1;
        } else if (!checkCeOk2(beforeData, afterData)) {
            return 2;

        } else if (!checkCeOk3(limit, beforeData, afterData)) {
            return 3;
        }
        //else if(!checkCeOk4())
        else if (!checkCeOk5(beforeData, afterData)) {
            return 5;
        } else if (!checkCeOk6(beforeData, afterData)) {
            return 6;
        } else if (!checkCeOk7(beforeData, afterData)) {
            return 7;
        }
        if (result != afterData.get(0).size()) {
            return 8;
        }
        return 0;
    }

    private boolean checkCeOk1(int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
                               ArrayList<HashMap<Integer, Integer>> afterData) {
        for (Integer emojiId : beforeData.get(0).keySet()) {
            if (beforeData.get(0).get(emojiId) >= limit) {
                if (!afterData.get(0).containsKey(emojiId)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkCeOk2(ArrayList<HashMap<Integer, Integer>> beforeData,
                               ArrayList<HashMap<Integer, Integer>> afterData) {
        for (Integer emojiId : afterData.get(0).keySet()) {
            if (!beforeData.get(0).containsKey(emojiId)
                    || !beforeData.get(0).get(emojiId).equals(afterData.get(0).get(emojiId))) {
                return false;
            }
        }
        return true;
    }

    private boolean checkCeOk3(int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
                               ArrayList<HashMap<Integer, Integer>> afterData) {
        int num = 0;
        for (Integer emojiId : beforeData.get(0).keySet()) {
            if (beforeData.get(0).get(emojiId) >= limit) {
                num++;
            }
        }
        return num == afterData.get(0).size();
    }

    private boolean checkCeOk5(ArrayList<HashMap<Integer, Integer>> beforeData,
                               ArrayList<HashMap<Integer, Integer>> afterData) {
        for (Integer messageId : beforeData.get(1).keySet()) {
            if (beforeData.get(1).get(messageId) != null
                    && afterData.get(0).containsKey(beforeData.get(1).get(messageId))) {
                if (!afterData.get(1).containsKey(messageId)
                        || !afterData.get(1).get(messageId).
                        equals(beforeData.get(1).get(messageId))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkCeOk6(ArrayList<HashMap<Integer, Integer>> beforeData,
                               ArrayList<HashMap<Integer, Integer>> afterData) {
        for (Integer messageId : beforeData.get(1).keySet()) {
            if (beforeData.get(1).get(messageId) == null) {
                if (!afterData.get(1).containsKey(messageId)
                        || !Objects.equals(afterData.get(1).get(messageId),
                        beforeData.get(1).get(messageId))) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkCeOk7(ArrayList<HashMap<Integer, Integer>> beforeData,
                               ArrayList<HashMap<Integer, Integer>> afterData) {
        int num = 0;
        for (Integer messageId : beforeData.get(1).keySet()) {
            if (beforeData.get(1).get(messageId) != null) {
                if (!afterData.get(0).containsKey(beforeData.get(1).get(messageId))) {
                    continue;
                }
            }
            num++;
        }
        //System.out.println(num);
        return afterData.get(1).size() == num;
    }
}


