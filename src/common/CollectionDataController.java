package common;

import collections.Kitten;
import collections.Student;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CollectionDataController {
    private final PostgreSQLDatabase database;

    public CollectionDataController(PostgreSQLDatabase database) {
        this.database = database;
        try {
            database.executeQuery("DROP TABLE IF EXISTS kitten_collection ").close();
            database.executeQuery("DROP TABLE IF EXISTS students_collection ").close();
            database.executeQuery("CREATE TABLE IF NOT EXISTS students_collection (" +
                    "id bigint PRIMARY KEY UNIQUE, " +
                    "name varchar(128) NOT NULL, " +
                    "kittens_name varchar(128) NOT NULL)").close();
            database.executeQuery("CREATE TABLE IF NOT EXISTS kitten_collection (" +
                    "kitten_id serial PRIMARY KEY, " +
                    "name varchar(128) NOT NULL, " +
                    "favourite_students varchar(128), " +
                    "kittens_owner varchar(128) NOT NULL)").close();

            ResultSet res = database.executeQuery("SELECT MAX(id) FROM students_collection");
            long maxId = 0;
            while (res.next()) {
                if (res.getInt(1) >= maxId) {
                    maxId = res.getInt(1);
                }
            }
            database.executeQuery("DROP SEQUENCE IF EXISTS id_sequence").close();
            database.executeQuery("CREATE SEQUENCE IF NOT EXISTS id_sequence START " + (maxId + 1)).close();
            res.close();

            res = database.executeQuery("SELECT MAX(kitten_id) FROM kitten_collection");
            int maxIdkit = 0;
            while (res.next()) {
                if (res.getInt(1) >= maxId) {
                    maxIdkit = res.getInt(1);
                }
            }
            database.executeQuery("DROP SEQUENCE IF EXISTS kittens_id_sequence").close();
            database.executeQuery("CREATE SEQUENCE IF NOT EXISTS kittens_id_sequence START " + (maxIdkit + 1)).close();
            res.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public List<String> getAllName() {
        List<String> studentsName = new ArrayList<>();
        for (Student student : getAll()) {
            studentsName.add(student.getName());
        }
        return studentsName;
    }

    public List<Student> getAll() {
        List<Student> students = new ArrayList<>();
        try {
            ResultSet resultSet = database.executeQuery("SELECT * FROM students_collection");
            while (resultSet.next()) {
                students.add(construct(resultSet));
            }
            resultSet.close();
        } catch (SQLException ex) {
        }
        return students;
    }

    public List<String> getAllStudentsOfKitten(Kitten kitten){
        List<String> students = new ArrayList<>();
        try{
            ResultSet resultSet = database.executeQuery("SELECT favourite_students FROM kitten_collection WHERE name='%s'", kitten.getName());
            resultSet.next();
            students.add(resultSet.getString(1));
            resultSet.close();
            return students;
        }catch (SQLException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public List<String> getAllKittensOfStudent(Student student) {
        List<String> kittens = new ArrayList<>();
        try {
            ResultSet resultSet = database.executeQuery("SELECT kittens_name FROM students_collection WHERE name='%s'", student.getName());
            resultSet.next();
            kittens.add(resultSet.getString(1));
            resultSet.close();
            return kittens;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<String> getALlKittensNames() {
        List<String> kittensNames = new ArrayList<>();
        for (Kitten kitten : getAllKittens()) {
            kittensNames.add(kitten.getName());
        }
        return kittensNames;
    }

    public List<Kitten> getAllKittens() {
        List<Kitten> kittens = new ArrayList<>();
        try {
            ResultSet resultSet = database.executeQuery("SELECT * FROM kitten_collection");
            while (resultSet.next()) {
                kittens.add(constructKitten(resultSet));
            }
            resultSet.close();
            return kittens;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Kitten constructKitten(ResultSet resultSet) {
        try {
            String kittensName = resultSet.getString("name");
            String[] favouriteStudents = resultSet.getString("favourite_students").trim().split(",");
            Kitten kitten = new Kitten(kittensName);
            kitten.setOwner(resultSet.getString("kittens_owner"));
            for (String name : favouriteStudents) {
                kitten.addFavouriteStudents(new Student(name));
            }
            return kitten;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public Student construct(ResultSet resultSet) {
        try {
            String kittensNameSet = resultSet.getString("kittens_name");
            String[] kittensName = kittensNameSet.split(",");
            Student student = new Student(resultSet.getString("name"));
            for (String name : kittensName) {
                Kitten kitten = new Kitten(name);
                ResultSet result = database.executeQuery("SELECT kittens_owner FROM kitten_collection WHERE name='%s'", name);
                while (result.next()) {
                    String owner = result.getString(1);
                    kitten.setOwner(owner);
                }
                result.close();
                student.addKittens(kitten);
            }
            student.setId(resultSet.getInt("id"));
            return student;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void delete(Student student) {
        try {
            database.executeQuery("DELETE FROM students_collection WHERE id=%d", student.getId()).close();
        } catch (SQLException ex) {
        }
    }

    public boolean insert(Student student) {
        try {
            ResultSet resultSet = database.executeQuery("SELECT MAX(id) FROM students_collection");
            resultSet.next();
            student.setId(resultSet.getInt(1) + 1);
            resultSet.close();
            List<String> kittensNameCollection = new ArrayList<>();
            student.getKittens().forEach(kitten -> kittensNameCollection.add(kitten.getName()));

            database.setStatement("INSERT INTO students_collection(id, name, kittens_name)  VALUES(?,?,?)");
            database.getStatement().setInt(1, student.getId());
            database.getStatement().setString(2, student.getName());
//            database.getStatement().setString(3, String.valueOf(student.getName()));
            database.getStatement().setString(3, String.valueOf(kittensNameCollection));
            database.executeQueryStatement().close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean insertKitten(Kitten kitten) {
        try {
            List<String> students = new ArrayList<>();
            for (Student student : kitten.getFavouriteStudents()){
                students.add(student.getName());
            }
            ResultSet resultSet = database.executeQuery("SELECT nextval('kittens_id_sequence')");
            resultSet.next();
            int id = resultSet.getInt(1);
            database.setStatement("INSERT INTO kitten_collection (kitten_id, name, favourite_students, kittens_owner) VALUES (?, " +
                    "?,?,?)");
            database.getStatement().setInt(1, id);
            database.getStatement().setString(2, kitten.getName());
            database.getStatement().setString(3, String.valueOf(students));
            database.getStatement().setString(4, kitten.getOwner());
            database.executeQueryStatement().close();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
