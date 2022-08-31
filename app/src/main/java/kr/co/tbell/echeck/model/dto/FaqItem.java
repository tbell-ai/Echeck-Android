package kr.co.tbell.echeck.model.dto;

public class FaqItem {

    private String question;

    private String answer;

    private boolean expandable;

    public FaqItem(String question, String answer) {
        this.question = question;
        this.answer = answer;
        this.expandable = false;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    @Override
    public String toString() {
        return "Faq{" +
                "question='" + question + '\'' +
                ", answer='" + answer + '\'' +
                ", expandable=" + expandable +
                '}';
    }
}
