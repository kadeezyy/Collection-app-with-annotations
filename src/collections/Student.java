package collections;

import annotation.CollectionField;
import common.CollectionDataController;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Student {
    @CollectionField(mode = "LAZY")
    private List<Kitten> kittens;


    CollectionDataController collectionDataController;
    private Integer id;
    private final String name;


    public Student(String name) {
        this.name = name;
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(CollectionField.class)) {
                CollectionField collectionField = field.getAnnotation(CollectionField.class);
                if (collectionField.mode().equals("EMPTY")) {
                    kittens = new ArrayList<>();
                }
            }
        }
        validate();
    }


    @Override
    public String toString() {
//        validate();
        List<String> kittensList = new ArrayList<>();
        try {
            kittens.forEach(kitten -> kittensList.add(kitten.getName()));
        } catch (NullPointerException ex) {

        }
        return (String.format("Любимые котята студента по имени '%s': \n", name) + kittensList.toString()
                .replace("[", "").replace("]", " "));
    }

    public Student setCollectionDataController(CollectionDataController collectionDataController) {
        this.collectionDataController = collectionDataController;
        return this;
    }


    public Kitten getByKey(int key) {
        validate();
        key--;
        try {
            for (Field field : this.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(CollectionField.class)) {
                    CollectionField collectionField = field.getAnnotation(CollectionField.class);
                    if (kittens.size() <= key) {
                        while ((key - kittens.size()) / collectionField.pageSize() >= 0) {
                            validate();
                        }
                    }
                }
            }
            return kittens.get(key);
        } catch (IndexOutOfBoundsException ex) {
            System.out.println("В базе данных нет такого элемента");
            return null;
        }
    }

    public void addKittens(Kitten kitten) {
        kittens.add(kitten);
        collectionDataController.insertKitten(kitten);
    }

    public void addAllKittens(List<Kitten> list) {
        if (validate()) {
            kittens.addAll(list);
        }
    }

    public boolean validate() {
        boolean validated = false;
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(CollectionField.class)) {
                CollectionField collectionField = field.getAnnotation(CollectionField.class);
                if (collectionField.mode().equals("EAGER") && !(collectionDataController == null)) {
                    System.out.println("Hello EAGER!");
                    kittens = collectionDataController.getAllKittensOfStudent(this);
                    validated = true;
                }
                if (collectionField.mode().equals("LAZY")) {
                    if (kittens == null && !(collectionDataController == null)) {
                        System.out.println("Hello LAZY!");
                        kittens = collectionDataController.getAllKittensOfStudent(this);
                        validated = true;
                    }
                    if (collectionDataController == null) {
                        validated = false;
                    }
                }
                if (collectionField.mode().equals("PAGE")) {
                    if (kittens == null && !(collectionDataController == null)) {
                        System.out.println("Hello PAGE!");
                        kittens = new ArrayList<>();
                        List<Kitten> bufferKittens = new ArrayList<>(collectionDataController.getAllKittensOfStudent(this));
                        int diff = bufferKittens.size();

                        if (diff == 0) {
                            System.out.println("Вы дошли до конца коллекции");
                        }
                        if (diff <= collectionField.pageSize()) {
                            for (int i = 0; i < diff; i++) {
                                kittens.add(bufferKittens.get(kittens.size()));
                            }
                        }
                        for (int i = 0; i < collectionField.pageSize(); i++) {
                            kittens.add(bufferKittens.get(kittens.size()));
                        }
                        validated = true;
                    } else if (!(kittens == null) && !(collectionDataController == null)) {
                        List<Kitten> bufferKittens = new ArrayList<>(collectionDataController.getAllKittensOfStudent(this));
                        for (int i = 0; i < collectionField.pageSize(); i++) {
                            kittens.add(bufferKittens.get(kittens.size()));
                        }
                        validated = true;
                    }
                }
            }
        }
        return validated;
    }

    public String getName() {
        return name;
    }

    public Collection<Kitten> getKittens() {
        validate();
        return kittens;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

//class LazyInvocationLoader implements InvocationHandler {
//    private CollectionDataController collectionDataController;
//
//    LazyInvocationLoader(CollectionDataController collectionDataController) {
//        this.collectionDataController = collectionDataController;
//    }
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        return method.invoke(collectionDataController.getAll());
//    }
//}
