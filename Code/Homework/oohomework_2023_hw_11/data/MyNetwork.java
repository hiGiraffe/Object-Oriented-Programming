package data;

import com.oocourse.spec3.exceptions.AcquaintanceNotFoundException;
import com.oocourse.spec3.exceptions.EmojiIdNotFoundException;
import com.oocourse.spec3.exceptions.EqualEmojiIdException;
import com.oocourse.spec3.exceptions.EqualGroupIdException;
import com.oocourse.spec3.exceptions.EqualMessageIdException;
import com.oocourse.spec3.exceptions.EqualPersonIdException;
import com.oocourse.spec3.exceptions.EqualRelationException;
import com.oocourse.spec3.exceptions.GroupIdNotFoundException;
import com.oocourse.spec3.exceptions.MessageIdNotFoundException;
import com.oocourse.spec3.exceptions.PathNotFoundException;
import com.oocourse.spec3.exceptions.PersonIdNotFoundException;
import com.oocourse.spec3.exceptions.RelationNotFoundException;
import com.oocourse.spec3.main.EmojiMessage;
import com.oocourse.spec3.main.Group;
import com.oocourse.spec3.main.Message;
import com.oocourse.spec3.main.Network;
import com.oocourse.spec3.main.NoticeMessage;
import com.oocourse.spec3.main.Person;
import com.oocourse.spec3.main.RedEnvelopeMessage;
import exceptions.MyEmojiIdNotFoundException;
import exceptions.MyEqualEmojiIdException;
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
    private final ArrayList<Integer> emojiIdList;
    private final HashMap<Integer, Integer> emojiHeatList;
    private Union union;
    private int tripleSum;
    private int blockSum;

    private final ArrayList<Integer> idMin;
    private final ArrayList<Integer> idMax;
    private final ArrayList<Integer> values;
    private final OkTest okTest;
    private final Query query;

    public MyNetwork() {
        people = new ArrayList<>();
        messages = new ArrayList<>();
        groups = new ArrayList<>();
        emojiIdList = new ArrayList<>();
        emojiHeatList = new HashMap<Integer, Integer>();
        union = new Union();
        tripleSum = 0;
        blockSum = 0;

        idMin = new ArrayList<>();
        idMax = new ArrayList<>();
        values = new ArrayList<>();

        okTest = new OkTest();
        query = new Query(people);
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
            values.add(value);
        }
    }

    @Override
    public int queryValue(int id1, int id2) throws
            PersonIdNotFoundException, RelationNotFoundException {
        return query.queryValue(id1, id2);
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
                        values.remove(i);
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
            EqualMessageIdException, EqualPersonIdException, EmojiIdNotFoundException {
        if (containsMessage(message.getId())) {
            throw new MyEqualMessageIdException(message.getId());
        } else if ((message instanceof EmojiMessage)
                && !containsEmojiId(((EmojiMessage) message).getEmojiId())) {
            throw new MyEmojiIdNotFoundException(((EmojiMessage) message).getEmojiId());
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

            if (message instanceof RedEnvelopeMessage) {
                (message.getPerson1()).addMoney(-((RedEnvelopeMessage) message).getMoney());
                (message.getPerson2()).addMoney(((RedEnvelopeMessage) message).getMoney());
            } else if (message instanceof EmojiMessage) {
                for (Integer emojiId : emojiIdList) {
                    if (emojiId == ((EmojiMessage) message).getEmojiId()) {
                        emojiHeatList.put(emojiId, emojiHeatList.get(emojiId) + 1);
                    }
                }
            }
        } else { //type=1
            for (Person person : people) {
                if (message.getGroup().hasPerson(person)) {
                    person.addSocialValue(message.getSocialValue());
                }
            }
            messages.remove(message);

            if (message instanceof RedEnvelopeMessage) {
                ((MyGroup) message.getGroup()).broadcast((RedEnvelopeMessage) message);
            } else if (message instanceof EmojiMessage) {
                for (Integer emojiId : emojiIdList) {
                    if (emojiId == ((EmojiMessage) message).getEmojiId()) {
                        emojiHeatList.put(emojiId, emojiHeatList.get(emojiId) + 1);
                    }
                }
            }
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

    @Override
    public List<Message> queryReceivedMessages(int id) throws PersonIdNotFoundException {
        MyPerson person = (MyPerson) getPerson(id);
        if (person == null) {
            throw new MyPersonIdNotFoundException(id);
        }
        return person.getReceivedMessages();
    }

    //value最大且id最小的
    @Override
    public int queryBestAcquaintance(int id) throws
            PersonIdNotFoundException, AcquaintanceNotFoundException {
        return query.queryBestAcquaintance(id);
    }

    @Override
    public int queryCoupleSum() {
        return query.queryCoupleSum();
    }

    public boolean containsEmojiId(int id) {
        for (Integer emojiId : emojiIdList) {
            if (id == emojiId) {
                return true;
            }
        }
        return false;
    }

    public void storeEmojiId(int id) throws EqualEmojiIdException {
        if (containsEmojiId(id)) {
            throw new MyEqualEmojiIdException(id);
        }
        emojiIdList.add(id);
        emojiHeatList.put(id, 0);
    }

    public int queryMoney(int id) throws PersonIdNotFoundException {
        if (!contains(id)) {
            throw new MyPersonIdNotFoundException(id);
        }
        return getPerson(id).getMoney();
    }

    public int queryPopularity(int id) throws EmojiIdNotFoundException {
        if (!containsEmojiId(id)) {
            throw new MyEmojiIdNotFoundException(id);
        }
        return emojiHeatList.get(id);
    }

    public int deleteColdEmoji(int limit) {
        emojiIdList.removeIf(e -> emojiHeatList.get(e) < limit);
        emojiHeatList.keySet().removeIf(e -> emojiHeatList.get(e) < limit);
        messages.removeIf(m -> ((m instanceof EmojiMessage)
                && !containsEmojiId(((EmojiMessage) m).getEmojiId())));
        return emojiIdList.size();
    }

    public void clearNotices(int personId) throws PersonIdNotFoundException {
        if (!contains(personId)) {
            throw new MyPersonIdNotFoundException(personId);
        }
        Person person = getPerson(personId);
        person.getMessages().removeIf(m -> (m instanceof NoticeMessage));
    }

    public int queryLeastMoments(int id) throws PersonIdNotFoundException, PathNotFoundException {
        return query.queryLeastMoments(id);
    }

    @Override
    public int deleteColdEmojiOKTest(int limit, ArrayList<HashMap<Integer, Integer>> beforeData,
                                     ArrayList<HashMap<Integer, Integer>> afterData, int result) {
        return okTest.deleteColdEmojiOkTest(limit, beforeData, afterData, result);
    }
}
