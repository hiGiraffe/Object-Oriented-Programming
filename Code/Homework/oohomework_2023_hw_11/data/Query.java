package data;

import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.main.Person;
import exceptions.MyAcquaintanceNotFoundException;
import exceptions.MyPathNotFoundException;
import exceptions.MyPersonIdNotFoundException;
import exceptions.MyRelationNotFoundException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class Query {
    private final ArrayList<Person> people;

    public Query(ArrayList<Person> people) {
        this.people = people;
    }

    public Person getPerson(int id) {
        for (Person person : people) {
            if (person.getId() == id) {
                return person;
            }
        }
        return null;
    }

    public boolean contains(int id) {
        for (Person person : people) {
            if (person.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private void dijkstraWhile(HashMap<Integer, Boolean> vis,
                               HashMap<Integer, Integer> dis,
                               PriorityQueue<Integer> priorityQueue,
                               HashMap<Integer, Integer> roots) {
        while (!priorityQueue.isEmpty()) {
            Integer index = priorityQueue.poll();
            if (!vis.get(index)) {
                int min = dis.get(index);
                vis.put(index, true);
                for (Person person : ((MyPerson) this.getPerson(index)).getAcquaintance()) {
                    int tmp = min + this.getPerson(index).queryValue(person);
                    if (tmp < dis.get(person.getId())) {
                        dis.put(person.getId(), tmp);
                        roots.put(person.getId(), roots.get(index));
                        priorityQueue.remove(person.getId());
                        priorityQueue.offer(person.getId());
                    }
                }
            }
        }
    }

    private void dijkstraInit(int id, HashMap<Integer, Boolean> vis,
                              HashMap<Integer, Integer> dis,
                              PriorityQueue<Integer> priorityQueue,
                              HashMap<Integer, Integer> roots) {
        for (Person person : people) {
            if (person.getId() == id) {
                vis.put(person.getId(), true);
                dis.put(person.getId(), 0);
            } else {
                if (this.getPerson(id).isLinked(person)) {
                    dis.put(person.getId(), this.getPerson(id).queryValue(person));
                    priorityQueue.offer(person.getId());
                    roots.put(person.getId(), person.getId());
                } else {
                    dis.put(person.getId(), Integer.MAX_VALUE);
                }
                vis.put(person.getId(), false);
            }
        }
    }

    private void dijkstra(int id,
                          HashMap<Integer, Integer> distances,
                          HashMap<Integer, Integer> roots) {
        //优先队列
        Comparator<Integer> comparator = Comparator.comparingInt(distances::get);
        PriorityQueue<Integer> priorityQueue = new PriorityQueue<>(comparator);
        HashMap<Integer, Boolean> vis = new HashMap<>();
        dijkstraInit(id, vis, distances, priorityQueue, roots);
        dijkstraWhile(vis, distances, priorityQueue, roots);
    }

    public int queryValue(int id1, int id2) throws
            PersonIdNotFoundException, RelationNotFoundException {
        MyPerson person1 = (MyPerson) getPerson(id1);
        MyPerson person2 = (MyPerson) getPerson(id2);
        if (person1 == null) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (person2 == null) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (!person1.isLinked(person2)) {
            throw new MyRelationNotFoundException(id1, id2);
        } else {
            return person1.queryValue(person2);
        }

    }

    public int queryBestAcquaintance(int id) throws
            PersonIdNotFoundException, AcquaintanceNotFoundException {
        MyPerson person = (MyPerson) getPerson(id);
        if (person == null) {
            throw new MyPersonIdNotFoundException(id);
        } else if (person.getAcquaintanceSize() == 0) {
            throw new MyAcquaintanceNotFoundException(id);
        } else {
            return person.queryBestAcquaintance();
        }
    }

    public int queryCoupleSum() {
        int sum = 0;
        for (Person person : people) {
            if (((MyPerson) person).isAcquaintanceEmpty()) {
                continue;
            }
            int idi;
            try {
                idi = this.queryBestAcquaintance(person.getId());
            } catch (PersonIdNotFoundException e) {
                throw new RuntimeException(e);
            } catch (AcquaintanceNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (getPerson(idi) == null
                    || ((MyPerson) getPerson(idi)).isAcquaintanceEmpty()
                    || idi > person.getId()) {
                continue;
            }
            int idj;
            try {
                idj = this.queryBestAcquaintance(idi);
            } catch (PersonIdNotFoundException e) {
                throw new RuntimeException(e);
            } catch (AcquaintanceNotFoundException e) {
                throw new RuntimeException(e);
            }
            if (idj == person.getId()) {
                sum++;
            }
        }
        return sum;
    }

    public int queryLeastMoments(int id)
            throws PersonIdNotFoundException, PathNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            int min = Integer.MAX_VALUE;
            boolean flag = false;
            //距离信息
            HashMap<Integer, Integer> distances = new HashMap<>();
            //根节点
            HashMap<Integer, Integer> roots = new HashMap<>();
            this.dijkstra(id, distances, roots);

            //讨论区
            for (Integer rootId : roots.keySet()) {
                for (Person person : ((MyPerson) getPerson(rootId)).getAcquaintance()) {
                    if (roots.containsKey(person.getId())
                            && !roots.get(rootId).equals(roots.get(person.getId()))) {
                        int tmp = getPerson(rootId).queryValue(person)
                                + distances.get(rootId)
                                + distances.get(person.getId());
                        if (min > tmp) {
                            flag = true;
                            min = tmp;
                        }
                    }
                }
            }

            for (Person person : ((MyPerson) getPerson(id)).getAcquaintance()) {
                if (!roots.get(person.getId()).equals(person.getId())) {
                    int tmp = person.queryValue(getPerson(id)) + distances.get(person.getId());
                    if (min > tmp) {
                        flag = true;
                        min = tmp;
                    }
                }
            }
            if (flag == false) {
                throw new MyPathNotFoundException(id);
            }
            return min;
        }
    }
}
