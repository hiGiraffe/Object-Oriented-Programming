package data;

import com.oocourse.spec2.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec2.exceptions.EqualGroupIdException;
import com.oocourse.spec2.exceptions.EqualMessageIdException;
import com.oocourse.spec2.exceptions.EqualPersonIdException;
import com.oocourse.spec2.exceptions.EqualRelationException;
import com.oocourse.spec2.exceptions.GroupIdNotFoundException;
import com.oocourse.spec2.exceptions.MessageIdNotFoundException;
import com.oocourse.spec2.exceptions.PersonIdNotFoundException;
import com.oocourse.spec2.exceptions.RelationNotFoundException;
import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Message;
import com.oocourse.spec2.main.Network;
import com.oocourse.spec2.main.Person;
import exceptions.MyAcquaintanceNotFoundException;
import exceptions.MyEqualGroupIdException;
import exceptions.MyEqualMessageIdException;
import exceptions.MyEqualPersonIdException;
import exceptions.MyEqualRelationException;
import exceptions.MyGroupIdNotFoundException;
import exceptions.MyMessageIdNotFoundException;
import exceptions.MyPersonIdNotFoundException;
import exceptions.MyRelationNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyNetwork implements Network {
    private final ArrayList<Person> people;
    private final ArrayList<Message> messages;
    private final ArrayList<Group> groups;
    private Union union;
    private int tripleSum;
    private int blockSum;

    private final ArrayList<Integer> idMin;
    private final ArrayList<Integer> idMax;

    private final OkTest okTest;

    public MyNetwork() {
        people = new ArrayList<>();
        messages = new ArrayList<>();
        groups = new ArrayList<>();
        union = new Union();
        tripleSum = 0;
        blockSum = 0;

        idMin = new ArrayList<>();
        idMax = new ArrayList<>();

        okTest = new OkTest();
    }

    @Override
    public boolean contains(int id) {
        for (Person person : people) {
            if (person.getId() == id) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Person getPerson(int id) {
        for (Person person : people) {
            if (person.getId() == id) {
                return person;
            }
        }
        return null;
    }

    @Override
    public void addPerson(Person person) throws EqualPersonIdException {
        for (Person value : people) {
            if (value.equals(person)) {
                throw new MyEqualPersonIdException(person.getId());
            }
        }
        people.add(person);
        union.addPerson(person.getId());
        blockSum++;
    }

    @Override
    public void addRelation(int id1, int id2, int value) throws
            PersonIdNotFoundException, EqualRelationException {
        MyPerson person1 = (MyPerson) getPerson(id1);
        MyPerson person2 = (MyPerson) getPerson(id2);

        if (person1 == null) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (person2 == null) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (person1.isLinked(person2)) {
            throw new MyEqualRelationException(id1, id2);
        } else {
            //blockSum
            if (!union.isUnion(id1, id2)) {
                blockSum--;
            }
            //TripleSum
            if (person1.getAcquaintanceSize() < person2.getAcquaintanceSize()) {
                tripleSum = person1.addTripleSum(person2, tripleSum);
            } else {
                tripleSum = person2.addTripleSum(person1, tripleSum);
            }

            person1.addAcquaintance(person2, value);
            person2.addAcquaintance(person1, value);
            union.unionPerson(id1, id2);

            if (id1 <= id2) {
                idMin.add(id1);
                idMax.add(id2);
            } else {
                idMin.add(id2);
                idMax.add(id1);
            }

        }

    }

    @Override
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

    @Override
    public boolean isCircle(int id1, int id2) throws PersonIdNotFoundException {
        MyPerson person1 = (MyPerson) getPerson(id1);
        MyPerson person2 = (MyPerson) getPerson(id2);
        if (person1 == null) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (person2 == null) {
            throw new MyPersonIdNotFoundException(id2);
        } else {
            return union.isUnion(id1, id2);
        }
    }

    @Override
    public int queryBlockSum() {
        return blockSum;
    }

    @Override
    public int queryTripleSum() {
        return tripleSum;
    }

    public boolean queryTripleSumOkTest(HashMap<Integer, HashMap<Integer, Integer>> beforeData,
                                        HashMap<Integer, HashMap<Integer, Integer>> afterData,
                                        int result) {
        if (!beforeData.equals(afterData)) {
            return false;
        }
        MyNetwork network = new MyNetwork();

        for (Integer personid : beforeData.keySet()) {
            try {
                network.addPerson(new MyPerson(personid, "0", 0));
            } catch (EqualPersonIdException e) {
                throw new RuntimeException(e);
            }
        }
        for (Integer person1id : beforeData.keySet()) {
            for (Integer person2id : beforeData.get(person1id).keySet()) {
                if (!network.getPerson(person1id).isLinked(network.getPerson(person2id))) {
                    //System.out.println("link"+person1id+" "+person2id);
                    try {
                        network.addRelation(person1id, person2id,
                                beforeData.get(person1id).get(person2id));
                    } catch (PersonIdNotFoundException e) {
                        throw new RuntimeException(e);
                    } catch (EqualRelationException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        //System.out.println(result+" "+network.queryTripleSum());
        if (network.queryTripleSum() == result) {
            return true;
        } else {
            return false;
        }
    }

    /*getPerson(id1).queryValue(getPerson(id2)) + value > 0时
        两边的值加上value
      getPerson(id1).queryValue(getPerson(id2)) + value <= 0时
        链接关系要被删掉
        value和acquaintance要减一
     */
    @Override
    public void modifyRelation(int id1, int id2, int value) throws PersonIdNotFoundException,
            EqualPersonIdException, RelationNotFoundException {
        MyPerson person1 = (MyPerson) getPerson(id1);
        MyPerson person2 = (MyPerson) getPerson(id2);
        if (person1 == null) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (person2 == null) {
            throw new MyPersonIdNotFoundException(id2);
        } else if (id1 == id2) {
            throw new MyEqualPersonIdException(id1);
        } else if (!person1.isLinked(person2)) {
            throw new MyRelationNotFoundException(id1, id2);
        } else {
            if (person1.queryValue(person2) + value > 0) {
                person1.modifyRelation(person2, value);
                person2.modifyRelation(person1, value);
            } else {
                person1.deleteAcquaintance(person2);
                person2.deleteAcquaintance(person1);

                //删边的存储
                int idMin2 = person1.getId() <= person2.getId() ? person1.getId() : person2.getId();
                int idMax2 = person1.getId() <= person2.getId() ? person2.getId() : person1.getId();
                for (int i = 0; i < idMin.size(); i++) {
                    if (idMin.get(i) == idMin2 && idMax.get(i) == idMax2) {
                        idMin.remove(i);
                        idMax.remove(i);
                        break;
                    }
                }

                rebuildUnion();

                //blockSum
                if (!union.isUnion(person1.getId(), person2.getId())) {
                    blockSum++;
                }
                //TripleSum
                if (person1.getAcquaintanceSize() < person2.getAcquaintanceSize()) {
                    tripleSum = person1.subTripleSum(person2, tripleSum);
                } else {
                    tripleSum = person2.subTripleSum(person1, tripleSum);
                }
            }
        }

    }

    private void rebuildUnion() {
        union = new Union();
        for (Person person : people) {
            union.addPerson(person.getId());
        }
        for (int i = 0; i < idMin.size(); i++) {
            union.unionPerson(idMin.get(i), idMax.get(i));
        }
    }

    @Override
    public void addGroup(Group group) throws EqualGroupIdException {
        for (Group group1 : groups) {
            if (group1.equals(group)) {
                throw new MyEqualGroupIdException(group.getId());
            }
        }
        groups.add(group);
    }

    @Override
    public Group getGroup(int id) {
        for (Group group : groups) {
            if (group.getId() == id) {
                return group;
            }
        }
        return null;
    }

    @Override
    public void addToGroup(int id1, int id2) throws GroupIdNotFoundException,
            PersonIdNotFoundException, EqualPersonIdException {
        MyGroup group = (MyGroup) getGroup(id2);
        MyPerson person = (MyPerson) getPerson(id1);
        if (group == null) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (person == null) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (group.hasPerson(person)) {
            throw new MyEqualPersonIdException(id1);
        } else {
            if (group.getSize() <= 1111) {
                group.addPerson(person);
            }
        }
    }

    @Override
    public int queryGroupValueSum(int id) throws GroupIdNotFoundException {
        MyGroup group = (MyGroup) getGroup(id);
        if (group == null) {
            throw new MyGroupIdNotFoundException(id);
        } else {
            return group.getValueSum();
        }
    }

    @Override
    public int queryGroupAgeVar(int id) throws GroupIdNotFoundException {
        MyGroup group = (MyGroup) getGroup(id);
        if (group == null) {
            throw new MyGroupIdNotFoundException(id);
        } else {
            return group.getAgeVar();
        }
    }

    /*
      @ ensures (\forall Person i; getGroup(id2).hasPerson(i);
      @          \old(getGroup(id2).hasPerson(i)));
      @ ensures \old(getGroup(id2).people.length) == getGroup(id2).people.length + 1;
      @ ensures getGroup(id2).hasPerson(getPerson(id1)) == false;
      @*/
    @Override
    public void delFromGroup(int id1, int id2)
            throws GroupIdNotFoundException, PersonIdNotFoundException, EqualPersonIdException {
        MyPerson person = (MyPerson) getPerson(id1);
        MyGroup group = (MyGroup) getGroup(id2);
        if (group == null) {
            throw new MyGroupIdNotFoundException(id2);
        } else if (person == null) {
            throw new MyPersonIdNotFoundException(id1);
        } else if (!group.hasPerson(person)) {
            throw new MyEqualPersonIdException(id1);
        } else {
            group.delPerson(person);
        }
    }

    @Override
    public boolean containsMessage(int id) {
        MyMessage message = (MyMessage) getMessage(id);
        return message != null;
    }

    @Override
    public void addMessage(Message message) throws
            EqualMessageIdException, EqualPersonIdException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        } else if (message.getType() == 0 && message.getPerson1() == message.getPerson2()) {
            throw new MyEqualPersonIdException(message.getPerson1().getId());
        } else {
            messages.add(message);
        }
    }

    @Override
    public Message getMessage(int id) {
        for (Message message : messages) {
            if (message.getId() == id) {
                return message;
            }
        }
        return null;
    }

    @Override
    public void sendMessage(int id) throws
            RelationNotFoundException, MessageIdNotFoundException, PersonIdNotFoundException {
        MyMessage message = (MyMessage) getMessage(id);
        if (message == null) {
            throw new MyMessageIdNotFoundException(id);
        } else if (message.getType() == 0
                && !(message.getPerson1().isLinked(message.getPerson2()))) {
            throw new MyRelationNotFoundException(message.getPerson1().getId(),
                    message.getPerson2().getId());
        } else if (message.getType() == 1
                && !(message.getGroup().hasPerson(message.getPerson1()))) {
            throw new MyPersonIdNotFoundException(message.getPerson1().getId());
        } else if (message.getType() == 0
                && message.getPerson1().isLinked(message.getPerson2())) {
            (message.getPerson1()).addSocialValue(message.getSocialValue());
            (message.getPerson2()).addSocialValue(message.getSocialValue());
            ((MyPerson) message.getPerson2()).addMessageHead(message);
            messages.remove(message);
        } else { //type=1
            for (Person person : people) {
                if (message.getGroup().hasPerson(person)) {
                    person.addSocialValue(message.getSocialValue());
                }
            }
            messages.remove(message);
        }
    }

    @Override
    public int querySocialValue(int id) throws PersonIdNotFoundException {
        MyPerson person = (MyPerson) getPerson(id);
        if (person == null) {
            throw new MyPersonIdNotFoundException(id);
        } else {
            return person.getSocialValue();
        }
    }


    /*@ public normal_behavior
      @ requires contains(id);
      @ ensures \result == getPerson(id).getReceivedMessages();
      @ also
      @ public exceptional_behavior
      @ signals (PersonIdNotFoundException e) !contains(id);
      @*/
    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        MyPerson person = (MyPerson) getPerson(id);
        if (person == null) {
            throw new MyPersonIdNotFoundException(id);
        }
        return person.getReceivedMessages();
    }


    /*@ public normal_behavior
      @ requires contains(id) && getPerson(id).acquaintance.length != 0;
      @ ensures \result == (\min int bestId;
      @         (\exists int i; 0 <= i && i < getPerson(id).acquaintance.length &&
      @             getPerson(id).acquaintance[i].getId() == bestId;
      @             (\forall int j; 0 <= j && j < getPerson(id).acquaintance.length;
      @                 getPerson(id).value[j] <= getPerson(id).value[i]));
      @         bestId);
      @*/
    //value最大且id最小的
    @Override
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

    @Override
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
            if (((MyPerson) getPerson(idi)).isAcquaintanceEmpty() || idi > person.getId()) {
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

    @Override
    public int modifyRelationOKTest(int id1, int id2, int value, HashMap<Integer,
            HashMap<Integer, Integer>> beforeData, HashMap<Integer,
            HashMap<Integer, Integer>> afterData) {
        return okTest.modifyRelationOkTest(id1, id2, value, beforeData, afterData);
    }

}
