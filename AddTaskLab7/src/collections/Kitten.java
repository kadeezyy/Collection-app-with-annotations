package collections;

import java.util.ArrayList;
import java.util.Collection;

public class Kitten {
    private final String name;
    private final Collection<String> favouriteStudents = new ArrayList<>();
    private String owner;

    public Kitten(String name) {
        this.name = name;
    }

    public void addFavouriteStudents(Student student) {
        favouriteStudents.add(student.getName());
    }

    public Collection<String> getFavouriteStudents() {
        return favouriteStudents;
    }

    public String toString() {
        return (String.format("Котенок по имени %s, любимые студенты:  \n" +
                "%s \nВладелец: %s", name,favouriteStudents.toString().replace("[", "")
                .replace("]", ""), owner));
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
