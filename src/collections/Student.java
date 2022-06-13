package collections;

import java.util.ArrayList;
import java.util.Collection;

public class Student {
    private Collection<Kitten> kittens = new ArrayList<>();
    private Integer id;
    private final String name;

    public Student(String name){
        this.name = name;
    }


    public void addKittens(Kitten kitten){
        kittens.add(kitten);
    }

    public String getName(){
        return name;
    }

    public Collection<Kitten> getKittens(){
        return kittens;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public int getId(){
        return id;
    }
}
