package GUI;

public class Student {
    private int student_id;
    private String student_name;
    private String email;
    private String username;
    private String branch;
    private int year;

    public Student(int student_id, String student_name, String email, String username, String branch, int year) {
        this.student_id = student_id;
        this.student_name = student_name;
        this.email = email;
        this.username = username;
        this.branch = branch;
        this.year = year;
    }

    public int getStudent_id() { return student_id; }
    public String getStudent_name() { return student_name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getBranch() { return branch; }
    public int getYear() { return year; }
}
