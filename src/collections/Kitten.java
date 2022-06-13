package collections;

import java.util.ArrayList;
import java.util.Collection;

public class Kitten {
    private final String name;
    private final Collection<Student> favouriteStudents = new ArrayList<>();
    private String owner;

    public Kitten(String name) {
        this.name = name;
    }

    public void addFavouriteStudents(Student student) {
        favouriteStudents.add(student);
    }

    public Collection<Student> getFavouriteStudents() {
        return favouriteStudents;
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
