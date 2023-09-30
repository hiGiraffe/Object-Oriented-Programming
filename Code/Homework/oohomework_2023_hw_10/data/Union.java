package data;

import java.util.HashMap;

public class Union {
    private HashMap<Integer, Integer> parents;
    private HashMap<Integer, Integer> weight;

    public Union() {
        parents = new HashMap<>();
        weight = new HashMap<>();
    }

    public void addPerson(int id) {
        parents.put(id, id);
        weight.put(id, 1);
    }

    public int find(int id) {
        int idTmp = id;
        while (idTmp != parents.get(idTmp)) {
            parents.put(idTmp, parents.get(parents.get(idTmp)));
            idTmp = parents.get(idTmp);
        }
        return idTmp;
    }

    public boolean isUnion(int id1, int id2) {
        return find(id1) == find(id2);
    }

    public void unionPerson(int id1, int id2) {
        int father1 = find(id1);
        int father2 = find(id2);
        if (weight.get(father1) > weight.get(father2)) {
            parents.put(father2, father1);
            weight.put(father1, weight.get(father1) + weight.get(father2));
        } else {
            parents.put(father1, father2);
            weight.put(father2, weight.get(father1) + weight.get(father2));
        }
    }

}
