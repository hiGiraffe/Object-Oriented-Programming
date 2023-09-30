package data;

import java.util.HashMap;

public class OkTest {

    public OkTest() {
    }

    public int modifyRelationOkTest(int id1, int id2, int value, HashMap<Integer,
            HashMap<Integer, Integer>> beforeData, HashMap<Integer,
            HashMap<Integer, Integer>> afterData) {
        if (beforeData.containsKey(id1) && beforeData.containsKey(id2)
                && id1 != id2
                && beforeData.get(id1).containsKey(id2)
                && queryValue(id1, id2, beforeData) + value > 0) {
            return modifyRelationOkTestType0(id1, id2, value, beforeData, afterData);
        } else if (beforeData.containsKey(id1) && beforeData.containsKey(id2)
                && id1 != id2
                && beforeData.get(id1).containsKey(id2)
                && queryValue(id1, id2, beforeData) + value <= 0) {
            return modifyRelationOkTestType1(id1, id2, value, beforeData, afterData);
        } else {
            if (beforeData.equals(afterData)) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private int modifyRelationOkTestType0(int id1, int id2, int value, HashMap<Integer,
            HashMap<Integer, Integer>> beforeData, HashMap<Integer,
            HashMap<Integer, Integer>> afterData) {
        if (beforeData.size() != afterData.size()) {
            return 1;
        } else if (!checkMrok2(beforeData, afterData)) {
            return 2;
        } else if (!checkMrok3(id1, id2, beforeData, afterData)) {
            return 3;
        } else if (!afterData.get(id1).containsKey(id2)
                || !afterData.get(id2).containsKey(id1)) {
            return 4;
        } else if (queryValue(id1, id2, afterData)
                != queryValue(id1, id2, beforeData) + value) {
            return 5;
        } else if (queryValue(id2, id1, afterData)
                != queryValue(id2, id1, beforeData) + value) {
            return 6;
        } else if (queryAcquaintanceLength(id1, afterData)
                != queryAcquaintanceLength(id1, beforeData)) {
            return 7;
        } else if (queryAcquaintanceLength(id2, afterData)
                != queryAcquaintanceLength(id2, beforeData)) {
            return 8;
        } else if (!queryAcquaintanceSame(id1, beforeData, afterData)) {
            return 9;
        } else if (!queryAcquaintanceSame(id2, beforeData, afterData)) {
            return 10;
        } else if (!queryValueSameBesidesId(id1, id2, beforeData, afterData)) {
            return 11;
        } else if (!queryValueSameBesidesId(id2, id1, beforeData, afterData)) {
            return 12;
        }
        return 0;
    }

    private int modifyRelationOkTestType1(int id1, int id2, int value, HashMap<Integer,
            HashMap<Integer, Integer>> beforeData, HashMap<Integer,
            HashMap<Integer, Integer>> afterData) {
        if (beforeData.size() != afterData.size()) {
            return 1;
        } else if (!checkMrok2(beforeData, afterData)) {
            return 2;
        } else if (!checkMrok3(id1, id2, beforeData, afterData)) {
            return 3;
        } else if (afterData.get(id1).containsKey(id2) || afterData.get(id2).containsKey(id1)) {
            return 15;
        } else if (beforeData.get(id1).size() != afterData.get(id1).size() + 1) {
            return 16;
        } else if (beforeData.get(id2).size() != afterData.get(id2).size() + 1) {
            return 17;
        } else if (!queryValueSameToAfterData(id1, beforeData, afterData)) {
            return 20;
        } else if (!queryValueSameToAfterData(id2, beforeData, afterData)) {
            return 21;
        }
        return 0;
    }

    private int queryValue(int id1, int id2,
                           HashMap<Integer, HashMap<Integer, Integer>> data) {
        return data.get(id1).get(id2);
    }

    private int queryAcquaintanceLength(int id, HashMap<Integer, HashMap<Integer, Integer>> data) {
        return data.get(id).size();
    }

    private boolean queryAcquaintanceSame(int id,
                                          HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                          HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        if (afterData.get(id).keySet().equals(beforeData.get(id).keySet())) {
            return true;
        }
        return false;
    }

    private boolean queryValueSameBesidesId(int idFirst, int idBesides,
                                            HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                            HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        for (Integer id : afterData.get(idFirst).keySet()) {
            if (id == idBesides) {
                continue;
            }
            if (!afterData.get(idFirst).get(id).equals(beforeData.get(idFirst).get(id))) {
                return false;
            }
        }
        return true;
    }

    private boolean queryValueSameToAfterData(int id, HashMap<Integer,
            HashMap<Integer, Integer>> beforeData, HashMap<Integer,
            HashMap<Integer, Integer>> afterData) {
        for (Integer i : afterData.get(id).keySet()) {
            if (!afterData.get(id).get(i).equals(beforeData.get(id).get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean checkMrok2(HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                               HashMap<Integer, HashMap<Integer, Integer>> afterData) {
        for (Integer peoplei : beforeData.keySet()) {
            int flag = 0;
            for (Integer peoplej : afterData.keySet()) {
                if (peoplei == peoplej) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean checkMrok3(int id1, int id2, HashMap<Integer,
            HashMap<Integer, Integer>> beforeData, HashMap<Integer,
            HashMap<Integer, Integer>> afterData) {

        for (Integer id : beforeData.keySet()) {
            if (id == id1 || id == id2) {
                continue;
            }
            if (!beforeData.get(id).equals(afterData.get(id))) {
                return false;
            }
        }
        return true;
    }
}


