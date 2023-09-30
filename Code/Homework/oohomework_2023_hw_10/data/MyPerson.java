package data;

import com.oocourse.spec2.main.Message;
import com.oocourse.spec2.main.Person;

import java.util.ArrayList;
import java.util.List;

public class MyPerson implements Person {
    private final int id;
    private final String name;
    private final int age;
    private final ArrayList<Person> acquaintance;
    private final ArrayList<Integer> value;
    private int socialValue;
    private final ArrayList<Message> messages;

    public MyPerson(int id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.acquaintance = new ArrayList<>();
        this.value = new ArrayList<>();
        this.socialValue = 0;
        this.messages = new ArrayList<>();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getAge() {
        return age;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Person)) {
            return false;
        }
        MyPerson myPerson = (MyPerson) obj;
        return id == myPerson.getId();
    }

    @Override
    public boolean isLinked(Person person) {
        if (person.getId() == id) {
            return true;
        }
        for (int i = 0; i < acquaintance.size(); ++i) {
            if (acquaintance.get(i).getId() == person.getId()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int queryValue(Person person) {
        for (int i = 0; i < acquaintance.size(); ++i) {
            if (acquaintance.get(i).getId() == person.getId()) {
                return value.get(i);
            }
        }
        return 0;
    }

    @Override
    public int compareTo(Person p2) {
        return name.compareTo(p2.getName());
    }

    public void addAcquaintance(Person person, int value) {
        this.acquaintance.add(person);
        this.value.add(value);
    }

    public void deleteAcquaintance(Person person) {
        for (int i = 0; i < acquaintance.size(); i++) {
            if (acquaintance.get(i).equals(person)) {
                acquaintance.remove(i);
                value.remove(i);
                return;
            }
        }
    }

    public int getAcquaintanceSize() {
        return this.acquaintance.size();
    }

    public int addTripleSum(Person person, int tripleSum) {
        int triple = tripleSum;
        for (Person person1 : acquaintance) {
            if (person.isLinked(person1)) {
                //System.out.println("link"+person.getId()+" "+person1.getId()+" "+id);
                triple++;
            }
        }
        return triple;
    }

    public int subTripleSum(Person person, int tripleSum) {
        int triple = tripleSum;
        for (Person person1 : acquaintance) {
            if (person.isLinked(person1)) {
                //System.out.println("link"+person.getId()+" "+person1.getId()+" "+id);
                triple--;
            }
        }
        return triple;
    }

    @Override
    public void addSocialValue(int num) {
        socialValue += num;
    }

    @Override
    public int getSocialValue() {
        return this.socialValue;
    }

    @Override
    public List<Message> getMessages() {
        return this.messages;
    }

    @Override
    public List<Message> getReceivedMessages() {
        int num = 0;
        ArrayList<Message> result = new ArrayList<>();
        for (Message message : messages) {
            if (num > 4) {
                break;
            }
            result.add(message);
            num++;
        }
        return result;
    }

    public void modifyRelation(Person person, int valueChange) {
        for (int i = 0; i < acquaintance.size(); ++i) {
            if (acquaintance.get(i).getId() == person.getId()) {
                value.set(i, value.get(i) + valueChange);
            }
        }
    }

    public void addMessageHead(Message message) {
        this.messages.add(0, message);
    }

    public int queryBestAcquaintance() {
        int maxValue = 0;
        int bestid = 0;
        for (int i = 0; i < value.size(); ++i) {
            if (value.get(i) > maxValue) {
                maxValue = value.get(i);
                bestid = acquaintance.get(i).getId();
            }
        }
        for (int i = 0; i < acquaintance.size(); ++i) {
            if (value.get(i) == maxValue && acquaintance.get(i).getId() < bestid) {
                bestid = acquaintance.get(i).getId();
            }
        }
        return bestid;
    }

    public boolean isAcquaintanceEmpty() {
        return acquaintance.size() == 0;
    }

}
