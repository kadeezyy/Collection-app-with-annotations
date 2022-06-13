import collections.Kitten;
import collections.Student;
import common.CollectionDataController;
import common.PostgreSQLDatabase;
import common.SSHTunnel;

public class Main {
    public static void main(String[] args) {
        PostgreSQLDatabase database = initDatabase();
        database.connect();

        Kitten kitten = new Kitten("cat");
        kitten.addFavouriteStudents(new Student("man"));
        kitten.addFavouriteStudents(new Student("woman"));
        kitten.setOwner("woman");
        CollectionDataController collectionDataController = new CollectionDataController(database);
        collectionDataController.insertKitten(kitten);
        System.out.println("Все котенки: " + collectionDataController.getALlKittensNames());
        System.out.println("Любимые студенты котят: " + collectionDataController.getAllStudentsOfKitten(kitten));

        Student student = new Student("woman");
        student.addKittens(new Kitten("meow"));
        student.addKittens(new Kitten("uwu"));
        collectionDataController.insert(student);
        System.out.println("Все студенты: " + collectionDataController.getAllName());
        System.out.println("Котята студентов: " + collectionDataController.getAllKittensOfStudent(student));
    }


    private static PostgreSQLDatabase initDatabase() {
        String user = "s335089";
        String password = "gly273";
        SSHTunnel tunnel = new SSHTunnel("se.ifmo.ru", user, password, 2222, "pg", 8594, 5432);
        return new PostgreSQLDatabase("localhost", tunnel.create(), "studs", user, password);
    }
}
