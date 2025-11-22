package GUI;

public class Teacher {
    private int teacher_id;
    private String teacher_name;
    private String email;
    private String username;
    private String department;

    public Teacher(int teacher_id, String teacher_name, String email, String username, String department) {
        this.teacher_id = teacher_id;
        this.teacher_name = teacher_name;
        this.email = email;
        this.username = username;
        this.department = department;
    }

    public int getTeacher_id() { return teacher_id; }
    public String getTeacher_name() { return teacher_name; }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getDepartment() { return department; }
}
