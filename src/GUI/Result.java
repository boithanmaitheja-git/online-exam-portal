package GUI;

public class Result {
    private String student_name;
    private String exam_title;
    private double score;
    private double percentage;
    private String grade;

    public Result(String student_name, String exam_title, double score, double percentage, String grade) {
        this.student_name = student_name;
        this.exam_title = exam_title;
        this.score = score;
        this.percentage = percentage;
        this.grade = grade;
    }

    public String getStudent_name() { return student_name; }
    public String getExam_title() { return exam_title; }
    public double getScore() { return score; }
    public double getPercentage() { return percentage; }
    public String getGrade() { return grade; }
}
