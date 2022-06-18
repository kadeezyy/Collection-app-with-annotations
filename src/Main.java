import collections.Kitten;
import collections.Student;
import common.CollectionDataController;
import common.PostgreSQLDatabase;
import common.SSHTunnel;


public class Main {
    public static void main(String[] args) {
        PostgreSQLDatabase database = initDatabase();
        database.connect();
        CollectionDataController collectionDataController = new CollectionDataController(database);

        Kitten kitten = new Kitten("1");
        kitten.addFavouriteStudents(new Student("man"));
        kitten.addFavouriteStudents(new Student("woman"));
        kitten.setOwner("woman");

        Kitten kitten1 = new Kitten("2");
        kitten1.setOwner("man");

        Kitten kitten2 = new Kitten("3");
        kitten2.addFavouriteStudents(new Student("man"));
        kitten2.addFavouriteStudents(new Student("woman"));
        kitten2.setOwner("woman");

        Kitten kitten3 = new Kitten("4");
        kitten3.setOwner("man");

        Kitten kitten4 = new Kitten("5");
        kitten4.addFavouriteStudents(new Student("man"));
        kitten4.addFavouriteStudents(new Student("woman"));
        kitten4.setOwner("woman");

        Kitten kitten5 = new Kitten("6");
        kitten5.setOwner("man");

        Kitten kitten6 = new Kitten("7");
        kitten6.addFavouriteStudents(new Student("man"));
        kitten6.addFavouriteStudents(new Student("woman"));
        kitten6.setOwner("woman");

        Kitten kitten7 = new Kitten("8");
        kitten7.setOwner("man");

        Student student1 = collectionDataController.getStudentById(6).setCollectionDataController(collectionDataController);
//        student1.addKittens(kitten);
//        student1.addKittens(kitten1);
//        student1.addKittens(kitten2);
//        student1.addKittens(kitten3);
//        student1.addKittens(kitten4);
//        student1.addKittens(kitten5);
//        student1.addKittens(kitten6);
//        student1.addKittens(kitten7);

        System.out.println(student1.getByKey(5));
        System.out.println(student1);
    }

    private static PostgreSQLDatabase initDatabase() {
        String user = "s341474";
        String password = "drj262";
        SSHTunnel tunnel = new SSHTunnel("se.ifmo.ru", user, password, 2222, "pg", 8954, 5432);
        return new PostgreSQLDatabase("localhost", tunnel.create(), "studs", user, password);
    }
}

