public class ResultScreen {
    private GradeManager gradeManager;

    public ResultScreen(GradeManager gradeManager) {

    initComponents();

    this.gradeManager = gradeManager;

    showResult();
    }

    private void showResult() {

    int boxScore = gradeManager.getBoxScore();

    int puzzleScore = gradeManager.getPuzzleScore();

    int totalScore = gradeManager.getTotalScore();

    String grade = gradeManager.calculateGrade();

    lblBoxScore.setText(String.valueOf(boxScore));

    lblPuzzleScore.setText(String.valueOf(puzzleScore));

    lblTotalScore.setText(String.valueOf(totalScore));

    lblGrade.setText(grade);
    }

    GradeManager gradeManager1 = new GradeManager();
    int boxScore = gradeManager.getBoxScore();
    int puzzleScore = gradeManager.getPuzzleScore();
    int total = gradeManager.getTotalScore();
    String grade = gradeManager.getGrade();
    
}
