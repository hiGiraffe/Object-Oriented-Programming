package data;

import com.oocourse.spec2.main.Group;
import com.oocourse.spec2.main.Person;

import java.util.ArrayList;

public class MyGroup implements Group {
    private final int id;
    private ArrayList<Person> people;
    private int ageSum;

    public MyGroup(int id) {
        this.id = id;
        this.people = new ArrayList<>();
        ageSum = 0;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && ((Group) obj).getId() == id) {
            return true;
        }
        return false;
    }

    @Override
    public void addPerson(Person person) {
        ageSum += person.getAge();
        people.add(person);
    }

    @Override
    public boolean hasPerson(Person person) {
        for (Person person1 : people) {
            if (person.equals(person1)) {
                return true;
            }
        }
        return false;
    }

    public int getValueSum() {
        int valueSum = 0;
        for (Person person1 : people) {
            for (Person person2 : people) {
                if (person1.isLinked(person2)) {
                    valueSum += person1.queryValue(person2);
                }
            }
        }
        return valueSum;
    }

    public int getAgeMean() {
        if (people.size() == 0) {
            return 0;
        } else {
            return ageSum / people.size();
        }
    }

    public int getAgeVar() {
        long ageVarSum = 0;
        int size = people.size();
        if (size == 0) {
            return 0;
        } else {
            for (Person person : people) {
                ageVarSum += (long) (person.getAge() - ageSum / size)
                        * (person.getAge() - ageSum / size);
            }
        }
        return (int) (ageVarSum / people.size());
    }

    public void delPerson(Person person) {
        ageSum -= person.getAge();
        people.remove(person);
    }

    @Override
    public int getSize() {
        return people.size();
    }
}
